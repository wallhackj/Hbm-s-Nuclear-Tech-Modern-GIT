package com.hbm.tileentity;

import api.hbm.energy.ILoadedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class TileEntityLoadedBase extends BlockEntity implements ILoadedTile {
	public boolean isLoaded = true;

	public TileEntityLoadedBase(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}

	public void onChunkUnload() {
		super.onChunkUnloaded();
		this.isLoaded = false;
	}
}
