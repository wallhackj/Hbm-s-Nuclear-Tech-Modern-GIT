package com.hbm.blocks;

import com.hbm.blocks.machine.pile.BlockGraphiteDrilledBase;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class BlockGraphiteDrilled extends BlockGraphiteDrilledBase {
	
	public BlockGraphiteDrilled(String s){
		super(s);
	}

//	@Override
	public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ){
		if(!player.getItemInHand(hand).isEmpty()) {
			
			Direction.Axis axis = state.getValue(AXIS);

			if(facing.getAxis() == axis) {
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				if(checkInteraction(world, x, y, z, axis, player, hand, ModItems.pile_rod_uranium, ModBlocks.block_graphite_fuel)) return true;
				if(checkInteraction(world, x, y, z, axis, player, hand, ModItems.pile_rod_plutonium, ModBlocks.block_graphite_plutonium)) return true;
				if(checkInteraction(world, x, y, z, axis, player, hand, ModItems.pile_rod_source, ModBlocks.block_graphite_source)) return true;
				if(checkInteraction(world, x, y, z, axis, player, hand, ModItems.pile_rod_boron, ModBlocks.block_graphite_rod)) return true;
				if(checkInteraction(world, x, y, z, null, player, hand, ModItems.ingot_graphite, ModBlocks.block_graphite)) return true;
			}
		}
		
		return false;
	}
	
	private boolean checkInteraction(Level world, int x, int y, int z, Direction.Axis meta, Player player, InteractionHand hand, Item item, Block block) {
		
		if(player.getItemInHand(hand).getItem() == item) {
			player.getItemInHand(hand).shrink(1);
			if(block instanceof BlockGraphiteDrilledBase){
				world.setBlockAndUpdate(new BlockPos(x, y, z), block.getDefaultState().withProperty(AXIS, meta), 3);
			} else {
				world.setBlockAndUpdate(new BlockPos(x, y, z), block.getDefaultState(), 3);
			}
			

			world.playSound(null, x + 0.5, y + 1.5, z + 0.5, HBMSoundHandler.upgradePlug, SoundCategory.BLOCKS, 1.0F, 1.0F);
			
			return true;
		}
		
		return false;
	}
}