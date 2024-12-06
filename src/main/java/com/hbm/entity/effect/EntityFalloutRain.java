package com.hbm.entity.effect;

import java.util.*;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.config.VersatileConfig;
import com.hbm.config.CompatibilityConfig;
import com.hbm.interfaces.IConstantRenderer;
import com.hbm.entity.effect.EntityFalloutUnderGround;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.saveddata.AuxSavedData;

//Chunkloading stuff
import java.util.ArrayList;
import java.util.List;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.main.MainRegistry;
import com.hbm.blocks.generic.WasteLog;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.world.ForgeChunkManager;

import static net.minecraft.world.level.block.Blocks.AIR;
import static net.minecraft.world.level.block.Blocks.STONE;


public class EntityFalloutRain extends Entity implements IConstantRenderer, IChunkLoader {
	private static final DataParameter<Integer> SCALE = EntityDataManager.createKey(EntityFalloutRain.class, DataSerializers.VARINT);
	public boolean done = false;
	public boolean doFallout = false;
	public boolean doFlood = false;
	public boolean doDrop = false;
	public int waterLevel = 0;
	public boolean spawnFire = false;

	private Ticket loaderTicket;

	private double s0;
	private double s1;
	private double s2;
	private double s3;
	private double s4;
	private double s5;
	private double s6;
	private int fallingRadius;

	private boolean firstTick = true;
	private final List<Long> chunksToProcess = new ArrayList<>();
	private final List<Long> outerChunksToProcess = new ArrayList<>();
	private int falloutTickNumber = 0;

	public int falloutBallRadius = 0;

	public EntityFalloutRain(Level world) {
		super(null,world);
//		this.setSize(4, 20);
//		this.ignoreFrustumCheck = false;
//		this.isImmuneToFire = true;

		this.waterLevel = getInt(CompatibilityConfig.fillCraterWithWater
				.get(world.provider.getDimension()));
		if(this.waterLevel == 0){
			this.waterLevel = world.getSeaLevel();
		} else if(this.waterLevel < 0 && this.waterLevel > -world.getSeaLevel()){
			this.waterLevel = world.getSeaLevel() - this.waterLevel;
		}
		this.spawnFire = BombConfig.spawnFire;
	}

	public EntityFalloutRain(Level p_i1582_1_, int maxage) {
		super(null,p_i1582_1_);
//		this.setSize(4, 20);
//		this.isImmuneToFire = true;
	}

	private static int getInt(Object e){
		if(e == null)
			return 0;
		return (int)e;
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(this.getX(), this.getY(), this.getZ(), this.getX(),
				this.getY(), this.getZ());
	}

	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	protected void entityInit() {
		init(ForgeChunkManager.requestTicket(MainRegistry.instance, level(), Type.ENTITY));
		this.dataManager.register(SCALE, Integer.valueOf(0));
	}

	@Override
	public void init(Ticket ticket) {
		if(!level().isClientSide) {
			
            if(ticket != null) {
            	
                if(loaderTicket == null) {
                	
                	loaderTicket = ticket;
                	loaderTicket.bindEntity(this);
                	loaderTicket.getModData();
                }

                ForgeChunkManager.forceChunk(loaderTicket, new ChunkPos(chunkCoordX, chunkCoordZ));
            }
        }
	}

