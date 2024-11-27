package com.hbm.blocks;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BlockControlPanelType implements StringRepresentable{
    CUSTOM_PANEL,
    FRONT_PANEL;

    @Override
    public @NotNull String getSerializedName() {
        return toString().toLowerCase(Locale.ENGLISH);
    }
}
