package com.hbm.entity.logic;

import com.hbm.config.CompatibilityConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.explosion.ExplosionBalefire;
import com.hbm.main.MainRegistry;
import com.hbm.util.ContaminationUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraftforge.common.world.ForgeChunkManager;


import java.util.ArrayList;
import java.util.List;


public class EntityBalefire extends Entity implements IChunkLoader {

    public int age = 0;
    public int destructionRange = 0;
    public ExplosionBalefire exp;
    public int speed = 1;
    public boolean did = false;
    public boolean mute = false;
    private Ticket loaderTicket;

//    @Override
    protected void readEntityFromNBT(CompoundTag nbt) {
        age = nbt.getInt("age");
        destructionRange = nbt.getInt("destructionRange");
        speed = nbt.getInt("speed");
        did = nbt.getBoolean("did");
        mute = nbt.getBoolean("mute");

        exp = new ExplosionBalefire((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(),
                this.destructionRange);
        exp.readFromNbt(nbt, "exp_");

        this.did = true;
    }

//    @Override
    protected void writeEntityToNBT(CompoundTag nbt) {
        nbt.putInt("age", age);
        nbt.putInt("destructionRange", destructionRange);
        nbt.putInt("speed", speed);
        nbt.putBoolean("did", did);
        nbt.putBoolean("mute", mute);

        if (exp != null)
            exp.saveToNbt(nbt, "exp_");

    }

    public EntityBalefire(Level p_i1582_1_) {
        super(null, p_i1582_1_);
    }

    @Override
    public void tick() {
        super.tick();
        if (!CompatibilityConfig.isWarDim(level())) {
            this.discard();
            return;
        }
        if (!this.did) {
            if (GeneralConfig.enableExtendedLogging && !level().isClientSide)
//                MainRegistry.logger.log(Level.INFO, "[NUKE] Initialized BF explosion at " + posX + " / " + posY + " / " + posZ + " with strength " + destructionRange + "!");

            exp = new ExplosionBalefire((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(),
                    this.destructionRange);

            this.did = true;
        }

        speed += 1;    //increase speed to keep up with expansion

        boolean flag = false;

        for (int i = 0; i < this.speed; i++) {
            flag = exp.update();

            if (flag) {
                this.discard();
            }
        }

        if (!flag) {
            if (this.destructionRange > 15) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.AMBIENT,
                        this.destructionRange * 0.05F, 0.8F + this.random.nextFloat() * 0.2F);
            } else {
                if (random.nextInt(5) == 0)
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT,
                            this.destructionRange * 0.05F, 0.8F + this.random.nextFloat() * 0.2F);
            }
            ContaminationUtil.radiate(this.level(), this.getX(), this.getY(), this.getZ(),
                    this.destructionRange * 2D, this.destructionRange * 2000F, 0F,
                    this.destructionRange * 100F, this.destructionRange * 500F);
        }

        age++;
    }

    @Override
    protected void entityInit() {
        init(ForgeChunkManager.requestTicket(MainRegistry.instance, level(), EntityPositionSource.Type.ENTITY));
    }

    @Override
    public void init(Ticket ticket) {
        if (!level().isClientSide) {

            if (ticket != null) {

                if (loaderTicket == null) {

                    loaderTicket = ticket;
                    loaderTicket.bindEntity(this);
                    loaderTicket.getModData();
                }

                ForgeChunkManager.forceChunk(loaderTicket, new ChunkPos(chunkCoordX, chunkCoordZ));
            }
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

    public EntityBalefire mute() {
        this.mute = true;
        return this;
    }
}
