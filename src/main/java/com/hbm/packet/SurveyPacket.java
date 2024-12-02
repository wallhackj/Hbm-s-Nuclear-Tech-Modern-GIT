package com.hbm.packet;

import com.hbm.capability.HbmLivingCapability;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;


public class SurveyPacket implements IMessage {
	int rbmkHeight;

	public SurveyPacket(){
	}

	public SurveyPacket(int rbmkHeight){
		this.rbmkHeight = rbmkHeight;
	}

	@Override
	public void fromBytes(ByteBuf buf){

		rbmkHeight = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(rbmkHeight);
	}

	public static class Handler implements IMessageHandler<SurveyPacket, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(SurveyPacket m, MessageContext ctx){

			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					TileEntityRBMKBase.rbmkHeight = m.rbmkHeight;
				} catch(Exception x) {
				}
			});

			return null;
		}
	}
}
