package com.hbm.entity.grenade;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeGeneric extends EntityGrenadeBouncyBase {

    public EntityGrenadeGeneric(Level p_i1773_1_) {
        super(p_i1773_1_);
    }

    public EntityGrenadeGeneric(Level p_i1774_1_, LivingEntity p_i1774_2_, InteractionHand hand) {
        super(p_i1774_1_, p_i1774_2_, hand);
    }

    public EntityGrenadeGeneric(Level p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
        super(p_i1775_1_, p_i1775_2_, p_i1775_4_, p_i1775_6_);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0F,
                    Level.ExplosionInteraction.TNT);
        }
    }

    @Override
    protected int getMaxTimer() {
        return 100;
    }

    @Override
    protected double getBounceMod() {
        return 0.25D;
    }

    @Override
    protected void defineSynchedData() {

    }
}
