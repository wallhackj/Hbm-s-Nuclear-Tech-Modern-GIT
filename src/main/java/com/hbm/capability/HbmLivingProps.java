package com.hbm.capability;

import java.util.List;
import java.util.UUID;

import com.hbm.interfaces.IItemHazard;
import com.hbm.capability.HbmLivingCapability.EntityHbmProps;
import com.hbm.capability.HbmLivingCapability.IEntityHbmProps;
import com.hbm.lib.ModDamageSource;
import com.hbm.main.AdvancementManager;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;


public class HbmLivingProps {

	public static final UUID digamma_UUID = UUID.fromString("2a3d8aec-5ab9-4218-9b8b-ca812bdf378b");

	public static IEntityHbmProps getData(LivingEntity entity){
		return entity.getCapability(HbmLivingCapability.EntityHbmPropsProvider.ENT_HBM_PROPS_CAP, null) ?
                (IEntityHbmProps) entity.getCapability(HbmLivingCapability.EntityHbmPropsProvider.ENT_HBM_PROPS_CAP,
						null) :
				HbmLivingCapability.EntityHbmPropsProvider.DUMMY;
	}

	/// RADIATION ///
	public static float getRadiation(LivingEntity entity){
		return getData(entity).getRads();
	}

	public static void setRadiation(LivingEntity entity, float rad){
		getData(entity).setRads(rad);
	}

	public static void incrementRadiation(LivingEntity entity, float rad){
		float radiation = getRadiation(entity) + rad;

		if(radiation > 25000000)
			radiation = 25000000;
		if(radiation < 0)
			radiation = 0;

		setRadiation(entity, radiation);
	}

	// Neutron Radiation

	public static float getNeutron(LivingEntity entity){
		return getData(entity).getNeutrons();
	}

	public static void setNeutron(LivingEntity entity, float rad){
		getData(entity).setNeutrons(rad);
	}


	/// RAD ENV ///
	public static float getRadEnv(LivingEntity entity){
		return getData(entity).getRadsEnv();
	}

	public static void setRadEnv(LivingEntity entity, float rad){
		getData(entity).setRadsEnv(rad);
	}

	/// RAD BUF ///
	public static float getRadBuf(LivingEntity entity){
		return getData(entity).getRadBuf();
	}

	public static void setRadBuf(LivingEntity entity, float rad){
		getData(entity).setRadBuf(rad);
	}

	/// DIGAMA ///
	public static float getDigamma(LivingEntity entity){
		return getData(entity).getDigamma();
	}

