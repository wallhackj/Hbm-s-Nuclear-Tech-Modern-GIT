package com.hbm.tileentity.machine;

import api.hbm.block.IToolable.ToolType;
import com.hbm.items.ModItems;
import com.hbm.handler.ArmorUtil;
import com.hbm.items.tool.ItemTooling;
import com.hbm.items.tool.ItemKeyPin;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class TileEntityLockableBase extends BlockEntity {
    protected int lock;
    private boolean isLocked = false;
    protected double lockMod = 0.1D;

    public TileEntityLockableBase(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean canLock(Player player, InteractionHand hand, Direction facing) {
        return true;
    }

    public void lock() {

        if (lock == 0) {
            MainRegistry.logger.error("A block has been set to locked state before setting pins, this should not happen and may cause errors! " + this.toString());
        }
        if (isLocked == false)
            markDirty();
        isLocked = true;
    }

    public void setPins(int pins) {
        if (lock != pins)
            markDirty();
        lock = pins;
    }

    public int getPins() {
        return lock;
    }

    public void setMod(double mod) {
        if (lockMod != mod)
            markDirty();
        lockMod = mod;
    }

    public double getMod() {
        return lockMod;
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        lock = compound.getInt("lock");
        isLocked = compound.getBoolean("isLocked");
        lockMod = compound.getDouble("lockMod");
        super.readFromNBT(compound);
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag compound) {
        compound.putInt("lock", lock);
        compound.putBoolean("isLocked", isLocked);
        compound.putDouble("lockMod", lockMod);
        return super.writeToNBT(compound);
    }

    public boolean canAccess(Player player) {
        if (player == null) { //!isLocked ||
            return false;
        } else {
            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof ItemKeyPin && ItemKeyPin.getPins(stack) == this.lock) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), HBMSoundHandler.lockOpen,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }

            if (stack.getItem() == ModItems.key_red) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), HBMSoundHandler.lockOpen,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }

            return tryPick(player);
        }
    }

    public static int hasLockPickTools(Player player) {
        ItemStack stackR = player.getMainHandItem();
        ItemStack stackL = player.getOffhandItem();

        if (stackR == null || stackL == null) return -1;
        if (stackR.getItem() == ModItems.pin) {
            if (stackL.getItem() instanceof ItemTooling && ((ItemTooling) stackL.getItem()).getType() == ToolType.SCREWDRIVER) {
                return 1;
            }
        } else if (stackL.getItem() == ModItems.pin) {
            if (stackR.getItem() instanceof ItemTooling && ((ItemTooling) stackR.getItem()).getType() == ToolType.SCREWDRIVER) {
                return 2;
            }
        }
        return -1;
    }

    public boolean tryPick(Player player) {

        boolean canPick = false;
        int hand = hasLockPickTools(player);
        double chanceOfSuccess = this.lockMod * 100;

        if (hand == 1) {
            player.getMainHandItem().shrink(1);
            canPick = true;
        } else if (hand == 2) {
            player.getOffhandItem().shrink(1);
            canPick = true;
        }

        if (canPick) {

            if (ArmorUtil.checkArmorPiece(player, ModItems.jackt, 2) || ArmorUtil.checkArmorPiece(player,
                    ModItems.jackt2, 2))
                chanceOfSuccess *= 100D;

            double rand = player.level().random.nextDouble() * 100;

            if (chanceOfSuccess > rand) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), HBMSoundHandler.pinUnlock,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), HBMSoundHandler.pinBreak,
                    SoundSource.BLOCKS, 1.0F, 0.8F + player.level().random.nextFloat() * 0.2F);
        }

        return false;
    }
}
