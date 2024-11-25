package api.hbm.block;


import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IEnterableBlock {
    public boolean canItemEnter(Level world, int x, int y, int z, Direction dir, IConveyorItem entity);
    public void onItemEnter(Level world, int x, int y, int z, Direction dir, IConveyorItem entity);

    public boolean canPackageEnter(Level world, int x, int y, int z, Direction dir, IConveyorPackage entity);
    public void onPackageEnter(Level world, int x, int y, int z, Direction dir, IConveyorPackage entity);
}
