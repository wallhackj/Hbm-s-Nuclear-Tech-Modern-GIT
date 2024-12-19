package com.hbm.blocks;

import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.InventoryHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BlockDummyable extends Block {
    //Drillgon200: I'm far to lazy to figure out what all the meta values should be translated to in properties
    public static final IntegerProperty META = IntegerProperty.create("meta", 0, 15);

    public BlockDummyable(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(META, 0));
    }
    /// BLOCK METADATA ///

    //0-5 		dummy rotation 		(for dummy neighbor checks)
    //6-11 		extra 				(6 rotations with flag, for pipe connectors and the like)
    //12-15 	block rotation 		(for rendering the TE)

    //meta offset from dummy to TE rotation
    public static final int offset = 10;
    //meta offset from dummy to extra rotation
    public static final int extra = 6;

    public static boolean safeRem = false;

    //	@Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isClientSide || safeRem)
            return;

        int metadata = state.getValue(META);

        //if it's an extra, remove the extra-ness
        if (metadata >= extra)
            metadata -= extra;

        ForgeDirection dir = ForgeDirection.getOrientation(metadata).getOpposite();
        Block b = world.getBlockState(new BlockPos(pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ)).getBlock();
        if (b.getClass() != this.getClass()) {
            world.removeBlock(pos, false);
        }
    }

    //	@Override
    public void updateTick(Level world, BlockPos pos, BlockState state, Random rand) {
        if (world.isClientSide)
            return;

        int metadata = state.getValue(META);

        if (metadata >= extra)
            metadata -= extra;

        Direction dir = Direction.from3DDataValue(metadata).getOpposite();
        Block neighbor = world.getBlockState(pos.relative(dir)).getBlock();

        if (neighbor.getClass() != this.getClass()) {
            world.removeBlock(pos, false);
        }
    }

    public int[] findCore(BlockGetter world, int x, int y, int z) {
        positions.clear();
        return findCoreRec(world, x, y, z);
    }

    List<BlockPos> positions = new ArrayList<BlockPos>();

    public int[] findCoreRec(BlockGetter world, int x, int y, int z) {

        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);

        if (state.getBlock().getClass() != this.getClass())
            return null;

        int metadata = state.getValue(META);

        //if it's an extra, remove the extra-ness
        if (metadata >= extra)
            metadata -= extra;

        //if the block matches and the orientation is "UNKNOWN", it's the core
        if (ForgeDirection.getOrientation(metadata) == ForgeDirection.UNKNOWN)
            return new int[]{x, y, z};

        if (positions.contains(pos))
            return null;

        ForgeDirection dir = ForgeDirection.getOrientation(metadata).getOpposite();

        positions.add(pos);

        return findCoreRec(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
    }

    //    @Override
    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity player, ItemStack itemStack) {
        if (!(player instanceof Player))
            return;

        world.removeBlock(pos, false);

        Player pl = (Player) player;
        InteractionHand hand = pl.getMainHandItem() == itemStack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        int i = (int) (Math.floor((player.getYRot() * 4.0F / 360.0F) + 0.5D)) & 3;
        int o = -getOffset();
        pos = new BlockPos(pos.getX(), pos.getY() + getHeightOffset(), pos.getZ());

        ForgeDirection dir = ForgeDirection.NORTH;

        if (i == 0) {
            dir = ForgeDirection.getOrientation(2);
        }
        if (i == 1) {
            dir = ForgeDirection.getOrientation(5);
        }
        if (i == 2) {
            dir = ForgeDirection.getOrientation(3);
        }
        if (i == 3) {
            dir = ForgeDirection.getOrientation(4);
        }

        dir = getDirModified(dir);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!checkRequirement(world, x, y, z, dir, o)) {
            if (!pl.getAbilities().instabuild) {
                ItemStack stack = pl.getInventory().getSelected();
                Item item = BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this));


                if (stack.isEmpty()) {
                    pl.getInventory().setItem(pl.getInventory().selected, new ItemStack(this));
                } else if (stack.getItem() != item || stack.getCount() == stack.getMaxStackSize()) {
                    pl.getInventory().add(new ItemStack(this));
                } else {
                    pl.getItemInHand(hand).grow(1);
                }
            }
            return;
        }

        if (!world.isClientSide) {
            world.setBlock(new BlockPos(x + dir.offsetX * o, y + dir.offsetY * o,
                            z + dir.offsetZ * o), this.defaultBlockState().setValue(META, dir.ordinal() + offset),
                    3);
            fillSpace(world, x, y, z, dir, o);
        }
        pos = new BlockPos(pos.getX(), pos.getY() - getHeightOffset(), pos.getZ());
        world.scheduleTick(pos, this, 1);
        world.scheduleTick(pos, this, 2);

        super.setPlacedBy(world, pos, state, player, itemStack);
    }

    protected boolean standardOpenBehavior(Level world, int x, int y, int z, Player player, int id) {
        if (world.isClientSide) {
            return true;
        } else if (!player.isCrouching()) {
            int[] pos = this.findCore(world, x, y, z);

            if (pos == null)
                return false;

            BlockEntity blockEntity = world.getBlockEntity(new BlockPos(pos[0], pos[1], pos[2]));
            if (blockEntity != null) {
//				&& blockEntity.getType() == this.getBlockEntityType()
                player.openMenu((MenuProvider) blockEntity);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    protected ForgeDirection getDirModified(ForgeDirection dir) {
        return dir;
    }

    protected boolean checkRequirement(Level world, int x, int y, int z, ForgeDirection dir, int o) {
        return MultiblockHandlerXR.checkSpace(world, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, getDimensions(), x, y, z, dir);
    }

//    protected abstract boolean checkRequirement(Level world, int xx, int yy, int zz, ForgeDirection dir, int o);

    protected void fillSpace(Level world, int x, int y, int z, ForgeDirection dir, int o) {
        MultiblockHandlerXR.fillSpace(world, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, getDimensions(), this, dir);
    }

    //"upgrades" regular dummy blocks to ones with the extra flag
    public void makeExtra(Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }

        int meta = state.getValue(META);

        if (meta > 5) {
            return;  // No extra if meta is already greater than 5
        }

        // Set the block state to include the extra flag
        safeRem = true;
        world.setBlockAndUpdate(pos, state.setValue(META, meta + extra));
        safeRem = false;
    }

    //Drillgon200: Removes the extra. I could have sworn there was already a method for this, but I can't find it.
    public void removeExtra(Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (world.getBlockState(pos).getBlock() != this)
            return;

        int meta = world.getBlockState(pos).getValue(META);

        if (meta <= 5 || meta >= 12)
            return;

        //world.setBlockMetadataWithNotify(x, y, z, meta + extra, 3);
        safeRem = true;
        world.setBlockAndUpdate(pos, this.defaultBlockState().setValue(META, meta - extra));
        safeRem = false;
    }

    //checks if the dummy metadata is within the extra range
    public boolean hasExtra(int meta) {
        return meta > 5 && meta < 12;
    }

    //	@Override
    public void breakBlock(Level world, BlockPos pos, BlockState state) {
        int i = state.getValue(META);
        if (i >= 12) {
            //ForgeDirection d = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z) - offset);
            //MultiblockHandler.emptySpace(world, x, y, z, getDimensions(), this, d);
        } else if (!safeRem) {

            if (i >= extra)
                i -= extra;

            ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();
            int[] pos1 = findCore(world, pos.getX() + dir.offsetX, pos.getY() + dir.offsetY, pos.getZ() + dir.offsetZ);

            if (pos1 != null) {

                //ForgeDirection d = ForgeDirection.getOrientation(world.getBlockMetadata(pos[0], pos[1], pos[2]) - offset);
                world.removeBlock(new BlockPos(pos1[0], pos1[1], pos1[2]), false);
            }
        }
        InventoryHelper.dropInventoryItems(world, pos, world.getBlockEntity(pos));
//		super.breakBlock(world, pos, state);
    }

    //	@Override
    public RenderType getRenderType(BlockState state) {
        return RenderType.LINE_STRIP;
    }

    //	@Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    //	@Override
    public boolean isBlockNormalCube(BlockState state) {
        return false;
    }

    //	@Override
    public boolean isNormalCube(BlockState state) {
        return false;
    }

    //	@Override
    public boolean isNormalCube(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    //	@Override
    public boolean shouldSideBeRendered(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return false;
    }

    //	@Override
    protected BlockStateProvider createBlockState() {
//		return new BlockStateProvider (this, META);
        return null;
    }

    //	@Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(META);
    }

    //	@Override
    public BlockState getStateFromMeta(int meta) {
        return this.defaultBlockState().setValue(META, meta);
    }

    public abstract int[] getDimensions();

    public abstract int getOffset();

    public int getHeightOffset() {
        return 0;
    }

//    protected abstract void fillSpace(Level world, int xxx, int yyy, int zzz, ForgeDirection dir, int o);
//
//    protected abstract boolean checkRequirement(Level world, int xx, int yy, int zz, ForgeDirection dir, int o);
}
