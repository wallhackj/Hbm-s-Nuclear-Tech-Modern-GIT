package com.hbm.items.tool;

import java.util.List;
import com.hbm.items.ModItems;
import com.hbm.util.I18nUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;


public class ItemKeyPin extends Item {

	public ItemKeyPin(String s) {
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		
		ModItems.ALL_ITEMS.add(this);
	}
	
//	@Override
	public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
		if(getPins(stack) != 0)
			tooltip.add(I18nUtil.resolveKey("desc.keypin1", getPins(stack)));
		else
			tooltip.add(I18nUtil.resolveKey("desc.keypin2"));
		
		if(this == ModItems.key_fake) {

			tooltip.add("");
			tooltip.add(I18nUtil.resolveKey("desc.keypin3"));
		}
	}
	
	public static int getPins(ItemStack stack) {
		if(stack.getTag() == null) {
			stack.setTag(new CompoundTag());
			return 0;
		}
		return stack.getTag().getInt("pins");
	}
	
	public static void setPins(ItemStack stack, int i) {
		if(stack.getTag() == null) {
			stack.setTag(new CompoundTag());
		}
		stack.getTag().putInt("pins", i);
	}
	
	public boolean canTransfer() {
		return this != ModItems.key_fake;
	}
}
