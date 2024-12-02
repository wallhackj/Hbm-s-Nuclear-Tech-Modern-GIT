package com.hbm.packet;

import net.minecraftforge.network.NetworkEvent;

public interface IMessageHandler<REQ extends IMessage, REPLY extends IMessage> {
    REPLY onMessage(REQ var1, NetworkEvent.Context var2);
}
