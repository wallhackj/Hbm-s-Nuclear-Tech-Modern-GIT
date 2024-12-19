package com.hbm.blocks.bomb;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.explosion.ExplosionChaos;


import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.Blocks.AIR;

public class CheaterVirus extends Block {

	static boolean protect = true;

	public CheaterVirus(Properties properties, String s) {
		super(properties);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.setTickRandomly(true);

		ModBlocks.ALL_BLOCKS.add(this);
	}

//	@Override
	public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
		super.destroy(worldIn, pos, state);
		if(CheaterVirus.protect)
			worldIn.setBlockAndUpdate(pos, state);
	}

//	@Override
	public void updateTick(Level world, BlockPos pos1, BlockState state, Random rand) {
		if(GeneralConfig.enableVirus) {
			int x = pos1.getX();
			int y = pos1.getY();
			int z = pos1.getZ();
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			if(world.getBlockState(pos.set(x + 1, y, z)).getBlock() != ModBlocks.cheater_virus &&
					world.getBlockState(pos.set(x + 1, y, z)).getBlock() != AIR &&
					world.getBlockState(pos.set(x + 1, y, z)).getBlock() != ModBlocks.cheater_virus) {
				world.setBlockAndUpdate(pos.set(x + 1, y, z), ModBlocks.cheater_virus.defaultBlockState());
				;
			}

			if(world.getBlockState(pos.set(x, y + 1, z)).getBlock() != ModBlocks.cheater_virus && world.getBlockState(pos.set(x, y + 1, z)).getBlock() != Blocks.AIR && world.getBlockState(pos.set(x, y + 1, z)).getBlock() != ModBlocks.cheater_virus) {
				world.setBlockAndUpdate(pos.set(x, y + 1, z), ModBlocks.cheater_virus.defaultBlockState());
				;
			}

			if(world.getBlockState(pos.set(x, y, z + 1)).getBlock() != ModBlocks.cheater_virus && world.getBlockState(pos.set(x, y, z + 1)).getBlock() != Blocks.AIR && world.getBlockState(pos.set(x, y, z + 1)).getBlock() != ModBlocks.cheater_virus) {
				world.setBlockAndUpdate(pos.set(x, y, z + 1), ModBlocks.cheater_virus.defaultBlockState());
				;
			}

			if(world.getBlockState(pos.set(x - 1, y, z)).getBlock() != ModBlocks.cheater_virus &&
					world.getBlockState(pos.set(x - 1, y, z)).getBlock() != AIR && world.getBlockState(pos.set(x - 1, y, z)).getBlock() != ModBlocks.cheater_virus) {
				world.setBlockAndUpdate(pos.set(x - 1, y, z), ModBlocks.cheater_virus.defaultBlockState());
				;
			}

			if(world.getBlockState(pos.set(x, y - 1, z)).getBlock() != ModBlocks.cheater_virus &&
					world.getBlockState(pos.set(x, y - 1, z)).getBlock() != AIR &&
					world.getBlockState(pos.set(x, y - 1, z)).getBlock() != ModBlocks.cheater_virus) {
				world.setBlockAndUpdate(pos.set(x, y - 1, z), ModBlocks.cheater_virus.defaultBlockState());
				;
			}

			if(world.getBlockState(pos.set(x, y, z - 1)).getBlock() != ModBlocks.cheater_virus && world.getBlockState(pos.set(x, y, z - 1)).getBlock() != Blocks.AIR && world.getBlockState(pos.set(x, y, z - 1)).getBlock() != ModBlocks.cheater_virus) {
				world.setBlockAndUpdate(pos.set(x, y, z - 1), ModBlocks.cheater_virus.defaultBlockState());
				;
			}

			protect = false;
			world.setBlockAndUpdate(pos.set(x, y, z), AIR.defaultBlockState());
			protect = true;
		}
	}

//	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos1, Block blockIn, BlockPos fromPos) {
		int x = pos1.getX();
		int y = pos1.getY();
		int z = pos1.getZ();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		if((world.getBlockState(pos.set(x + 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.set(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.set(x + 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && (world.getBlockState(pos.set(x - 1, y, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.set(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.set(x - 1, y, z)).getBlock() == ModBlocks.cheater_virus_seed) && (world.getBlockState(pos.set(x, y + 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.set(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.set(x, y + 1, z)).getBlock() == ModBlocks.cheater_virus_seed)
				&& (world.getBlockState(pos.set(x, y - 1, z)).getBlock() == Blocks.AIR || world.getBlockState(pos.set(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.set(x, y - 1, z)).getBlock() == ModBlocks.cheater_virus_seed) && (world.getBlockState(pos.set(x, y, z + 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.set(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.set(x, y, z + 1)).getBlock() == ModBlocks.cheater_virus_seed) && (world.getBlockState(pos.set(x, y, z - 1)).getBlock() == Blocks.AIR || world.getBlockState(pos.set(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus || world.getBlockState(pos.set(x, y, z - 1)).getBlock() == ModBlocks.cheater_virus_seed) && !world.isRemote) {
			protect = false;
			world.setBlockAndUpdate(pos.set(x, y, z), Blocks.AIR.defaultBlockState());
			ExplosionChaos.spreadVirus(world, x, y, z, 5);
			protect = true;
		}
	}

//	@Override
	public void onEntityWalk(Level worldIn, BlockPos pos, Entity entityIn) {
		if(entityIn instanceof LivingEntity) {
//			((LivingEntity) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 60 * 60 * 60, 9));
		}
	}

}
