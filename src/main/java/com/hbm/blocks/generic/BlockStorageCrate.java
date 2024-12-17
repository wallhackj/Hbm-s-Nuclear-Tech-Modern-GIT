package com.hbm.blocks.generic;

import java.util.List;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemLock;
import com.hbm.lib.InventoryHelper;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.config.MachineConfig;
import com.hbm.tileentity.machine.TileEntityLockableBase;
import com.hbm.tileentity.machine.TileEntityCrateIron;
import com.hbm.tileentity.machine.TileEntityCrateSteel;
import com.hbm.tileentity.machine.TileEntityCrateTungsten;
import com.hbm.tileentity.machine.TileEntityCrateDesh;
import com.hbm.tileentity.machine.TileEntitySafe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;


public class BlockStorageCrate extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static boolean dropInv = true;

    public BlockStorageCrate(Material materialIn, String s) {
        super(materialIn);
        this.setUnlocalizedName(s);
        this.setRegistryName(s);
        this.setSoundType(SoundType.METAL);

        ModBlocks.ALL_BLOCKS.add(this);
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        if (this == ModBlocks.crate_iron)
            return new TileEntityCrateIron();
        if (this == ModBlocks.crate_steel)
            return new TileEntityCrateSteel();
        if (this == ModBlocks.crate_tungsten)
            return new TileEntityCrateTungsten();
        if (this == ModBlocks.crate_desh)
            return new TileEntityCrateDesh();
        if (this == ModBlocks.safe)
            return new TileEntitySafe();
        return null;
    }

    public int getSlots() {
        if (this == ModBlocks.crate_iron)
            return 36;
        if (this == ModBlocks.crate_steel)
            return 54;
        if (this == ModBlocks.crate_tungsten)
            return 27;
        if (this == ModBlocks.crate_desh)
            return 104;
        if (this == ModBlocks.safe)
            return 15;
        return 0;
    }


    @Override
    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest) {
        if (!player.capabilities.isCreativeMode && !world.isClientSide && willHarvest) {

            ItemStack drop = new ItemStack(this);
            TileEntity te = world.getTileEntity(pos);

            CompoundTag nbt = new CompoundTag();

            if (te != null) {
                IItemHandler inventory;
                if (te instanceof TileEntitySafe) {

                    inventory = ((TileEntitySafe) te).getPackingCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                } else {
                    inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                }

                for (int i = 0; i < inventory.getSlots(); i++) {

                    ItemStack stack = inventory.getStackInSlot(i);
                    if (stack.isEmpty())
                        continue;

                    CompoundTag slot = new CompoundTag();
                    stack.writeToNBT(slot);
                    nbt.put("slot" + i, slot);
                }
            }

            if (te instanceof TileEntityLockableBase) {
                TileEntityLockableBase lockable = (TileEntityLockableBase) te;

                if (lockable.isLocked()) {
                    nbt.putInt("lock", lockable.getPins());
                    nbt.putDouble("lockMod", lockable.getMod());
                }
            }


            if (!nbt.isEmpty()) {
                drop.setTag(nbt);

                if (nbt.toString().length() > MachineConfig.crateByteSize * 1000) {
                    player.sendMessage(new TextComponentString("§cWarning: Container NBT exceeds " 
                            + MachineConfig.crateByteSize + "kB, contents will be ejected!"));
                    InventoryHelper.dropInventoryItems(world, pos, world.getTileEntity(pos));
                    InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5,
                            pos.getZ() + 0.5, new ItemStack(Item.getItemFromBlock(this)));
                    return world.removeBlock(pos, false);
                }
            }

            InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        }

        this.dropInv = false;
        boolean flag = world.removeBlock(pos, false);
        this.dropInv = true;

        return flag;
    }

    @Override
    public Block setSoundType(SoundType sound) {
        return super.setSoundType(sound);
    }

    @Override
    public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
        if (this.dropInv) {
            InventoryHelper.dropInventoryItems(worldIn, pos, worldIn.getBlockEntity(pos));
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand,
                                    Direction facing, float hitX, float hitY, float hitZ) {
        if (world.isClientSide) {
            return true;
        } else if (player.getMainHandItem().isEmpty() && (player.getMainHandItem().getItem() instanceof ItemLock ||
                player.getMainHandItem().getItem() == ModItems.key_kit)) {
            return false;

        } else if (!player.isCrouching()) {
            BlockEntity entity = world.getBlockEntity(pos);
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            if (entity instanceof TileEntityCrateIron && ((TileEntityCrateIron) entity).canAccess(player)) {
                player.openGui(MainRegistry.instance, ModBlocks.guiID_crate_iron, world, x, y, z);
            }
            if (entity instanceof TileEntityCrateSteel && ((TileEntityCrateSteel) entity).canAccess(player)) {
                player.openGui(MainRegistry.instance, ModBlocks.guiID_crate_steel, world, x, y, z);
            }
            if (entity instanceof TileEntityCrateTungsten && ((TileEntityCrateTungsten) entity).canAccess(player)) {
                player.openGui(MainRegistry.instance, ModBlocks.guiID_crate_tungsten, world, x, y, z);
            }
            if (entity instanceof TileEntityCrateDesh && ((TileEntityCrateDesh) entity).canAccess(player)) {
                player.openGui(MainRegistry.instance, ModBlocks.guiID_crate_desh, world, x, y, z);
            }
            if (entity instanceof TileEntitySafe && ((TileEntitySafe) entity).canAccess(player)) {
                player.openGui(MainRegistry.instance, ModBlocks.guiID_safe, world, x, y, z);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te != null && stack.hasTag()) {
            IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

            CompoundTag nbt = stack.getTag();
            for (int i = 0; i < inventory.getSlots(); i++) {
                inventory.insertItem(i, new ItemStack(nbt.getCompoundTag("slot" + i)), false);
            }

            if (te instanceof TileEntityLockableBase) {
                TileEntityLockableBase lockable = (TileEntityLockableBase) te;

                if (nbt.contains("lock")) {
                    lockable.setPins(nbt.getInt("lock"));
                    lockable.setMod(nbt.getDouble("lockMod"));
                    lockable.lock();
                }
            }
        }

        if (this != ModBlocks.safe)
            super.onBlockPlacedBy(world, pos, state, placer, stack);
        else
            world.setBlock(pos, state.setValue(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, float hitX, float hitY,
                                           float hitZ, int meta, LivingEntity placer, InteractionHand hand) {
        return this.defaultBlockState().setValue(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return ((Direction) state.getValue(FACING)).get3DDataValue();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        Direction enumfacing = Direction.getFront(meta);

        if (enumfacing.getAxis() == Direction.Axis.Y) {
            enumfacing = Direction.NORTH;
        }

        return this.defaultBlockState().setValue(FACING, enumfacing);
    }

    @Override
    public BlockState withRotation(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
        return state.setValue(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }

    @Override
    public EnumBlockRenderType getRenderType(BlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> list, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        int totalSlots = getSlots();
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            int slotCount = 0;
            for (int i = 0; i < totalSlots; i++) {
                if (nbt.contains("slot" + i)) {
                    slotCount++;
                }
            }
            float percent = Library.roundFloat(slotCount * 100F / totalSlots, 1);
            String color = "§e";
            String color2 = "§6";
            if (percent >= 75) {
                color = "§c";
                color2 = "§4";
            } else if (percent < 25) {
                color = "§a";
                color2 = "§2";
            }
            list.add(color + slotCount + color2 + "/" + totalSlots + " Slots used " + color + "(" + percent + "%)§r");

        } else {
            list.add("§a0§2/" + totalSlots + " Slots used §a(0.0%)§r");
        }
    }
}
