package com.hbm.handler;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.apache.commons.io.IOUtils;

import com.hbm.blocks.ModBlocks;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;

public class MultiblockBBHandler {

	public static final MultiblockBounds FENSU_BOUNDS = load(new ResourceLocation(RefStrings.MODID, "multiblock_bounds/bb_fensu0.mbb"));
	
	public static final Map<Object, MultiblockBounds> REGISTRY = new HashMap<Object, MultiblockBounds>();
	
	public static MultiblockBounds load(ResourceLocation loc){
		try {
			InputStream s = MainRegistry.class.getResourceAsStream("/assets/"+loc.getNamespace()+"/"+loc.getNamespace());
			return parse(ByteBuffer.wrap(IOUtils.toByteArray(s)));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static MultiblockBounds parse(ByteBuffer buf){
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int version = buf.getInt();
		int offsetX = buf.getInt();
		int offsetY = buf.getInt();
		int offsetZ = buf.getInt();
		AABB[] boundingBoxes = new AABB[buf.getInt()];
		int numBlocks = buf.getInt();
		
		Map<BlockPos, AABB[]> blocks = new HashMap<>();
		
		for(int i = 0; i < boundingBoxes.length; i ++){
			boundingBoxes[i] = new AABB(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
		}
		for(int i = 0; i < numBlocks; i ++){
			BlockPos pos = new BlockPos(buf.getInt(), buf.getInt(), buf.getInt());
			AABB[] blockBoxes = new AABB[buf.getInt()];
			for(int j = 0; j < blockBoxes.length; j ++){
				blockBoxes[j] = new AABB(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
			}
			blocks.put(pos, blockBoxes);
		}
		
		return new MultiblockBounds(boundingBoxes, blocks);
	}
	
	public static void init(){
		REGISTRY.put(ModBlocks.machine_fensu, FENSU_BOUNDS);
	}
	
	public static class MultiblockBounds {
		public AABB[] boxes;
		public Map<BlockPos, AABB[]> blocks;
		
		public MultiblockBounds(AABB[] boxes, Map<BlockPos, AABB[]> blocks) {
			this.boxes = boxes;
			this.blocks = blocks;
		}
	}
	
}
