package ftblag.biotechnik.entity;

import ftblag.biotechnik.BTRegistry;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.List;

public class EntityRFOrb extends Entity {

    public int orbAge = 0;
    public int orbMaxAge = 150;
    public int orbCooldown;
    private int orbHealth = 5;
    public int rfValue;
    private Player closestPlayer;

    public EntityRFOrb(Level world, double x, double y, double z, int rfValue) {
        this(BTRegistry.ORB_TYPE.get(), world);
        setPos(x, y, z);
        this.setYRot((float)(this.random.nextDouble() * 360.0D));
        this.setDeltaMovement((this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D);
        this.rfValue = rfValue;
    }

    public EntityRFOrb(PlayMessages.SpawnEntity packet, Level world) {
        this(BTRegistry.ORB_TYPE.get(), world);
    }

    public EntityRFOrb(Level world) {
        this(BTRegistry.ORB_TYPE.get(), world);
    }

    public EntityRFOrb(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 0.5D * 64.0D * getViewScale();
        return distance < d * d;
    }

    @Override
    public void tick() {
        super.tick();
        if (orbCooldown > 0)
            --orbCooldown;

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.isEyeInFluid(FluidTags.WATER)) {
            this.setUnderwaterMovement();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
            this.setDeltaMovement((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), (double)0.2F, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
        }

        if (!this.level.noCollision(this.getBoundingBox())) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }

        double d0 = 8.0D;
        if (tickCount % 5 == 0 && (this.closestPlayer == null || this.closestPlayer.distanceToSqr(this) > 64.0D)) {
            Player player = null;

            List<? extends Player> players = this.level.players();
            if (players.size() > 0) {
                double distance = Double.MAX_VALUE;

                for (Player entity : players) {
                    if (!entity.isSpectator()) {
                        double d = entity.distanceToSqr(this);

                        if (d < d0 * d0 && d < distance && isCollectorOnHotbar(rfValue, entity) >= 0) {
                            distance = d;
                            player = entity;
                        }
                    }
                }
            }

            this.closestPlayer = player;
        }

        if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
            this.closestPlayer = null;
        }

        if (this.closestPlayer != null) {
            Vec3 vector3d = new Vec3(this.closestPlayer.getX() - this.getX(), this.closestPlayer.getY() + (double)this.closestPlayer.getEyeHeight() / 2.0D - this.getY(), this.closestPlayer.getZ() - this.getZ());
            double d1 = vector3d.lengthSqr();
            if (d1 < 64.0D) {
                double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(vector3d.normalize().scale(d2 * d2 * 0.1D)));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        float f = 0.98F;
        if (this.onGround) {
            BlockPos pos =new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            f = this.level.getBlockState(pos).getFriction(this.level, pos, this) * 0.98F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.98D, f));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
        }

        ++orbAge;
        if (orbAge >= orbMaxAge)
            discard();
    }

    private void setUnderwaterMovement() {
        Vec3 vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d.x * (double)0.99F, Math.min(vector3d.y + (double)5.0E-4F, 0.06F), vector3d.z * (double)0.99F);
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level.isClientSide || this.isRemoved()) return false; //Forge: Fixes MC-53850
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markHurt();
            this.orbHealth = (int)((float)this.orbHealth - amount);
            if (this.orbHealth <= 0) {
                this.discard();
            }

            return false;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort("Health", (short)this.orbHealth);
        compound.putShort("Age", (short)this.orbAge);
        compound.putShort("Value", (short)this.rfValue);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.orbHealth = compound.getShort("Health");
        this.orbAge = compound.getShort("Age");
        this.rfValue = compound.getShort("Value");
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level.isClientSide) {
            final int slot = isCollectorOnHotbar(rfValue, player);
            if (orbCooldown == 0 && slot >= 0) {
                final ItemRFCollector wand = (ItemRFCollector) player.getInventory().items.get(slot).getItem();
                wand.addRF(player.getInventory().items.get(slot), rfValue, false);

                playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f * ((random.nextFloat() - random.nextFloat()) * 0.7f + 1.8f));

                this.discard();
            }

        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static int isCollectorOnHotbar(int amount, Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack is = player.getInventory().items.get(i);
            if (!is.isEmpty() && is.getItem() instanceof ItemRFCollector)
                if (((ItemRFCollector) is.getItem()).addRF(is, amount, true) < amount)
                    return i;
        }
        return -1;
    }
}
