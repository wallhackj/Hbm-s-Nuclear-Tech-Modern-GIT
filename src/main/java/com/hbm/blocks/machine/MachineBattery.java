package com.hbm.blocks.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.ILookOverlay;
import com.hbm.lib.Library;
import com.hbm.lib.InventoryHelper;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.TileEntityMachineBattery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MachineBattery extends Block implements ILookOverlay {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private long maxPower;

    public MachineBattery(Material materialIn, long power, String s) {
        super(materialIn);
        this.setUnlocalizedName(s);
        this.setRegistryName(s);
        this.setCreativeTab(MainRegistry.machineTab);
        this.maxPower = power;

        ModBlocks.ALL_BLOCKS.add(this);
    }

    //	@Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityMachineBattery();
    }

    public long getMaxPower() {
        return maxPower;
    }

    //	@Override
    public void onBlockAdded(Level worldIn, BlockPos pos, BlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(Level worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.isClientSide) {
            BlockState iblockstate = worldIn.getBlockState(pos.north());
            BlockState iblockstate1 = worldIn.getBlockState(pos.south());
            BlockState iblockstate2 = worldIn.getBlockState(pos.west());
            BlockState iblockstate3 = worldIn.getBlockState(pos.east());
            Direction enumfacing = (Direction) state.getValue(FACING);

            if (enumfacing == Direction.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = Direction.SOUTH;
            } else if (enumfacing == Direction.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                enumfacing = Direction.NORTH;
            } else if (enumfacing == Direction.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                enumfacing = Direction.EAST;
            } else if (enumfacing == Direction.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                enumfacing = Direction.WEST;
            }

            worldIn.setBlock(pos, state.setValue(FACING, enumfacing), 2);
        }
    }

//    @Override
    public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, float hitX, float hitY,
                                            float hitZ, int meta, LivingEntity placer, InteractionHand hand) {
        return this.defaultBlockState().setValue(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

//    @Override
    public int getMetaFromState(BlockState state) {
        return ((Direction) state.getValue(FACING)).get3DDataValue();
    }

//    @Override
    public BlockState getStateFromMeta(int meta) {
        Direction enumfacing = Direction.getFront(meta);

        if (enumfacing.getAxis() == Direction.Axis.Y) {
            enumfacing = Direction.NORTH;
        }

        return this.defaultBlockState().setValue(FACING, enumfacing);
    }

//    @Override
    public BlockState withRotation(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }

//    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }

//    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest) {
        if (!player.capabilities.isCreativeMode && !world.isClientSide && willHarvest) {

            ItemStack drop = new ItemStack(this);
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileEntityMachineBattery) {
                TileEntityMachineBattery battery = (TileEntityMachineBattery) te;

                CompoundTag nbt = new CompoundTag();
                battery.writeNBT(nbt);

                if (!nbt.hasNoTags()) {
                    drop.setTag(nbt);
                }
            }

            InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        }
        return world.removeBlock(pos, false);
    }

    @Override
    public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlock(pos, state.setValue(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        BlockEntity te = worldIn.getBlockEntity(pos);
        if (stack.hasTag()) {
            if (te instanceof TileEntityMachineBattery) {
                TileEntityMachineBattery battery = (TileEntityMachineBattery) te;
                if (stack.hasDisplayName()) {
                    battery.setCustomName(stack.getDisplayName());
                }
                try {
                    CompoundTag stackNBT = stack.getTag();
                    if (stackNBT.contains("NBT_PERSISTENT_KEY")) {
                        battery.readNBT(stackNBT);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

//    @Override
    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand,
                                    Direction facing, float hitX, float hitY, float hitZ) {
        if (world.isClientSide) {
            return true;
        } else if (!player.isCrouching()) {
            TileEntityMachineBattery entity = (TileEntityMachineBattery) world.getBlockEntity(pos);
            if (entity != null) {
                player.openGui(MainRegistry.instance, ModBlocks.guiID_machine_battery, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TileEntityMachineBattery) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityMachineBattery) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(BlockState state) {
        return EnumBlockRenderType.MODEL;
    }

//    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

//    @Override
    public int getComparatorInputOverride(BlockState blockState, Level worldIn, BlockPos pos) {

        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof TileEntityMachineBattery))
            return 0;

        TileEntityMachineBattery battery = (TileEntityMachineBattery) te;
        return (int) battery.getPowerRemainingScaled(15L);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> list, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        long charge = 0L;
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (nbt.contains("NBT_PERSISTENT_KEY")) {
                charge = nbt.getCompound("NBT_PERSISTENT_KEY").getLong("power");
            }
        }

        if (charge == 0L) {
            list.add("§c0§4/" + Library.getShortNumber(this.maxPower) + "HE §c(0.0%)§r");
        } else {
            double percent = Math.round(charge * 1000L / this.maxPower) * 0.1D;
            String color = "§e";
            String color2 = "§6";
            if (percent < 25) {
                color = "§c";
                color2 = "§4";
            } else if (percent >= 75) {
                color = "§a";
                color2 = "§2";
            }
            list.add(color + Library.getShortNumber(charge) + color2 + "/" + Library.getShortNumber(this.maxPower) + "HE " + color + "(" + percent + "%)§r");
        }
    }

//    @Override
    public void printHook(Pre event, Level world, int x, int y, int z) {

        BlockEntity te = world.getBlockEntity(new BlockPos(x, y, z));

        if (!(te instanceof TileEntityMachineBattery))
            return;

        TileEntityMachineBattery battery = (TileEntityMachineBattery) te;
        List<String> text = new ArrayList();
        text.add(Library.getShortNumber(battery.power) + "/" + Library.getShortNumber(getMaxPower()) + " HE");
        if (battery.powerDelta == 0) {
            text.add("§e-- §r0HE/s");
        } else if (battery.powerDelta > 0) {
            text.add("§a-> §r" + Library.getShortNumber(battery.powerDelta) + "HE/s");
        } else {
            text.add("§c<- §r" + Library.getShortNumber(-battery.powerDelta) + "HE/s");
        }
        text.add("&[" + Library.getColorProgress((double) battery.power / (double) getMaxPower()) + "&]    " + Library.getPercentage((double) battery.power / (double) getMaxPower()) + "%");
        ILookOverlay.printGeneric(event, getLocalizedName(), 0xffff00, 0x404000, text);
    }
}
