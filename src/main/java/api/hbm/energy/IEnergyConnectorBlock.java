package api.hbm.energy;

import com.hbm.lib.ForgeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;


/**
 * Interface for all blocks that should visually connect to cables without having an IEnergyConnector tile entity.
 * This is meant for BLOCKS
 * @author hbm
 *
 */
public interface IEnergyConnectorBlock {
	
	/**
	 * Same as IEnergyConnector's method but for regular blocks that might not even have TEs. Used for rendering only!
	 * @param world
	 * @param dir
	 * @return
	 */
	public boolean canConnect(BlockGetter world, BlockPos pos, ForgeDirection dir);
}
