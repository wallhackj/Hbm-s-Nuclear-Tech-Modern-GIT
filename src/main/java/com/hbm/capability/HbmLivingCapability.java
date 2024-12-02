package com.hbm.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.hbm.capability.HbmLivingProps.ContaminationEffect;
import net.minecraft.nbt.CompoundTag;

import static org.joml.Math.clamp;

public class HbmLivingCapability {
	
	public interface IEntityHbmProps {
		public float getRads();
		public void setRads(float rads);
		public void increaseRads(float rads);
		public void decreaseRads(float rads);

		public float getNeutrons();
		public void setNeutrons(float rads);
		
		public float getRadsEnv();
		public void setRadsEnv(float rads);
		
		public float getRadBuf();
		public void setRadBuf(float buf);
		
		public float getDigamma();
		public void setDigamma(float dig);
		public void increaseDigamma(float dig);
		public void decreaseDigamma(float dig);
		
		public int getAsbestos();
		public void setAsbestos(int asbestos);
		
		public int getBlacklung();
		public void setBlacklung(int blacklung);
		
		public int getBombTimer();
		public void setBombTimer(int bombTimer);
		
		public int getContagion();
		public void setContagion(int cont);
		
		public List<ContaminationEffect> getContaminationEffectList();
		
		public void saveNBTData(CompoundTag tag);
		public void loadNBTData(CompoundTag tag);
	}
	
	public static class EntityHbmProps implements IEntityHbmProps {

		public static final Callable<IEntityHbmProps> FACTORY = EntityHbmProps::new;
		
		private float rads = 0;
		private float neutrons = 0;
		private float envRads = 0;
		private float radBuf = 0;
		private float digamma = 0;
		private int asbestos = 0;
		public static final int maxAsbestos = 60 * 60 * 20;
		private int blacklung;
		public static final int maxBlacklung = 60 * 60 * 20;
		private int bombTimer;
		private int contagion;
		private List<ContaminationEffect> contamination = new ArrayList<>();
		
		@Override
		public float getRads() {
			return rads;
		}

		@Override
		public void setRads(float rads) {
			this.rads = clamp(rads, 0, 2500);
		}

		@Override
		public float getNeutrons() {
			return neutrons;
		}

		@Override
		public void setNeutrons(float neutrons) {
			this.neutrons = Math.max(neutrons, 0);
		}
		
		@Override
		public void increaseRads(float rads){
			this.rads = clamp(this.rads + rads, 0, 2500);
		}
		
		@Override
		public void decreaseRads(float rads){
			this.rads = clamp(this.rads - rads, 0, 2500);
		}

		@Override
		public float getRadsEnv(){
			return envRads;
		}

		@Override
		public void setRadsEnv(float rads){
			envRads = rads;
		}

		@Override
		public float getRadBuf(){
			return radBuf;
		}

		@Override
		public void setRadBuf(float buf){
			radBuf = buf;
		}

		@Override
		public float getDigamma(){
			return digamma;
		}

		@Override
		public void setDigamma(float dig){
			digamma = dig;
		}

		@Override
		public void increaseDigamma(float dig){
			this.digamma = clamp(this.digamma + dig, 0, 1000);
		}
		
		@Override
		public void decreaseDigamma(float dig){
			this.digamma = clamp(this.digamma - dig, 0, 1000);
		}

		@Override
		public int getAsbestos(){
			return asbestos;
		}

		@Override
		public void setAsbestos(int asbestos){
			this.asbestos = asbestos;
		}

		@Override
		public int getBlacklung(){
			return blacklung;
		}

		@Override
		public void setBlacklung(int blacklung){
			this.blacklung = blacklung;
		}

		@Override
		public int getBombTimer(){
			return bombTimer;
		}

		@Override
		public void setBombTimer(int bombTimer){
			this.bombTimer = bombTimer;
		}

		@Override
		public int getContagion(){
			return contagion;
		}

		@Override
		public void setContagion(int cont){
			contagion = cont;
		}
		
		@Override
		public List<ContaminationEffect> getContaminationEffectList(){
			return contamination;
		}
		
		@Override
		public void saveNBTData(CompoundTag tag){
			tag.putFloat("rads", getRads());
			tag.putFloat("neutrons", getNeutrons());
			tag.putFloat("envRads", getRadsEnv());
			tag.putFloat("radBuf", getRadBuf());
			tag.putFloat("digamma", getDigamma());
			tag.putInt("asbestos", getAsbestos());
			tag.putInt("blacklung", blacklung);
			tag.putInt("bombtimer", bombTimer);
			tag.putInt("contagion", contagion);
			tag.putInt("conteffectsize", contamination.size());
			for(int i = 0; i < contamination.size(); i ++){
				contamination.get(i).save(tag, i);
			}
		}

		@Override
		public void loadNBTData(CompoundTag tag){
			setRads(tag.getFloat("rads"));
			setNeutrons(tag.getFloat("neutrons"));
			setRadsEnv(tag.getFloat("envRads"));
			setRadBuf(tag.getFloat("radBuf"));
			setDigamma(tag.getFloat("digamma"));
			setAsbestos(tag.getInt("asbestos"));
			setBlacklung(tag.getInt("blacklung"));
			setBombTimer(tag.getInt("bombtimer"));
			setContagion(tag.getInt("contagion"));
			contamination.clear();
			for(int i = 0; i < tag.getInt("conteffectsize"); i ++){
				contamination.add(ContaminationEffect.load(tag, i));
			}
		}
	}
	
//	public static class EntityHbmPropsStorage implements Capability.IStorage<IEntityHbmProps>{
//
//		public CompoundTag writeNBT(Capability<IEntityHbmProps> capability, IEntityHbmProps instance, Direction side) {
//			CompoundTag tag = new CompoundTag();
//			instance.saveNBTData(tag);
//			return tag;
//		}
//
//		@Override
//		public void readNBT(Capability<IEntityHbmProps> capability, IEntityHbmProps instance, Direction side, NBTBase nbt) {
//			if(nbt instanceof CompoundTag){
//				CompoundTag tag = (CompoundTag)nbt;
//				instance.loadNBTData(tag);
//			}
//		}

	}

