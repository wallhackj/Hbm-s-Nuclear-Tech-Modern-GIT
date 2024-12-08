package com.hbm.explosion;

import java.util.List;

import com.hbm.config.CompatibilityConfig;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.WasteLog;
import com.hbm.handler.ArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;


public class ExplosionThermo {

	public static void freeze(Level world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int r = bombStartStrength * 2;
		int r2 = r * r;
		int r22 = r2 / 2;
		for (int xx = -r; xx < r; xx++) {
			int X = xx + x;
			int XX = xx * xx;
			for (int yy = -r; yy < r; yy++) {
				int Y = yy + y;
				int YY = XX + yy * yy;
				for (int zz = -r; zz < r; zz++) {
					int Z = zz + z;
					int ZZ = YY + zz * zz;
					if (ZZ < r22 + world.random.nextInt(r22 / 2))
						pos.move(X, Y, Z);
					freezeDest(world, pos);
				}
			}
		}
	}

	public static void scorch(Level world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int r = bombStartStrength * 2;
		int r2 = r * r;
		int r22 = r2 / 2;
		for (int xx = -r; xx < r; xx++) {
			int X = xx + x;
			int XX = xx * xx;
			for (int yy = -r; yy < r; yy++) {
				int Y = yy + y;
				int YY = XX + yy * yy;
				for (int zz = -r; zz < r; zz++) {
					int Z = zz + z;
					int ZZ = YY + zz * zz;
					if (ZZ < r22 + world.random.nextInt(r22 / 2))
						pos.move(X, Y, Z);
					scorchDest(world, pos);
				}
			}
		}
	}

	public static void scorchDest(Level world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		if (block == Blocks.GRASS) {
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		
		} else if (block == ModBlocks.frozen_grass) {
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		
		} else if (block == Blocks.DIRT) {
			world.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		
		} else if (block == ModBlocks.frozen_dirt) {
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		
		} else if (block == Blocks.NETHERRACK) {
			world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
		
		} else if (block == ModBlocks.frozen_log) {
			world.setBlockAndUpdate(pos, (ModBlocks.waste_log).withSameRotationState(world.getBlockState(pos)));
		
		} else if(block instanceof BlockLog) {
			world.setBlockAndUpdate(pos, (ModBlocks.waste_log).getSameRotationState(world.getBlockState(pos)));
		
		} else if (block == ModBlocks.frozen_planks) {
			world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
		
		} else if (block == Blocks.PLANKS) {
			world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
		
		} else if (block == Blocks.STONE) {
			world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
		
		} else if (block == Blocks.COBBLESTONE) {
			world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
		
		} else if (block == Blocks.STONEBRICK) {
			world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
		
		} else if (block == Blocks.OBSIDIAN) {
			world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
		
		} else if (block instanceof BlockLeaves) {
			world.removeBlock(pos, false);
		
		} else if (block == Blocks.WATER) {
			world.removeBlock(pos, false);
		
		} else if (block == Blocks.WATER) {
			world.removeBlock(pos, false);
		
		} else if (block == Blocks.PACKED_ICE) {
			world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
		
		} else if (block == Blocks.ICE) {
			world.removeBlock(pos, false);
		
		} else if (block == Blocks.SNOW) {
			world.removeBlock(pos, false);
		
		} else if (block == Blocks.SNOW) {
			world.removeBlock(pos, false);
		}
	}

	public static void freezeDest(Level world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		if (block == Blocks.GRASS) {
			world.setBlockAndUpdate(pos, ModBlocks.frozen_grass.defaultBlockState());
		
		} else if (block == Blocks.DIRT) {
			world.setBlockAndUpdate(pos, ModBlocks.frozen_dirt.defaultBlockState());
		
		} else if (block == Blocks.JUNGLE_PLANKS) {
			world.setBlockAndUpdate(pos, ModBlocks.frozen_planks.defaultBlockState());
		
		} else if (block == ModBlocks.waste_log) {
			world.setBlockAndUpdate(pos, (ModBlocks.frozen_log).withSameRotationState(world.getBlockState(pos)));
		
		} else if(block instanceof BlockLog) {
			world.setBlockAndUpdate(pos, (ModBlocks.frozen_log).getSameRotationState(world.getBlockState(pos)));
		
		} else if (block == ModBlocks.waste_planks) {
			world.setBlockAndUpdate(pos, ModBlocks.frozen_planks.defaultBlockState());
		
		} else if (block == Blocks.STONE) {
			world.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
		
		} else if (block == Blocks.COBBLESTONE) {
			world.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
		
		} else if (block == Blocks.STONEBRICK) {
			world.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
		
		} else if (block instanceof BlockLeaves) {
			world.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
		
		} else if (block == Blocks.LAVA) {
			world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
		
		} else if (block == Blocks.LAVA) {
			world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
		
		} else if (block == Blocks.WATER) {
			world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
		
		} else if (block == Blocks.WATER) {
			world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
		}
	}