	List<ChunkPos> loadedChunks = new ArrayList<ChunkPos>();
	@Override
	public void loadNeighboringChunks(int newChunkX, int newChunkZ) {
		if(!level().isClientSide && loaderTicket != null)
        {
            for(ChunkPos chunk : loadedChunks)
            {
                ForgeChunkManager.unforceChunk(loaderTicket, chunk);
            }

            loadedChunks.clear();
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ));
            loadedChunks.add(new ChunkPos(newChunkX + 1, newChunkZ + 1));
            loadedChunks.add(new ChunkPos(newChunkX - 1, newChunkZ - 1));
            loadedChunks.add(new ChunkPos(newChunkX + 1, newChunkZ - 1));
            loadedChunks.add(new ChunkPos(newChunkX - 1, newChunkZ + 1));
            loadedChunks.add(new ChunkPos(newChunkX + 1, newChunkZ));
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ + 1));
            loadedChunks.add(new ChunkPos(newChunkX - 1, newChunkZ));
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ - 1));

            for(ChunkPos chunk : loadedChunks)
            {
                ForgeChunkManager.forceChunk(loaderTicket, chunk);
            }
        }
	}

	private void gatherChunks() {
		Set<Long> chunks = new LinkedHashSet<>(); // LinkedHashSet preserves insertion order
		Set<Long> outerChunks = new LinkedHashSet<>();
		int outerRange = doFallout ? getScale() : fallingRadius;
		// Basically defines something like the step size, but as indirect proportion. The actual angle used for rotation will always end up at 360° for angle == adjustedMaxAngle
		// So yea, I mathematically worked out that 20 is a good value for this, with the minimum possible being 18 in order to reach all chunks
		int adjustedMaxAngle = 20 * outerRange / 32; // step size = 20 * chunks / 2
		for (int angle = 0; angle <= adjustedMaxAngle; angle++) {
			Vec3 vector = Vec3.createVectorHelper(outerRange, 0, 0);
			vector.rotateAroundY((float) (angle * Math.PI / 180.0 / (adjustedMaxAngle / 360.0))); // Ugh, mutable data classes (also, ugh, radians; it uses degrees in 1.18; took me two hours to debug)
			outerChunks.add(ChunkPos.asLong((int) (getX() + vector.xCoord) >> 4, (int) (getZ() + vector.zCoord) >> 4));
		}
		for (int distance = 0; distance <= outerRange; distance += 8) for (int angle = 0; angle <= adjustedMaxAngle; angle++) {
			Vec3 vector = Vec3.createVectorHelper(distance, 0, 0);
			vector.rotateAroundY((float) (angle * Math.PI / 180.0 / (adjustedMaxAngle / 360.0)));
			long chunkCoord = ChunkPos.asLong((int) (getX() + vector.xCoord) >> 4, (int) (getZ() + vector.zCoord) >> 4);
			if (!outerChunks.contains(chunkCoord)) chunks.add(chunkCoord);
		}

		chunksToProcess.addAll(chunks);
		outerChunksToProcess.addAll(outerChunks);
		Collections.reverse(chunksToProcess); // So it starts nicely from the middle
		Collections.reverse(outerChunksToProcess);
	}

	private void unloadAllChunks() {
		if(loaderTicket != null){
			for(ChunkPos chunk : loadedChunks) {
		        ForgeChunkManager.unforceChunk(loaderTicket, chunk);
		    }
		}
	}

	public void stompAround(){
		if (!chunksToProcess.isEmpty()) {
			long chunkPos = chunksToProcess.remove(chunksToProcess.size() - 1); // Just so it doesn't shift the whole list every time
			int chunkPosX = (int) (chunkPos & Integer.MAX_VALUE);
			int chunkPosZ = (int) (chunkPos >> 32 & Integer.MAX_VALUE);
			for(int x = chunkPosX << 4; x < (chunkPosX << 4) + 16; x++) {
				for(int z = chunkPosZ << 4; z < (chunkPosZ << 4) + 16; z++) {
					stomp(new BlockPos.MutableBlockPos(x, 0, z), Math.hypot(x - getX(), z - getZ()));
				}
			}
			
		} else if (!outerChunksToProcess.isEmpty()) {
			long chunkPos = outerChunksToProcess.remove(outerChunksToProcess.size() - 1);
			int chunkPosX = (int) (chunkPos & Integer.MAX_VALUE);
			int chunkPosZ = (int) (chunkPos >> 32 & Integer.MAX_VALUE);
			for(int x = chunkPosX << 4; x < (chunkPosX << 4) + 16; x++) {
				for(int z = chunkPosZ << 4; z < (chunkPosZ << 4) + 16; z++) {
					double distance = Math.hypot(x - getX(), z - getZ());
					if(distance <= getScale()) {
						stomp(new BlockPos.MutableBlockPos(x, 0, z), distance);
					}
				}
			}
			
		} else {
			remove(RemovalReason.DISCARDED);
		}
	}

	@Override
	public void onUpdate() {

		if(!level().isClientSide) {
			if(!CompatibilityConfig.isWarDim(level())){
				this.remove(RemovalReason.DISCARDED);
			} else if(firstTick) {
				if(chunksToProcess.isEmpty() && outerChunksToProcess.isEmpty()) gatherChunks();
				firstTick = false;
			}
			if(falloutTickNumber >= BombConfig.fChunkSpeed){
				if(!this.isAlive()) {
					long start = System.currentTimeMillis();
					while(!this.isAlive() && System.currentTimeMillis() < start + BombConfig.falloutMS){
						stompAround();
					}
				}
				falloutTickNumber = 0;
			}
			falloutTickNumber++;

			if(this.isAlive()) {
				if(falloutBallRadius > 0){
					EntityFalloutUnderGround falloutBall = new EntityFalloutUnderGround(this.level());
					falloutBall.posX = this.getX();
					falloutBall.posY = this.getY();
					falloutBall.posZ = this.getZ();
					falloutBall.setScale(falloutBallRadius);
					this.level().addFreshEntity(falloutBall);
				}
				unloadAllChunks();
				this.done = true;
				if(RadiationConfig.rain > 0 && doFlood) {
					if((doFallout && getScale() > 100) || (doFlood && getScale() > 50)){
						level().getWorldInfo().setRaining(true);
						level().getWorldInfo().setRainTime(RadiationConfig.rain);
					}
					if((doFallout && getScale() > 150) || (doFlood && getScale() > 100)){
						level().getWorldInfo().setThundering(true);
						level().getWorldInfo().setThunderTime(RadiationConfig.rain);
						AuxSavedData.setThunder(level(), RadiationConfig.rain);
					}
				}
			}
		}
	}

	private void letFall(Level world, BlockPos.MutableBlockPos pos, int lastGapHeight, int contactHeight){
		int fallChance = RadiationConfig.blocksFallCh;
		if(fallChance < 1)
			return;
		if(fallChance < 100){
			int chance = world.random.nextInt(100);
			if(chance < fallChance)
				return;
		}
		
		int bottomHeight = lastGapHeight;
		BlockPos.MutableBlockPos gapPos = new BlockPos.MutableBlockPos(pos.getX(), 0, pos.getZ());
		
		for(int i = lastGapHeight; i <= contactHeight; i++) {
			pos.setY(i);
			Block b = world.getBlockState(pos).getBlock();
			if(!b.isReplaceable(world, pos)){

				float hardness = b.getExplosionResistance();
				if(hardness >= 0 && hardness < 50 && i != bottomHeight){
					gapPos.setY(bottomHeight);
					world.setBlockAndUpdate(gapPos, world.getBlockState(pos));
					world.removeBlock(pos, false);
				}
				bottomHeight++;
			}	
		}
	}

	private int[] doFallout(BlockPos.MutableBlockPos pos, double dist){
		int stoneDepth = 0;
		int maxStoneDepth = 0;

		if(dist > s1)
			maxStoneDepth = 0;
		else if(dist > s2)
			maxStoneDepth = 1;
		else if(dist > s3)
			maxStoneDepth = 2;
		else if(dist > s4)
			maxStoneDepth = 3;
		else if(dist > s5)
			maxStoneDepth = 4;
		else if(dist > s6)
			maxStoneDepth = 5;
		else if(dist <= s6)
			maxStoneDepth = 6;

		boolean lastReachedStone = false;
		boolean reachedStone = false;
		int contactHeight = 420;
		int lastGapHeight = 420;
		boolean gapFound = false;

		BlockState b;
		Block bblock;
		Block bmaterial;
		for(int y = 255; y >= 0; y--) {
			pos.setY(y);
			b = level().getBlockState(pos);
			bblock = b.getBlock();
			bmaterial = b.getBlock();
			lastReachedStone = reachedStone;

			if(bblock != AIR && contactHeight == 420)
				contactHeight = Math.min(y+1, 255);
			
			if(reachedStone && bmaterial != AIR){
				stoneDepth++;
			}
			else{
				reachedStone = b.getBlock() == Blocks.STONE;
			}
			if(reachedStone && stoneDepth > maxStoneDepth){
				break;
			}
			
			if(bmaterial == AIR || bmaterial.isLiquid()){
				if(y < contactHeight){
					gapFound = true;
					lastGapHeight = y;
				}
				continue;
			}

			if(bblock == Blocks.BEDROCK || bblock == ModBlocks.ore_bedrock_oil || bblock == ModBlocks.ore_bedrock_block){
				if(level().isEmptyBlock(pos.above())) level().setBlockAndUpdate(pos.above()
						, ModBlocks.toxic_block.defaultBlockState());
				break;
			}

			if(y == contactHeight-1 && bblock != ModBlocks.fallout && Math.abs(random.nextGaussian() * (dist * dist) / (s0 * s0)) < 0.05 && rand.nextDouble() < 0.05 && ModBlocks.fallout.canPlaceBlockAt(world, pos.up())) {
				placeBlockFromDist(dist, ModBlocks.fallout, pos.above());
			}

			if(spawnFire && dist < s2 && bblock.isFlammable(null, level(), pos, Direction.UP) &&
					level().isEmptyBlock(pos.above())) {
				level().setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
			}

			if(bblock == ModBlocks.waste_leaves){
				if(!(dist > s1 || (dist > fallingRadius && (level().random.nextFloat() < (-5F*(fallingRadius/dist)+5F))))){
					level().removeBlock(pos, false);
				}
				continue;
			}

			if(bblock instanceof LeavesBlock) {
				if(dist > s1 || (dist > fallingRadius && (level().random.nextFloat() < (-5F*(fallingRadius/dist)+5F)))){
					level().setBlockAndUpdate(pos, ModBlocks.waste_leaves.defaultBlockState());
				} else {
					level().removeBlock(pos, false);
				}
				continue;
			}

			if(bblock == Blocks.BROWN_MUSHROOM || bblock == Blocks.RED_MUSHROOM){
				if(dist < s0)
					level().setBlockAndUpdate(pos, ModBlocks.mush.defaultBlockState());
				continue;
			}

			// if(b.getBlock() == Blocks.WATER) {
			// 	world.setBlockState(pos, ModBlocks.radwater_block.getDefaultState());
			// }

			if(bblock instanceof BlockOre && reachedStone && !lastReachedStone && dist < s1){
				level().setBlockAndUpdate(pos, ModBlocks.toxic_block.defaultBlockState());
				continue;
			}

			else if(bblock instanceof BlockStone || bblock == Blocks.COBBLESTONE) {
				double ranDist = dist * (1D + level().random.nextDouble()*0.1D);
				if(ranDist > s1 || stoneDepth==maxStoneDepth)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_slaked.getStateFromMeta(level().random.nextInt(4)));
				else if(ranDist > s2 || stoneDepth==maxStoneDepth-1)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_0.getStateFromMeta(level().random.nextInt(4)));
				else if(ranDist > s3 || stoneDepth==maxStoneDepth-2)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_1.getStateFromMeta(level().random.nextInt(4)));
				else if(ranDist > s4 || stoneDepth==maxStoneDepth-3)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_2.getStateFromMeta(level().random.nextInt(4)));
				else if(ranDist > s5 || stoneDepth==maxStoneDepth-4)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_3.getStateFromMeta(level().random.nextInt(4)));
				else if(ranDist > s6 || stoneDepth==maxStoneDepth-5)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_4.getStateFromMeta(level().random.nextInt(4)));
				else if(ranDist <= s6 || stoneDepth==maxStoneDepth-6)
					level().setBlockAndUpdate(pos, ModBlocks.sellafield_core.getStateFromMeta(level().random.nextInt(4)));
				else
					break;
				continue;

			} else if(bblock instanceof GrassBlock) {
				placeBlockFromDist(dist, ModBlocks.waste_earth, pos);
				continue;

			} else if(bblock instanceof GravelBlock) {
				placeBlockFromDist(dist, ModBlocks.waste_gravel, pos);
				continue;

			} else if(bblock instanceof BlockDirt) {
				BlockDirt.DirtType meta = b.getValue(BlockDirt.VARIANT);
				if(meta == BlockDirt.DirtType.DIRT)
					placeBlockFromDist(dist, ModBlocks.waste_dirt, pos);
				else if(meta == BlockDirt.DirtType.COARSE_DIRT)
					placeBlockFromDist(dist, ModBlocks.waste_gravel, pos);
				else if(meta == BlockDirt.DirtType.PODZOL)
					placeBlockFromDist(dist, ModBlocks.waste_mycelium, pos);
				continue;
			} else if(bblock == Blocks.FARMLAND) {
				placeBlockFromDist(dist, ModBlocks.waste_dirt, pos);
				continue;
			} else if(bblock instanceof BlockSnow) {
				placeBlockFromDist(dist, ModBlocks.waste_snow, pos);
				continue;

			} else if(bblock instanceof BlockSnowBlock) {
				placeBlockFromDist(dist, ModBlocks.waste_snow_block, pos);
				continue;

			} else if(bblock instanceof BlockIce) {
				level().setBlockAndUpdate(pos, ModBlocks.waste_ice.defaultBlockState());
				continue;

			} else if(bblock instanceof BushBlock) {
				if(level().getBlockState(pos.below()).getBlock() == Blocks.FARMLAND){
					placeBlockFromDist(dist, ModBlocks.waste_dirt, pos.below());
					placeBlockFromDist(dist, ModBlocks.waste_grass_tall, pos);
				} else if(level().getBlockState(pos.below()).getBlock() instanceof BlockGrass){
					placeBlockFromDist(dist, ModBlocks.waste_earth, pos.below());
					placeBlockFromDist(dist, ModBlocks.waste_grass_tall, pos);
				} else if(level().getBlockState(pos.below()).getBlock() == Blocks.MYCELIUM){
					placeBlockFromDist(dist, ModBlocks.waste_mycelium, pos.below());
					level().setBlockAndUpdate(pos, ModBlocks.mush.defaultBlockState());
				}
				continue;

			} else if(bblock == Blocks.MYCELIUM) {
				placeBlockFromDist(dist, ModBlocks.waste_mycelium, pos);
				continue;

			} else if(bblock == Blocks.SANDSTONE) {
				placeBlockFromDist(dist, ModBlocks.waste_sandstone, pos);
				continue;
			} else if(bblock == Blocks.RED_SANDSTONE) {
				placeBlockFromDist(dist, ModBlocks.waste_sandstone_red, pos);
				continue;
			} else if(bblock == Blocks.CLAY|| bblock == Blocks.STAINED_HARDENED_CLAY) {
				placeBlockFromDist(dist, ModBlocks.waste_terracotta, pos);
				continue;
			} else if(bblock instanceof BlockSand) {
				BlockSand.EnumType meta = b.getValue(BlockSand.VARIANT);
				if(random.nextInt(60) == 0) {
					placeBlockFromDist(dist, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_trinitite : ModBlocks.waste_trinitite_red, pos);
				} else {
					placeBlockFromDist(dist, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_sand : ModBlocks.waste_sand_red, pos);
				}
				continue;
			}

			else if(bblock == Blocks.CLAY) {
				level().setBlockAndUpdate(pos, Blocks.CLAY.defaultBlockState());
				continue;
			}

			else if(bblock == Blocks.MOSSY_COBBLESTONE) {
				level().setBlockAndUpdate(pos, Blocks.COAL_ORE.defaultBlockState());
				continue;
			}

			else if(bblock == Blocks.COAL_ORE) {
				if(dist < s5){
					int ra = random.nextInt(150);
					if(ra < 7) {
						level().setBlockAndUpdate(pos, Blocks.DIAMOND_ORE.defaultBlockState());
					} else if(ra < 10) {
						level().setBlockAndUpdate(pos, Blocks.EMERALD_ORE.defaultBlockState());
					}
				}
				continue;
			}

			else if(bblock == Blocks.BROWN_MUSHROOM_BLOCK || bblock == Blocks.RED_MUSHROOM_BLOCK) {
				if(dist < s0){
					BlockHugeMushroom.EnumType meta = b.getValue(BlockHugeMushroom.VARIANT);
					if(meta == BlockHugeMushroom.EnumType.STEM) {
						level().setBlockAndUpdate(pos, ModBlocks.mush_block_stem.defaultBlockState());
					} else {
						level().setBlockAndUpdate(pos, ModBlocks.mush_block.defaultBlockState());
					}
				}
				continue;
			}

			else if(bblock instanceof BlockLog) {
				if(dist < s0)
					level().setBlockAndUpdate(pos, ((WasteLog)ModBlocks.waste_log).getSameRotationState(b));
				continue;
			}

			else if(bmaterial == Material.WOOD && bblock != ModBlocks.waste_log &&
					bblock != ModBlocks.waste_planks) {
				if(dist < s0)
					level().setBlockAndUpdate(pos, ModBlocks.waste_planks.defaultBlockState());
				continue;
			}
			else if(b.getBlock() == ModBlocks.sellafield_4) {
				level().setBlockAndUpdate(pos, ModBlocks.sellafield_core
						.getStateFromMeta(level().random.nextInt(4)));
				continue;
			}
			else if(b.getBlock() == ModBlocks.sellafield_3) {
				level().setBlockAndUpdate(pos, ModBlocks.sellafield_4
						.getStateFromMeta(level().random.nextInt(4)));
				continue;
			}
			else if(b.getBlock() == ModBlocks.sellafield_2) {
				level().setBlockAndUpdate(pos, ModBlocks.sellafield_3
						.getStateFromMeta(level().random.nextInt(4)));
				continue;
			}
			else if(b.getBlock() == ModBlocks.sellafield_1) {
				level().setBlockAndUpdate(pos, ModBlocks.sellafield_2
						.getStateFromMeta(level().random.nextInt(4)));
				continue;
			}
			else if(b.getBlock() == ModBlocks.sellafield_0) {
				level().setBlockAndUpdate(pos, ModBlocks.sellafield_1
						.getStateFromMeta(level().random.nextInt(4)));
				continue;
			}
			else if(b.getBlock() == ModBlocks.sellafield_slaked) {
				level().setBlockAndUpdate(pos, ModBlocks.sellafield_0
						.getStateFromMeta(level().random.nextInt(4)));
				continue;
			}
			else if(b.getBlock() == Blocks.VINE) {
				level().removeBlock(pos, false);
				continue;
			}
			else if(bblock == ModBlocks.ore_uranium) {
				if(dist <= s6){
					if (random.nextInt(VersatileConfig.getSchrabOreChance()) == 0)
						level().setBlockAndUpdate(pos, ModBlocks.ore_schrabidium.defaultBlockState());
					else
						level().setBlockAndUpdate(pos, ModBlocks.ore_uranium_scorched.defaultBlockState());
				}
				break;
			}

			else if(bblock == ModBlocks.ore_nether_uranium) {
				if(dist <= s5){
					if(random.nextInt(VersatileConfig.getSchrabOreChance()) == 0)
						level().setBlockAndUpdate(pos, ModBlocks.ore_nether_schrabidium.defaultBlockState());
					else
						level().setBlockAndUpdate(pos, ModBlocks.ore_nether_uranium_scorched.defaultBlockState());
				}
				break;

			}

			else if(bblock == ModBlocks.ore_gneiss_uranium) {
				if(dist <= s4){
					if(random.nextInt(VersatileConfig.getSchrabOreChance()) == 0)
						level().setBlockAndUpdate(pos, ModBlocks.ore_gneiss_schrabidium.defaultBlockState());
					else
						level().setBlockAndUpdate(pos, ModBlocks.ore_gneiss_uranium_scorched.defaultBlockState());
				}
				break;
				// this piece stops the "stomp" from reaching below ground
			}
			else if(bblock == ModBlocks.brick_concrete) {
				if(random.nextInt(80) == 0)
					level().setBlockAndUpdate(pos, ModBlocks.brick_concrete_broken.defaultBlockState());
				break;
				// this piece stops the "stomp" from reaching below ground
			} 
			else if(bblock.getExplosionResistance() > 300){
				break;
			}
		}
		return new int[]{gapFound ? 1 : 0, lastGapHeight, contactHeight};
	}

	private int[] doNoFallout(BlockPos.MutableBlockPos pos, double dist){
		int stoneDepth = 0;
		int maxStoneDepth = 6;

		boolean lastReachedStone = false;
		boolean reachedStone = false;
		int contactHeight = 420;
		int lastGapHeight = 420;
		boolean gapFound = false;
		for(int y = 255; y >= 0; y--) {
			pos.setY(y);
			BlockState b = level().getBlockState(pos);
			Block bblock = b.getBlock();
			Block bmaterial = b.getBlock();
			lastReachedStone = reachedStone;

			if(bblock.isCollidable() && contactHeight == 420)
				contactHeight = Math.min(y+1, 255);
			
			if(reachedStone && bmaterial != AIR){
				stoneDepth++;
			}
			else{
				reachedStone = b.getBlock() == STONE;
			}
			if(reachedStone && stoneDepth > maxStoneDepth){
				break;
			}
			
			if(bmaterial == AIR || bmaterial.isLiquid()){
				if(y < contactHeight){
					gapFound = true;
					lastGapHeight = y;
				}
			}
		}
		return new int[]{gapFound ? 1 : 0, lastGapHeight, contactHeight};
	}

	public void placeBlockFromDist(double dist, Block b, BlockPos pos){
		double ranDist = dist * (1D + level().random.nextDouble()*0.2D);
		if(ranDist > s1)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(0));
		else if(ranDist > s2)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(1));
		else if(ranDist > s3)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(2));
		else if(ranDist > s4)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(3));
		else if(ranDist > s5)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(4));
		else if(ranDist > s6)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(5));
		else if(ranDist <= s6)
			level().setBlockAndUpdate(pos, b.getStateFromMeta(6));
	}

	private void flood(BlockPos.MutableBlockPos pos){
		if(CompatibilityConfig.doFillCraterWithWater && waterLevel > 1){
			for(int y = waterLevel-1; y > 1; y--) {
				pos.setY(y);
				if(level().isEmptyBlock(pos) || level().getBlockState(pos)
						.getBlock() == Blocks.WATER){
					level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
				}
			}
		}
	}

	private void drain(BlockPos.MutableBlockPos pos){
		for(int y = 255; y > 1; y--) {
			pos.setY(y);
			if(!level().isEmptyBlock(pos) && (level().getBlockState(pos).getBlock() == Blocks.WATER ||
					level().getBlockState(pos).getBlock() == Blocks.WATER)){
				level().removeBlock(pos, false);
			}
		}
	}

	private void stomp(BlockPos.MutableBlockPos pos, double dist) {
		if(dist > s0){
			if(level().random.nextFloat() > 0.05F+(5F*(s0/dist)-4F)){
				return;
			}
		}
		int[] gapData = null;
		if(doFallout)
			gapData = doFallout(pos, dist);
		else
			gapData = doNoFallout(pos, dist);

		if(dist < fallingRadius){
			if(doDrop && gapData != null && gapData[0] == 1)
				letFall(level(), pos, gapData[1], gapData[2]);
			if(doFlood)
				flood(pos);
			else
				drain(pos);
		}
	}

	

	@Override
	protected void readEntityFromNBT(CompoundTag nbt) {
		setScale(nbt.getInt("scale"), nbt.getInt("dropRadius"));
		falloutBallRadius = nbt.getInt("fBall");
		if(nbt.contains("chunks"))
			chunksToProcess.addAll(readChunksFromIntArray(nbt.getIntArray("chunks")));
		if(nbt.contains("outerChunks"))
			outerChunksToProcess.addAll(readChunksFromIntArray(nbt.getIntArray("outerChunks")));
		doFallout = nbt.getBoolean("doFallout");
		doFlood = nbt.getBoolean("doFlood");
	}

	private Collection<Long> readChunksFromIntArray(int[] data) {
		List<Long> coords = new ArrayList<>();
		boolean firstPart = true;
		int x = 0;
		for (int coord : data) {
			if (firstPart)
				x = coord;
			else
				coords.add(ChunkPos.asLong(x, coord));
			firstPart = !firstPart;
		}
		return coords;
	}

