package com.hbm.blocks.generic;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.IItemHazard;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.hbm.modules.ItemHazardModule;
import com.hbm.saveddata.RadiationSavedData;
import com.hbm.util.ContaminationUtil;
import com.hbm.potion.HbmPotion;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;


public class BlockHazard extends Block implements IItemHazard {
	
	ItemHazardModule module;
	
	private float radIn = 0.0F;
	private float radMax = 0.0F;
	private float rad3d = 0.0F;
	private ExtDisplayEffect extEffect = null;
	
	private boolean beaconable = false;

	
	
	public BlockHazard(Material mat, String s) {
		super(null);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.module = new ItemHazardModule();
//
//		ModBlocks.ALL_BLOCKS.add(this);
	}
	// was Iron
	public BlockHazard(String s) {
		this((Material) null, s);
	}

	public BlockHazard(Material mat, SoundType type, String s) {
		this(mat, s);
		setSoundType(type);
	}
	// was Iron
	public BlockHazard(SoundType type, String s) {
		this((Material) null, s);
		setSoundType(type);
	}
	
	public BlockHazard setDisplayEffect(ExtDisplayEffect extEffect) {
		this.extEffect = extEffect;
		return this;
	}

//	@Override
	public Properties setSoundType(SoundType sound) {
		return BlockBehaviour.Properties.of().sound(sound);
	}

//	@Override
//	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand){
		super.randomDisplayTick(stateIn, worldIn, pos, rand);

		if(extEffect == null)
			return;
		
		switch(extEffect) {
		case RADFOG:
		case SCHRAB:
		case FLAMES:
			sPart(worldIn, pos.getX(), pos.getY(), pos.getZ(), rand);
			break;
			
		case SPARKS:
			break;
			
		case LAVAPOP:
			worldIn.spawnParticle(ParticleTypes.LAVA, pos.getX() + rand.nextFloat(), pos.getY() + 1.1F, pos.getZ() + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			break;
			
		default: break;
		}
	}
	
	private void sPart(Level world, int x, int y, int z, Random rand) {

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

			if(dir == ForgeDirection.DOWN && this.extEffect == ExtDisplayEffect.FLAMES)
				continue;

			if(world.getBlockState(new BlockPos(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)).getMaterial() == Material.AIR) {

				double ix = x + 0.5F + dir.offsetX + rand.nextDouble() * 3 - 1.5D;
				double iy = y + 0.5F + dir.offsetY + rand.nextDouble() * 3 - 1.5D;
				double iz = z + 0.5F + dir.offsetZ + rand.nextDouble() * 3 - 1.5D;

				if(dir.offsetX != 0)
					ix = x + 0.5F + dir.offsetX * 0.5 + rand.nextDouble() * dir.offsetX;
				if(dir.offsetY != 0)
					iy = y + 0.5F + dir.offsetY * 0.5 + rand.nextDouble() * dir.offsetY;
				if(dir.offsetZ != 0)
					iz = z + 0.5F + dir.offsetZ * 0.5 + rand.nextDouble() * dir.offsetZ;

				if(this.extEffect == ExtDisplayEffect.RADFOG) {
					world.spawnParticle(ParticleTypes.TOTEM_OF_UNDYING, ix, iy, iz, 0.0, 0.0, 0.0);
				}
				if(this.extEffect == ExtDisplayEffect.SCHRAB) {
					NBTTagCompound data = new NBTTagCompound();
					data.setString("type", "schrabfog");
					data.setDouble("posX", ix);
					data.setDouble("posY", iy);
					data.setDouble("posZ", iz);
					MainRegistry.proxy.effectNT(data);
				}
				if(this.extEffect == ExtDisplayEffect.FLAMES) {
					world.spawnParticle(ParticleTypes.FLAME, ix, iy, iz, 0.0, 0.0, 0.0);
					world.spawnParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.0, 0.0);
					world.spawnParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.1, 0.0);
				}
			}
		}
	}

	@Override
	public ItemHazardModule getModule() {
		return module;
	}

	@Override
	public IItemHazard addRadiation(float radiation) {
		this.getModule().addRadiation(radiation);
		this.radIn = radiation * 0.1F;
		this.radMax = radiation;
		return this;
	}

	public BlockHazard makeBeaconable() {
		this.beaconable = true;
		return this;
	}

	public BlockHazard addRad3d(int rad3d) {
		this.rad3d = rad3d;
		return this;
	}

//	@Override
	public boolean isBeaconBase(BlockGetter worldObj, BlockPos pos, BlockPos beacon){
		return beaconable;
	}
	
//	@Override
	public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand){

		if(this.rad3d > 0){
			ContaminationUtil.radiate(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32, this.rad3d, 0, this.module.fire * 5000, 0, 0);
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
		}
		if(this.equals(ModBlocks.block_meteor_molten)) {
        	if(!worldIn.isClientSide)
        		worldIn.setBlockAndUpdate(pos, ModBlocks.block_meteor_cobble);
        	worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);
        	return;
        }
		if(this.radIn > 0) {
			RadiationSavedData.incrementRad(worldIn, pos, radIn, radIn*10F);
		}
	}

	
//	@Override
	public int tickRate(Level world) {
		if(this.rad3d > 0)
			return 20;
		if(this.radIn > 0)
			return 60+world.rand.nextInt(500);
		return super.tickRate(world);
	}

//	@Override
	public void onBlockAdded(Level worldIn, BlockPos pos, BlockState state){
		super.onBlockAdded(worldIn, pos, state);
		if(this.radIn > 0 || this.rad3d > 0){
			this.setTickRandomly(true);
			worldIn.setBlockAndUpdate(pos, this.defaultBlockState());
		}
	}

//	@Override
	public void onBlockDestroyedByPlayer(Level world, BlockPos pos, BlockState state) {
		if(this.equals(ModBlocks.block_meteor_molten)) {
        	if(!world.isClientSide)
        		world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        }
	}
	
	public static enum ExtDisplayEffect {
		RADFOG,
		SPARKS,
		SCHRAB,
		FLAMES,
		LAVAPOP
	}

//	@Override
	public void onEntityWalk(Level worldIn, BlockPos pos, Entity entity) {
		if(entity instanceof LivingEntity)
			this.module.applyEffects((LivingEntity)entity, 0.5F, 0, false, InteractionHand.MAIN_HAND);

		
    	if (entity instanceof LivingEntity && this.equals(ModBlocks.brick_jungle_mystic))
    	{
    		((LivingEntity) entity).addPotionEffect(new PotionEffect(HbmPotion.taint, 15 * 20, 2));
    		return;
    	}
	}

//	@Override
	public void onEntityCollidedWithBlock(Level worldIn, BlockPos pos, BlockState state, Entity entity){
		if(entity instanceof LivingEntity)
			this.module.applyEffects((LivingEntity)entity, 0.5F, 0, false, InteractionHand.MAIN_HAND);

		
    	if (entity instanceof LivingEntity && this.equals(ModBlocks.brick_jungle_mystic))
    	{
    		((LivingEntity) entity).addPotionEffect(new PotionEffect(HbmPotion.taint, 15 * 20, 2));
    		return;
    	}
	}

//	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		if(this.equals(ModBlocks.frozen_planks))
		{
			return Items.SNOWBALL;
		}
		if(this.equals(ModBlocks.frozen_dirt))
		{
			return Items.SNOWBALL;
		}
		return Item.getItemFromBlock(this);
	}
}