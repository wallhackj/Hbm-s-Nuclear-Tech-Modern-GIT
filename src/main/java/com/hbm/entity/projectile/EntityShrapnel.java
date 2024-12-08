package com.hbm.entity.projectile;

import com.hbm.config.CompatibilityConfig;
import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.lib.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static net.minecraft.world.item.Items.AIR;


public class EntityShrapnel extends ThrowableProjectile {
	
	public static final EntityDataAccessor<Byte> TRAIL = SynchedEntityData.defineId(EntityShrapnel.class,
			EntityDataSerializers.BYTE);

    public EntityShrapnel(Level p_i1773_1_) {
        super(null,p_i1773_1_);
    }

    public EntityShrapnel(Level p_i1774_1_, LivingEntity p_i1774_2_) {
		super(null, p_i1774_1_);
    }

//    @Override
	public void entityInit() {
        this.entityData.define(TRAIL, (byte) 0);
    }

    public EntityShrapnel(Level p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
        super(null, p_i1775_6_, p_i1775_2_, p_i1775_4_,p_i1775_1_);
    }
    
//    @Override
    public void onUpdate() {
    	super.tick();
    	if(level().isClientSide)
    		level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
    }

//    @Override
	protected void onImpact(HitResult mop) {
		super.onHit(mop);
    	if(!CompatibilityConfig.isWarDim(level())){
			this.discard();
			return;
		}
		if (mop.getType() == HitResult.Type.ENTITY) {
            ((EntityHitResult) mop).getEntity();
            ((EntityHitResult) mop).getEntity().hurt(ModDamageSource.shrapnel, 15);
        }

        if(this.tickCount > 5) {
        	this.discard();
        	if(this.entityData.get(TRAIL) == 2) {
				
				if(!level().isClientSide && mop.getType() == HitResult.Type.BLOCK) {
                    BlockPos hitPos = ((BlockHitResult) mop).getBlockPos();

                    if (this.getDeltaMovement().y < -0.2D) {

                        if (level().getBlockState(hitPos.above()).canBeReplaced())
                            level().setBlockAndUpdate(hitPos.above(), ModBlocks.volcanic_lava_block.defaultBlockState());

                        for (int x = hitPos.getX() - 1; x <= hitPos.getX() + 1; x++) {
                            for (int y = hitPos.getY(); y <= hitPos.getY() + 2; y++) {
                                for (int z = hitPos.getZ() - 1; z <= hitPos.getZ() + 1; z++) {
                                    if (level().getBlockState(new BlockPos(x, y, z)).isAir())
                                        level().setBlockAndUpdate(new BlockPos(x, y, z),
                                                ModBlocks.gas_monoxide.defaultBlockState());
                                }
                            }
                        }
                    }

                    if (this.getDeltaMovement().y > 0) {
                        ExplosionNT explosion = new ExplosionNT(level(), null, mop.getLocation().x() + 0.5,
                                mop.getLocation().y() + 0.5, mop.getLocation().z() + 0.5, 7);
                        explosion.addAttrib(ExAttrib.NODROP);
                        explosion.addAttrib(ExAttrib.LAVA_V);
                        explosion.addAttrib(ExAttrib.NOSOUND);
                        explosion.addAttrib(ExAttrib.ALLMOD);
                        explosion.addAttrib(ExAttrib.NOHURT);
                        explosion.explode();
                    }
                }
				
			} else {
				for(int i = 0; i < 5; i++)
	        		level().addParticle(ParticleTypes.LAVA, getX(), getY(), getZ(), 0.0,
                            0.0, 0.0);
			}

        	level().playSound(null, getX(), getY(), getZ(), SoundEvents.LAVA_EXTINGUISH,
                    SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }
    
    public void setTrail(boolean b) {
        	this.entityData.set(TRAIL, (byte)(b ? 1 : 0));
    }
    
    public void setVolcano(boolean b) {
		this.entityData.set(TRAIL, (byte) (b ? 2 : 0));
	}

    @Override
    protected void defineSynchedData() {

    }
}