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
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockFireworks extends Block implements EntityBlock {

	public BlockFireworks(Material materialIn, String s) {
		super(null);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}

//	@Override
	public BlockEntity createNewTileEntity(Level LevelIn, int meta) {
		return new TileEntityFireworks(null, null, null);
	}
	
//	@Override
	public boolean onBlockActivated(Level Level, BlockPos pos, BlockState state, Player player, InteractionHand hand,
									Direction facing, float hitX, float hitY, float hitZ) {
		if(Level.isClientSide)
			return true;

		TileEntityFireworks te = (TileEntityFireworks)Level.getBlockEntity(pos);

		if(player.getItemInHand(hand) != null && !player.getItemInHand(hand).isEmpty()) {

			if(player.getItemInHand(hand).getItem() == Items.GUNPOWDER) {
				te.charges += player.getItemInHand(hand).getCount() * 3;
//				te.markDirty();
				player.getItemInHand(hand).setCount(0);
				return true;
			}

			if(player.getItemInHand(hand).getItem() == ModItems.sulfur) {
				te.charges += player.getItemInHand(hand).getCount();
//				te.markDirty();
				player.getItemInHand(hand).setCount(0);
				return true;
			}

			if(player.getItemInHand(hand).getItem() instanceof DyeItem) {
				te.color = DyeItem.getId(Item.byId(player.getItemInHand(hand).getDamageValue()));
				te.setChanged();
				player.getItemInHand(hand).shrink(1);
				return true;
			}

			if(player.getItemInHand(hand).getItem() == Items.NAME_TAG) {
				te.message = String.valueOf(player.getItemInHand(hand).getDisplayName());
				te.setChanged();
				player.getItemInHand(hand).shrink(1);
				return true;
			}
		}

//		player.displayClientMessage(new TranslatableComponent(this.getUnlocalizedName() + ".name").setStyle(new Style()
//				.setColor(TextFormatting.GOLD)));
//		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".charges", te.charges)
//				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
//		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".color", Integer.toHexString(te.color))
//				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
//		player.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".message", te.message).setStyle(new Style()
//				.setColor(TextFormatting.YELLOW)));

		return true;
	}

//	@Override
	public RenderShape getRenderType(BlockState state) {
		return RenderShape.MODEL;
	}

//	@Override
	public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
		tooltip.add(I18nUtil.resolveKey("desc.fireworks.1"));
		tooltip.add(I18nUtil.resolveKey("desc.fireworks.2"));
		tooltip.add(" "+I18nUtil.resolveKey("desc.fireworks.3"));
		tooltip.add(" "+I18nUtil.resolveKey("desc.fireworks.4"));
		tooltip.add(" "+I18nUtil.resolveKey("desc.fireworks.5"));

	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return null;
	}
}
