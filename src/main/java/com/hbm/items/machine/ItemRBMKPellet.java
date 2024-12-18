package com.hbm.items.machine;

import java.util.List;

import com.hbm.interfaces.IItemHazard;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.modules.ItemHazardModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemRBMKPellet extends Item implements IItemHazard {
    public String fullName = "";
    ItemHazardModule module;

    public ItemRBMKPellet(String fullName, String s) {
        this.setUnlocalizedName(s);
        this.setRegistryName(s);
        this.fullName = fullName;
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(MainRegistry.controlTab);
        this.module = new ItemHazardModule();
        //generateJsons(s);

        ModItems.ALL_ITEMS.add(this);
    }

//    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == CreativeTabs.SEARCH || tab == this.getCreativeTab()) {
            for (int i = 0; i < 10; ++i) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

//    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(ChatFormatting.ITALIC + this.fullName);
        tooltip.add(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + "Pellet for recycling");

        int meta = rectify(stack.getDamageValue());

        switch (meta % 5) {
            case 0:
                tooltip.add(ChatFormatting.GOLD + "Brand New");
                break;
            case 1:
                tooltip.add(ChatFormatting.YELLOW + "Barely Depleted");
                break;
            case 2:
                tooltip.add(ChatFormatting.GREEN + "Moderately Depleted");
                break;
            case 3:
                tooltip.add(ChatFormatting.DARK_GREEN + "Highly Depleted");
                break;
            case 4:
                tooltip.add(ChatFormatting.DARK_GRAY + "Fully Depleted");
                break;
        }

        if (hasXenon(meta))
            tooltip.add(ChatFormatting.DARK_PURPLE + "High Xenon Poison");

        updateModule(stack);
        this.module.addInformation(stack, tooltip, flagIn);
    }

    private boolean hasXenon(int meta) {
        return rectify(meta) >= 5;
    }

    private int rectify(int meta) {
        return Math.abs(meta) % 10;
    }

    @Override
    public ItemHazardModule getModule() {
        return this.module;
    }

//    @Override
    public void onUpdate(ItemStack stack, Level world, Entity entity, int i, boolean b) {

        if (entity instanceof LivingEntity) {
            updateModule(stack);
            this.module.applyEffects((LivingEntity) entity, stack.getCount(), i, b,
                    ((LivingEntity) entity).getMainHandItem() == stack ?
                            InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        }
    }

//    @Override
    public boolean onEntityItemUpdate(Item item) {
        super.onEntityItemUpdate(item);
        updateModule(item.getItem());
        return this.module.onEntityItemUpdate(item);
    }

    private void updateModule(ItemStack stack) {

        int index = stack.getDamageValue() % 5;
        float mod = (index * index) / 5F;

        if (stack.getDamageValue() >= 5) {
            mod *= 10F;
            mod += 1F;
        }

        this.module.setMod(1F + mod);
    }
}