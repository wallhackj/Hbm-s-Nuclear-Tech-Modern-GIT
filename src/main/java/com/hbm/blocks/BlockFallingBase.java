package com.hbm.blocks;

import java.util.List;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;


public class BlockFallingBase extends FallingBlock {

    public BlockFallingBase(Material m, String s, SoundType type) {
        super(null);
//		super(m);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
//		this.setCreativeTab(MainRegistry.controlTab);
//		this.setHarvestLevel("shovel", 0);
//		this.setSoundType(type);
//		ModBlocks.ALL_BLOCKS.add(this);
    }

    //	@Override
    public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
        if (this.equals(ModBlocks.gravel_diamond)) {
            tooltip.add("There is some kind of joke here,");
            tooltip.add("but I can't quite tell what it is.");
            tooltip.add("");
            tooltip.add("Update, 2020-07-04:");
            tooltip.add("We deny any implications of a joke on");
            tooltip.add("the basis that it was so severely unfunny");
            tooltip.add("that people started stabbing their eyes out.");
            tooltip.add("");
            tooltip.add("Update, 2020-17-04:");
            tooltip.add("As it turns out, \"Diamond Gravel\" was");
            tooltip.add("never really a thing, rendering what might");
            tooltip.add("have been a joke as totally nonsensical.");
            tooltip.add("We apologize for getting your hopes up with");
            tooltip.add("this non-joke that hasn't been made.");
            tooltip.add("");
            tooltip.add("i added an item for a joke that isn't even here, what am i, stupid? can't even tell the difference between gravel and a gavel, how did i not forget how to breathe yet?");
        }

        if (this.equals(ModBlocks.sand_boron)) {
            tooltip.add("Used to reduce reactivity and increase cooldown in destroyed RBMK cores.");
        }
    }

}
