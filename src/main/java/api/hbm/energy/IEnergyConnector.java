package api.hbm.energy;

import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.lib.ForgeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


/**
 * For anything that connects to power and can be transferred power to, the bottom-level interface.
 * This is mean for TILE ENTITIES
 * @author hbm
 */
public interface IEnergyConnector extends ILoadedTile {
	
	/**
	 * Returns the amount of power that remains in the source after transfer
	 * @param power
	 * @return
	 */
	public long transferPower(long power);
	
	/**
	 * Whether the given side can be connected to
	 * dir refers to the side of this block, not the connecting block doing the check
	 * @param dir
	 * @return
	 */
	public default boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.UNKNOWN;
	}
	
	/**
	 * The current power of either the machine or an entire network
	 * @return
	 */
	public long getPower();
	
	/**
	 * The capacity of either the machine or an entire network
	 * @return
	 */
	public long getMaxPower();
	
	public default long getTransferWeight() {
		return Math.max(getMaxPower() - getPower(), 0);
	}
	
	/**
	 * Basic implementation of subscribing to a nearby power grid
	 * @param world
	 * @param dir
	 * @param pos
	 */
	public default void trySubscribe(Level world, BlockPos pos, ForgeDirection dir) {

		BlockEntity te = world.getBlockEntity(pos);
		boolean red = false;
		
		if(te instanceof IEnergyConductor) {
			IEnergyConductor con = (IEnergyConductor) te;
			
			if(!con.canConnect(dir.getOpposite()))
				return;
			
			if(con.getPowerNet() != null && !con.getPowerNet().isSubscribed(this))
				con.getPowerNet().subscribe(this);
			
			if(con.getPowerNet() != null)
				red = true;
		}
		
		// if(particleDebug) {//
		// 	NBTTagCompound data = new NBTTagCompound();
		// 	data.setString("type", "network");
		// 	data.setString("mode", "power");
		// 	double posX = pos.getX() + 0.5 + dir.offsetX * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
		// 	double posY = pos.getY() + 0.5 + dir.offsetY * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
		// 	double posZ = pos.getZ() + 0.5 + dir.offsetZ * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
		// 	data.setDouble("mX", -dir.offsetX * (red ? 0.025 : 0.1));
		// 	data.setDouble("mY", -dir.offsetY * (red ? 0.025 : 0.1));
		// 	data.setDouble("mZ", -dir.offsetZ * (red ? 0.025 : 0.1));
		// 	PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, posX, posY, posZ), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 25));
		// }
	}
	
	public default void tryUnsubscribe(Level world, BlockPos pos) {

		BlockEntity te = world.getBlockEntity(pos);
		
		if(te instanceof IEnergyConductor) {
			IEnergyConductor con = (IEnergyConductor) te;
			
			if(con.getPowerNet() != null && con.getPowerNet().isSubscribed(this))
				con.getPowerNet().unsubscribe(this);
		}
	}
	
	public static final boolean particleDebug = true;
	
	public default Vec3 getDebugParticlePos() {
		BlockPos pos = ((BlockEntity) this).getBlockPos();
		Vec3 vec = Vec3.createVectorHelper(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		return vec;
	}
	
	public default ConnectionPriority getPriority() {
		return ConnectionPriority.NORMAL;
	}
	
	public enum ConnectionPriority {
		LOW,
		NORMAL,
		HIGH
	}

	public default boolean isStorage() { //used for batteries
		return false;
	}

	public default void updateStandardConnections(Level world, BlockEntity te) {
		updateStandardConnections(world, te.getBlockPos());
	}
		
	public default void updateStandardConnections(Level world, BlockPos pos) {
		
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			this.trySubscribe(world, pos.offset(dir.offsetX, dir.offsetY, dir.offsetZ), dir);
		}
	}

	public default void updateConnectionsExcept(Level world, BlockPos pos, ForgeDirection nogo) {
		
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if(dir != nogo)
				this.trySubscribe(world, pos.offset(dir.offsetX, dir.offsetY, dir.offsetZ), dir);
		}
	}
}
