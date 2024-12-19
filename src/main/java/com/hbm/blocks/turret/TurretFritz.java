package com.hbm.blocks.turret;

import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.turret.TileEntityTurretFritz;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TurretFritz extends TurretBaseNT {

	public TurretFritz(Material materialIn, String s){
		super(materialIn, s);
	}
	
//	@Override
	public BlockEntity createNewTileEntity(Level worldIn, int meta){
		if(meta >= 12)
			return new TileEntityTurretFritz();
		return new TileEntityProxyCombo(true, true, false);
	}
	
//	@Override
	public void openGUI(Level world, Player player, int x, int y, int z){
		if (!world.isClientSide) {
			BlockEntity blockEntity = world.getBlockEntity(new BlockPos(x, y, z));
			if (blockEntity instanceof MenuProvider) {
				player.openMenu((MenuProvider) blockEntity);
			}
		}
	}
}
