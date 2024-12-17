package com.hbm.main;

import java.io.File;

import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.sound.AudioWrapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy {
    public void registerRenderInfo() {
    }

    public void registerTileEntitySpecialRenderer() {
    }

    public void registerItemRenderer() {
    }

    public void registerEntityRenderer() {
    }

    public void registerBlockRenderer() {
    }

    public void particleControl(double x, double y, double z, int type) {
    }

    public void spawnParticle(double x, double y, double z, String type, float[] args) {
    }

    public void spawnSFX(Level world, double posX, double posY, double posZ, int type, Vec3 payload) {
    }

    public void effectNT(CompoundTag data) {
    }

//    public void registerMissileItems(IRegistry<ModelResourceLocation, IBakedModel> reg) {
//    }

    public AudioWrapper getLoopedSound(SoundEvent sound, SoundSource cat, float x, float y, float z, float volume, float pitch) {
        return null;
    }

    public AudioWrapper getLoopedSoundStartStop(Level world, SoundEvent sound, SoundEvent start, SoundEvent stop, SoundSource cat, float x, float y, float z, float volume, float pitch) {
        return null;
    }

    public void preInit() {
    }

    public void checkGLCaps() {
    }

    ;

    public File getDataDir(MinecraftServer server) {
        return server.getServerDirectory();
    }

    public void postInit() {
    }

    public boolean opengl33() {
        return true;//Doesn't matter for servers, and this won't print an error message.
    }

    public boolean getIsKeyPressed(EnumKeybind key) {
        return false;
    }

    public Player me() {
        return null;
    }

    public float partialTicks() {
        return 1;
    }

    public void playSound(String sound, Object data) {
    }

    public void displayTooltip(String msg) {
    }

    public void setRecoil(float rec) {
    }

    public boolean isVanished(Entity e) {
        return false;
    }
}