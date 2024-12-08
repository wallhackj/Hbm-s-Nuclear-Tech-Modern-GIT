package com.hbm.blocks.bomb;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class CheaterVirusSeed extends Block {

	public CheaterVirusSeed(Material materialIn, String s) {
		super(materialIn);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		this.setTickRandomly(true);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos1, BlockState state) {
		super.breakBlock(world, pos1, state);
		if(!GeneralConfig.enableVirus)
			return;
		int x = pos1.getX();
		int y = pos1.getY();
		int z = pos1.getZ();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    	if((world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			!world.isRemote) {
			world.setBlockState(pos.setPos(x, y, z), Blocks.AIR.getDefaultState());
    	} else {
    		world.setBlockState(pos.setPos(x, y, z), ModBlocks.cheater_virus.getDefaultState());
    	}
	}
	
	@Override
	public void updateTick(World world, BlockPos pos1, IBlockState state, Random rand) {
		if(!GeneralConfig.enableVirus)
			return;
		int x = pos1.getX();
		int y = pos1.getY();
		int z = pos1.getZ();
		MutableBlockPos pos = new BlockPos.MutableBlockPos();
		if((world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			!world.isRemote) {
			world.setBlockState(pos.setPos(x, y, z), Blocks.AIR.getDefaultState());
    	} else {
    		world.setBlockState(pos.setPos(x, y, z), ModBlocks.cheater_virus.getDefaultState());
    	}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos1, Block blockIn, BlockPos fromPos) {
		int x = pos1.getX();
		int y = pos1.getY();
		int z = pos1.getZ();
		MutableBlockPos pos = new BlockPos.MutableBlockPos();
		if((world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			(world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.setPos(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus_seed) && 
    			!world.isRemote) {
			world.setBlockState(pos.setPos(x, y, z), Blocks.AIR.getDefaultState());
    	}
	}

}
