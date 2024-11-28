package com.hbm.blocks;


import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public class ModSoundType extends SoundType {

	public ModSoundType(SoundEvent sound, float volumeIn, float pitchIn){
		super(volumeIn, pitchIn, sound, sound, sound, sound, sound);
	}
	
	@Override
	public SoundEvent getBreakSound(){
		return super.getBreakSound();
	}
	
	@Override
	public SoundEvent getStepSound(){
		return super.getStepSound();
	}

}
