package com.hbm.entity.effect;

import java.util.ArrayList;

import com.hbm.interfaces.IConstantRenderer;
import com.hbm.render.amlfrom1710.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import static net.minecraft.network.syncher.SynchedEntityData.defineId;
import static org.joml.Math.clamp;


/*
 * Toroidial Convection Simulation Explosion Effect
 * Tor                             Ex
 */
public class EntityNukeTorex extends Entity implements IConstantRenderer {
	public static final EntityDataAccessor<Float> SCALE = defineId(EntityNukeTorex.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Byte> TYPE = defineId(EntityNukeTorex.class,
			EntityDataSerializers.BYTE);

	public static final int firstCondenseHeight = 130;
	public static final int secondCondenseHeight = 170;
	public static final int blastWaveHeadstart = 5;
	public static final int maxCloudlets = 20_000;

	//Nuke colors
	public static final double nr1 = 2.5;
	public static final double ng1 = 1.3;
	public static final double nb1 = 0.4;
	public static final double nr2 = 0.1;
	public static final double ng2 = 0.075;
	public static final double nb2 = 0.05;

	//Balefire colors
	public static final double br1 = 1;
	public static final double bg1 = 2;
	public static final double bb1 = 0.5;
	public static final double br2 = 0.1;
	public static final double bg2 = 0.1;
	public static final double bb2 = 0.1;

	public double coreHeight = 3;
	public double convectionHeight = 3;
	public double torusWidth = 3;
	public double rollerSize = 1;
	public double heat = 1;
	public double lastSpawnY = -1;
	public ArrayList<Cloudlet> cloudlets = new ArrayList();
	public int maxAge = 1000;
	public float humidity = -1;

	public EntityNukeTorex(EntityType<?> type, Level p_i1582_1_) {
		super(type, p_i1582_1_);
//		this.setSize(20F, 40F);
		this.setInvulnerable(true);
		this.setNoGravity(true);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(SCALE, 1.0F);
		this.entityData.define(TYPE, (byte) 0);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {
		if (nbt.contains("scale"))
			setScale(nbt.getFloat("scale"));
		if (nbt.contains("type"))
			this.entityData.set(TYPE, nbt.getByte("type"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		nbt.putFloat("scale", this.entityData.get(SCALE));
		nbt.putByte("type", this.entityData.get(TYPE));
	}

//	@Override
//	@SideOnly(Side.CLIENT)
    public boolean shouldRender(double distance) {
		return true;
	}

	@Override
	public void tick() {
		if(level().isClientSide) {
			
			double s = this.getScale();
			double cs = 1.5;
			if(this.tickCount == 1) this.setScale((float) s);
			
			if(humidity == -1) humidity = level().getBiome(this.blockPosition()).get().getBaseTemperature();
			
			if(lastSpawnY == -1) {
				lastSpawnY = getY() - 3;
			}
			
//			int spawnTarget = Math.max(level().getHeight((int) Math.floor(getX()), (int) Math.floor(getZ())) - 3, 1);
			int spawnTarget = Math.max(level().getHeight() - 3, 1);
			double moveSpeed = 0.5D;
			
			if(Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
				lastSpawnY = spawnTarget;
			} else {
				lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
			}
			
			// spawn mush clouds
			double range = (torusWidth - rollerSize) * 0.5;
			double simSpeed = getSimulationSpeed();
			int lifetime = Math.min((this.tickCount * this.tickCount) + 200, maxAge - this.tickCount + 200);
			int toSpawn = (int) (0.6 * Math.min(Math.max(0, maxCloudlets-cloudlets.size()), Math.ceil(10 * simSpeed * simSpeed * Math.min(1, 1200/(double)lifetime))));
			

			for(int i = 0; i < toSpawn; i++) {
				double x = getX() + random.nextGaussian() * range;
				double z = getZ() + random.nextGaussian() * range;
				Cloudlet cloud = new Cloudlet(x, lastSpawnY, z, (float)(random.nextDouble() * 2D * Math.PI),
						0, lifetime);
				cloud.setScale((float) (Math.sqrt(s) * 3 + this.tickCount * 0.0025 * s),
						(float) (Math.sqrt(s) * 3 + this.tickCount * 0.0025 * 6 * cs * s));
				cloudlets.add(cloud);
			}

//			if(this.tickCount < 120 * s){
//				level().set.setLastLightningBolt(2);
//			}
			
			// spawn shock clouds
			if(this.tickCount < 150) {
				
				int cloudCount = Math.min(this.tickCount * 2, 100);
				int shockLife = Math.max(400 - this.tickCount * 20, 50);
				
				for(int i = 0; i < cloudCount; i++) {
					Vec3 vec = Vec3.createVectorHelper((this.tickCount + random.nextDouble() * 2) * 1.5,
							0, 0);
					float rot = (float) (Math.PI * 2 * random.nextDouble());
					vec.rotateAroundY(rot);
					// level().getHeight((int) (vec.xCoord + getX()) + 1, (int) (vec.zCoord + getZ())),
					this.cloudlets.add(new Cloudlet(vec.xCoord + getX(),
							level().getHeight(),
							vec.zCoord + getZ(), rot, 0, shockLife, TorexType.SHOCK)
							.setScale((float)s * 5F, (float)s * 2F).
							setMotion(clamp(0.25 * this.tickCount - 5, 0, 1)));
				}
			}
			
			// spawn ring clouds
			if(this.tickCount < 200) {
				lifetime *= s;
				for(int i = 0; i < 2; i++) {
					Cloudlet cloud = new Cloudlet(getX(), getY() + coreHeight, getZ(),
							(float)(random.nextDouble() * 2D * Math.PI), 0, lifetime, TorexType.RING);
					cloud.setScale((float) (Math.sqrt(s) * cs + this.tickCount * 0.0015 * s),
							(float) (Math.sqrt(s) * cs + this.tickCount * 0.0015 * 6 * cs * s));
					cloudlets.add(cloud);
				}
			}

			if(this.humidity > 0 && this.tickCount < 220){
				// spawn lower condensation clouds
				spawnCondensationClouds(this.tickCount, this.humidity, firstCondenseHeight, 80, 4,
						s, cs);

				// spawn upper condensation clouds
				spawnCondensationClouds(this.tickCount, this.humidity, secondCondenseHeight, 80,
						2, s, cs);
			}

			cloudlets.removeIf(x -> x.isDead);
			for(Cloudlet cloud : cloudlets) {
				cloud.update();
			}
			
			coreHeight += 0.15/* * s*/;
			torusWidth += 0.05/* * s*/;
			rollerSize = torusWidth * 0.35;
			convectionHeight = coreHeight + rollerSize;
			
			int maxHeat = (int) (50 * s * s);
			heat = maxHeat - Math.pow((maxHeat * this.tickCount) / maxAge, 0.6);
		}
		// this.setDead();
		if(!level().isClientSide && this.tickCount > maxAge) {
			this.discard();
		}
	}

	public void spawnCondensationClouds(int age, float humidity, int height, int count, int spreadAngle, double s,
										double cs){
		if((getY() + age) > height) {
			
			for(int i = 0; i < (int)(5 * humidity * count/(double)spreadAngle); i++) {
				for(int j = 1; j < spreadAngle; j++) {
					float angle = (float) (Math.PI * 2 * random.nextDouble());
					Vec3 vec = Vec3.createVectorHelper(0, age, 0);
					vec.rotateAroundZ((float)Math.acos((height-getY())/(age))+(float)Math.
							toRadians(humidity*humidity*90*j*(0.1*random.nextDouble()-0.05)));
					vec.rotateAroundY(angle);
					Cloudlet cloud = new Cloudlet(getX() + vec.xCoord, getY() + vec.yCoord,
							getZ() + vec.zCoord, angle, 0,
							(int) ((20 + age / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
					cloud.setScale(3F * (float) (cs * s), 4F * (float) (cs * s));
					cloudlets.add(cloud);
				}
			}
		}
	}
	
	public EntityNukeTorex setScale(float scale) {
		if(!level().isClientSide)
			this.entityData.set(SCALE, scale);
		this.coreHeight = this.coreHeight * scale;
		this.convectionHeight = this.convectionHeight * scale;
		this.torusWidth = this.torusWidth * scale;
		this.rollerSize = this.rollerSize * scale;
		this.maxAge = (int) (45 * 20 * scale);
		return this;
	}
	
	public EntityNukeTorex setType(int type) {
		this.entityData.set(TYPE, (byte) type);
		return this;
	}

	public double getScale() {
		return this.entityData.get(SCALE);
	}

	public byte getTypes() {
		return this.entityData.get(TYPE);
	}
	
	public double getSimulationSpeed() {
		
		int simSlow = maxAge / 4;
		int life = this.tickCount;
		
		if(life > maxAge) {
			return 0D;
		}
		
		if(life > simSlow) {
			return 1D - ((double)(life - simSlow) / (double)(maxAge - simSlow));
		}
		
		return 1.0D;
	}
	
	public float getAlpha() {
		
		int fadeOut = maxAge * 3 / 4;
		int life = this.tickCount;
		
		if(life > fadeOut) {
			float fac = (float)(life - fadeOut) / (float)(maxAge - fadeOut);
			return 1F - fac;
		}
		
		return 1.0F;
	}

	public Vec3 getInterpColor(double interp, byte type) {
		if(type == 0){
			return Vec3.createVectorHelper(
				(nr2 + (nr1 - nr2) * interp),
				(ng2 + (ng1 - ng2) * interp),
				(nb2 + (nb1 - nb2) * interp));
		}
		return Vec3.createVectorHelper(
			(br2 + (br1 - br2) * interp),
			(bg2 + (bg1 - bg2) * interp),
			(bb2 + (bb1 - bb2) * interp));
	}

	public class Cloudlet {

		public double posX;
		public double posY;
		public double posZ;
		public double prevPosX;
		public double prevPosY;
		public double prevPosZ;
		public double motionX;
		public double motionY;
		public double motionZ;
		public int age;
		public int cloudletLife;
		public float angle;
		public boolean isDead = false;
		float rangeMod = 1.0F;
		public float colorMod = 1.0F;
		public Vec3 color;
		public Vec3 prevColor;
		public TorexType type;
		private float startingScale = 3F;
		private float growingScale = 5F;
		
		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
			this(posX, posY, posZ, angle, age, maxAge, TorexType.STANDARD);
		}

		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.age = age;
			this.cloudletLife = maxAge;
			this.angle = angle;
			this.rangeMod = 0.3F + random.nextFloat() * 0.7F;
			this.colorMod = 0.8F + random.nextFloat() * 0.2F;
			this.type = type;
			
			this.updateColor();
		}

		private double motionMult = 1F;
		private double motionConvectionMult = 0.5F;
		private double motionLiftMult = 0.625F;
		private double motionRingMult = 0.5F;
		private double motionCondensationMult = 1F;
		private double motionShockwaveMult = 1F;
		
		
		private void update() {
			age++;
			
			if(age > cloudletLife) {
				this.isDead = true;
			}

			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			
			Vec3 simPos = Vec3.createVectorHelper(EntityNukeTorex.this.getX() - this.posX, 0,
					EntityNukeTorex.this.getZ() - this.posZ);
			double simPosX = EntityNukeTorex.this.getX() + simPos.lengthVector();
			double simPosZ = EntityNukeTorex.this.getZ() + 0D;
			
			if(this.type == TorexType.STANDARD) {
				Vec3 convection = getConvectionMotion(simPosX, simPosZ);
				Vec3 lift = getLiftMotion(simPosX, simPosZ);
				
				double factor = clamp((this.posY - EntityNukeTorex.this.position().y) / EntityNukeTorex.this.
								coreHeight,
						0, 1);
				this.motionX = convection.xCoord * factor + lift.xCoord * (1D - factor);
				this.motionY = convection.yCoord * factor + lift.yCoord * (1D - factor);
				this.motionZ = convection.zCoord * factor + lift.zCoord * (1D - factor);
			} else if(this.type == TorexType.RING) {
				Vec3 motion = getRingMotion(simPosX, simPosZ);
				this.motionX = motion.xCoord;
				this.motionY = motion.yCoord;
				this.motionZ = motion.zCoord;
			} else if(this.type == TorexType.CONDENSATION) {
				Vec3 motion = getCondensationMotion();
				this.motionX = motion.xCoord;
				this.motionY = motion.yCoord;
				this.motionZ = motion.zCoord;
			} else if(this.type == TorexType.SHOCK) {
				Vec3 motion = getShockwaveMotion();
				this.motionX = motion.xCoord;
				this.motionY = motion.yCoord;
				this.motionZ = motion.zCoord;
			}
			
			double mult = this.motionMult * getSimulationSpeed();
			
			this.posX += this.motionX * mult;
			this.posY += this.motionY * mult;
			this.posZ += this.motionZ * mult;
			
			this.updateColor();
		}
		
		private Vec3 getCondensationMotion() {
			Vec3 delta = Vec3.createVectorHelper(posX - EntityNukeTorex.this.getX(), 0,
					posZ - EntityNukeTorex.this.getZ()).normalize();
			double speed = motionCondensationMult * EntityNukeTorex.this.getScale() * 0.125D;
			delta.xCoord *= speed;
			delta.yCoord = 0;
			delta.zCoord *= speed;
			return delta;
		}

		private Vec3 getShockwaveMotion() {
			Vec3 delta = Vec3.createVectorHelper(posX - EntityNukeTorex.this.getX(), 0,
					posZ - EntityNukeTorex.this.getZ()).normalize();
			double speed = motionShockwaveMult * EntityNukeTorex.this.getScale() * 0.25D;
			delta.xCoord *= speed;
			delta.yCoord = 0;
			delta.zCoord *= speed;
			return delta;
		}
		
		private Vec3 getRingMotion(double simPosX, double simPosZ) {
			
			if(simPosX > EntityNukeTorex.this.getX() + torusWidth * 2)
				return Vec3.createVectorHelper(0, 0, 0);
			
			/* the position of the torus' outer ring center */
			Vec3 torusPos = Vec3.createVectorHelper(
					(EntityNukeTorex.this.getX() + torusWidth),
					(EntityNukeTorex.this.getY() + coreHeight * 0.5),
					EntityNukeTorex.this.getZ());
			
			/* the difference between the cloudlet and the torus' ring center */
			Vec3 delta = Vec3.createVectorHelper(torusPos.xCoord - simPosX,
					torusPos.yCoord - this.posY, torusPos.zCoord - simPosZ);
			
			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = EntityNukeTorex.this.rollerSize * this.rangeMod * 0.25;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.lengthVector() / roller - 1D;
			
			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]
			
			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			Vec3 rot = Vec3.createVectorHelper(-delta.xCoord / dist, -delta.yCoord / dist, -delta.zCoord / dist);
			/* rotate by the approximate angle */
			rot.rotateAroundZ(angle);
			
			/* the direction from the cloudlet to the target position on the perimeter */
			Vec3 motion = Vec3.createVectorHelper(
					torusPos.xCoord + rot.xCoord - simPosX,
					torusPos.yCoord + rot.yCoord - this.posY,
					torusPos.zCoord + rot.zCoord - simPosZ);
			
			motion = motion.normalize();
			motion.rotateAroundY(this.angle);
			double speed = motionRingMult * 0.5D;
			motion.xCoord *= speed;
			motion.yCoord *= speed;
			motion.zCoord *= speed;
			
			return motion;
		}
		
		/* simulated on a 2D-plane along the X/Y axis */
		private Vec3 getConvectionMotion(double simPosX, double simPosZ) {
			
			if(simPosX > EntityNukeTorex.this.getX() + torusWidth * 2)
				return Vec3.createVectorHelper(0, 0, 0);
			
			/* the position of the torus' outer ring center */
			Vec3 torusPos = Vec3.createVectorHelper(
					(EntityNukeTorex.this.getX() + torusWidth),
					(EntityNukeTorex.this.getY() + coreHeight),
					EntityNukeTorex.this.getZ());
			
			/* the difference between the cloudlet and the torus' ring center */
			Vec3 delta = Vec3.createVectorHelper(torusPos.xCoord - simPosX,
					torusPos.yCoord - this.posY, torusPos.zCoord - simPosZ);
			
			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = EntityNukeTorex.this.rollerSize * this.rangeMod;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.lengthVector() / roller - 1D;
			
			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]
			
			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			Vec3 rot = Vec3.createVectorHelper(-delta.xCoord / dist, -delta.yCoord / dist,
					-delta.zCoord / dist);
			/* rotate by the approximate angle */
			rot.rotateAroundZ(angle);
			
			/* the direction from the cloudlet to the target position on the perimeter */
			Vec3 motion = Vec3.createVectorHelper(
					torusPos.xCoord + rot.xCoord - simPosX,
					torusPos.yCoord + rot.yCoord - this.posY,
					torusPos.zCoord + rot.zCoord - simPosZ);
			
			motion = motion.normalize();
			motion.rotateAroundY(this.angle);

			motion.xCoord *= motionConvectionMult;
			motion.yCoord *= motionConvectionMult;
			motion.zCoord *= motionConvectionMult;
			
			return motion;
		}
		
		private Vec3 getLiftMotion(double simPosX, double simPosZ) {
			double scale = clamp(1D - (simPosX - (EntityNukeTorex.this.getX() + torusWidth)), 0, 1) * motionLiftMult;
			
			Vec3 motion = Vec3.createVectorHelper(EntityNukeTorex.this.getX() - this.posX,
					(EntityNukeTorex.this.getY() + convectionHeight) - this.posY,
					EntityNukeTorex.this.getZ() - this.posZ);
			
			motion = motion.normalize();
			motion.xCoord *= scale;
			motion.yCoord *= scale;
			motion.zCoord *= scale;
			
			return motion;
		}
		
		private void updateColor() {
			this.prevColor = this.color;

			double exX = EntityNukeTorex.this.getX();
			double exY = EntityNukeTorex.this.getY() + EntityNukeTorex.this.coreHeight;
			double exZ = EntityNukeTorex.this.getZ();

			double distX = exX - posX;
			double distY = exY - posY;
			double distZ = exZ - posZ;
			
			double distSq = distX * distX + distY * distY + distZ * distZ;
			distSq /= this.type == TorexType.SHOCK ? EntityNukeTorex.this.heat * 3 : EntityNukeTorex.this.heat;
			
			double col = 2D / Math.max(distSq, 1); //col goes from 2-0

			byte type = EntityNukeTorex.this.getTypes();
			
			this.color = EntityNukeTorex.this.getInterpColor(col, type);
		}
		
		public Vec3 getInterpPos(float interp) {
			return Vec3.createVectorHelper(
					prevPosX + (posX - prevPosX) * interp,
					prevPosY + (posY - prevPosY) * interp,
					prevPosZ + (posZ - prevPosZ) * interp);
		}
		
		public Vec3 getInterpColor(float interp) {
			
			if(this.type == TorexType.CONDENSATION) {
				return Vec3.createVectorHelper(1F, 1F, 1F);
			}
			
			double greying = 0;
			
			if(this.type == TorexType.RING) {
				greying += 0.05;
			}
			
			return Vec3.createVectorHelper(
					(prevColor.xCoord + (color.xCoord - prevColor.xCoord) * interp) + greying,
					(prevColor.yCoord + (color.yCoord - prevColor.yCoord) * interp) + greying,
					(prevColor.zCoord + (color.zCoord - prevColor.zCoord) * interp) + greying);
		}
		
		public float getAlpha() {
			float alpha = (1F - ((float)age / (float)cloudletLife)) * EntityNukeTorex.this.getAlpha();
			if(this.type == TorexType.CONDENSATION) alpha *= 0.25;
			return clamp(alpha, 0.0001F, 1F);
		}
		
		
		public float getScale() {
			return startingScale + ((float)age / (float)cloudletLife) * growingScale;
		}
		
		public Cloudlet setScale(float start, float grow) {
			this.startingScale = start;
			this.growingScale = grow;
			return this;
		}
		
		public Cloudlet setMotion(double mult) {
			this.motionMult = mult;
			return this;
		}
	}
	
	public static enum TorexType {
		STANDARD,
		RING,
		CONDENSATION,
		SHOCK
	}
	
	public static void statFac(Level world, double x, double y, double z, float scale) {
//		EntityNukeTorex torex = new EntityNukeTorex(world).setScale(clamp(scale * 0.01F, 0.25F, 5F));
//		torex.setPosition(x, y, z);
//		world.spawnEntity(torex);
	}
	
	public static void statFacBale(Level world, double x, double y, double z, float scale) {
//		EntityNukeTorex torex = new EntityNukeTorex(world).setScale(MathHelper.clamp(scale * 0.01F, 0.25F, 5F)).setType(1);
//		torex.setPosition(x, y, z);
//		world.spawnEntity(torex);
	}
}
