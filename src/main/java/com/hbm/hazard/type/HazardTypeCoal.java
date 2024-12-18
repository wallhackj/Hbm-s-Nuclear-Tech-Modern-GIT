package com.hbm.hazard.type;

import java.util.List;

import com.hbm.capability.HbmLivingProps;
import com.hbm.handler.ArmorUtil;
import com.hbm.hazard.HazardModifier;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HazardTypeCoal extends HazardTypeBase {

    @Override
    public void onUpdate(LivingEntity target, float level, ItemStack stack) {

        if (!ArmorRegistry.hasProtection(target, EquipmentSlot.HEAD, HazardClass.PARTICLE_COARSE))
            HbmLivingProps.incrementBlackLung(target, (int) Math.min(level, 10));
        else
            ArmorUtil.damageGasMaskFilter(target, (int) level);
    }

    @Override
    public void updateEntity(Item item, float level) {
    }

    @Override
    public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {
        list.add(ChatFormatting.DARK_GRAY + "[" + I18nUtil.resolveKey("trait.coal") + "]");
    }

}
