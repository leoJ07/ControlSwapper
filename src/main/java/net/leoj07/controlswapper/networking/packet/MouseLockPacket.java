package net.leoj07.controlswapper.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.leoj07.controlswapper.utils.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MouseLockPacket extends Packet {

    public boolean lock;
    public double x;
    public double y;

    public MouseLockPacket(boolean lock, double x, double y) {
        this.lock = lock;
        this.x = x;
        this.y = y;
    }

    MouseLockPacket(PacketByteBuf buf) {
        readFromBuf(buf);
    }

    @Override
    public PacketByteBuf writeToBuf(PacketByteBuf buf) {
        buf.writeBoolean(lock);
        buf.writeDouble(x);
        buf.writeDouble(y);

        return buf;
    }

    @Override
    public void readFromBuf(PacketByteBuf buf) {
        lock = buf.readBoolean();
        x = buf.readDouble();
        y = buf.readDouble();
    }

    @Override
    public Identifier getIdentifier() {
        return NetworkManager.MOUSE_LOCK_ID;
    }

    public static void receiveC2S(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MouseLockPacket packet = new MouseLockPacket(buf);

        if(!ControlSwapper.isControlOut(player)) ControlSwapper.updateClientInfo(player);
        ServerPlayerEntity controlIn = ControlSwapper.getControlIn(player);

        NetworkManager.sendPacketToClient(controlIn, packet);
    }

    public static void receiveS2C(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MouseLockPacket packet = new MouseLockPacket(buf);

        ControlSwapper.LOGGER.info("Received mouse lock packet on client");
        InputManager.onMouseLock(packet.lock, packet.x, packet.y);
    }
}
