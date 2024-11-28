package com.hbm.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Compat {

    // Method to load an Item by domain and name
    public static Item tryLoadItem(String domain, String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(domain, name));
    }

    // Method to load a Block by domain and name
    public static Block tryLoadBlock(String domain, String name) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(domain, name));

    }
}
