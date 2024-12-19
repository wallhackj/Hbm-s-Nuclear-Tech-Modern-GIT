package com.hbm.blocks.turret;

import com.hbm.blocks.BlockDummyable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class TurretBaseNT extends BlockDummyable {

    public TurretBaseNT(Properties properties) {
        super(properties);
    }

    @Override
    public int[] getDimensions() {
        return new int[]{0, 0, 1, 0, 1, 0};
    }

    @Override
    public int getOffset() {
        return 0;
    }

    //    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    //    @Override
    public boolean onBlockActivated(Level world, BlockPos bpos, BlockState state, Player player, InteractionHand hand,
                                    Direction facing, float hitX, float hitY, float hitZ) {
        if (world.isClientSide) {
            return true;
        } else if (!player.isCrouching()) {
            int[] pos = this.findCore(world, bpos.getX(), bpos.getY(), bpos.getZ());

            if (pos == null)
                return false;

            openGUI(world, player, pos[0], pos[1], pos[2]);
            return true;
        } else {
            return false;
        }
    }

    public abstract void openGUI(Level world, Player player, int x, int y, int z);

}
