package com.hbm.potion;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockTaint;
import com.hbm.capability.HbmLivingCapability;
import com.hbm.config.CompatibilityConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.entity.mob.EntityTaintedCreeper;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.ModDamageSource;
import com.hbm.lib.RefStrings;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.Objects;


public class HbmPotion extends MobEffect {

    public static HbmPotion taint;
    public static HbmPotion radiation;
    public static HbmPotion bang;
    public static HbmPotion mutation;
    public static HbmPotion radx;
    public static HbmPotion lead;
    public static HbmPotion radaway;
    public static HbmPotion telekinesis;
    public static HbmPotion phosphorus;
    public static HbmPotion stability;
    public static HbmPotion potionsickness;

    public HbmPotion(boolean isBad, int color, String name, int x, int y) {
        super(MobEffectCategory.HARMFUL, color);
//        super(isBad, color);
//        this.setPotionName(name);
//        this.setRegistryName(RefStrings.MODID, name);
//        this.setIconIndex(x, y);
    }

    public static void init() {
        taint = registerPotion(true, 8388736, "potion.hbm_taint", 0, 0);
        radiation = registerPotion(true, 8700200, "potion.hbm_radiation", 1, 0);
        bang = registerPotion(true, 1118481, "potion.hbm_bang", 3, 0);
        mutation = registerPotion(false, 0xFF8132, "potion.hbm_mutation", 2, 0);
        radx = registerPotion(false, 0x225900, "potion.hbm_radx", 5, 0);
        lead = registerPotion(true, 0x767682, "potion.hbm_lead", 6, 0);
        radaway = registerPotion(false, 0xFFE400, "potion.hbm_radaway", 7, 0);
        telekinesis = registerPotion(true, 0x00F3FF, "potion.hbm_telekinesis", 0, 1);
        phosphorus = registerPotion(true, 0xFF3A00, "potion.hbm_phosphorus", 1, 1);
        stability = registerPotion(false, 0xD0D0D0, "potion.hbm_stability", 2, 1);
        potionsickness = registerPotion(false, 0xFF8080, "potion.hbm_potionsickness", 3, 1);
    }

    public static HbmPotion registerPotion(boolean isBad, int color, String name, int x, int y) {
        HbmPotion effect = new HbmPotion(isBad, color, name, x, y);
        ForgeRegistries.MOB_EFFECTS.register(name, effect);
        return effect;
    }

//    @Override
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        ResourceLocation loc = new ResourceLocation(RefStrings.MODID, "textures/gui/potions.png");
        Minecraft.getInstance()
                .getTextureManager()
                .bindForSetup(loc);
//        return super.getEffectRendererInternal();
        return 1;
    }

    public void performEffect(LivingEntity entity, int level) {

        if (this == taint) {
            if (!(entity instanceof EntityTaintedCreeper) && entity.level().random.nextInt(80) == 0)
                entity.hurt(ModDamageSource.taint, (level + 1));

            if (GeneralConfig.enableHardcoreTaint && !entity.level().isClientSide &&
                    CompatibilityConfig.isWarDim(entity.level())) {

                int x = (int) (entity.getX() - 1);
                int y = (int) entity.getY();
                int z = (int) (entity.getZ());
                BlockPos pos = new BlockPos(x, y, z);

                if (entity.level().getBlockState(pos).canBeReplaced() &&
                        BlockTaint.hasPosNeightbour(entity.level(), pos)) {

                    entity.level().setBlock(pos, ModBlocks.taint.defaultBlockState()
                            .setValue(BlockTaint.TEXTURE, 14), 2);
                }
            }
        }
        if (this == radiation) {
            ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, (float) (level + 1F) * 0.05F);
        }
        if (this == radaway) {
//            if (entity.hasCapability(HbmLivingCapability.EntityHbmPropsProvider.ENT_HBM_PROPS_CAP, null))
//                entity.getCapability(HbmLivingCapability.EntityHbmPropsProvider.ENT_HBM_PROPS_CAP, null).decreaseRads((level + 1) * 0.05F);
        }
        if (this == bang) {
            if (CompatibilityConfig.isWarDim(entity.level())) {
                entity.hurt(ModDamageSource.bang, 10000 * (level + 1));

                if (!(entity instanceof Player)) {
                    entity.die(ModDamageSource.bang);
                    entity.setHealth(0);
                }
            }
            entity.level().playSound(null, entity.getOnPos(), HBMSoundHandler.laserBang, SoundSource.AMBIENT,
                    100.0F, 1.0F);
            ExplosionLarge.spawnParticles(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 10);
        }
        if (this == lead) {

            entity.hurt(ModDamageSource.lead, (level + 1));
        }
        if (this == telekinesis) {

            int remaining = Objects.requireNonNull(entity.getEffect(this)).getDuration();

            if (remaining > 1) {
                Vec3 motion = entity.getDeltaMovement();

                double newMotionX = motion.x + (entity.getRandom().nextFloat() - 0.5) * (level + 1) * 0.5;
                double newMotionY = motion.y + (entity.getRandom().nextFloat() - 0.5) * (level + 1) * 0.5;
                double newMotionZ = motion.z + (entity.getRandom().nextFloat() - 0.5) * (level + 1) * 0.5;

                entity.setDeltaMovement(newMotionX, newMotionY, newMotionZ);
            }
        }
        if (this == phosphorus && !entity.level().isClientSide && CompatibilityConfig.isWarDim(entity.level())) {

            entity.setSecondsOnFire(level + 1);
        }

        if (this == potionsickness && !entity.level().isClientSide) {

            if (entity.level().random.nextInt(128) == 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 8 * 20, 0));            }
        }
    }

    public boolean isReady(int par1, int par2) {

        if (this == taint || this == potionsickness) {

            return par1 % 2 == 0;
        }
        if (this == radiation || this == radaway || this == telekinesis || this == phosphorus) {

            return true;
        }
        if (this == bang) {

            return par1 <= 10;
        }
        if (this == lead) {

            int k = 60;
            return k > 0 ? par1 % k == 0 : true;
        }

        return false;
    }
}
