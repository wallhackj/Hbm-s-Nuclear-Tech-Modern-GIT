package api.hbm.block;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDrillInteraction {

	/**
	 * Whether the breaking of the block should be successful. Will destroy the block and not drop anything from clusters.
	 * Should use a random function, otherwise the clusters will stay there indefinitely printing free ore.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param drill Might be a tool, tile entity or anything that breaks blocks
	 * @return	
	 */
	public boolean canBreak(Level world, int x, int y, int z, BlockState state, IMiningDrill drill);
	
	/**
	 * Returns an itemstack, usually when the block is not destroyed. Laser drills may drop this and mechanical drills will add it to their inventories.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param drill Might be a tool, tile entity or anything that breaks blocks
	 * @return
	 */
	public ItemStack extractResource(Level world, int x, int y, int z, BlockState state, IMiningDrill drill);
	
	/**
	 * The hardness that should be considered instead of the hardness value of the block itself
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param state
	 * @param drill
	 * @return
	 */
	public float getRelativeHardness(Level world, int x, int y, int z, BlockState state, IMiningDrill drill);
}