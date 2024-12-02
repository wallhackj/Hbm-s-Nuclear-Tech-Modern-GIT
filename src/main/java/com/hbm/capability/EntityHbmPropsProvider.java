package com.hbm.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityHbmPropsProvider implements ICapabilitySerializable<CompoundTag> {

    public static final HbmLivingCapability.IEntityHbmProps DUMMY = new HbmLivingCapability.IEntityHbmProps() {
        @Override
        public float getRads() {
            return 0;
        }

        @Override
        public void setRads(float rads) {
        }

        @Override
        public float getNeutrons() {
            return 0;
        }

        @Override
        public void setNeutrons(float neutrons) {
        }

        @Override
        public void increaseRads(float rads) {
        }

        @Override
        public void decreaseRads(float rads) {
        }

        @Override
        public float getRadsEnv() {
            return 0;
        }

        @Override
        public void setRadsEnv(float rads) {
        }

        @Override
        public float getRadBuf() {
            return 0;
        }

        @Override
        public void setRadBuf(float buf) {
        }

        @Override
        public float getDigamma() {
            return 0;
        }

        @Override
        public void setDigamma(float dig) {
        }

        @Override
        public void increaseDigamma(float dig) {
        }

        @Override
        public void decreaseDigamma(float dig) {
        }

        @Override
        public int getAsbestos() {
            return 0;
        }

        @Override
        public void setAsbestos(int asbestos) {
        }

        @Override
        public void saveNBTData(CompoundTag tag) {
        }

        @Override
        public void loadNBTData(CompoundTag tag) {
        }

        @Override
        public List<HbmLivingProps.ContaminationEffect> getContaminationEffectList() {
            return new ArrayList<>(0);
        }

        @Override
        public int getBlacklung() {
            return 0;
        }

        @Override
        public void setBlacklung(int blacklung) {
        }

        @Override
        public int getBombTimer() {
            return 0;
        }

        @Override
        public void setBombTimer(int bombTimer) {
        }

        @Override
        public int getContagion() {
            return 0;
        }

        @Override
        public void setContagion(int cont) {
        }
    };

    public static final Capability<HbmLivingCapability.IEntityHbmProps> ENT_HBM_PROPS_CAP = CapabilityManager
            .get(new CapabilityToken<>() {});

//    private HbmLivingCapability.IEntityHbmProps instance = ENT_HBM_PROPS_CAP.getDefaultInstance();

//    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return capability == ENT_HBM_PROPS_CAP;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == ENT_HBM_PROPS_CAP) {
            // Return instance, manually cast to the correct type
//            return (LazyOptional<T>) this.instance;
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
//        return ENT_HBM_PROPS_CAP.getStorage().writeNBT(ENT_HBM_PROPS_CAP, instance, null);
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
//        ENT_HBM_PROPS_CAP.getStorage().readNBT(ENT_HBM_PROPS_CAP, instance, null, nbt);
    }

}
