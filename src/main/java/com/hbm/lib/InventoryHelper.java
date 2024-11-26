package com.hbm.lib;

import java.util.Optional;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

public class InventoryHelper {
	
	public static final Random RANDOM = new Random();

    public static void dropInventoryItems(Level world, BlockPos pos, ICapabilityProvider t) {
        if(t == null)
            return;
//        if(!t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
//            return;
        IItemHandler inventory = (IItemHandler) t.getCapability(null);
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
    }
	
	public static void spawnItemStack(Level worldIn, double x, double y, double z, ItemStack stack)
    {
        float f = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (!stack.isEmpty())
        {
            ItemEntity entityitem = new ItemEntity(worldIn, x + (double)f,
                    y + (double)f1, z + (double)f2,
                    stack.split(RANDOM.nextInt(21) + 10));
            entityitem.setDeltaMovement(RANDOM.nextGaussian() * 0.05000000074505806D,
                    RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D,
                    RANDOM.nextGaussian() * 0.05000000074505806D);
            worldIn.addFreshEntity(entityitem); // Use addFreshEntity instead of spawnEntity
        }
    }
}
