package com.hbm.entity.item;

import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.phys.Vec3;


public class EntityFireworks extends Entity {

	int color;
	int character;
	
	public EntityFireworks(Level worldIn) {
		super(null, worldIn);
	}
	
	public EntityFireworks(Level world, double x, double y, double z, int color, int character) {
		super(null, world);
//		this.setPositionAndRotation(x, y, z, 0.0F, 0.0F);]
		this.setPos(x, y, z);
		this.color = color;
		this.character = character;
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	public void tick() {
		super.tick();
		this.move(MoverType.SELF, new Vec3(0.0D,3.0D, 0.0D));
		this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0.0, -0.3, 0.0);
		this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, -0.2, 0.0);

		if(!level().isClientSide) {

			tickCount++;

			if(this.tickCount > 30) {

				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST,
						SoundSource.NEUTRAL, 20, 1F + this.random.nextFloat() * 0.2F);

				this.remove(RemovalReason.DISCARDED);
				CompoundTag data = new CompoundTag();
				data.putString("type", "fireworks");
				data.putInt("color", color);
				data.putInt("char", character);
//				PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, posX, posY, posZ),
//						new Climate.TargetPoint(this.level().provider.getDimension(), posX, posY, posZ, 300));
			}
		}
	}

//	@Override
	protected void entityInit() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.character = compound.getInt("char");
		this.color = compound.getInt("color");
		this.tickCount = compound.getInt("ticksExisted");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compoundTag) {

	}

	//	@Override
	protected void writeEntityToNBT(CompoundTag compound) {
		compound.putInt("char", character);
		compound.putInt("color", color);
		compound.putInt("ticksExisted", tickCount);
	}

}
