package com.hbm.blocks;

import net.minecraft.client.resources.model.Material;

public class MaterialGas extends Material {
    public MaterialGas() {
        super(MapColor.AIR);
        this.setNoPushMobility();
        this.setReplaceable();
    }

    public boolean isSolid() {
        return true;
    }

    public boolean getCanBlockGrass() {
        return false;
    }

    public boolean blocksMovement() {
        return false;
    }
}
