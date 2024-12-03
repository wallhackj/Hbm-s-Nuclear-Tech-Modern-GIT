package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNT;
import com.hbm.interfaces.IBomb;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;


public class BlockSemtex extends Block implements IBomb {
	
	public static final PropertyDirection FACING = BlockDirectional.FACING;
	
	public BlockSemtex(Material mat, String s) {
		super(mat);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
	@Override
	public Block setSoundType(SoundType sound){
		return super.setSoundType(sound);
	}

	@Override
	public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, EntityLivingBase placer, ItemStack stack){
		worldIn.setBlockAndUpdate(pos, state.withProperty(FACING,
				Direction.getDirectionFromEntityLiving(pos, placer).getOpposite()), 2);
	}
	

	@Override
	public void onBlockDestroyedByExplosion(Level worldIn, BlockPos pos, Explosion explosionIn){
		this.explode(worldIn, pos);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockIndirectlyGettingPowered(pos) > 0){
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
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}
	
	@Override
	public int getMetaFromState(BlockState state) {
//		return ((Direction)state.getValue(FACING)).getIndex();
		return state.getValue(FACING);
	}
	
	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction enumfacing = Direction.getFront(meta);
        return this.defaultBlockState().withProperty(FACING, enumfacing);
	}
	
	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}
	
	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn)
	{
	   return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}
}