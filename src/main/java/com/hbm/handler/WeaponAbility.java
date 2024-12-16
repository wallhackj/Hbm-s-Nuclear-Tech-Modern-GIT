package com.hbm.handler;

import java.util.Arrays;

import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemCell;
import com.hbm.items.tool.IItemAbility;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.WeightedRandomObject;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class WeaponAbility {

    public abstract void onHit(Level world, Player player, Entity victim, IItemAbility tool);

    public abstract String getName();

    @OnlyIn(Dist.CLIENT)
    public abstract String getFullName();

    public static class RadiationAbility extends WeaponAbility {

        float rad;

        public RadiationAbility(float rad) {
            this.rad = rad;
        }

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {
            if (victim instanceof LivingEntity)
                ContaminationUtil.contaminate((LivingEntity) victim, HazardType.RADIATION, ContaminationType.CREATIVE, rad);
        }

        @Override
        public String getName() {
            return "weapon.ability.radiation";
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getFullName() {
            return I18n.get(getName()) + " (" + rad + ")";
        }
    }

    public static class VampireAbility extends WeaponAbility {

        float amount;

        public VampireAbility(float amount) {
            this.amount = amount;
        }

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {

            if (victim instanceof LivingEntity) {

                LivingEntity living = (LivingEntity) victim;

                living.setHealth(living.getHealth() - amount);
                player.heal(amount);
            }
        }

        @Override
        public String getName() {
            return "weapon.ability.vampire";
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getFullName() {
            return I18n.get(getName()) + " (" + amount + ")";
        }
    }

    public static class StunAbility extends WeaponAbility {

        int duration;

        public StunAbility(int duration) {
            this.duration = duration;
        }

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {

            if (victim instanceof LivingEntity) {

                LivingEntity living = (LivingEntity) victim;

                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 20, 4));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration * 20, 4));
            }
        }

        @Override
        public String getName() {
            return "weapon.ability.stun";
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getFullName() {
            return I18n.get(getName()) + " (" + duration + ")";
        }
    }

    public static class PhosphorusAbility extends WeaponAbility {

        int duration;

        public PhosphorusAbility(int duration) {
            this.duration = duration;
        }

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {

            if (victim instanceof LivingEntity) {

                LivingEntity living = (LivingEntity) victim;

                living.addEffect(new MobEffectInstance(HbmPotion.phosphorus, duration * 20, 4));
            }
        }

        @Override
        public String getName() {
            return "weapon.ability.phosphorus";
        }

        @Override
        public String getFullName() {
            return I18n.get(getName()) + " (" + duration + ")";
        }
    }

    public static class ChainsawAbility extends WeaponAbility {

        int divider;

        public ChainsawAbility(int divider) {
            this.divider = divider;
        }

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {

            if (victim instanceof LivingEntity) {

                LivingEntity living = (LivingEntity) victim;

                if (living.getHealth() <= 0.0F) {

                    WeightedRandomObject[] ammo = new WeightedRandomObject[]{
                            new WeightedRandomObject(ModItems.ammo_12gauge, 10),
                            new WeightedRandomObject(ModItems.ammo_12gauge_shrapnel, 5),
                            new WeightedRandomObject(ModItems.ammo_12gauge_du, 3),
                            new WeightedRandomObject(ModItems.ammo_20gauge, 10),
                            new WeightedRandomObject(ModItems.ammo_20gauge_flechette, 5),
                            new WeightedRandomObject(ModItems.ammo_20gauge_slug, 5),
                            new WeightedRandomObject(ModItems.ammo_9mm, 10),
                            new WeightedRandomObject(ModItems.ammo_9mm_ap, 5),
                            new WeightedRandomObject(ModItems.ammo_5mm, 10),
                            new WeightedRandomObject(ModItems.ammo_5mm_du, 3),
                            new WeightedRandomObject(ModItems.ammo_556, 10),
                            new WeightedRandomObject(ModItems.ammo_556_phosphorus, 5),
                            new WeightedRandomObject(ModItems.ammo_556_flechette, 10),
                            new WeightedRandomObject(ModItems.ammo_556_flechette_phosphorus, 5),
                            new WeightedRandomObject(ModItems.ammo_50bmg, 10),
                            new WeightedRandomObject(ModItems.ammo_50bmg_incendiary, 5),
                            new WeightedRandomObject(ModItems.ammo_50bmg_ap, 5),
                            new WeightedRandomObject(ModItems.ammo_grenade, 5),
                            new WeightedRandomObject(ModItems.ammo_grenade_concussion, 3),
                            new WeightedRandomObject(ModItems.ammo_grenade_phosphorus, 3),
                            new WeightedRandomObject(ModItems.ammo_rocket, 5),
                            new WeightedRandomObject(ModItems.ammo_rocket_glare, 5),
                            new WeightedRandomObject(ModItems.ammo_rocket_phosphorus, 5),
                            new WeightedRandomObject(ModItems.ammo_rocket_rpc, 1),
                            new WeightedRandomObject(ModItems.syringe_metal_stimpak, 25),
                    };

                    //safeguard to prevent funnies from bosses with obscene health
                    int count = Math.min((int) Math.ceil(living.getMaxHealth() / divider), 250);

                    for (int i = 0; i < count; i++) {

                        living.dropItem(((WeightedRandomObject) WeightedRandom.getRandomItem(living.getRNG(),
                                Arrays.asList(ammo))).asItem(), 1);
                        world.addFreshEntity(new EntityXPOrb(world, living.getX(), living.getY(), living.getZ(), 1));
                    }

                    if (player instanceof ServerPlayer) {
                        CompoundTag data = new CompoundTag();
                        data.putString("type", "vanillaburst");
                        data.putInt("count", count * 4);
                        data.putDouble("motion", 0.1D);
                        data.putString("mode", "blockdust");
                        data.putInt("block", Block.getId(Blocks.REDSTONE_BLOCK.defaultBlockState()));
//                        PacketDispatcher.wrapper.sendTo(new AuxParticlePacketNT(data, living.getX(),
//                                living.getY() + living.getBbHeight() * 0.5, living.getZ()), (ServerPlayer) player);
                    }

                    world.playSound(null, living.getX(), living.getY() + living.getBbHeight() * 0.5,
                            living.getZ(), HBMSoundHandler.chainsaw, SoundSource.PLAYERS, 0.5F, 1.0F);
                }
            }
        }

        @Override
        public String getName() {
            return "weapon.ability.chainsaw";
        }

        @Override
        public String getFullName() {
            return I18n.get(getName()) + " (1:" + divider + ")";
        }
    }

    public static class BeheaderAbility extends WeaponAbility {

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {

            if (victim instanceof LivingEntity && ((LivingEntity) victim).getHealth() <= 0.0F) {

                LivingEntity living = (LivingEntity) victim;

                if (living instanceof Skeleton) {
                    living.entityDropItem(new ItemStack(Items.SKELETON_SKULL, 1), 0.0F);
                } else if (living instanceof WitherSkeleton) {
                    living.entityDropItem(ItemCell.getFullCell(ModForgeFluids.amat), 0.0F);
                } else if (living instanceof Zombie) {
                    living.entityDropItem(new ItemStack(Items.ROTTEN_FLESH, 1), 0.0F);
                } else if (living instanceof Creeper) {
                    living.entityDropItem(new ItemStack(Items.CREEPER_BANNER_PATTERN, 1), 0.0F);
                } else if (living instanceof Player) {

                    ItemStack head = new ItemStack(Items.SKELETON_SKULL, 1);
                    head.setTag(new CompoundTag());
                    head.getTag().putString("SkullOwner", ((Player) living).getDisplayName().getString());
                    ((Player) living).drop(head, true);
                } else {
                    living.entityDropItem(new ItemStack(Items.ROTTEN_FLESH, 3, 0), 0.0F);
                    living.entityDropItem(new ItemStack(Items.BONE, 2, 0), 0.0F);
                }
            }
        }

        @Override
        public String getName() {
            return "weapon.ability.beheader";
        }

        @Override
        public String getFullName() {
            return I18n.get(getName());
        }
    }

    public static class FireAbility extends WeaponAbility {

        int duration;

        public FireAbility(int duration) {
            this.duration = duration;
        }

        @Override
        public void onHit(Level world, Player player, Entity victim, IItemAbility tool) {
            if (victim instanceof LivingEntity) {
                victim.setSecondsOnFire(duration);
            }
        }

        @Override
        public String getName() {
            return "weapon.ability.fire";
        }

        @Override
        public String getFullName() {
            return I18n.get(getName()) + " (" + duration + ")";
        }
    }

}