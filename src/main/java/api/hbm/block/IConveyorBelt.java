package api.hbm.block;


import glmath.glm.vec._3.d.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IConveyorBelt {

    /** Returns true if the item should stay on the conveyor, false if the item should drop off */
    public boolean canItemStay(Level world, int x, int y, int z, Vec3d itemPos);
    public Vec3d getTravelLocation(Level world, int x, int y, int z, Vec3d itemPos, double speed);
    public Vec3d getClosestSnappingPosition(Level world, BlockPos pos, Vec3d itemPos);
}