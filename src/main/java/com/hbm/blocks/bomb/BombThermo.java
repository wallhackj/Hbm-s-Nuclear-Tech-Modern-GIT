package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionThermo;
import com.hbm.interfaces.IBomb;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class BombThermo extends Block implements IBomb {

	public BombThermo(Material materialIn, String s) {
//		super(materialIn);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
		super(null);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
//	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!worldIn.isClientSide && worldIn.hasNeighborSignal(pos))
        {
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        	if(this == ModBlocks.therm_endo)
        	{
        		ExplosionThermo.freeze(worldIn, pos.getX(), pos.getY(), pos.getZ(), 15);
        		ExplosionThermo.freezer(worldIn, pos.getX(), pos.getY(), pos.getZ(), 20);
        	}

        	if(this == ModBlocks.therm_exo)
        	{
        		ExplosionThermo.scorch(worldIn, pos.getX(), pos.getY(), pos.getZ(), 15);
        		ExplosionThermo.setEntitiesOnFire(worldIn, pos.getX(), pos.getY(), pos.getZ(), 20);
        	}
        	
        	worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, true);
        }
	}
	
	

	@Override
	public void explode(Level world, BlockPos pos) {
		if(world.isClientSide)
			return;
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    	if(this == ModBlocks.therm_endo)
    	{
    		ExplosionThermo.freeze(world, pos.getX(), pos.getY(), pos.getZ(), 15);
    		ExplosionThermo.freezer(world, pos.getX(), pos.getY(), pos.getZ(), 20);
    	}

    	if(this == ModBlocks.therm_exo)
    	{
    		ExplosionThermo.scorch(world, pos.getX(), pos.getY(), pos.getZ(), 15);
    		ExplosionThermo.setEntitiesOnFire(world, pos.getX(), pos.getY(), pos.getZ(), 20);
    	}
    	
    	world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, true);
	}

}
