package com.hbm.hazard.type;

import java.util.List;

import com.hbm.config.GeneralConfig;
import com.hbm.hazard.HazardModifier;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class HazardTypeRadiation extends HazardTypeBase {

    @Override
    public void onUpdate(LivingEntity target, float level, ItemStack stack) {

        boolean reacher = false;

        if (target instanceof Player && !GeneralConfig.enable528)
            reacher = Library.checkForHeld((Player) target, ModItems.reacher);

        if (level > 0) {
            float rad = level / 20F;

            if (reacher)
                rad = (float) Math.min(Math.sqrt(rad), rad); //to prevent radiation from going up when being <1

            ContaminationUtil.contaminate(target, HazardType.RADIATION, ContaminationType.CREATIVE, rad);
        }
    }

    @Override
    public void updateEntity(Item item, float level) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHazardInformation(Player player, List<String> list, float level, ItemStack stack, List<HazardModifier> modifiers) {

        level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);

        list.add(ChatFormatting.GREEN + "[" + I18nUtil.resolveKey("trait.radioactive") + "]");
        String rad = "" + (Math.floor(level * 1000) / 1000);
        list.add(ChatFormatting.YELLOW + rad + " " + I18nUtil.resolveKey("desc.rads"));

        if (stack.getCount() > 1) {
            list.add(ChatFormatting.YELLOW + I18nUtil.resolveKey("desc.stack") + " " + (Math.floor(level * 1000 * stack.getCount()) / 1000) + " " + I18nUtil.resolveKey("desc.rads"));
        }
    }

}