package com.hbm.hazard.type;

import java.util.List;

import com.hbm.config.GeneralConfig;
import com.hbm.hazard.HazardModifier;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class HazardTypeHot extends HazardTypeBase {

    @Override
    public void onUpdate(LivingEntity target, float level, ItemStack stack) {

        boolean reacher = false;

        if (target instanceof Player && !GeneralConfig.enable528)
            reacher = Library.checkForHeld((Player) target, ModItems.reacher);

        if (!reacher && !target.isInWaterOrRain())
            target.setSecondsOnFire((int) Math.ceil(level));
    }

    @Override
    public void updateEntity(Item item, float level) {
    }

    @Override
    public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {
        list.add(ChatFormatting.GOLD + "[" + I18nUtil.resolveKey("trait.hot") + "]");
    }

}
