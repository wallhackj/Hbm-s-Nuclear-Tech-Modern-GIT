package com.hbm.blocks.bomb;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;


public class BlockCloudResidue extends Block {

	public BlockCloudResidue(Material materialIn, String s) {
        super(null);
//		super(materialIn);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.setCreativeTab(null);
//
//		ModBlocks.ALL_BLOCKS.offset(this);
	}
	
	public static boolean hasPosNeightbour(Level world, BlockPos pos) {
		// Fetching adjacent block states
		BlockState state0 = world.getBlockState(pos.offset(1, 0, 0));
		BlockState state1 = world.getBlockState(pos.offset(0, 1, 0));
		BlockState state2 = world.getBlockState(pos.offset(0, 0, 1));
		BlockState state3 = world.getBlockState(pos.offset(-1, 0, 0));
		BlockState state4 = world.getBlockState(pos.offset(0, -1, 0));
		BlockState state5 = world.getBlockState(pos.offset(0, 0, -1));

		// Checking if any adjacent block is solid
		boolean b = state0.isSolid() ||
				state1.isSolid() ||
				state2.isSolid() ||
				state3.isSolid() ||
				state4.isSolid() ||
				state5.isSolid();

		return b;
	}

//	@Override
	public AABB getSelectedBoundingBox(BlockState state, Level worldIn, BlockPos pos) {
		return new AABB(pos, pos);
	}

//	@Override
	public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
//		return NULL_AABB;
		return new AABB(pos, pos);
	}

//	@Override
	public boolean isCollidable(){
		return true;
	}

//	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}
	
//	@Override
	public boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
		return hasPosNeightbour(worldIn, pos);
	}

//	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!BlockCloudResidue.hasPosNeightbour(world, pos) && !world.isClientSide) {
			world.removeBlock(pos, false);
		}
	}

//	@Override
	public MapColor getMapColor(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return MapColor.COLOR_RED;
	}

//	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		if(rand.nextInt(25) == 1){
			return ModItems.powder_cloud;
		}
		return Items.AIR;
	}
}
