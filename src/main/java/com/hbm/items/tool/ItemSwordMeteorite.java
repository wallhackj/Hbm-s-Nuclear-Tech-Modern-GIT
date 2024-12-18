package com.hbm.items.tool;

import java.util.List;

import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemSwordMeteorite extends ItemSwordAbility {
    public ItemSwordMeteorite(float damage, double movement, ToolMaterial material, String s) {
        super(damage, movement, material, s);
        this.setMaxDamage(0);
    }

//    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> list, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        if (this.equals() == ModItems.meteorite_sword) {
            list.add(ChatFormatting.ITALIC + "Forged from a fallen star");
            list.add(ChatFormatting.ITALIC + "Sharper than most terrestrial blades");
        }

        if (this == ModItems.meteorite_sword_seared) {
            list.add(ChatFormatting.ITALIC + "Fire strengthens the blade");
            list.add(ChatFormatting.ITALIC + "Making it even more powerful");
        }

        if (this == ModItems.meteorite_sword_reforged) {
            list.add(ChatFormatting.ITALIC + "The sword has been reforged");
            list.add(ChatFormatting.ITALIC + "To rectify past imperfections");
        }

        if (this == ModItems.meteorite_sword_hardened) {
            list.add(ChatFormatting.ITALIC + "Extremely high pressure has been used");
            list.add(ChatFormatting.ITALIC + "To harden the blade further");
        }

        if (this == ModItems.meteorite_sword_alloyed) {
            list.add(ChatFormatting.ITALIC + "Cobalt fills the fissures");
            list.add(ChatFormatting.ITALIC + "Strengthening the sword");
        }

        if (this == ModItems.meteorite_sword_machined) {
            list.add(ChatFormatting.ITALIC + "Advanced machinery was used");
            list.add(ChatFormatting.ITALIC + "To refine the blade even more");
        }

        if (this == ModItems.meteorite_sword_treated) {
            list.add(ChatFormatting.ITALIC + "Chemicals have been applied");
            list.add(ChatFormatting.ITALIC + "Making the sword more powerful");
        }

        if (this == ModItems.meteorite_sword_etched) {
            list.add(ChatFormatting.ITALIC + "Acids clean the material");
            list.add(ChatFormatting.ITALIC + "To make this the perfect sword");
        }

        if (this == ModItems.meteorite_sword_bred) {
            list.add(ChatFormatting.ITALIC + "Immense heat and radiation");
            list.add(ChatFormatting.ITALIC + "Compress the material");
        }

        if (this == ModItems.meteorite_sword_irradiated) {
            list.add(ChatFormatting.ITALIC + "The power of the Atom");
            list.add(ChatFormatting.ITALIC + "Gives the sword might");
        }

        if (this.equals(ModItems.meteorite_sword_fused)) {
            list.add(ChatFormatting.ITALIC + "This blade has met");
            list.add(ChatFormatting.ITALIC + "With the forces of the stars");
        }

        if (this == ModItems.meteorite_sword_baleful) {
            list.add(ChatFormatting.ITALIC + "This sword has met temperatures");
            list.add(ChatFormatting.ITALIC + "Far beyond what normal material can endure");
        }

        if (this == ModItems.meteorite_sword_warped) {
            list.add(ChatFormatting.ITALIC + "This sword experienced warping of reality");
            list.add(ChatFormatting.ITALIC + "It was stretched to a length of 10^10^187 ly");
            list.add(ChatFormatting.ITALIC + "and is now older than this universe");
        }

        if (this == ModItems.meteorite_sword_demonic) {
            list.add(ChatFormatting.ITALIC + "This sword has met §f§oGOD§7§o and the §4§oDEVIL§r");
            list.add(ChatFormatting.ITALIC + "It was transported to §4§o§kdemoniclove§7");
            list.add(ChatFormatting.ITALIC + "and came in contact with §4§o§ktheevilandthegood§r");
            list.add("§0[Infohazard]§r");
        }
    }

}
