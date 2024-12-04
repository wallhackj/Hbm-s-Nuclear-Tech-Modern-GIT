package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNT;
import com.hbm.interfaces.IBomb;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;


public class BlockSemtex extends Block implements IBomb {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing");

	public BlockSemtex(Material mat, String s) {
		super(BlockBehaviour.Properties.of().strength(5.0F)
				.sound(SoundType.STONE));
//		this.setRegistryName(registryName);
		ModBlocks.ALL_BLOCKS.add(this);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH));
	}
	
//	@Override
	public Block setSoundType(SoundType sound){
		super.properties.sound(sound);
		return this;
	}

//	@Override
	public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state,
								LivingEntity placer, ItemStack stack){
		Direction facing = Direction.orderedByNearest(placer)[0].getOpposite();
		worldIn.setBlock(pos, state.setValue(FACING, facing ) ,2);
	}
	

//	@Override
	public void onBlockDestroyedByExplosion(Level worldIn, BlockPos pos, Explosion explosionIn){
		this.explode(worldIn, pos);
	}
	
//	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.hasNeighborSignal(pos)){
			this.explode(worldIn, pos);
		}
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		if(!world.isClientSide) {
			new ExplosionNT(world, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					50).overrideResolution(64).explode();
			ExplosionLarge.spawnParticles(world, pos.getX(), pos.getY(), pos.getZ(),
					ExplosionLarge.cloudFunction(15));
		}
	}
	
//	@Override
	protected BlockState createBlockState() {
//		return new BlockStateContainer(this, new IProperty[]{FACING});
		return this.defaultBlockState().setValue(FACING, Direction.NORTH);
	}
	
//	@Override
	public int getMetaFromState(BlockState state) {
//		return ((Direction)state.getValue(FACING)).getIndex();
		return state.getValue(FACING).get3DDataValue();
	}
	
//	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction enumfacing = Direction.from3DDataValue(meta);
        return this.defaultBlockState().setValue(FACING, enumfacing);
	}
	
//	@Override
	public BlockState withRotation(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
//	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn)
	{
	   return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}