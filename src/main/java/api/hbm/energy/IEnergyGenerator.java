package api.hbm.energy;

import com.hbm.lib.ForgeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public interface IEnergyGenerator extends IEnergyUser {

	/**
	 * Standard implementation for machines that can only send energy but never receive it.
	 * @param power
	 */
	@Override
	public default long transferPower(long power) {
		return power;
	}

	/* should stop making non-receivers from interfering by applying their weight which doesn't even matter */
	@Override
	public default long getTransferWeight() {
		return 0;
	}

	public default void sendPower(Level world, BlockPos pos){
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			this.sendPower(world, pos.offset(dir.offsetX, dir.offsetY, dir.offsetZ), dir);
	}
}
