package api.hbm.block;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IToolable {
	public boolean onScrew(Level world, Player player, int x, int y, int z, Direction side, float fX, float fY, float fZ, InteractionHand hand, ToolType tool);

	public static enum ToolType {
		SCREWDRIVER, HAND_DRILL, DEFUSER
	}
}