//	@Override
	protected void writeEntityToNBT(CompoundTag nbt) {
		nbt.putInt("scale", getScale());
		nbt.putInt("fBall", falloutBallRadius);
		nbt.putInt("dropRadius", fallingRadius);
		nbt.putBoolean("doFallout", doFallout);
		nbt.putBoolean("doFlood", doFlood);

		nbt.putIntArray("chunks", writeChunksToIntArray(chunksToProcess));
		nbt.putIntArray("outerChunks", writeChunksToIntArray(outerChunksToProcess));
	}

	private int[] writeChunksToIntArray(List<Long> coords) {
		int[] data = new int[coords.size() * 2];
		for (int i = 0; i < coords.size(); i++) {
			data[i * 2] = (int) (coords.get(i) & Integer.MAX_VALUE);
			data[i * 2 + 1] = (int) (coords.get(i) >> 32 & Integer.MAX_VALUE);
		}
		return data;
	}

	public void setScale(int i, int craterRadius) {
		this.dataManager.set(SCALE, Integer.valueOf(i));
		this.s0 = 0.8D * i;
		this.s1 = 0.65D * i;
		this.s2 = 0.5D * i;
		this.s3 = 0.4D * i;
		this.s4 = 0.3D * i;
		this.s5 = 0.2D * i;
		this.s6 = 0.1D * i;
		this.fallingRadius = craterRadius;
		this.doDrop = this.fallingRadius > 20;
	}

	public int getScale() {

		int scale = this.dataManager.get(SCALE);

		return scale == 0 ? 1 : scale;
	}
}
