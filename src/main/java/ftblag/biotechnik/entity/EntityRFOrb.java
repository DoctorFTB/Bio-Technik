package ftblag.biotechnik.entity;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class EntityRFOrb extends Entity {

    public int orbAge = 0;
    public int orbMaxAge = 150;
    public int orbCooldown;
    private int orbHealth = 5;
    public int rfValue;
    private PlayerEntity closestPlayer;

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d = 0.5D * 64.0D * getRenderDistanceWeight();
        return distance < d * d;
    }

    public EntityRFOrb(World world, double x, double y, double z, int rfValue) {
        this(BioTechnik.orbType, world);
//        setSize(0.125F, 0.125F);
        setPosition(x, y, z);
        rotationYaw = (float) (Math.random() * 360.0D);
        this.setMotion((this.rand.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D, this.rand.nextDouble() * 0.2D * 2.0D, (this.rand.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D);
        this.rfValue = rfValue;
    }

    public EntityRFOrb(FMLPlayMessages.SpawnEntity packet, World world) {
        this(BioTechnik.orbType, world);
    }

    public EntityRFOrb(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void registerData() {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        float f1 = 0.5F;

        int i = super.getBrightnessForRender();
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f1 * 15.0F * 16.0F);
        if (j > 240)
            j = 240;

        return j | k << 16;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        super.tick();
        if (orbCooldown > 0)
            --orbCooldown;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (this.areEyesInFluid(FluidTags.WATER)) {
            Vec3d vec3d = this.getMotion();
            this.setMotion(vec3d.x * (double)0.99F, Math.min(vec3d.y + (double)5.0E-4F, (double)0.06F), vec3d.z * (double)0.99F);
        } else if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.03D, 0.0D));
        }

        if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
            this.setMotion((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), (double)0.2F, (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        if (!this.world.areCollisionShapesEmpty(this.getBoundingBox())) {
            this.pushOutOfBlocks(this.posX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.posZ);
        }

        double d0 = 8.0D;
        if (ticksExisted % 5 == 0 && (closestPlayer == null || this.closestPlayer.getDistanceSq(this) > 64.0D)) {
            List<PlayerEntity> targets = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).grow(d0, d0, d0));
            if (targets.size() > 0) {
                double distance = Double.MAX_VALUE;

                for (PlayerEntity t : targets) {
                    double d = t.getDistanceSq(this);
                    if (d < distance && isCollectorOnHotbar(rfValue, t) >= 0) {
                        distance = d;
                        closestPlayer = t;
                    }
                }
            }
        }

        if (this.closestPlayer != null) {
            Vec3d vec3d = new Vec3d(this.closestPlayer.posX - this.posX, this.closestPlayer.posY + (double)this.closestPlayer.getEyeHeight() / 2.0D - this.posY, this.closestPlayer.posZ - this.posZ);
            double d1 = vec3d.lengthSquared();
            if (d1 < 64.0D) {
                double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
                this.setMotion(this.getMotion().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
            }
        }

        this.move(MoverType.SELF, this.getMotion());
        float f = 0.98F;
        if (this.onGround) {
            BlockPos underPos = new BlockPos(this.posX, this.getBoundingBox().minY - 1.0D, this.posZ);
            f = this.world.getBlockState(underPos).getSlipperiness(this.world, underPos, this) * 0.98F;
        }

        this.setMotion(this.getMotion().mul((double)f, 0.98D, (double)f));
        if (this.onGround) {
            this.setMotion(this.getMotion().mul(1.0D, -0.9D, 1.0D));
        }

        ++orbAge;
        if (orbAge >= orbMaxAge)
            remove();
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    protected void dealFireDamage(int amount) {
        attackEntityFrom(DamageSource.IN_FIRE, amount);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.world.isRemote || this.removed) return false; //Forge: Fixes MC-53850
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markVelocityChanged();
            this.orbHealth = (int)((float)this.orbHealth - amount);
            if (this.orbHealth <= 0) {
                this.remove();
            }

            return false;
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putShort("Health", (short)this.orbHealth);
        compound.putShort("Age", (short)this.orbAge);
        compound.putShort("Value", (short)this.rfValue);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.orbHealth = compound.getShort("Health");
        this.orbAge = compound.getShort("Age");
        this.rfValue = compound.getShort("Value");
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity player) {
        if (!world.isRemote) {
            final int slot = isCollectorOnHotbar(rfValue, player);
            if (orbCooldown == 0 && slot >= 0) {
                final ItemRFCollector wand = (ItemRFCollector) player.inventory.mainInventory.get(slot).getItem();
                wand.addRF(player.inventory.mainInventory.get(slot), rfValue, false);
                playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f,
                        0.5f * ((rand.nextFloat() - rand.nextFloat()) * 0.7f + 1.8f));
                remove();
            }
        }
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() { // TODO
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static int isCollectorOnHotbar(int amount, PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack is = player.inventory.mainInventory.get(i);
            if (!is.isEmpty() && is.getItem() instanceof ItemRFCollector)
                if (((ItemRFCollector) is.getItem()).addRF(is, amount, true) < amount)
                    return i;
        }
        return -1;
    }
}
