package com.hbm.hazard.type;

import java.util.List;

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

public class HazardTypeBlinding extends HazardTypeBase {
    @Override
    public void onUpdate(LivingEntity target, float level, ItemStack stack) {

        if (!ArmorRegistry.hasProtection(target, EquipmentSlot.HEAD, HazardClass.LIGHT)) {
            target.addEffect(new PotionEffect(MobEffects.BLINDNESS, (int) level, 0));
        }
    }

    @Override
    public void updateEntity(Item item, float level) {
    }

    @Override
    public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {
        list.add(ChatFormatting.DARK_AQUA + "[" + I18nUtil.resolveKey("trait.blinding") + "]");
    }
}
