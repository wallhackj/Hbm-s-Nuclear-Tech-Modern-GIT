package com.hbm.packet;

import io.netty.buffer.ByteBuf;

public interface IMessage {
    void toBytes(ByteBuf buf);
    void fromBytes(ByteBuf buf);
}
