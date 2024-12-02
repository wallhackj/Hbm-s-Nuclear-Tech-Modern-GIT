package com.hbm.packet;

import com.hbm.tileentity.machine.TileEntityMachineEPress;
import com.hbm.tileentity.machine.TileEntityMachinePress;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;


public class TEPressPacket implements IMessage {

	int x;
	int y;
	int z;
	int item;
	int meta;
	int progress;

	public TEPressPacket()
	{

	}

	public TEPressPacket(int x, int y, int z, ItemStack stack, int progress)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.item = 0;
		this.meta = 0;
		if(stack != null) {
			this.item = Item.getId(stack.getItem());
			this.meta = stack.getDamageValue();
		}
		this.progress = progress;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		item = buf.readInt();
		meta = buf.readInt();
		progress = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(item);
		buf.writeInt(meta);
		buf.writeInt(progress);
	}

	public static class Handler implements IMessageHandler<TEPressPacket, IMessage> {

		@Override
		public IMessage onMessage(TEPressPacket m, NetworkEvent.Context ctx) {
			ctx.enqueueWork(() -> {
				// Handle the packet on the main thread
				Minecraft.getInstance().execute(() -> {
					BlockPos pos = new BlockPos(m.x, m.y, m.z);
					TileEntityMachinePress machinePress = (TileEntityMachinePress) Minecraft.getInstance().level.getBlockEntity(pos);
					if (machinePress != null) {
						machinePress.item = m.item;
						machinePress.meta = m.meta;
						machinePress.progress = m.progress;
					}
					TileEntityMachineEPress machineEPress = (TileEntityMachineEPress) Minecraft.getInstance().level.getBlockEntity(pos);
					if (machineEPress != null) {
						machineEPress.item = m.item;
						machineEPress.meta = m.meta;
						machineEPress.progress = m.progress;
					}
				});
			});
			ctx.setPacketHandled(true);

            return null;
        }
	}

}