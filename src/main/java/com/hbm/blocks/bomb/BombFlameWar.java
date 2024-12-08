package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.interfaces.IBomb;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class BombFlameWar extends Block implements IBomb {

	public BombFlameWar(Material materialIn, String s) {
//		super(materialIn);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
		super(null);
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
//	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(worldIn.hasNeighborSignal(pos)){
			explode(worldIn, pos);
		}
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		ExplosionChaos.explode(world, pos.getX(), pos.getY(), pos.getZ(), 15);
    	ExplosionChaos.spawnExplosion(world, pos.getX(), pos.getY(), pos.getZ(), 75);
    	ExplosionChaos.flameDeath(world, pos, 100);
	}

}
