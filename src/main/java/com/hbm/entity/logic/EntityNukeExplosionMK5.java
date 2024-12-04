
package com.hbm.entity.logic;

import com.hbm.config.BombConfig;
import com.hbm.config.CompatibilityConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.entity.effect.EntityFalloutUnderGround;
import com.hbm.entity.mob.EntityGlowingOne;
import com.hbm.explosion.ExplosionNukeRayBatched;
import com.hbm.main.MainRegistry;
import com.hbm.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.Ticket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.ArrayList;
import java.util.List;

public class EntityNukeExplosionMK5 extends Entity implements IChunkLoader {
    //Strength of the blast
    public int strength;
    //How many rays are calculated per tick
    public int radius;

    public boolean mute = false;
    public boolean spawnFire = false;

    public boolean fallout = true;
    private boolean floodPlease = false;
    private int falloutAdd = 0;
    private Ticket loaderTicket;

    ExplosionNukeRayBatched explosion;
    EntityFalloutUnderGround falloutBall;
    EntityFalloutRain falloutRain;

    private int nukeTickNumber = 0;


    public EntityNukeExplosionMK5(Level world) {
        super(world);
    }

    public EntityNukeExplosionMK5(Level world, int strength, int speed, int radius) {
        super(world);
        this.strength = strength;
        this.radius = radius;
    }

