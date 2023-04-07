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

public class MouseMovePacket extends Packet {

    public double x;
    public double y;

    public MouseMovePacket(double x, double y) {
        this.x = x;
        this.y = y;
    }

    MouseMovePacket(PacketByteBuf buf) {
        readFromBuf(buf);
    }

    @Override
    public PacketByteBuf writeToBuf(PacketByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        return buf;
    }

    @Override
    public void readFromBuf(PacketByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
    }

    @Override
    public Identifier getIdentifier() {
        return NetworkManager.MOUSE_MOVE_ID;
    }

    public static void receiveC2S(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MouseMovePacket packet = new MouseMovePacket(buf);

        if(!ControlSwapper.isControlIn(player)) ControlSwapper.updateClientInfo(player);
        ServerPlayerEntity controlOut = ControlSwapper.getControlOut(player);

        NetworkManager.sendPacketToClient(controlOut, packet);
    }

    public static void receiveS2C(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MouseMovePacket packet = new MouseMovePacket(buf);

        ControlSwapper.LOGGER.info("Received mouse move packet on client");
        InputManager.onMouseMove(packet.x, packet.y);
    }
}
