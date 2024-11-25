package api.hbm.block;

import com.hbm.handler.MultiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

public interface IBlockSideRotation {
    // Get the rotation for a specific side (now working with BlockState and BlockGetter)
    int getRotationFromSide(BlockGetter world, BlockPos pos, BlockState state, Block block, MultiblockHandler.EnumDirection side);
}