	public static void freezer(Level world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		float f = bombStartStrength;
		int i;
		int j;
		int k;
		double d5;
		double d6;
		double d7;
		double wat = bombStartStrength;

		bombStartStrength *= 2.0F;
		i = (int) Math.floor(x - wat - 1.0D);
		j = (int) Math.floor(x + wat + 1.0D);
		k = (int) Math.floor(y - wat - 1.0D);
		int i2 = (int) Math.floor(y + wat + 1.0D);
		int l = (int) Math.floor(z - wat - 1.0D);
		int j2 = (int) Math.floor(z + wat + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AABB(i, k, l, j, i2, j2));

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int i1 = 0; i1 < list.size(); ++i1) {
			Entity entity = (Entity) list.get(i1);
			double d4 = entity.getDistance(x, y, z) / bombStartStrength;

			if (d4 <= 1.0D) {
				d5 = entity.getX() - x;
				d6 = entity.getY() + entity.getEyeHeight() - y;
				d7 = entity.getZ() - z;
				double d9 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
				if (d9 < wat && !(entity instanceof EntityOcelot) && entity instanceof LivingEntity) {
					for (int a = (int) entity.getX() - 2; a < (int) entity.getX() + 1; a++) {
						for (int b = (int) entity.getY(); b < (int) entity.getY() + 3; b++) {
							for (int c = (int) entity.getZ() - 1; c < (int) entity.getZ() + 2; c++) {
								pos.move(a, b, c);
								world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
							}
						}
					}

					((LivingEntity) entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 60 * 20, 4));
					((LivingEntity) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 90 * 20, 2));
					((LivingEntity) entity).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 3 * 60 * 20, 2));
				}
			}
		}

		bombStartStrength = (int) f;
	}

	public static void setEntitiesOnFire(Level world, double x, double y, double z, int radius) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius));

		for(Entity e : list) {
			
			if(e.getDistance(x, y, z) <= radius) {

				if(!(e instanceof Player && ArmorUtil.checkForAsbestos((Player) e))) {
					
					if(e instanceof LivingEntity)
						((LivingEntity) e).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 15 * 20, 4));
					
					e.setFire(10);
				}
			}
		}
	}
	
	public static void scorchLight(Level world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int r = bombStartStrength * 2;
		int r2 = r*r;
		int r22 = r2/2;
		for (int xx = -r; xx < r; xx++)
		{
			int X = xx+x;
			int XX = xx*xx;
			for (int yy = -r; yy < r; yy++)
			{
				int Y = yy+y;
				int YY = XX+yy*yy;
				for (int zz = -r; zz < r; zz++)
				{
					int Z = zz+z;
					int ZZ = YY+zz*zz;
					if (ZZ<r22 + world.random.nextInt(r22/2))
						scorchDestLight(world, pos.move(X, Y, Z));
				}
			}
		}
	}
	
	public static void scorchDestLight(Level world, BlockPos pos) {
		BlockState blockstate = world.getBlockState(pos);
		Block block = blockstate.getBlock();
		
		if(block == Blocks.GRASS){
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		
		} else if(block == ModBlocks.frozen_grass){
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		
		} else if(block == Blocks.DIRT) {
			world.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		
		} else if(block == ModBlocks.frozen_dirt){
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		
		} else if(block == ModBlocks.waste_earth){
			world.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		
		} else if(block == ModBlocks.frozen_log){
			world.setBlockAndUpdate(pos, (ModBlocks.waste_log).withSameRotationState(world.getBlockState(pos)));
		
		} else if(block instanceof BlockLog){
			world.setBlockAndUpdate(pos, (ModBlocks.waste_log).getSameRotationState(world.getBlockState(pos)));
		
		} else if(block == ModBlocks.frozen_planks){
			world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
		
		} else if(block == Blocks.PLANKS){
			world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
		
		} else if(block == Blocks.OBSIDIAN){
			world.setBlockAndUpdate(pos, ModBlocks.gravel_obsidian.defaultBlockState());
		
		} else if(block instanceof BlockLeaves){
			world.removeBlock(pos, false);
		
		} else if(block == Blocks.WATER){
			world.removeBlock(pos, false);
		
		} else if(block == Blocks.FLOWING_WATER){
			world.removeBlock(pos, false);
		
		} else if(block == Blocks.PACKED_ICE){
			world.setBlockAndUpdate(pos, Blocks.FLOWING_WATER.defaultBlockState());
		
		} else if(block == Blocks.ICE){
			world.removeBlock(, false);
		
		} else if(block == Blocks.SAND){
			world.setBlockAndUpdate(pos, Blocks.GLASS.defaultBlockState());
		
		} else if(block == Blocks.CLAY){
			world.setBlockAndUpdate(pos, Blocks.STAINED_HARDENED_CLAY.defaultBlockState().withProperty(BlockColored.COLOR, EnumDyeColor.values()[world.rand.nextInt(16)]));
		}
	}
	public static void snow(Level world, int x, int y, int z, int bound) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
    	BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		
    	int r = bound;
    	int r2 = r*r;
    	int r22 = r2/2;
    	for (int xx = -r; xx < r; xx++)
    	{
    		int X = xx+x;
    		int XX = xx*xx;
    		for (int yy = -r; yy < r; yy++)
    		{
    			int Y = yy+y;
    			int YY = XX+yy*yy;
    			for (int zz = -r; zz < r; zz++)
    			{
    				int Z = zz+z;
    				int ZZ = YY+zz*zz;
    				if (ZZ<r22)
    				{
    					pos.move(X, Y + 1, Z);
    					if(Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos) &&
								(world.getBlockState(pos).getBlock() == Blocks.AIR ||
										world.getBlockState(pos).getBlock() == Blocks.FIRE)) {
    						world.setBlockEntity(pos, Blocks.SNOW_LAYER.defaultBlockState());
    					}
    				}
    			}
    		}
    	}
    }
}
