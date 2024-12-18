package com.hbm.hazard;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class HazardModifier {
    public abstract float modify(ItemStack stack, LivingEntity holder, float level);

    /**
     * Returns the level after applying all modifiers to it, in order.
     *
     * @param stack
     * @param entity
     * @param level
     * @param mods
     * @return
     */
    public static float evalAllModifiers(ItemStack stack, LivingEntity entity, float level, List<HazardModifier> mods) {

        for (HazardModifier mod : mods) {
            level = mod.modify(stack, entity, level);
        }

        return level;
    }
}
