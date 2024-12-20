package com.hbm.handler;

import java.util.Arrays;
import java.util.UUID;

import com.hbm.items.armor.ItemArmorMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ArmorModHandler {

	public static final int helmet_only = 0;
	public static final int plate_only = 1;
	public static final int legs_only = 2;
	public static final int boots_only = 3;
	public static final int servos = 4;
	public static final int cladding = 5;
	public static final int kevlar = 6;
	public static final int extra = 7;
	
	public static final UUID[] UUIDs = new UUID[] {
			UUID.fromString("8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),
			UUID.fromString("b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
			UUID.fromString("30b50d2a-4858-4e5b-88d4-3e3612224238"),
			UUID.fromString("426ee0d0-7587-4697-aaef-4772ab202e78")
	};
	
	public static final UUID[] fixedUUIDs = new UUID[] {
			UUID.fromString("e572caf4-3e65-4152-bc79-c4d4048cbd29"),
			UUID.fromString("bed30902-8a6a-4769-9f65-2a9b67469fff"),
			UUID.fromString("baebf7b3-1eda-4a14-b233-068e2493e9a2"),
			UUID.fromString("28016c1b-d992-4324-9409-a9f9f0ffb85c")
	};
	
	//The key for the CompoundTag that holds the armor mods
	public static final String MOD_COMPOUND_KEY = "ntm_armor_mods";
	//The key for the specific slot inside the armor mod NBT Tag
	public static final String MOD_SLOT_KEY = "mod_slot_";
	
	/**
	 * Checks if a mod can be applied to an armor piece
	 * Needs to be used to prevent people from inserting invalid items into the armor table
	 * @param armor
	 * @param mod
	 * @return
	 */
	public static boolean isApplicable(ItemStack armor, ItemStack mod) {
		
		if(armor == null || mod == null)
			return false;
		
		if(!(armor.getItem() instanceof ArmorItem))
			return false;
		
		if(!(mod.getItem() instanceof ItemArmorMod))
			return false;
		
		EquipmentSlot type = ((ArmorItem)armor.getItem()).getEquipmentSlot();
		
		ItemArmorMod aMod = (ItemArmorMod)mod.getItem();
		
		return (type == EquipmentSlot.HEAD && aMod.helmet) ||
				(type == EquipmentSlot.CHEST && aMod.chestplate) ||
				(type == EquipmentSlot.LEGS && aMod.leggings) ||
				(type == EquipmentSlot.FEET && aMod.boots);
	}
	
	/**
	 * Applies an mod to the given armor piece
	 * Make sure to check for applicability first
	 * Will override present mods so make sure to only use unmodded armor pieces
	 * @param armor
	 * @param mod
	 */
	public static void applyMod(ItemStack armor, ItemStack mod) {

		if(mod == null || mod.isEmpty() || armor == null || armor.isEmpty()) return;
		
		if(!armor.hasTag())
			armor.setTag(new CompoundTag());
		
		CompoundTag nbt = armor.getTag();
		
		if(!nbt.contains(MOD_COMPOUND_KEY))
			nbt.put(MOD_COMPOUND_KEY, new CompoundTag());
		
		CompoundTag mods = nbt.getCompound(MOD_COMPOUND_KEY);
		
		ItemArmorMod aMod = (ItemArmorMod)mod.getItem();
		int slot = aMod.type;
		
		CompoundTag cmp = new CompoundTag();
		mod.save(cmp);
		
		mods.put(MOD_SLOT_KEY + slot, cmp);
	}
	
	/**
	 * Removes the mod from the given slot
	 * @param armor
	 * @param slot
	 */
	public static void removeMod(ItemStack armor, int slot) {
		
		if(armor == null)
			return;
		
		if(!armor.hasTag())
			armor.setTag(new CompoundTag());
		
		CompoundTag nbt = armor.getTag();
		
		if(!nbt.contains(MOD_COMPOUND_KEY))
			nbt.put(MOD_COMPOUND_KEY, new CompoundTag());
		
		CompoundTag mods = nbt.getCompound(MOD_COMPOUND_KEY);
		mods.remove(MOD_SLOT_KEY + slot);
		
		if(!mods.isEmpty())
			clearMods(armor);
	}
	
	/**
	 * Removes ALL mods
	 * Should be used when the armor piece is put in the armor table slot AFTER the armor pieces have been separated
	 * @param armor
	 */
	public static void clearMods(ItemStack armor) {
		
		if(!armor.hasTag())
			return;
		
		CompoundTag nbt = armor.getTag();
		nbt.remove(MOD_COMPOUND_KEY);
		if(nbt.isEmpty())
			armor.setTag(null);
	}
	
	/**
	 * Does what the name implies
	 * @param armor
	 * @return
	 */
	public static boolean hasMods(ItemStack armor) {
		
		if(!armor.hasTag())
			return false;
		
		return armor.getTag().contains(MOD_COMPOUND_KEY);
	}
	
	public static ItemStack[] pryMods(ItemStack armor) {
		
		ItemStack[] slots = new ItemStack[8];

		if(!hasMods(armor)){
			Arrays.fill(slots, ItemStack.EMPTY);
			return slots;
		}
		
		CompoundTag nbt = armor.getTag();
		CompoundTag mods = nbt.getCompound(MOD_COMPOUND_KEY);
		
		for(int i = 0; i < 8; i++) {
			
			CompoundTag cmp = mods.getCompound(MOD_SLOT_KEY + i);
			
			ItemStack stack = new ItemStack((ItemLike) cmp);
			
			slots[i] = stack;
		}
		
		return slots;
	}
	
	public static ItemStack pryMod(ItemStack armor, int slot){
		if(!hasMods(armor))
			return ItemStack.EMPTY;
		CompoundTag nbt = armor.getTag();
		CompoundTag mods = nbt.getCompound(MOD_COMPOUND_KEY);
		
		CompoundTag cmp = mods.getCompound(MOD_SLOT_KEY + slot);
		ItemStack stack = new ItemStack((ItemLike) cmp);
		
		return stack;
	}
}