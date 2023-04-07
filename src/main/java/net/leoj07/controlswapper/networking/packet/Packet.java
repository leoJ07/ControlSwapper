package net.leoj07.controlswapper.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class Packet {

    public abstract PacketByteBuf writeToBuf(PacketByteBuf buf);
    public abstract void readFromBuf(PacketByteBuf buf);

    public PacketByteBuf createBuf() {
        return writeToBuf(PacketByteBufs.create());
    }

    public abstract Identifier getIdentifier();
}
