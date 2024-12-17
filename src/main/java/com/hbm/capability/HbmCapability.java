package com.hbm.capability;

import java.util.concurrent.Callable;
import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.main.MainRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class HbmCapability {

	public interface IHBMData {
		public boolean getKeyPressed(EnumKeybind key);
		public void setKeyPressed(EnumKeybind key, boolean pressed);
		public boolean getEnableBackpack();
		public boolean getEnableHUD();
		public void setEnableBackpack(boolean b);
		public void setEnableHUD(boolean b);
		
		public default boolean isJetpackActive() {
			return getEnableBackpack() && getKeyPressed(EnumKeybind.JETPACK);
		}
	}
	
	public static class HBMData implements IHBMData {

		public static final Callable<IHBMData> FACTORY = () -> {return new HBMData();};
		
		private boolean[] keysPressed = new boolean[EnumKeybind.values().length];
		
		public boolean enableBackpack = true;
		public boolean enableHUD = true;
		
		@Override
		public boolean getKeyPressed(EnumKeybind key) {
			return keysPressed[key.ordinal()];
		}

		@Override
		public void setKeyPressed(EnumKeybind key, boolean pressed) {
			if(!getKeyPressed(key) && pressed) {
				
				if(key == EnumKeybind.TOGGLE_JETPACK) {
					this.enableBackpack = !this.enableBackpack;
					
					if(this.enableBackpack)
						MainRegistry.proxy.displayTooltip(ChatFormatting.GREEN + "Jetpack ON");
					else
						MainRegistry.proxy.displayTooltip(ChatFormatting.RED + "Jetpack OFF");
				}
				if(key == EnumKeybind.TOGGLE_HEAD) {
					this.enableHUD = !this.enableHUD;
					
					if(this.enableHUD)
						MainRegistry.proxy.displayTooltip(ChatFormatting.GREEN + "HUD ON");
					else
						MainRegistry.proxy.displayTooltip(ChatFormatting.RED + "HUD OFF");
				}
			}
			keysPressed[key.ordinal()] = pressed;
		}
		
		@Override
		public boolean getEnableBackpack(){
			return enableBackpack;
		}

		@Override
		public boolean getEnableHUD(){
			return enableHUD;
		}

		@Override
		public void setEnableBackpack(boolean b){
			enableBackpack = b;
		}

		@Override
		public void setEnableHUD(boolean b){
			enableHUD = b;
		}
		
	}
	
	public static class HBMDataStorage {

		public CompoundTag writeNBT(Capability<IHBMData> capability, IHBMData instance, Direction side) {
			CompoundTag tag = new CompoundTag();
			for(EnumKeybind key : EnumKeybind.values()){
				tag.putBoolean(key.name(), instance.getKeyPressed(key));
			}
			tag.putBoolean("enableBackpack", instance.getEnableBackpack());
			tag.putBoolean("enableHUD", instance.getEnableHUD());
			return tag;
		}

		public void readNBT(Capability<IHBMData> capability, IHBMData instance, Direction side, CompoundTag nbt) {
			if(nbt instanceof CompoundTag){
				CompoundTag tag = nbt;
				for(EnumKeybind key : EnumKeybind.values()){
					instance.setKeyPressed(key, tag.getBoolean(key.name()));
				}
				instance.setEnableBackpack(tag.getBoolean("enableBackpack"));
				instance.setEnableHUD(tag.getBoolean("enableHUD"));
			}
		}
		
	}
	
	public static class HBMDataProvider implements ICapabilitySerializable<CompoundTag> {

		public static final IHBMData DUMMY = new IHBMData(){

			@Override
			public boolean getKeyPressed(EnumKeybind key) {
				return false;
			}

			@Override
			public void setKeyPressed(EnumKeybind key, boolean pressed) {
			}

			@Override
			public boolean getEnableBackpack(){
				return false;
			}

			@Override
			public boolean getEnableHUD(){
				return false;
			}

			@Override
			public void setEnableBackpack(boolean b){
			}

			@Override
			public void setEnableHUD(boolean b){
			}
		};

		public static final Capability<IHBMData> HBM_CAP = CapabilityManager.get(new CapabilityToken<>() {});

		private final LazyOptional<IHBMData> instance = LazyOptional.of(HBMData::new);
		
//		private IHBMData instance = HBM_CAP.getDefaultInstance();
		@Override
		public boolean hasCapability(Capability<?> capability, Direction facing) {
			return capability == HBM_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, Direction facing) {
			return capability == HBM_CAP ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public CompoundTag serializeNBT() {
			return HBM_CAP.getStorage().writeNBT(HBM_CAP, instance, null);
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			HBM_CAP.getStorage().readNBT(HBM_CAP, instance, null, nbt);
		}
		
	}
	
	public static IHBMData getData(Entity e){
//		if(e.hasCapability(HBMDataProvider.HBM_CAP, null))
//			return e.getCapability(HBMDataProvider.HBM_CAP, null);
//		return HBMDataProvider.DUMMY;
		return e.getCapability(HBMDataProvider.HBM_CAP)
				.orElseThrow(() -> new IllegalStateException("Capability not present!"));
	}
}
