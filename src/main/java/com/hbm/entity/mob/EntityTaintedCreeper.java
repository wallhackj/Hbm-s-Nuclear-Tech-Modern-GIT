package com.hbm.entity.mob;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockTaint;
import com.hbm.config.GeneralConfig;
import com.hbm.interfaces.IRadiationImmune;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


public class EntityTaintedCreeper extends Creeper implements IRadiationImmune {
	
	private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(EntityTaintedCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(EntityTaintedCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IGNITED = SynchedEntityData.defineId(EntityTaintedCreeper.class, EntityDataSerializers.BOOLEAN);

	 /**
     * Time when this creeper was last in an active state (Messed up code here, probably causes creeper animation to go
     * weird)
     */
    private int lastActiveTime;
    /** The amount of time since the creeper was close enough to the player to ignite */
    private int timeSinceIgnited;
    private int fuseTime = 30;
    /** Explosion radius for this creeper. */
    private int explosionRadius = 20;
    private static final String __OBFID = "CL_00001684";
	public EntityTaintedCreeper(Level world) {
		super(null, world);
        this.goalSelector.addGoal(1, new FloatGoal(this));
//        this.goalSelector.addGoal(2, new EntityAITaintedCreeperSwell(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
//			this.tasks.addTask(1, new EntityAISwimming(this));
//	        this.tasks.addTask(2, new EntityAITaintedCreeperSwell(this));
//	        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, false));
//	        this.tasks.addTask(4, new EntityAIWander(this, 0.8D));
//	        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
//	        this.tasks.addTask(6, new EntityAILookIdle(this));
//	        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
//	        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
//	        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityOcelot>(this, EntityOcelot.class, true));
	}
	
//	 @Override
		protected void applyEntityAttributes()
	    {
//	        super.applyEntityAttributes();
//	        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
//	        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
            super.getAttributes();
	    }
	 
//	 @Override
	public boolean isAIDisabled() {
		return false;
	}

	 /**
	     * The maximum height from where the entity is allowed to jump (used in pathfinder)
	     */
	    public int getMaxFallHeight()
	    {
            this.getMeleeAttackReferencePosition();
            return 3 + (int)(this.getHealth() - 1.0F);
	    }

	    public void fall(float distance, float damageMultiplier)
	    {
	        super.calculateFallDamage(distance, damageMultiplier);
	        this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + distance * 1.5F);

	        if (this.timeSinceIgnited > this.fuseTime - 5)
	        {
	            this.timeSinceIgnited = this.fuseTime - 5;
	        }
	    }
//	@Override
	protected void entityInit() {
//		super.entityInit();
//		this.dataManager.register(STATE, -1);
//		this.dataManager.register(POWERED, Boolean.FALSE);
//        this.dataManager.register(IGNITED, Boolean.FALSE);
        super.defineSynchedData();
        this.entityData.define(EntityTaintedCreeper.STATE, -1);
        this.entityData.define(EntityTaintedCreeper.POWERED, false);
        this.entityData.define(EntityTaintedCreeper.IGNITED, false);
	}
	
//	@Override
	public void writeEntityToNBT(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);

        if (this.entityData.get(POWERED))
        {
            compound.putBoolean("powered", true);
        }

        compound.putShort("Fuse", (short)this.fuseTime);
        compound.putByte("ExplosionRadius", (byte)this.explosionRadius);
        compound.putBoolean("ignited", this.hasIgnited());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
//	@Override
    public void readEntityFromNBT(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.entityData.set(POWERED, compound.getBoolean("powered"));

        if (compound.contains("Fuse", 99))
        {
            this.fuseTime = compound.getShort("Fuse");
        }

        if (compound.contains("ExplosionRadius", 99))
        {
            this.explosionRadius = compound.getByte("ExplosionRadius");
        }

        if (compound.getBoolean("ignited"))
        {
            this.ignite();
        }
    }
	
	/**
     * Called to update the entity's position/logic.
     */
//    @Override
	public void onUpdate()
    {
        if (this.isAlive())
        {
            this.lastActiveTime = this.timeSinceIgnited;

            if (this.hasIgnited())
            {
                this.setCreeperState(1);
            }

            int i = this.getCreeperState();

            if (i > 0 && this.timeSinceIgnited == 0)
            {
                this.playSound(SoundEvents.CREEPER_HURT, 1.0F * 30 / 75, 0.5F);
                
            }

            this.timeSinceIgnited += i;

            if (this.timeSinceIgnited < 0)
            {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime)
            {
                this.timeSinceIgnited = this.fuseTime;
                this.explode();
            }
        }

        super.tick();
        
        if(this.getHealth() < this.getMaxHealth() && this.tickCount % 10 == 0)
        {
        	this.heal(1.0F);
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */   
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    	return SoundEvents.CREEPER_HURT;
    }

    /**
     * Returns the sound this mob makes on death.
     */
	@Override
	protected @NotNull SoundEvent getDeathSound() {
		return SoundEvents.CREEPER_DEATH;
	}

    /**
     * Called when the mob's health reaches 0.
     */
//    @Override
	public void onDeath(DamageSource p_70645_1_)
    {
        super.die(p_70645_1_);
    }

//    @Override
	public boolean attackEntityAsMob(Entity p_70652_1_)
    {
        return true;
    }

    /**
     * Returns true if the creeper is powered by a lightning bolt.
     */
    public boolean getPowered()
    {
        return this.entityData.get(POWERED);
    }

    /**
     * Params: (Float)Render tick. Returns the intensity of the creeper's flash when it is ignited.
     */
    @OnlyIn(Dist.CLIENT)
    public float getCreeperFlashIntensity(float p_70831_1_)
    {
        return (this.lastActiveTime + (this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (this.fuseTime - 2);
    }

//    @Override
	protected Item getDropItem()
    {
        return Item.byBlock(Blocks.TNT);
    }

    /**
     * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
     */
    public int getCreeperState()
    {
        return this.entityData.get(STATE);
    }

    /**
     * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
     */
    public void setCreeperState(int i)
    {
        this.entityData.set(STATE, i);
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
//    @Override
	public void onStruckByLightning(LightningBolt p_70077_1_)
    {
        super.thunderHit(null,p_70077_1_);
        this.entityData.set(POWERED, Boolean.TRUE);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);

        if (itemstack.getItem() == Items.FLINT_AND_STEEL)
        {
        	this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE,
                    this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            player.swing(hand);

            if (!this.level().isClientSide)
            {
                this.ignite();
                itemstack.setDamageValue(1);
            }
        }

        return super.mobInteract(player, hand);
    }
   

    private void explode()
    {
        if (!this.level().isClientSide)
        {

            if (this.getPowered())
            {
            	this.explosionRadius *= 3;
            }

            level().explode(this, getX(), getY(), getZ(), 5.0F, null);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            if(this.getPowered())
            {
	            
			    for(int i = 0; i < 255; i++) {
			    	int a = random.nextInt(15) + (int)getX() - 7;
			    	int b = random.nextInt(15) + (int)getY() - 7;
			    	int c = random.nextInt(15) + (int)getZ() - 7;
			    	pos.move(a, b, c);
			           if(level().getBlockState(pos).canBeReplaced() && BlockTaint.hasPosNeightbour(level(), pos)) {
			        	   
			        	   if(GeneralConfig.enableHardcoreTaint)
			        		   level().setBlock(pos, ModBlocks.taint.defaultBlockState().setValue(BlockTaint.TEXTURE,
                                       random.nextInt(3) + 5), 2);
			        	   else
			        		   level().setBlock(pos, ModBlocks.taint.defaultBlockState().setValue(BlockTaint.TEXTURE,
                                       random.nextInt(3)), 2);
			           }
			    }
			    
            } else {
	            
			    for(int i = 0; i < 85; i++) {
			    	int a = random.nextInt(7) + (int)getX() - 3;
			    	int b = random.nextInt(7) + (int)getY() - 3;
			    	int c = random.nextInt(7) + (int)getZ() - 3;
			    	pos.move(a, b, c);
			           if(level().getBlockState(pos).canBeReplaced() && BlockTaint.hasPosNeightbour(level(), pos)) {
			        	   
			        	   if(GeneralConfig.enableHardcoreTaint)
			        		   level().setBlock(pos, ModBlocks.taint.defaultBlockState().setValue(BlockTaint.TEXTURE,
                                       random.nextInt(6) + 10), 2);
			        	   else
			        		   level().setBlock(pos, ModBlocks.taint.defaultBlockState().setValue(BlockTaint.TEXTURE,
                                       random.nextInt(3) + 4), 2);
			           }
			    }
            }

            this.discard();
        }
    }
    
    public void ignite() {
        this.entityData.set(IGNITED, Boolean.TRUE);
    }
    public boolean hasIgnited(){
    	return this.entityData.get(IGNITED);
    }
}
