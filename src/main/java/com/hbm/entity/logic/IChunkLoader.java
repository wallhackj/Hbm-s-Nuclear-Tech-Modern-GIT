package com.hbm.entity.logic;


import net.minecraft.server.level.Ticket;

public interface IChunkLoader {

	public void init(Ticket ticket);
	public void loadNeighboringChunks(int newChunkX, int newChunkZ);
}
