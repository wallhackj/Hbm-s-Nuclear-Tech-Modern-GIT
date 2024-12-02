package com.hbm.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.Spaghetti;
import com.hbm.lib.ItemStackHandlerWrapper;
import com.hbm.packet.NBTPacket;
import com.hbm.packet.PacketDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;


@Spaghetti("Not spaghetti in itself, but for the love of god please use this base class for all machines")
public abstract class TileEntityMachineBase extends TileEntityLoadedBase implements INBTPacketReceiver {

    public ItemStackHandler inventory;

    private String customName;

    public TileEntityMachineBase(int scount) {
        this(scount, 64);
    }

    public TileEntityMachineBase(int scount, int slotlimit) {
        super(null, null, null);
        inventory = getNewInventory(scount, slotlimit);
    }

    public ItemStackHandler getNewInventory(int scount, int slotlimit) {
        return new ItemStackHandler(scount) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
//                markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return slotlimit;
            }
        };
    }

    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.customName : getName();
    }

    public abstract String getName();

    public boolean hasCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public boolean isUseableByPlayer(Player player) {
        if (level.getBlockEntity(worldPosition)!= this) {
            return false;
        } else {
            return player.getDistanceSq(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 128;
        }
    }

    public int[] getAccessibleSlotsFromSide(Direction e) {
        return new int[]{};
    }

    public int getGaugeScaled(int i, FluidTank tank) {
        return tank.getFluidAmount() * i / tank.getCapacity();
    }

    public void networkPack(CompoundTag nbt, int range) {

        if (!level.isClientSide)
            PacketDispatcher.wrapper.sendToAllAround(new NBTPacket(nbt, worldPosition),
                    new Climate.TargetPoint(this.world.provider.getDimension(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), range));
    }

    public void networkUnpack(CompoundTag nbt) {
    }

    public void handleButtonPacket(int value, int meta) {
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag compound) {
        compound.put("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        if (compound.contains("inventory"))
            inventory.deserializeNBT(compound.getCompound("inventory"));
        super.readFromNBT(compound);
    }

    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return true;
    }

    public boolean canInsertItem(int slot, ItemStack itemStack, int amount) {
        return this.isItemValidForSlot(slot, itemStack);
    }

    public boolean canExtractItem(int slot, ItemStack itemStack, int amount) {
        return true;
    }

    public int countMufflers() {

        int count = 0;

        for (Direction dir : Direction.values())
            if (level.getBlockState(worldPosition.offset(dir.getNormal()))
                    .getBlock() == ModBlocks.muffler)
                count++;

        return count;
    }

    public float getVolume(int toSilence) {

        float volume = 1 - (countMufflers() / (float) toSilence);

        return Math.max(volume, 0);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventory != null) {
            if (facing == null)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                    .cast(new ItemStackHandlerWrapper(inventory, getAccessibleSlotsFromSide(facing)) {

                        @Override
                        public ItemStack extractItem(int slot, int amount, boolean simulate) {
                            if (canExtractItem(slot, inventory.getStackInSlot(slot), amount))
                                return super.extractItem(slot, amount, simulate);
                            return ItemStack.EMPTY;
                        }

                        @Override
                        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                            if (canInsertItem(slot, stack, stack.getCount()))
                                return super.insertItem(slot, stack, simulate);
                            return stack;
                        }
                    });
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                inventory != null) || super.hasCapability(capability, facing);
    }
}
