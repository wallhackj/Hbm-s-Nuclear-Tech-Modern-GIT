package com.hbm.blocks;

import java.util.List;

import com.hbm.util.I18nUtil;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static net.minecraft.world.item.Tiers.GOLD;

public class BlockBase extends Block {

	public BlockBase(Material m, String s){
		super(null);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.setHarvestLevel("pickaxe", 0);
//		this.setCreativeTab(MainRegistry.controlTab);
//		ModBlocks.ALL_BLOCKS.add(this);
	}

	public BlockBase(Material m, SoundType sound, String s){
		super(null);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.setSoundType(sound);
//		this.setHarvestLevel("pickaxe", 0);
//		this.setCreativeTab(MainRegistry.controlTab);
//		ModBlocks.ALL_BLOCKS.add(this);
	}

//	@Override
	public void addInformation(ItemStack stack, Level player, List<String> list, TooltipFlag advanced) {
		if (stack.getItem() instanceof BlockItem) {
			BlockItem blockItem = (BlockItem) stack.getItem();
			if (blockItem.getBlock().equals(ModBlocks.meteor_battery)) {
				list.add(I18nUtil.resolveKey("desc.teslacoils"));
			}
		}

//		float hardness = this.getExplosionResistance(null);
		var hardness = this.explosionResistance;
		if(hardness > 50){
			list.add(GOLD + I18nUtil.resolveKey("trait.blastres", hardness));
//			list.add(TextFormatting.GOLD + I18nUtil.resolveKey("trait.blastres", hardness));
		}
	}

	public Properties setSoundType(SoundType sound){
		return BlockBehaviour.Properties.of().sound(sound);
	}
}