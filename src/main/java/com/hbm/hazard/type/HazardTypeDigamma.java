package com.hbm.hazard.type;

import java.util.List;

import com.hbm.hazard.HazardModifier;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class HazardTypeDigamma extends HazardTypeBase {

	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		ContaminationUtil.applyDigammaData(target, level / 20F);
	}

	@Override
	public void updateEntity(Item item, float level) { }

	@Override
	public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		
		level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);
		
		float d = (float)(Math.floor(level * 10000F)) / 10F;
		list.add(ChatFormatting.RED + "[" + I18nUtil.resolveKey("trait.digamma") + "]");
		list.add(ChatFormatting.DARK_RED + "" + d + I18nUtil.resolveKey("desc.digammaed"));
		
		if(stack.getCount() > 1) {
			list.add(ChatFormatting.DARK_RED + I18nUtil.resolveKey("desc.stack")+" "
					+ (Math.floor(level * 10000F * stack.getCount()) / 10F) + I18nUtil.resolveKey("desc.digammaed"));
		}
	}

}