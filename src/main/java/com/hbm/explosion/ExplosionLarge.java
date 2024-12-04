package com.hbm.explosion;

import com.hbm.config.CompatibilityConfig;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.particle.EntityGasFlameFX;
import com.hbm.entity.projectile.EntityOilSpill;
import com.hbm.entity.projectile.EntityRubble;
import com.hbm.entity.projectile.EntityShrapnel;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.util.ContaminationUtil;
import glmath.glm.vec._3.d.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;


public class ExplosionLarge {

    static Random rand = new Random();

    public static void spawnParticlesRadial(Level world, double x, double y, double z, int count) {

        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "radial");
        data.putInt("count", count);
//		PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, x, y, z),
//				new TargetPoint(world.provider.getDimension(), x, y, z, 250));
    }

    public static void spawnParticles(Level world, double x, double y, double z, int count) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "cloud");
        data.putInt("count", count);
//		PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, x, y, z),
//				new TargetPoint(world.provider.getDimension(), x, y, z, 250));
    }

    public static void spawnBurst(Level world, double x, double y, double z, int count, double strength) {

        Vec3d vec = new Vec3d(strength, 0, 0);
        vec = vec.rotateYaw(rand.nextInt(360));

        for (int i = 0; i < count; i++) {
            EntityGasFlameFX fx = new EntityGasFlameFX(world, x, y, z, 0.0, 0.0, 0.0);
            fx.motionY = 0;
            fx.motionX = vec.x;
            fx.motionZ = vec.z;
            world.spawnEntity(fx);

            vec = vec.rotateYaw(360 / count);
        }
    }

    public static void spawnShock(Level world, double x, double y, double z, int count, double strength) {

        CompoundTag data = new CompoundTag();
        data.putString("type", "smoke");
        data.putString("mode", "shock");
        data.putInt("count", count);
        data.putDouble("strength", strength);
//		PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, x, y + 0.5, z),
//				new TargetPoint(world.provider.getDimension(), x, y, z, 250));
    }

    public static void spawnRubble(Level world, double x, double y, double z, int count) {

        for (int i = 0; i < count; i++) {
            EntityRubble rubble = new EntityRubble(world);
            rubble.posX = x;
            rubble.posY = y;
            rubble.posZ = z;
            rubble.motionY = 0.75 * (1 + ((count + rand.nextInt(count * 5))) / 25);
            rubble.motionX = rand.nextGaussian() * 0.75 * (1 + (count / 50));
            rubble.motionZ = rand.nextGaussian() * 0.75 * (1 + (count / 50));
            rubble.setMetaBasedOnBlock(Blocks.STONE, 0);
            world.addFreshEntity(rubble);
        }
    }

    public static void spawnShrapnels(Level world, double x, double y, double z, int count) {

        for (int i = 0; i < count; i++) {
            EntityShrapnel shrapnel = new EntityShrapnel(world);
            shrapnel.posX = x;
            shrapnel.posY = y;
            shrapnel.posZ = z;
            shrapnel.motionY = ((rand.nextFloat() * 0.5) + 0.5) * (1 + (count / (15 + rand.nextInt(21)))) + (rand.nextFloat() / 50 * count);
            shrapnel.motionX = rand.nextGaussian() * 1 * (1 + (count / 50));
            shrapnel.motionZ = rand.nextGaussian() * 1 * (1 + (count / 50));
            shrapnel.setTrail(rand.nextInt(3) == 0);
            world.spawnEntity(shrapnel);
        }
    }

    @SuppressWarnings("deprecation")
    public static void jolt(Level world, double posX, double posY, double posZ, double strength, int count, double vel) {
        if (!CompatibilityConfig.isWarDim(world)) {
            return;
        }
        for (int j = 0; j < count; j++) {

            double phi = rand.nextDouble() * (Math.PI * 2);
            double costheta = rand.nextDouble() * 2 - 1;
            double theta = Math.acos(costheta);
            double x = Math.sin(theta) * Math.cos(phi);
            double y = Math.sin(theta) * Math.sin(phi);
            double z = Math.cos(theta);

            Vec3d vec = new Vec3d(x, y, z);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int i = 0; i < strength; i++) {
                double x0 = posX + (vec.x * i);
                double y0 = posY + (vec.y * i);
                double z0 = posZ + (vec.z * i);
                pos.setPos((int) x0, (int) y0, (int) z0);

                if (!world.isClientSide) {
                    BlockState blockstate = world.getBlockState(pos);
                    Block block = blockstate.getBlock();
                    if (blockstate.getMaterial().isLiquid()) {
                        world.removeBlock(pos, false);
                    }

                    if (block != Blocks.AIR) {

                        if (block.getExplosionResistance(null) > 70)
                            continue;

                        EntityRubble rubble = new EntityRubble(world);
                        rubble.posX = x0 + 0.5F;
                        rubble.posY = y0 + 0.5F;
                        rubble.posZ = z0 + 0.5F;
                        rubble.setMetaBasedOnBlock(block, block.getMetaFromState(blockstate));

                        Vec3d vec4 = new Vec3d(posX - rubble.posX, posY - rubble.posY, posZ - rubble.posZ);
                        vec4.normalize();

                        rubble.motionX = vec4.x * vel;
                        rubble.motionY = vec4.y * vel;
                        rubble.motionZ = vec4.z * vel;

                        world.addFreshEntity(rubble);

                        world.removeBlock(pos, false);
                        break;
                    }
                }
            }
        }
    }

    public static void spawnTracers(Level world, double x, double y, double z, int count) {

        for (int i = 0; i < count; i++) {
            EntityShrapnel shrapnel = new EntityShrapnel(world);
            shrapnel.posX = x;
            shrapnel.posY = y;
            shrapnel.posZ = z;
            shrapnel.motionY = ((rand.nextFloat() * 0.5) + 0.5) * (1 + (count / (15 + rand.nextInt(21)))) + (rand.nextFloat() / 50 * count) * 0.25F;
            shrapnel.motionX = rand.nextGaussian() * 1 * (1 + (count / 50)) * 0.25F;
            shrapnel.motionZ = rand.nextGaussian() * 1 * (1 + (count / 50)) * 0.25F;
            shrapnel.setTrail(true);
            world.addFreshEntity(shrapnel);
        }
    }

    public static void spawnShrapnelShower(Level world, double x, double y, double z, double motionX, double motionY, double motionZ, int count, double deviation) {

        for (int i = 0; i < count; i++) {
            EntityShrapnel shrapnel = new EntityShrapnel(world);
            shrapnel.posX = x;
            shrapnel.posY = y;
            shrapnel.posZ = z;
            shrapnel.motionX = motionX + rand.nextGaussian() * deviation;
            shrapnel.motionY = motionY + rand.nextGaussian() * deviation;
            shrapnel.motionZ = motionZ + rand.nextGaussian() * deviation;
            shrapnel.setTrail(rand.nextInt(3) == 0);
            world.addFreshEntity(shrapnel);
        }
    }

    public static void spawnMissileDebris(Level world, double x, double y, double z, double motionX, double motionY, double motionZ, double deviation, List<ItemStack> debris, ItemStack rareDrop) {

        if (debris != null) {
            for (int i = 0; i < debris.size(); i++) {
                if (debris.get(i) != null) {
                    int k = rand.nextInt(debris.get(i).getCount() + 1);
                    for (int j = 0; j < k; j++) {
                        ItemEntity item = new ItemEntity(world, x, y, z, new ItemStack(debris.get(i).getItem()));
                        item.motionX = (motionX + rand.nextGaussian() * deviation) * 0.85;
                        item.motionY = (motionY + rand.nextGaussian() * deviation) * 0.85;
                        item.motionZ = (motionZ + rand.nextGaussian() * deviation) * 0.85;
                        item.posX = item.posX + item.motionX * 2;
                        item.posY = item.posY + item.motionY * 2;
                        item.posZ = item.posZ + item.motionZ * 2;

                        world.addFreshEntity(item);
                    }
                }
            }
        }
    }

    public static void explode(Level world, double x, double y, double z, float strength, boolean cloud, boolean rubble, boolean shrapnel) {
        if (CompatibilityConfig.isWarDim(world)) {
            world.addFreshEntity(EntityNukeExplosionMK5.statFacNoRad(world, (int) strength, x, y, z));

            ContaminationUtil.radiate(world, x, y, z, strength, 0, 0, 0, strength * 15F);
        }
        if (cloud)
            spawnParticles(world, x, y + 2, z, cloudFunction((int) strength));
        if (rubble)
            spawnRubble(world, x, y + 2, z, rubbleFunction((int) strength));
        if (shrapnel)
            spawnShrapnels(world, x, y + 2, z, shrapnelFunction((int) strength));
    }

    public static int cloudFunction(int i) {
        // return (int)(345 * (1 - Math.pow(Math.E, -i/15)) + 15);
        return (int) (545 * (1 - Math.pow(Math.E, -i / 15)) + 15);
    }

    public static int rubbleFunction(int i) {
        return i / 10;
    }

    public static int shrapnelFunction(int i) {
        return i / 3;
    }

    public static void explodeFire(Level world, double x, double y, double z, float strength, boolean cloud, boolean rubble, boolean shrapnel) {
        if (CompatibilityConfig.isWarDim(world)) {
            world.addFreshEntity(EntityNukeExplosionMK5.statFacNoRadFire(world, (int) strength, x, y, z));

            ContaminationUtil.radiate(world, x, y, z, strength, 0, 0, strength * 20F, strength * 5F);
        }
        if (cloud)
            spawnParticles(world, x, y + 2, z, cloudFunction((int) strength));
        if (rubble)
            spawnRubble(world, x, y + 2, z, rubbleFunction((int) strength));
        if (shrapnel)
            spawnShrapnels(world, x, y + 2, z, shrapnelFunction((int) strength));
    }

    public static void spawnOilSpills(Level world, double x, double y, double z, int count) {

        for (int i = 0; i < count; i++) {
            EntityOilSpill shrapnel = new EntityOilSpill(world);
            shrapnel.posX = x;
            shrapnel.posY = y;
            shrapnel.posZ = z;
            shrapnel.motionY = ((rand.nextFloat() * 0.5) + 0.5) * (1 + (count / (15 + rand.nextInt(21)))) + (rand.nextFloat() / 50 * count) * 0.25F;
            shrapnel.motionX = rand.nextGaussian() * 1 * (1 + (count / 50)) * 0.15F;
            shrapnel.motionZ = rand.nextGaussian() * 1 * (1 + (count / 50)) * 0.15F;
            world.spawnEntity(shrapnel);
        }
    }

    public static void buster(Level world, double x, double y, double z, Vec3 vector, float strength, float depth) {

        vector = vector.normalize();
        if (CompatibilityConfig.isWarDim(world)) {
            for (int i = 0; i <= depth; i += 3) {

                ContaminationUtil.radiate(world, x + vector.xCoord * i, y + vector.yCoord * i, z + vector.zCoord * i, strength, 0, 0, 0, strength * 10F);
                world.addFreshEntity(EntityNukeExplosionMK5.statFacNoRad(world, (int) strength, x + vector.xCoord * i, y + vector.yCoord * i, z + vector.zCoord * i));
            }
        }
        spawnParticles(world, x, y + 2, z, cloudFunction((int) strength));
        spawnRubble(world, x, y + 2, z, rubbleFunction((int) strength));
        spawnShrapnels(world, x, y + 2, z, shrapnelFunction((int) strength));
    }
}
