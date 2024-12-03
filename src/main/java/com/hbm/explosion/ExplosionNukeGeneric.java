package com.hbm.explosion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.lang.NoClassDefFoundError;

import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.hbm.config.CompatibilityConfig;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.WasteLog;
import com.hbm.config.VersatileConfig;
import com.hbm.handler.ArmorUtil;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.lib.ModDamageSource;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.turret.TileEntityTurretBase;
import api.hbm.energy.IEnergyUser;

import net.minecraftforge.energy.IEnergyStorage;

public class ExplosionNukeGeneric {

	private final static Random random = new Random();
	
	public static void empBlast(net.minecraft.world.level.Level world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int r = bombStartStrength;
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
					if (ZZ < r22) {
						pos.offset(X, Y, Z);
						emp(world, pos);
					}
				}
			}
		}
	}
	
	public static void succ(Level world, int x, int y, int z, int radius) {
		int i;
		int j;
		int k;
		double d5;
		double d6;
		double d7;
		double wat = radius;

		// bombStartStrength *= 2.0F;
		i = (int) Math.floor(x - wat - 1.0D);
		j = (int) Math.floor(x + wat + 1.0D);
		k = (int) Math.floor(y - wat - 1.0D);
		int i2 = (int) Math.floor(y + wat + 1.0D);
		int l = (int) Math.floor(z - wat - 1.0D);
		int j2 = (int) Math.floor(z + wat + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AABB(i, k, l, j, i2, j2));

		for (int i1 = 0; i1 < list.size(); ++i1) {
			Entity entity = (Entity) list.get(i1);
			
			if(entity instanceof EntityBlackHole)
				continue;
			
			double d4 = entity.getDistance(x, y, z) / radius;

			if (d4 <= 1.0D) {
				d5 = entity.getX() - x;
				d6 = entity.getY() + entity.getEyeHeight() - y;
				d7 = entity.getZ() - z;
				double d9 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
				if (d9 < wat && !(entity instanceof Player && ArmorUtil.checkArmor((Player) entity,
						ModItems.euphemium_helmet, ModItems.euphemium_plate, ModItems.euphemium_legs,
						ModItems.euphemium_boots))) {
					d5 /= d9;
					d6 /= d9;
					d7 /= d9;
					
					if (!(entity instanceof Player && ((Player) entity).capabilities.isCreativeMode)) {
						double d8 = 0.125 + (random.nextDouble() * 0.25);
						entity.motionX -= d5 * d8;
						entity.motionY -= d6 * d8;
						entity.motionZ -= d7 * d8;
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static int destruction(Level world, BlockPos pos) {
		int rand;
		if (!world.isClientSide) {
			BlockState b = world.getBlockState(pos);
			if (b.getBlock().getExplosionResistance()>=200f) {	//500 is the resistance of liquids
				//blocks to be spared
				int protection = (int)(b.getBlock().getExplosionResistance()/300f);
				if (b.getBlock() == ModBlocks.brick_concrete) {
					rand = random.nextInt(8);
					if (rand == 0) {
						world.setBlockAndUpdate(pos, Blocks.GRAVEL.defaultBlockState());
						return 0;
					}
				} else if (b.getBlock() == ModBlocks.brick_light) {
					rand = random.nextInt(3);
					if (rand == 0) {
						world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
						return 0;
					}else if (rand == 1){
						world.setBlockAndUpdate(pos,ModBlocks.block_scrap.defaultBlockState());
						return 0;
					}
				} else if (b.getBlock() == ModBlocks.brick_obsidian) {
					rand = random.nextInt(20);
					if (rand == 0) {
						world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
					}
				} else if (b.getBlock() == Blocks.OBSIDIAN) {
					world.setBlockAndUpdate(pos, ModBlocks.gravel_obsidian.defaultBlockState());
					return 0;
				} else if(random.nextInt(protection+3)==0){
					world.setBlockAndUpdate(pos, ModBlocks.block_scrap.defaultBlockState());
				}
				return protection;
			}else{//otherwise, kill the block!
				world.removeBlock(pos, false);
			}
		}
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	public static int vaporDest(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			BlockState b = world.getBlockState(pos);
			if (b.getBlock().getExplosionResistance()<0.5f //most light things
					|| b.getBlock() == Blocks.WEB || b.getBlock() == ModBlocks.red_cable
					|| b.getBlock() instanceof BlockLiquid) {
				world.removeBlock(pos, false);
				return 0;
			} else if (b.getBlock().getExplosionResistance()<=3.0f && !b.isOpaqueCube()){
				if(b.getBlock() != Blocks.CHEST && b.getBlock() != Blocks.FARMLAND){
					//destroy all medium resistance blocks that aren't chests or farmland
					world.removeBlock(pos, false);
					return 0;
				}
			}
			
			if (b.getBlock().isFlammable(null, world, pos, Direction.UP)
					&& world.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
				world.setBlockAndUpdate(pos.up(), Blocks.FIRE.defaultBlockState(),2);
			}
			return (int)( b.getBlock().getExplosionResistance()/300f);
		}
		return 0;
	}

	public static void waste(Level world, int x, int y, int z, int radius) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int r = radius;
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
					if (ZZ < r22 + world.random.nextInt(r22 / 5)) {
						if (world.getBlockState(pos.offset(X, Y, Z)).getBlock() != Blocks.AIR)
							wasteDest(world, pos);
					}
				}
			}
		}
	}

	public static void wasteDest(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			int rand;
			BlockState bs = world.getBlockState(pos);
			Block b = bs.getBlock();
			if(b == Blocks.AIR){
				return;
			}

			else if (b == Blocks.ACACIA_DOOR || b == Blocks.BIRCH_DOOR || b == Blocks.DARK_OAK_DOOR || b == Blocks.JUNGLE_DOOR || b == Blocks.OAK_DOOR || b == Blocks.SPRUCE_DOOR || b == Blocks.IRON_DOOR) {
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}

			else if (b == Blocks.GRASS) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_earth.defaultBlockState());
			}

			else if (b == Blocks.MYCELIUM) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_mycelium.defaultBlockState());
			}

			else if (b == Blocks.SAND) {
				rand = random.nextInt(20);
				if (rand == 1 && bs.getValue(BlockSand.VARIANT) == BlockSand.EnumType.SAND) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_trinitite.defaultBlockState());
				}
				if (rand == 1 && bs.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_trinitite_red.defaultBlockState());
				}
			}

			else if (b == Blocks.CLAY) {
				world.setBlockAndUpdate(pos, Blocks.HARDENED_CLAY.getDefaultState());
			}

			else if (b == Blocks.MOSSY_COBBLESTONE) {
				world.setBlockAndUpdate(pos, Blocks.COAL_ORE.defaultBlockState());
			}

			else if (b == Blocks.COAL_ORE) {
				rand = random.nextInt(10);
				if (rand == 1 || rand == 2 || rand == 3) {
					world.setBlockAndUpdate(pos, Blocks.DIAMOND_ORE.defaultBlockState());
				}
				if (rand == 9) {
					world.setBlockAndUpdate(pos, Blocks.EMERALD_ORE.defaultBlockState());
				}
			}

			else if(b instanceof BlockLog) {
				world.setBlockAndUpdate(pos, ((WasteLog)ModBlocks.waste_log).getSameRotationState(bs));
			}

			else if (b == Blocks.BROWN_MUSHROOM_BLOCK) {
				if (bs.getValue(BlockHugeMushroom.VARIANT) == BlockHugeMushroom.EnumType.STEM) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_log.defaultBlockState());
				} else {
					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				}
			}

			else if(b == Blocks.DIRT || b == Blocks.FARMLAND) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_dirt.defaultBlockState());
			}

			else if(b instanceof BlockSnow) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_snow.defaultBlockState());
			}

			else if(b instanceof BlockSnowBlock) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_snow_block.defaultBlockState());
			} 

			else if(b instanceof BlockIce) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_ice.defaultBlockState());
			}
			
			else if(b instanceof BlockBush || b == Blocks.TALLGRASS) {
				world.setBlockAndUpdate(pos, Blocks.DEADBUSH.getDefaultState());
			}

			else if(b == Blocks.STONE){
				world.setBlock(pos, ModBlocks.sellafield_slaked.getStateFromMeta(world.random.nextInt(4)));
			}

			else if(b == Blocks.BEDROCK){
				world.setBlockAndUpdate(pos.offset(0, 1, 0),
						ModBlocks.toxic_block.defaultBlockState());
			}

			else if (b == Blocks.RED_MUSHROOM_BLOCK) {
				if (bs.getValue(BlockHugeMushroom.VARIANT) == BlockHugeMushroom.EnumType.STEM) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_log.defaultBlockState());
				} else {
					world.setBlock(pos, Blocks.AIR.defaultBlockState(),2);
				}
			}
			
			else if (bs.getMaterial() == Material.WOOD && bs.isOpaqueCube() && b != ModBlocks.waste_log) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
			}

			else if (b == ModBlocks.ore_uranium) {
				rand = random.nextInt(VersatileConfig.getSchrabOreChance());
				if (rand == 1) {
					world.setBlockAndUpdate(pos, ModBlocks.ore_schrabidium.defaultBlockState());
				} else {
					world.setBlockAndUpdate(pos, ModBlocks.ore_uranium_scorched.defaultBlockState());
				}
			}

			else if (b == ModBlocks.ore_nether_uranium) {
				rand = random.nextInt(VersatileConfig.getSchrabOreChance());
				if (rand == 1) {
					world.setBlockAndUpdate(pos, ModBlocks.ore_nether_schrabidium.defaultBlockState());
				} else {
					world.setBlockAndUpdate(pos, ModBlocks.ore_nether_uranium_scorched.defaultBlockState());
				}
			}
			
			else if (b == ModBlocks.ore_gneiss_uranium) {
				rand = random.nextInt(VersatileConfig.getSchrabOreChance());
				if (rand == 1) {
					world.setBlockAndUpdate(pos, ModBlocks.ore_gneiss_schrabidium.defaultBlockState());
				} else {
					world.setBlockAndUpdate(pos, ModBlocks.ore_gneiss_uranium_scorched.defaultBlockState());
				}
			}

		}
	}

	public static void wasteNoSchrab(Level world, BlockPos pos, int radius) {
		if(!CompatibilityConfig.isWarDim(world)){
			return;
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		int r = radius;
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
					if (ZZ < r22 + world.random.nextInt(r22 / 5)) {
						mpos.offset(X, Y, Z);
						if (world.getBlockState(mpos).getBlock() != Blocks.AIR)
							wasteDestNoSchrab(world, mpos);
					}
				}
			}
		}
	}

	public static void wasteDestNoSchrab(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			int rand;
			Block b = world.getBlockState(pos).getBlock();

			if(b == Blocks.AIR){
				return;	
			}

			else if (b == Blocks.GLASS || b == Blocks.STAINED_GLASS
					|| b == Blocks.ACACIA_DOOR || b == Blocks.BIRCH_DOOR || b == Blocks.DARK_OAK_DOOR || b == Blocks.JUNGLE_DOOR || b == Blocks.OAK_DOOR || b == Blocks.SPRUCE_DOOR || b == Blocks.IRON_DOOR
					|| b == Blocks.LEAVES || b == Blocks.LEAVES2) {
				world.removeBlock(pos, false);
			}

			else if (b == Blocks.GRASS) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_earth.defaultBlockState());
			}

			else if (b == Blocks.MYCELIUM) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_mycelium.defaultBlockState());
			}

			else if (b == Blocks.SAND) {
				rand = random.nextInt(20);
				if (rand == 1 && world.getBlockState(pos)
						.getValue(BlockSand.VARIANT) == BlockSand.EnumType.SAND) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_trinitite.defaultBlockState());
				} else if (rand == 1 && world.getBlockState(pos)
						.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_trinitite_red.defaultBlockState());
				}
			}

			else if (b == Blocks.CLAY) {
				world.setBlockAndUpdate(pos, Blocks.HARDENED_CLAY.getDefaultState());
			}

			else if(b == Blocks.DIRT) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_dirt.defaultBlockState());
			}

			else if(b instanceof BlockSnow) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_snow.defaultBlockState());
			} 
			
			else if(b instanceof BlockSnowBlock) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_snow_block.defaultBlockState());
			} 

			else if(b instanceof BlockIce) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_ice.defaultBlockState());
			}

			else if(b instanceof BlockBush || b == Blocks.TALLGRASS) {
				world.setBlockAndUpdate(pos, Blocks.DEADBUSH.getDefaultState());
			}

			else if(b == Blocks.STONE){
				world.setBlock(pos, ModBlocks.sellafield_slaked.getStateFromMeta(world.random.nextInt(4)));
			}

			else if(b == Blocks.BEDROCK){
				world.setBlockAndUpdate(pos.offset(0, 1, 0),
						ModBlocks.toxic_block.defaultBlockState());
			}

			else if (b == Blocks.MOSSY_COBBLESTONE) {
				world.setBlockAndUpdate(pos, Blocks.COAL_ORE.defaultBlockState());
			}

			else if (b == Blocks.COAL_ORE) {
				rand = random.nextInt(30);
				if (rand == 1 || rand == 2 || rand == 3) {
					world.setBlockAndUpdate(pos, Blocks.DIAMOND_ORE.defaultBlockState());
				}
				if (rand == 29) {
					world.setBlockAndUpdate(pos, Blocks.EMERALD_ORE.defaultBlockState());
				}
			}

			else if (b == Blocks.LOG || b == Blocks.LOG2) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_log.defaultBlockState());
			}

			else if (b == Blocks.PLANKS) {
				world.setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
			}

			else if (b == Blocks.BROWN_MUSHROOM_BLOCK) {
				if (world.getBlockState(pos).getValue(BlockHugeMushroom.VARIANT) == BlockHugeMushroom.EnumType.STEM) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_log.defaultBlockState());
				} else {
					world.removeBlock(pos, false);
				}
			}

			else if (b == Blocks.RED_MUSHROOM_BLOCK) {
				if (world.getBlockState(pos)
						.getValue(BlockHugeMushroom.VARIANT) == BlockHugeMushroom.EnumType.STEM) {
					world.setBlockAndUpdate(pos, ModBlocks.waste_log.defaultBlockState());
				} else {
					world.removeBlock(pos, false);
				}
			}
		}
	}

	public static void emp(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			if(!CompatibilityConfig.isWarDim(world)){
				return;
			}
			Block b = world.getBlockState(pos).getBlock();
			TileEntity te = world.getTileEntity(pos);
			
			if (te != null && te instanceof IEnergyUser) {
				
				((IEnergyUser)te).setPower(0);
				
				if(random.nextInt(5) < 1)
					world.setBlockAndUpdate(pos, ModBlocks.block_electrical_scrap.defaultBlockState());
			}
			try{
				if (te != null && te instanceof IEnergyProvider) {

					((IEnergyProvider)te).extractEnergy(EnumFacing.UP,
							((IEnergyProvider)te).getEnergyStored(EnumFacing.UP), false);
					((IEnergyProvider)te).extractEnergy(EnumFacing.DOWN,
							((IEnergyProvider)te).getEnergyStored(EnumFacing.DOWN), false);
					((IEnergyProvider)te).extractEnergy(EnumFacing.NORTH,
							((IEnergyProvider)te).getEnergyStored(EnumFacing.NORTH), false);
					((IEnergyProvider)te).extractEnergy(EnumFacing.SOUTH,
							((IEnergyProvider)te).getEnergyStored(EnumFacing.SOUTH), false);
					((IEnergyProvider)te).extractEnergy(EnumFacing.EAST,
							((IEnergyProvider)te).getEnergyStored(EnumFacing.EAST), false);
					((IEnergyProvider)te).extractEnergy(EnumFacing.WEST,
							((IEnergyProvider)te).getEnergyStored(EnumFacing.WEST), false);
					
					if(random.nextInt(5) <= 1)
						world.setBlockAndUpdate(pos, ModBlocks.block_electrical_scrap.defaultBlockState());
				}
			} catch(NoClassDefFoundError e){}
			if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, null)){
				IEnergyStorage handle = te.getCapability(CapabilityEnergy.ENERGY, null);
				handle.extractEnergy(handle.getEnergyStored(), false);
				if(random.nextInt(5) <= 1)
					world.setBlockAndUpdate(pos, ModBlocks.block_electrical_scrap.defaultBlockState());
			}
			if((b == ModBlocks.fusion_conductor || b == ModBlocks.fwatz_conductor ||
					b == ModBlocks.fusion_motor || b == ModBlocks.fusion_heater ||
					b == ModBlocks.fwatz_computer) && random.nextInt(10) == 0)
				world.setBlockAndUpdate(pos, ModBlocks.block_electrical_scrap.defaultBlockState());
		}
	}
	
	public static void loadSoliniumFromFile(){
		File config = new File(MainRegistry.proxy.getDataDir().getPath() + "/config/hbm/solinium.cfg");
		if (!config.exists())
			try {
				config.getParentFile().mkdirs();
				FileWriter write = new FileWriter(config);
				write.write("# Format: modid:blockName|modid:blockName\n" + 
							"# Left blocks are transformed to right, one per line\n");
				write.close();
				
			} catch (IOException e) {
//				MainRegistry.logger.log(Level.ERROR, "ERROR: Could not create config file: " + config.getAbsolutePath());
				e.printStackTrace();
				return;
			}
		if(config.exists()){
			BufferedReader read = null;
			int lineCount = 0;
			try {
				read = new BufferedReader(new FileReader(config));
				String currentLine = null;
				
				while((currentLine = read.readLine()) != null){
					lineCount ++;
					if(currentLine.startsWith("#") || currentLine.length() == 0)
						continue;
					String[] blocks = currentLine.trim().split("|");
					if(blocks.length != 2)
						continue;
					String[] modidBlock1 = blocks[0].split(":");
					String[] modidBlock2 = blocks[1].split(":");
					Block b1 = Block.REGISTRY.getObject(new ResourceLocation(modidBlock1[0], modidBlock1[1]));
					Block b2 = Block.REGISTRY.getObject(new ResourceLocation(modidBlock2[0], modidBlock2[1]));
					if(b1 == null || b2 == null){
//						MainRegistry.logger.log(Level.ERROR, "Failed to find block for solinium config on line: " + lineCount);
						continue;
					}
					soliniumConfig.put(b1, b2);
				}
			} catch (FileNotFoundException e) {
//				MainRegistry.logger.log(Level.ERROR, "Could not find solinium config file! This should never happen.");
				e.printStackTrace();
			} catch (IOException e){
//				MainRegistry.logger.log(Level.ERROR, "Error reading solinium config!");
				e.printStackTrace();
			} finally {
				if(read != null)
					try {
						read.close();
					} catch (IOException e) {}
			}
		}
	}
	
	public static Map<Block, Block> soliniumConfig = new HashMap<>();
	
	public static void solinium(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			
			BlockState b = world.getBlockState(pos);
//			Material m = b.getMaterial();
			
			if(soliniumConfig.containsKey(b.getBlock())){
				world.setBlockAndUpdate(pos, soliniumConfig.get(b.getBlock()).defaultBlockState());
				return;
			}
			
			if(b.getBlock() == Blocks.GRASS || b.getBlock() == Blocks.DIRT || b.getBlock() == Blocks.MYCELIUM || b.getBlock() == ModBlocks.waste_earth || b.getBlock() == ModBlocks.waste_dirt || b.getBlock() == ModBlocks.waste_mycelium) {
				if(random.nextInt(5) < 2)
					world.setBlockAndUpdate(pos, Blocks.DIRT.getStateFromMeta(1));
				else
					world.setBlockAndUpdate(pos, Blocks.DIRT.getDefaultState());
				return;
			}

			if(b.getBlock() == ModBlocks.sellafield_slaked) {
				world.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
				return;
			}

			if( b.getBlock() == ModBlocks.sellafield_0 || b.getBlock() == ModBlocks.sellafield_1) {
				world.setBlockAndUpdate(pos, Blocks.STONE.getStateFromMeta(5));
				return;
			}

			if(b.getBlock() == ModBlocks.sellafield_2 || b.getBlock() == ModBlocks.sellafield_3) {
				world.setBlockAndUpdate(pos, Blocks.STONE.getStateFromMeta(3));
				return;
			}

			if(b.getBlock() == ModBlocks.sellafield_4 || b.getBlock() == ModBlocks.sellafield_core) {
				world.setBlockAndUpdate(pos, Blocks.STONE.getStateFromMeta(1));
				return;
			}

			if(b.getBlock() == ModBlocks.toxic_block) {
				world.removeBlock(pos, false);
				return;
			}

			if(b.getBlock() == ModBlocks.waste_trinitite || b.getBlock() == ModBlocks.waste_sand) {
				world.setBlockAndUpdate(pos, Blocks.SAND.defaultBlockState());
				return;
			}

			if(b.getBlock() == ModBlocks.waste_terracotta) {
				world.setBlockAndUpdate(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState());
				return;
			}

			if(b.getBlock() == ModBlocks.waste_trinitite_red) {
				world.setBlockAndUpdate(pos, Blocks.SAND.getStateFromMeta(1));
				return;
			}

			if(b.getBlock() == ModBlocks.waste_sandstone) {
				world.setBlockAndUpdate(pos, Blocks.SANDSTONE.defaultBlockState());
				return;
			}

			if(b.getBlock() == ModBlocks.waste_sandstone_red) {
				world.setBlockAndUpdate(pos, Blocks.RED_SANDSTONE.defaultBlockState());
				return;
			}
			
			if(b.getBlock() == ModBlocks.waste_gravel) {
				world.setBlockAndUpdate(pos, Blocks.GRAVEL.defaultBlockState());
				return;
			}

			if(b.getBlock() == ModBlocks.taint) {
				world.setBlockAndUpdate(pos, ModBlocks.stone_gneiss.defaultBlockState());
				return;
			}
			
			if(m == Material.CACTUS || m == Material.CORAL || m == Material.LEAVES || m == Material.PLANTS ||
					m == Material.SPONGE || m == Material.VINE || m == Material.GOURD || m == Material.WOOD) {
				world.removeBlock(pos, false);
			}
		}
	}
}
