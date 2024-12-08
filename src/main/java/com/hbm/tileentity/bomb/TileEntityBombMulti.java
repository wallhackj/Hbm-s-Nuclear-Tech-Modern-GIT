package com.hbm.tileentity.bomb;

import com.hbm.items.ModItems;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraftforge.items.ItemStackHandler;

public class TileEntityBombMulti extends BlockEntity {

	public ItemStackHandler inventory;
	private String customName;
	
	public TileEntityBombMulti() {
        super(null, null, null);
        inventory = new ItemStackHandler(6){
			@Override
			protected void onContentsChanged(int slot) {
//				markDirty();
				super.onContentsChanged(slot);
			}
		};
	}
	
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container.bombMulti";
	}

	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}
	
	public void setCustomName(String name) {
		this.customName = name;
	}
	
	public boolean isUseableByPlayer(Player player) {
		if(level.getBlockEntity(worldPosition) != this)
		{
			return false;
		}else{
			return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
					worldPosition.getZ() + 0.5D) <=64;
		}
	}
	
//	@Override
	public void readFromNBT(CompoundTag compound) {
		if(compound.contains("inventory"))
			inventory.deserializeNBT(compound.getCompound("inventory"));
		super.load(compound);
	}
	
//	@Override
	public void writeToNBT(CompoundTag compound) {
		compound.put("inventory", inventory.serializeNBT());
        super.saveAdditional(compound);
    }
	
	public boolean isLoaded(){
		
		if(inventory.getStackInSlot(0).getItem() == Item.byBlock(Blocks.TNT) &&
				inventory.getStackInSlot(1).getItem() == Item.byBlock(Blocks.TNT) &&
				inventory.getStackInSlot(3).getItem() == Item.byBlock(Blocks.TNT) &&
				inventory.getStackInSlot(4).getItem() == Item.byBlock(Blocks.TNT))
		{
			return true;
		}
			
		return false;
	}
	
	public int return2type() {

		if(inventory.getStackInSlot(2) != null)
		{
		if(inventory.getStackInSlot(2).getItem() == Items.GUNPOWDER)
		{
			return 1;
		}
		
		if(inventory.getStackInSlot(2).getItem() == Item.byBlock(Blocks.TNT))
		{
			return 2;
		}
		
		if(inventory.getStackInSlot(2).getItem() == ModItems.pellet_cluster)
		{
			return 3;
		}
		
		if(inventory.getStackInSlot(2).getItem() == ModItems.powder_fire)
		{
			return 4;
		}
		
		if(inventory.getStackInSlot(2).getItem() == ModItems.powder_poison)
		{
			return 5;
		}
		
		if(inventory.getStackInSlot(2).getItem() == ModItems.pellet_gas)
		{
			return 6;
		}
		}
		return 0;
	}
	
	public int return5type() {
		
		if(inventory.getStackInSlot(5) != null)
		{
		if(inventory.getStackInSlot(5).getItem() == Items.GUNPOWDER)
		{
			return 1;
		}
		
		if(inventory.getStackInSlot(5).getItem() == Item.byBlock(Blocks.TNT))
		{
			return 2;
		}
		
		if(inventory.getStackInSlot(5).getItem() == ModItems.pellet_cluster)
		{
			return 3;
		}
		
		if(inventory.getStackInSlot(5).getItem() == ModItems.powder_fire)
		{
			return 4;
		}
		
		if(inventory.getStackInSlot(5).getItem() == ModItems.powder_poison)
		{
			return 5;
		}
		
		if(inventory.getStackInSlot(5).getItem() == ModItems.pellet_gas)
		{
			return 6;
		}
		}
		return 0;
	}
	
	public void clearSlots() {
		for(int i = 0; i < inventory.getSlots(); i++)
		{
			inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
	
	@Override
	public AABB getRenderBoundingBox() {
		return BlockEntity.INFINITE_EXTENT_AABB;
	}
	
//	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}
	
//	@Override
//	public boolean hasCapability(Capability<?> capability, Direction facing) {
//		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
//				super.hasCapability(capability, facing);
//	}
//
//	@Override
//	public <T> T getCapability(Capability<T> capability, Direction facing) {
//		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
//				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory) : super.getCapability(capability, facing);
//	}
}
