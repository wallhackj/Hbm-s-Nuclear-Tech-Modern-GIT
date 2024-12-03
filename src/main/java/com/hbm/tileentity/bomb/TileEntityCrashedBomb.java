package com.hbm.tileentity.bomb;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityCrashedBomb extends BlockEntity {

	public TileEntityCrashedBomb(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return BlockEntity.INFINITE_EXTENT_AABB;
	}
	
//	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}
}
