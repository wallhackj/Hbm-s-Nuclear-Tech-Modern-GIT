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

public class HazardTypeAsbestos extends HazardTypeBase {

	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		if(ArmorRegistry.hasProtection(target, EquipmentSlot.HEAD, HazardClass.PARTICLE_FINE))
			ArmorUtil.damageGasMaskFilter(target, (int) level);
		else
			HbmLivingProps.incrementAsbestos(target, (int) Math.min(level, 10));
	}

	@Override
	public void updateEntity(Item item, float level) { }

	@Override
	public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		list.add(ChatFormatting.WHITE + "[" + I18nUtil.resolveKey("trait.asbestos") + "]");
	}
}