	public static void setDigamma(LivingEntity entity, float digamma){

		getData(entity).setDigamma(digamma);

		float healthMod = (float)Math.pow(0.5, digamma) - 1F;

		AttributeInstance attributeinstance = entity.getAttributes().getInstance(MAX_HEALTH);

		try {
			attributeinstance.removeModifier(attributeinstance.getModifier(digamma_UUID));
		} catch(Exception ex) {
		}

		attributeinstance.addPermanentModifier(new AttributeModifier(digamma_UUID, "digamma", healthMod, AttributeModifier.Operation.fromValue(2)));

		if(entity.getHealth() > entity.getMaxHealth()) {
			entity.setHealth(entity.getMaxHealth());
		}

		if((entity.getMaxHealth() <= 0 || digamma >= 10.0F) && entity.isAlive()) {
			entity.setAbsorptionAmount(0);
			entity.hurt(ModDamageSource.digamma, 5000000F);
			entity.setHealth(0);
			entity.die(ModDamageSource.digamma);

			CompoundTag data = new CompoundTag();
			data.putString("type", "sweat");
			data.putInt("count", 50);
			data.putInt("block", Block.getIdFromBlock(Blocks.SOUL_SAND));
			data.putInt("entity", entity.getId());
			PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, 0, 0, 0),
					new Climate.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 50));
		}

		if(entity instanceof Player) {

			float di = getData(entity).getDigamma();

			if(di > 0F)
				AdvancementManager.grantAchievement(((Player)entity), AdvancementManager.digammaSee);
			if(di >= 2F)
				AdvancementManager.grantAchievement(((Player)entity), AdvancementManager.digammaFeel);
			if(di >= 10F)
				AdvancementManager.grantAchievement(((Player)entity), AdvancementManager.digammaKnow);
		}
	}

	public static void incrementDigamma(LivingEntity entity, float digamma){
		float dRad = getDigamma(entity) + digamma;

		if(dRad > 10)
			dRad = 10;
		if(dRad < 0)
			dRad = 0;

		setDigamma(entity, dRad);
	}

	/// ASBESTOS ///
	public static int getAsbestos(LivingEntity entity){
		return getData(entity).getAsbestos();
	}

	public static void setAsbestos(LivingEntity entity, int asbestos){
		getData(entity).setAsbestos(asbestos);

		if(asbestos >= EntityHbmProps.maxAsbestos) {
			getData(entity).setAsbestos(0);
			entity.hurt(ModDamageSource.asbestos, 1000);
		}
	}

	public static void incrementAsbestos(LivingEntity entity, int asbestos){
		setAsbestos(entity, getAsbestos(entity) + asbestos);
	}

	public static void addCont(LivingEntity entity, ContaminationEffect cont){
		getData(entity).getContaminationEffectList().add(cont);
	}

	/// BLACK LUNG DISEASE ///
	public static int getBlackLung(LivingEntity entity){
		return getData(entity).getBlacklung();
	}

	public static void setBlackLung(LivingEntity entity, int blacklung){
		getData(entity).setBlacklung(blacklung);

		if(blacklung >= EntityHbmProps.maxBlacklung) {
			getData(entity).setBlacklung(0);
			entity.hurt(ModDamageSource.blacklung, 1000);
		}
	}

	public static void incrementBlackLung(LivingEntity entity, int blacklung){
		setBlackLung(entity, getBlackLung(entity) + blacklung);
	}

	/// TIME BOMB ///
	public static int getTimer(LivingEntity entity){
		return getData(entity).getBombTimer();
	}

	public static void setTimer(LivingEntity entity, int bombTimer){
		getData(entity).setBombTimer(bombTimer);
	}

	/// CONTAGION ///
	public static int getContagion(LivingEntity entity){
		return getData(entity).getContagion();
	}

	public static void setContagion(LivingEntity entity, int contageon){
		getData(entity).setContagion(contageon);
	}

	public static List<ContaminationEffect> getCont(LivingEntity e){
		return getData(e).getContaminationEffectList();
	}

	public static class ContaminationEffect {

		public float maxRad;
		public int maxTime;
		public int time;
		public boolean ignoreArmor;

		public ContaminationEffect(float rad, int time, boolean ignoreArmor){
			this.maxRad = rad;
			this.maxTime = this.time = time;
			this.ignoreArmor = ignoreArmor;
		}

		public float getRad(){
			return maxRad * ((float)time / (float)maxTime);
		}

		public void save(CompoundTag nbt, int index){
			CompoundTag me = new CompoundTag();
			me.putFloat("maxRad", this.maxRad);
			me.putInt("maxTime", this.maxTime);
			me.putInt("time", this.time);
			me.putBoolean("ignoreArmor", ignoreArmor);
			nbt.put("cont_" + index, me);
		}

		public static ContaminationEffect load(CompoundTag nbt, int index){
			CompoundTag me = (CompoundTag)nbt.get("cont_" + index);
			float maxRad = me.getFloat("maxRad");
			int maxTime = nbt.getInt("maxTime");
			int time = nbt.getInt("time");
			boolean ignoreArmor = nbt.getBoolean("ignoreArmor");

			ContaminationEffect effect = new ContaminationEffect(maxRad, maxTime, ignoreArmor);
			effect.time = time;
			return effect;
		}
	}
}
