package com.hbm.blocks.generic;


import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class BlockHazardFuel extends BlockHazard {

	private int burntime;
	public int encouragement;
	public int flammability;
	
	public BlockHazardFuel(Material m, String s, int en, int flam, int burntime){
		super(m, s);
		this.encouragement = en;
		this.flammability = flam;
		this.burntime = burntime;
	}

	public int getBurnTime(){
		return burntime;
	}
	
//	@Override
	public int getFlammability(BlockGetter world, BlockPos pos, Direction face){
		return flammability;
	}
	
//	@Override
	public int getFireSpreadSpeed(BlockGetter world, BlockPos pos, Direction face){
		return encouragement;
	}

//	@Override
	public boolean isFlammable(BlockGetter world, BlockPos pos, Direction face){
		return true;
	}

//	@Override
	public boolean isFireSource(Level world, BlockPos pos, Direction side){
		return true;
	}
}
