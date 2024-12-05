package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.effect.EntityEMPBlast;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.interfaces.IBomb;
import com.hbm.lib.HBMSoundHandler;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class BombFloat extends Block implements IBomb {

	public BombFloat(Material materialIn, String s) {
        super(null);
//		super(materialIn);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
//	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0)
        {
        	explode(worldIn, pos);
        }
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HBMSoundHandler.sparkShoot,
				SoundSource.BLOCKS, 5.0f, world.random.nextFloat() * 0.2F + 0.9F);
		
		if(!world.isClientSide) {
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    		if(this == ModBlocks.float_bomb) {
            	ExplosionChaos.floater(world, pos, 15, 50);
            	ExplosionChaos.move(world, pos, 15, 0, 50, 0);
    		}
    		if(this == ModBlocks.emp_bomb) {
    			ExplosionNukeGeneric.empBlast(world, pos.getX(), pos.getY(), pos.getZ(), 50);
    			EntityEMPBlast wave = new EntityEMPBlast(world, 50);
    			wave.posX = pos.getX() + 0.5;
    			wave.posY = pos.getY() + 0.5;
    			wave.posZ = pos.getZ() + 0.5;
    			world.addFreshEntity(wave);
    		}
		}
	}

}
