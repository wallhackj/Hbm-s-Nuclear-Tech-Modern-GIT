package com.hbm.hazard.type;

import java.util.List;

import com.hbm.hazard.HazardModifier;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HazardTypeHydroactive extends HazardTypeBase {

    @Override
    public void onUpdate(LivingEntity target, float level, ItemStack stack) {
        if (target.isInWaterOrRain()) {
            stack.setCount(0);
            target.level().explode(null, target.getX(), target.getY() + target.getEyeHeight() -
                    target.getYOffset(), target.getZ(), level, false, true);
        }
    }

    @Override
    public void updateEntity(Item item, float level) {
        if (item.getBlockState(new BlockPos((int) Math.floor(item.posX), (int) Math.floor(item.posY), (int) Math.floor(item.posZ))).getMaterial() == Material.WATER) {
            item.setDead();
            item.world.newExplosion(null, item.posX, item.posY + item.height * 0.5, item.posZ, level, false, true);
        }
    }

    @Override
    public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {
        list.add(ChatFormatting.RED + "[" + I18nUtil.resolveKey("trait.hydro") + "]");
    }
}