package ftblag.biotechnik.entity;

import java.util.List;

import ftblag.biotechnik.BTUtils;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FTB_lag.
 */
public class EntityRFOrb extends Entity {
    public int orbAge = 0;
    public int orbMaxAge = 150;
    public int orbCooldown;
    private int orbHealth = 5;
    public int rfValue;
    private EntityPlayer closestPlayer;

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d = 0.5D * 64.0D * getRenderDistanceWeight();
        return distance < d * d;
    }

    public EntityRFOrb(World world, double x, double y, double z, int rfValue) {
        super(world);
        setSize(0.125F, 0.125F);
        setPosition(x, y, z);
        rotationYaw = (float) (Math.random() * 360.0D);
        motionX = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F;
        motionY = (float) (Math.random() * 0.2D) * 2.0F;
        motionZ = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F;
        this.rfValue = rfValue;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    public EntityRFOrb(World world) {
        super(world);
        setSize(0.125F, 0.125F);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    @SideOnly(Side.CLIENT)
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
    public void onUpdate() {
        super.onUpdate();
        if (orbCooldown > 0)
            --orbCooldown;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        motionY -= 0.029999999329447746D;
        if (world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ)))
                .getMaterial() == Material.LAVA) {
            motionY = 0.20000000298023224D;
            motionX = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            motionZ = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
            playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
        }

        pushOutOfBlocks(posX, (getEntityBoundingBox().minY + getEntityBoundingBox().maxY) / 2.0D, posZ);
        double d0 = 8.0D;
        if (ticksExisted % 5 == 0 && closestPlayer == null) {
            List<Entity> targets = world.getEntitiesWithinAABB(EntityPlayer.class,
                    new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).expand(d0, d0, d0));
            if (targets.size() > 0) {
                double distance = Double.MAX_VALUE;

                for (Entity t : targets) {
                    double d = ((EntityPlayer) t).getDistanceSq(this);
                    if (d < distance && isCollectorOnHotbar(rfValue, (EntityPlayer) t) >= 0) {
                        distance = d;
                        closestPlayer = (EntityPlayer) t;
                    }
                }
            }
        }

        if (closestPlayer != null) {
            double d1 = (closestPlayer.posX - posX) / d0;
            double d2 = (closestPlayer.posY + closestPlayer.getEyeHeight() - posY) / d0;
            double d3 = (closestPlayer.posZ - posZ) / d0;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0D - d4;
            if (d5 > 0.0D) {
                d5 = d5 * d5;
                motionX += d1 / d4 * d5 * 0.1D;
                motionY += d2 / d4 * d5 * 0.1D;
                motionZ += d3 / d4 * d5 * 0.1D;
            }
        }

        move(MoverType.PLAYER, motionX, motionY, motionZ);
        float f = 0.98F;
        if (onGround) {
            f = 0.58800006F;
            IBlockState state = world.getBlockState(new BlockPos(MathHelper.floor(posX),
                    MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(posZ)));
            Block i = state.getBlock();
            if (!i.isAir(state, world, new BlockPos(MathHelper.floor(posX),
                    MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(posZ))))
                f = i.slipperiness * 0.98F;
        }

        motionX *= f;
        motionY *= 0.9800000190734863D;
        motionZ *= f;
        if (onGround)
            motionY *= -0.8999999761581421D;

        ++orbAge;
        if (orbAge >= orbMaxAge)
            setDead();
    }

    @Override
    public boolean handleWaterMovement() {
        return world.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
    }

    @Override
    protected void dealFireDamage(int amount) {
        attackEntityFrom(DamageSource.IN_FIRE, amount);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source))
            return false;
        markVelocityChanged();
        orbHealth -= (int) amount;
        if (orbHealth <= 0)
            setDead();
        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (byte) orbHealth);
        compound.setShort("Age", (short) orbAge);
        compound.setShort("Value", (short) rfValue);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        orbHealth = compound.getShort("Health") & 0xFF;
        orbAge = compound.getShort("Age");
        rfValue = compound.getShort("Value");
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!world.isRemote) {
            final int slot = isCollectorOnHotbar(rfValue, player);
            if (orbCooldown == 0 && player.xpCooldown == 0 && slot >= 0) {
                final ItemRFCollector wand = (ItemRFCollector) player.inventory.mainInventory.get(slot).getItem();
                wand.addRF(player.inventory.mainInventory.get(slot), rfValue, false);
                player.xpCooldown = 2;
                playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f,
                        0.5f * ((rand.nextFloat() - rand.nextFloat()) * 0.7f + 1.8f));
                setDead();
            }
        }
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    public static int isCollectorOnHotbar(int amount, EntityPlayer player) {
        for (int i = 0; i < 9; i++) {
            ItemStack is = player.inventory.mainInventory.get(i);
            if (BTUtils.isOk(is) && is.getItem() instanceof ItemRFCollector)
                if (((ItemRFCollector) is.getItem()).addRF(is, amount, true) < amount)
                    return i;
        }
        return -1;
    }
}