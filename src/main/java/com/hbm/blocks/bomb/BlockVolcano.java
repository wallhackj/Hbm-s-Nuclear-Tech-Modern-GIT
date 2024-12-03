package com.hbm.blocks.bomb;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.entity.projectile.EntityShrapnel;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class BlockVolcano extends Block {

	public static final PropertyInteger META = BlockDummyable.META;
	
	public BlockVolcano(String s) {
		super(Material.IRON);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items){
		if(tab == CreativeTabs.SEARCH || tab == this.getCreativeTabToDisplayOn())
			for(int i = 0; i < 4; ++i) {
				items.add(new ItemStack(this, 1, i));
			}
	}
	
	@Override
	public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced){
		int meta = stack.getDamageValue();

		tooltip.add(BlockVolcano.isGrowing(meta) ? (TextFormatting.RED + "DOES GROW") :
				(TextFormatting.DARK_GRAY + "DOES NOT GROW"));
		tooltip.add(BlockVolcano.isExtinguishing(meta) ? (TextFormatting.RED + "DOES EXTINGUISH") :
				(TextFormatting.DARK_GRAY + "DOES NOT EXTINGUISH"));
	}
	
	@Override
	public int tickRate(Level world) {
		return 5;
	}

	@Override
	public void onBlockAdded(Level world, BlockPos pos, BlockState state){
		if(!world.isClientSide)
			world.scheduleUpdate(pos, this, this.tickRate(world));
	}
	
	@Override
	public void updateTick(Level world, BlockPos pos, BlockState state, Random rand){
		if(!world.isClientSide) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			
			int meta = world.getBlockState(pos).getValue(META);
			blastMagmaChannel(world, x, y, z, rand);
			raiseMagma(world, x, y, z, rand);
			spawnBlobs(world, x, y, z, rand);
			spawnSmoke(world, x, y, z, rand);
			
			updateVolcano(world, x, y, z, rand, meta);
		}
	}

	private void blastMagmaChannel(Level world, int x, int y, int z, Random rand) {
		List<ExAttrib> attribs = Arrays.asList(new ExAttrib[] {ExAttrib.NODROP, ExAttrib.LAVA_V, ExAttrib.NOSOUND, ExAttrib.ALLMOD, ExAttrib.NOHURT});
		
		ExplosionNT explosion = new ExplosionNT(world, null, x + 0.5, y + rand.nextInt(15) + 1.5, z + 0.5, 7);
		explosion.addAllAttrib(attribs);
		explosion.explode();
		
		ExplosionNT explosion2 = new ExplosionNT(world, null, x + 0.5 + rand.nextGaussian() * 3, rand.nextInt(y + 1), z + 0.5 + rand.nextGaussian() * 3, 10);
		explosion2.addAllAttrib(attribs);
		explosion2.explode();
	}
	
	private void raiseMagma(Level world, int x, int y, int z, Random rand) {
		int rX = x - 10 + rand.nextInt(21);
		int rY = y + rand.nextInt(11);
		int rZ = z - 10 + rand.nextInt(21);
		BlockPos pos = new BlockPos(rX, rY, rZ);
		
		if(world.getBlockState(pos).getBlock() == Blocks.AIR &&
				world.getBlockState(pos.down()).getBlock() == ModBlocks.volcanic_lava_block)
			world.setBlockAndUpdate(pos, ModBlocks.volcanic_lava_block.defaultBlockState());
	}
	
	private void spawnBlobs(Level world, int x, int y, int z, Random rand) {
		
		for(int i = 0; i < 3; i++) {
			EntityShrapnel frag = new EntityShrapnel(world);
			frag.setLocationAndAngles(x + 0.5, y + 1.5, z + 0.5, 0.0F, 0.0F);
			frag.motionY = 1D + rand.nextDouble();
			frag.motionX = rand.nextGaussian() * 0.2D;
			frag.motionZ = rand.nextGaussian() * 0.2D;
			frag.setVolcano(true);
			world.spawnEntity(frag);
		}
	}
	
	/*
	 * I SEE SMOKE, AND WHERE THERE'S SMOKE THERE'S FIRE!
	 */
	private void spawnSmoke(Level world, int x, int y, int z, Random rand) {
		CompoundTag dPart = new CompoundTag();
		dPart.putString("type", "vanillaExt");
		dPart.putString("mode", "volcano");
		PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(dPart, x + 0.5, y + 10, z + 0.5),
				new TargetPoint(world.provider.getDimension(), x + 0.5, y + 10, z + 0.5, 250));
	}
	
	private void updateVolcano(Level world, int x, int y, int z, Random rand, int meta) {
		BlockPos pos = new BlockPos(x, y, z);
		if(rand.nextDouble() < this.getProgressChance(world, x, y, z, rand, meta)) {
			
			//if there's progress, check if the volcano can grow or not
			if(shouldGrow(world, x, y, z, rand, meta)) {
				
				//raise the level for growing volcanos, spawn lava, schedule update at the new position
				y++;
				world.scheduleUpdate(pos, this, this.tickRate(world));
				
				for(int i = -1; i <= 1; i++) {
					for(int j = -1; j <= 1; j++) {
						for(int k = -1; k <= 1; k++) {
							
							if(i + j + k == 0) {
								world.setBlockAndUpdate(pos, this.defaultBlockState().withProperty(META, meta), 3);
							} else {
								world.setBlockAndUpdate(pos.offset(i, j, k),
										ModBlocks.volcanic_lava_block.defaultBlockState());
							}
						}
					}
				}
				
			//a progressing volcano that can't grow will extinguish
			} else if(isExtinguishing(meta)) {
				world.setBlockAndUpdate(pos, ModBlocks.volcanic_lava_block.defaultBlockState());
			}
			
		//if there's no progress, schedule an update on the current position
		}
		
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	public static final int META_STATIC_ACTIVE = 0;
	public static final int META_STATIC_EXTINGUISHING = 1;
	public static final int META_GROWING_ACTIVE = 2;
	public static final int META_GROWING_EXTINGUISHING = 3;
	
	public static boolean isGrowing(int meta) {
		return meta == META_GROWING_ACTIVE || meta == META_GROWING_EXTINGUISHING;
	}
	
	public static boolean isExtinguishing(int meta) {
		return meta == META_STATIC_EXTINGUISHING || meta == META_GROWING_EXTINGUISHING;
	}
	
	private boolean shouldGrow(Level world, int x, int y, int z, Random rand, int meta) {
		
		//non-growing volcanoes should extinguish
		if(!isGrowing(meta))
			return false;
		
		//growing volcanoes extinguish when exceeding 200 blocks
		return y < 200;
	}
	
	private double getProgressChance(Level world, int x, int y, int z, Random rand, int meta) {

		if(meta == META_STATIC_EXTINGUISHING)
			return 0.00003D; //about once every hour
		
		if(isGrowing(meta)) {
			
			if(meta != META_GROWING_ACTIVE || y < 199)
				return 0.007D; //about 250x an hour
		}
		
		return 0;
	}
	
//	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, META);
	}
	
//	@Override
	public int getMetaFromState(BlockState state){
		return state.getValue(META);
	}
	
//	@Override
	public BlockState getStateFromMeta(int meta){
		return this.defaultBlockState().withProperty(META, meta);
	}
}