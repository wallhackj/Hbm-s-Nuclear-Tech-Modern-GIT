package com.hbm.tileentity.bomb;

import com.hbm.entity.item.EntityFireworks;
import com.hbm.lib.HBMSoundHandler;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class TileEntityFireworks extends BlockEntity implements Tickable {

	public int color = 0xff0000;
	public String message = "EAT MY ASS";
	public int charges;

	int index;
	int delay;

	public TileEntityFireworks(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	@Override
	public void tick() {
		if(!level.isClientSide) {

			if(level.hasNeighborSignal(worldPosition) && !message.isEmpty() && charges > 0) {

				delay--;

				if(delay <= 0) {
					delay = 30;

					int c = (int)(message.charAt(index));

					int mod = index % 9;

					double offX = (mod / 3 - 1) * 0.3125;
					double offZ = (mod % 3 - 1) * 0.3125;

					EntityFireworks fireworks = new EntityFireworks(level, worldPosition.getX() + 0.5 + offX,
							worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5 + offZ, color, c);
					level.addFreshEntity(fireworks);

					level.playSound(null, fireworks.getX(), fireworks.getY(), fireworks.getZ(),
							HBMSoundHandler.rocketFlame, SoundSource.BLOCKS, 3.0F, 1.0F);

					charges--;
//					this.remove(Entity.RemovalReason.DISCARDED);

					CompoundTag data = new CompoundTag();
					data.putString("type", "vanillaExt");
					data.putString("mode", "flame");
//					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, pos.getX() + 0.5 + offX,
//							pos.getY() + 1.125, pos.getZ() + 0.5 + offZ),
//							new TargetPoint(this.world.provider.getDimension(), pos.getX() + 0.5 + offX,
//									pos.getY() + 1.125, pos.getZ() + 0.5 + offZ, 100));

					index++;

					if(index >= message.length()) {
						index = 0;
						delay = 100;
					}
				}

			} else {
				delay = 0;
				index = 0;
			}
		}
	}
	
	@Override
	public void load(CompoundTag compound) {
		this.charges = compound.getInt("charges");
		this.color = compound.getInt("color");
		this.message = compound.getString("message");
		super.load(compound);
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.putInt("charges", charges);
		compound.putInt("color", color);
		compound.putString("message", message);
        super.saveAdditional(compound);
    }

}
