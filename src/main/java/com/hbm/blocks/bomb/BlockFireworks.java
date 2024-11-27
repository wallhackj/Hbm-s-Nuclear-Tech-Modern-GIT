package com.hbm.blocks.bomb;

import java.util.List;

import com.hbm.util.I18nUtil;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityFireworks;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class BlockFireworks extends BlockContainer {

	public BlockFireworks(Material materialIn, String s) {
		super(materialIn);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(Level LevelIn, int meta) {
		return new TileEntityFireworks();
	}
	
	@Override
	public boolean onBlockActivated(Level Level, BlockPos pos, BlockState state, Player player, InteractionHand hand,
									Direction facing, float hitX, float hitY, float hitZ) {
		if(Level.isClientSide)
			return true;

		TileEntityFireworks te = (TileEntityFireworks)Level.getTileEntity(pos);

		if(player.getHeldItem(hand) != null && !player.getHeldItem(hand).isEmpty()) {

			if(player.getHeldItem(hand).getItem() == Items.GUNPOWDER) {
				te.charges += player.getHeldItem(hand).getCount() * 3;
				te.markDirty();
				player.getHeldItem(hand).setCount(0);
				return true;
			}

			if(player.getHeldItem(hand).getItem() == ModItems.sulfur) {
				te.charges += player.getHeldItem(hand).getCount();
				te.markDirty();
				player.getHeldItem(hand).setCount(0);
				return true;
			}

			if(player.getHeldItem(hand).getItem() instanceof ItemDye) {
				te.color = ItemDye.DYE_COLORS[player.getHeldItem(hand).getItemDamage()];
				te.markDirty();
				player.getHeldItem(hand).shrink(1);
				return true;
			}

			if(player.getHeldItem(hand).getItem() == Items.NAME_TAG) {
				te.message = player.getHeldItem(hand).getDisplayName();
				te.markDirty();
				player.getHeldItem(hand).shrink(1);
				return true;
			}
		}

		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".name").setStyle(new Style().setColor(TextFormatting.GOLD)));
		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".charges", te.charges).setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".color", Integer.toHexString(te.color)).setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".message", te.message).setStyle(new Style().setColor(TextFormatting.YELLOW)));

		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(BlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void addInformation(ItemStack stack, Level player, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18nUtil.resolveKey("desc.fireworks.1"));
		tooltip.add(I18nUtil.resolveKey("desc.fireworks.2"));
		tooltip.add(" "+I18nUtil.resolveKey("desc.fireworks.3"));
		tooltip.add(" "+I18nUtil.resolveKey("desc.fireworks.4"));
		tooltip.add(" "+I18nUtil.resolveKey("desc.fireworks.5"));

	}
}
