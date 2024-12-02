package com.hbm.packet;

import java.io.IOException;

import com.hbm.main.MainRegistry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

public class AuxParticlePacketNT implements IMessage {
	
	PacketBuffer buffer;

	public AuxParticlePacketNT() { }

	public AuxParticlePacketNT(CompoundTag nbt, double x, double y, double z) {
		
		this.buffer = new PacketBuffer(Unpooled.buffer());

		nbt.putDouble("posX", x);
		nbt.putDouble("posY", y);
		nbt.putDouble("posZ", z);
		
		buffer.writeCompoundTag(nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		if (buffer == null) {
			buffer = new PacketBuffer(Unpooled.buffer());
		}
		buffer.writeBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		if (buffer == null) {
			buffer = new PacketBuffer(Unpooled.buffer());
		}
		buf.writeBytes(buffer);
	}

	public static class Handler implements IMessageHandler<AuxParticlePacketNT, IMessage> {
		
		@Override
		public IMessage onMessage(AuxParticlePacketNT m, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				if(Minecraft.getMinecraft().world == null)
					return;
				
				try {
					
					CompoundTag nbt = m.buffer.readCompoundTag();
					
					if(nbt != null)
						MainRegistry.proxy.effectNT(nbt);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			return null;
		}
	}

}