    @Override
    public void onUpdate() {
        if (level().isClientSide) return;

        if (strength == 0 || !CompatibilityConfig.isWarDim(level())) {
            this.clearLoadedChunks();
            this.unloadMainChunk();
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        //load own chunk
        loadMainChunk();

        float rads, fire, blast;
        rads = fire = blast = 0;

        //radiate until there is fallout rain
        if (fallout && falloutRain == null) {
            rads = (float) (Math.pow(radius, 4) * (float) Math.pow(0.5, this.tickCount * 0.125) + strength);
            if (tickCount == 42)
                EntityGlowingOne.convertInRadiusToGlow(level(), this.getX(), this.getY(), this.getZ(), radius * 1.5);
        }

        if (tickCount < 2400 && tickCount % 10 == 0) {
            fire = (fallout ? 10F : 2F) * (float) Math.pow(radius, 3) * (float) Math.pow(0.5, this.tickCount * 0.025);
            blast = (float) Math.pow(radius, 3) * 0.2F;
            ContaminationUtil.radiate(level(), this.getX(), this.getY(), this.getZ(), Math.min(1000, radius * 2), rads,
                    0F, fire, blast, this.tickCount * 1.5F);
        }
        //make some noise
        if (!mute) {
            if (this.radius > 30) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.AMBIENT, this.radius * 0.05F,
                        0.8F + this.random.nextFloat() * 0.2F);
                if (random.nextInt(5) == 0)
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, this.radius * 0.05F,
                            0.8F + this.random.nextFloat() * 0.2F);
            } else {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, Math.max(2F, this.radius * 0.1F),
                        0.8F + this.random.nextFloat() * 0.2F);
            }
        }

        //Create Explosion Rays
        if (explosion == null) {
            explosion = new ExplosionNukeRayBatched(level(), (int) this.getX(), (int) this.getY(), (int) this.getZ(),
                    this.strength, this.radius);
        }

        //Calculating crater
        if (!explosion.isAusf3Complete) {
            explosion.collectTip(BombConfig.mk5);

            //Excecuting destruction
        } else if (explosion.perChunk.size() > 0) {
            explosion.processChunk(BombConfig.mk5);

        } else {

            if (fallout) {
                EntityFalloutUnderGround falloutBall = new EntityFalloutUnderGround(this.level());
                falloutBall.posX = this.getX();
                falloutBall.posY = this.getY();
                falloutBall.posZ = this.getZ();
                falloutBall.setScale((int) (this.radius * (BombConfig.falloutRange / 100F) + falloutAdd));

                falloutBall.falloutRainDoFallout = fallout && !explosion.isContained;
                falloutBall.falloutRainDoFlood = floodPlease;
                falloutBall.falloutRainFire = spawnFire;
                falloutBall.falloutRainRadius1 = (int) ((this.radius * 2.5F + falloutAdd) * BombConfig.falloutRange * 0.01F);
                falloutBall.falloutRainRadius2 = this.radius + 4;
                this.level().addFreshEntity(falloutBall);
            } else {
                EntityFalloutRain falloutRain = new EntityFalloutRain(this.level());
                falloutRain.doFallout = false;
                falloutRain.doFlood = floodPlease;
                falloutRain.posX = this.posX;
                falloutRain.posY = this.posY;
                falloutRain.posZ = this.posZ;
                if (spawnFire)
                    falloutRain.spawnFire = true;
                falloutRain.setScale((int) ((this.radius * 2.5F + falloutAdd) * BombConfig.falloutRange * 0.01F), this.radius + 4);
                this.level().addFreshEntity(falloutRain);
            }

            this.clearLoadedChunks();
            unloadMainChunk();
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void entityInit() {
        init(ForgeChunkManager.requestTicket(MainRegistry.instance, level(), Type.ENTITY));
    }

    @Override
    public void init(Ticket ticket) {
        if (!level().isClientSide && ticket != null) {

            if (loaderTicket == null) {
                loaderTicket = ticket;
                loaderTicket.bindEntity(this);
                loaderTicket.getModData();
            }

            ForgeChunkManager.forceChunk(loaderTicket, new ChunkPos(chunkCoordX, chunkCoordZ));
        }
    }


    List<ChunkPos> loadedChunks = new ArrayList<ChunkPos>();

    @Override
    public void loadNeighboringChunks(int newChunkX, int newChunkZ) {
        if (!level().isClientSide && loaderTicket != null) {
            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.unforceChunk(loaderTicket, chunk);
            }

            loadedChunks.clear();
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ));
            loadedChunks.add(new ChunkPos(newChunkX + 1, newChunkZ + 1));
            loadedChunks.add(new ChunkPos(newChunkX - 1, newChunkZ - 1));
            loadedChunks.add(new ChunkPos(newChunkX + 1, newChunkZ - 1));
            loadedChunks.add(new ChunkPos(newChunkX - 1, newChunkZ + 1));
            loadedChunks.add(new ChunkPos(newChunkX + 1, newChunkZ));
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ + 1));
            loadedChunks.add(new ChunkPos(newChunkX - 1, newChunkZ));
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ - 1));

            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.forceChunk(loaderTicket, chunk);
            }
        }
    }

    public void clearLoadedChunks() {
        if (!level().isClientSide && loaderTicket != null && loadedChunks != null) {
            for (ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.unforceChunk(loaderTicket, chunk);
            }
        }
    }

    private ChunkPos mainChunk;

    public void loadMainChunk() {
        if (!level().isClientSide && loaderTicket != null && this.mainChunk == null) {
            this.mainChunk = new ChunkPos((int) Math.floor(this.getX() / 16D), (int) Math.floor(this.getZ() / 16D));
            ForgeChunkManager.forceChunk(loaderTicket, this.mainChunk);
        }
    }

    public void unloadMainChunk() {
        if (!level().isClientSide && loaderTicket != null && this.mainChunk != null) {
            ForgeChunkManager.unforceChunk(loaderTicket, this.mainChunk);
        }
    }

    private static boolean isWet(Level world, BlockPos pos) {
        Holder<Biome> b = world.getBiome(pos);
        return b.get().getBaseTemperature() == Biome.TempCategory.OCEAN || b.isHighHumidity() || b == Biomes.BEACH ||
                b == Biomes.OCEAN || b == Biomes.RIVER || b == Biomes.DEEP_OCEAN || b == Biomes.FROZEN_OCEAN ||
                b == Biomes.FROZEN_RIVER || b == Biomes.STONE_BEACH || b == Biomes.SWAMPLAND;
    }

    @Override
    public void readEntityFromNBT(CompoundTag nbt) {
        radius = nbt.getInt("radius");
        strength = nbt.getInt("strength");
        falloutAdd = nbt.getInt("falloutAdd");
        fallout = nbt.getBoolean("fallout");
        floodPlease = nbt.getBoolean("floodPlease");
        spawnFire = nbt.getBoolean("spawnFire");
        mute = nbt.getBoolean("mute");
        if (explosion == null) {
            explosion = new ExplosionNukeRayBatched(level(), (int) this.getX(), (int) this.getY(), (int) this.getZ(),
                    this.strength, this.radius);
        }
        explosion.readEntityFromNBT(nbt);
    }

    @Override
    public void writeEntityToNBT(CompoundTag nbt) {
        nbt.putInt("radius", radius);
        nbt.putInt("strength", strength);
        nbt.putInt("falloutAdd", falloutAdd);
        nbt.putBoolean("fallout", fallout);
        nbt.putBoolean("floodPlease", floodPlease);
        nbt.putBoolean("spawnFire", spawnFire);
        nbt.putBoolean("mute", mute);
        if (explosion != null) {
            explosion.writeEntityToNBT(nbt);
        }
    }

    public static EntityNukeExplosionMK5 statFac(Level world, int r, double x, double y, double z) {
        if (GeneralConfig.enableExtendedLogging && !world.isClientSide)
//            MainRegistry.logger.log(Level.INFO, "[NUKE] Initialized explosion at " + x + " / " + y + " / " + z + " with radius " + r + "!");

        if (r == 0)
            r = 25;

        EntityNukeExplosionMK5 mk5 = new EntityNukeExplosionMK5(world);

        mk5.strength = (int) (2 * r);
        mk5.radius = r;

        mk5.setPosition(x, y, z);
        mk5.floodPlease = isWet(world, new BlockPos(x, y, z));
        if (BombConfig.disableNuclear)
            mk5.fallout = false;
        return mk5;
    }

    public static EntityNukeExplosionMK5 statFacFire(Level world, int r, double x, double y, double z) {

        EntityNukeExplosionMK5 mk5 = statFac(world, r, x, y, z);
        mk5.spawnFire = true;
        return mk5;
    }

    public static EntityNukeExplosionMK5 statFacNoRad(net.minecraft.world.level.Level world, int r, double x,
                                                      double y, double z) {

        EntityNukeExplosionMK5 mk5 = statFac(world, r, x, y, z);
        mk5.fallout = false;
        return mk5;
    }

    public static EntityNukeExplosionMK5 statFacNoRadFire(Level world, int r, double x, double y, double z) {

        EntityNukeExplosionMK5 mk5 = statFac(world, r, x, y, z);
        mk5.fallout = false;
        mk5.spawnFire = true;
        return mk5;
    }

    public EntityNukeExplosionMK5 moreFallout(int fallout) {
        falloutAdd = fallout;
        return this;
    }

    public EntityNukeExplosionMK5 mute() {
        this.mute = true;
        return this;
    }
}
