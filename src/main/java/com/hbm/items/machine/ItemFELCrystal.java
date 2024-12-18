package com.hbm.items.machine;

import java.util.List;

import com.hbm.items.ModItems;
import com.hbm.util.I18nUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemFELCrystal extends Item {

    public EnumWavelengths wavelength = EnumWavelengths.NULL;

    public ItemFELCrystal(EnumWavelengths wavelength, String s) {
        this.wavelength = wavelength;
        this.setMaxStackSize(1);
        this.setUnlocalizedName(s);
        this.setRegistryName(s);

        ModItems.ALL_ITEMS.add(this);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> list, TooltipFlag flagIn) {
        String desc = (stack.getItem() == ModItems.laser_crystal_digamma) ? (ChatFormatting.OBFUSCATED
                + "THERADIANCEOFATHOUSANDSUNS") : (this.getUnlocalizedNameInefficiently(stack) + ".desc");
        list.add(I18nUtil.resolveKey(desc));
        list.add(wavelength.textColor + I18nUtil.resolveKey(wavelength.name) + " - " + wavelength.textColor +
                I18nUtil.resolveKey(this.wavelength.wavelengthRange));
    }

    public static enum EnumWavelengths {
        NULL("la creatura", "6 dollar", 0x010101, 0x010101, ChatFormatting.WHITE), //why do you exist?

        RADIO("wavelengths.name.radio", "wavelengths.waveRange.radio", 0x3E8010,
                0x80FF00, ChatFormatting.YELLOW),
        MICRO("wavelengths.name.micro", "wavelengths.waveRange.micro", 0x804010,
                0xFF8000, ChatFormatting.GOLD),
        IR("wavelengths.name.ir", "wavelengths.waveRange.ir", 0x802010, 0xFE2010,
                ChatFormatting.RED),
        VISIBLE("wavelengths.name.visible", "wavelengths.waveRange.visible", 0, 0,
                ChatFormatting.GREEN),
        UV("wavelengths.name.uv", "wavelengths.waveRange.uv", 0x801080, 0xFF00FF,
                ChatFormatting.LIGHT_PURPLE),
        XRAY("wavelengths.name.xray", "wavelengths.waveRange.xray", 0x108080, 0x00FFFF,
                ChatFormatting.DARK_AQUA),
        GAMMA("wavelengths.name.gamma", "wavelengths.waveRange.gamma", 0x108010, 0x00FF00,
                ChatFormatting.DARK_GREEN),
        DRX("wavelengths.name.drx", "wavelengths.waveRange.drx", 0x801010, 0xFF0000,
                ChatFormatting.DARK_RED);

        public String name = "";
        public String wavelengthRange = "";
        public int renderedBeamColor;
        public int guiColor;
        public ChatFormatting textColor;

        private EnumWavelengths(String name, String wavelength, int color, int guiColor, ChatFormatting textColor) {
            this.name = name;
            this.wavelengthRange = wavelength;
            this.renderedBeamColor = color;
            this.guiColor = guiColor;
            this.textColor = textColor;
        }
    }
}