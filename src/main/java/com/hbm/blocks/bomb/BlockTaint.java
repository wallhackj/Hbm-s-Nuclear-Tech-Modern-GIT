package com.hbm.blocks.bomb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.mob.EntityTaintedCreeper;
import com.hbm.main.MainRegistry;
import com.hbm.potion.HbmPotion;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockTaint extends Block {
	public static final IntegerProperty TEXTURE = IntegerProperty.create("taintage", 0, 15);
	
	public BlockTaint(Material m, String s) {
//		super(m);
//		this.setTickRandomly(true);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.setCreativeTab(MainRegistry.controlTab);
//		this.setDefaultState(this.blockState.getBaseState().withProperty(TEXTURE, 0));
		super(BlockBehaviour.Properties.of().randomTicks());
		this.registerDefaultState(this.defaultBlockState().setValue(TEXTURE, 0));
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
//	@Override
	public void updateTick(Level world, BlockPos pos1, BlockState state, Random rand) {
		int meta = state.getValue(TEXTURE);
    	if(!world.isClientSide && meta < 15) {
    		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
	    	for(int i = 0; i < 15; i++) {
	    		int a = rand.nextInt(11) + pos1.getX() - 5;
	    		int b = rand.nextInt(11) + pos1.getY() - 5;
	    		int c = rand.nextInt(11) + pos1.getZ() - 5;
	    		pos.offset(a, b, c);
	            if(world.getBlockState(pos).canBeReplaced() && !world.getBlockState(pos).liquid() &&
						BlockTaint.hasPosNeightbour(world, pos))
	            	world.setBlock(pos, ModBlocks.taint.defaultBlockState()
							.setValue(TEXTURE, meta + 1), 2);
	    	}
	            
		    for(int i = 0; i < 85; i++) {
		    	int a = rand.nextInt(7) + pos1.getX() - 3;
		    	int b = rand.nextInt(7) + pos1.getY() - 3;
		    	int c = rand.nextInt(7) + pos1.getZ() - 3;
		    	pos.offset(a, b, c);
	           	if(world.getBlockState(pos).canBeReplaced() && !world.getBlockState(pos).liquid() &&
						BlockTaint.hasPosNeightbour(world, pos))
		           	world.setBlock(pos, ModBlocks.taint
							.defaultBlockState().setValue(TEXTURE, meta + 1), 2);
		    }
    	}
	}

	private static boolean checkAttachment(Level world, BlockPos pos){
		if(!world.isEmptyBlock(pos)){
            return world.getBlockState(pos).getBlock() != ModBlocks.taint;
    	}
    	return false;
    }


	//Drillgon200: Ah yes, spelling.
	 public static boolean hasPosNeightbour(Level world, BlockPos pos) {
	    	return checkAttachment(world, pos.offset(1, 0, 0)) ||
					checkAttachment(world, pos.offset(0, 1, 0)) ||
					checkAttachment(world, pos.offset(0, 0, 1)) ||
					checkAttachment(world, pos.offset(-1, 0, 0)) ||
					checkAttachment(world, pos.offset(0, -1, 0)) ||
					checkAttachment(world, pos.offset(0, 0, -1));
	}

//	@Override
	public AABB getSelectedBoundingBox(BlockState state, Level worldIn, BlockPos pos) {
		return new AABB(pos, pos);
	}

//	@Override
	public VoxelShape getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
		return Shapes.empty();
	}

//	@Override
	public boolean isCollidable(){
		return true;
	}

//	@Override
	public boolean isReplaceable(BlockGetter worldIn, BlockPos pos){
		return false;
	}


//	@Override
	public void onEntityCollidedWithBlock(Level world, BlockPos pos, BlockState state, Entity entity) {
		int meta = state.getValue(TEXTURE);
		int levelEffect = 15 - meta;
		
    	List<ItemStack> list = new ArrayList<ItemStack>();
		MobEffectInstance effect = new MobEffectInstance(HbmPotion.taint, 15 * 20, levelEffect);
    	effect.setCurativeItems(list);
    	
    	if(entity instanceof LivingEntity) {
    		if(world.random.nextInt(50) == 0) {
    			((LivingEntity)entity).addEffect(effect);
    		}
    	}
    	
    	if(entity instanceof Creeper creeper) {
    		EntityTaintedCreeper taintedCreeper = new EntityTaintedCreeper(world);
			taintedCreeper.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());

    		if(!world.isClientSide) {
    			entity.remove(Entity.RemovalReason.DISCARDED);
    			world.addFreshEntity(taintedCreeper);
    		}
    	}
	}
	
//	@Override
	public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
		tooltip.add("DO NOT TOUCH, BREATHE OR STARE AT. RUN!");
	}

//	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, null, null);
	}
	
//	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(TEXTURE);
	}

//	@Override
	public BlockState getStateFromMeta(int meta) {
		return this.defaultBlockState().setValue(TEXTURE, meta);
	}
	
//	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!BlockTaint.hasPosNeightbour(world, pos) && !world.isClientSide)
			world.removeBlock(pos, false);
	}
	
//	@Override
	public boolean causesSuffocation(BlockState state) {
		return true;
	}
}
