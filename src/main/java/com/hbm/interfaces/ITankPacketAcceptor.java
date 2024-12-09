package com.hbm.interfaces;


import net.minecraft.nbt.CompoundTag;

public interface ITankPacketAcceptor {
	public void recievePacket(CompoundTag[] tags);
}
