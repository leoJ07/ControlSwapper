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

public class MouseScrollPacket extends Packet {

    public double horizontal;
    public double vertical;

    public MouseScrollPacket(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    MouseScrollPacket(PacketByteBuf buf) {
        readFromBuf(buf);
    }

    @Override
    public PacketByteBuf writeToBuf(PacketByteBuf buf) {
        buf.writeDouble(horizontal);
        buf.writeDouble(vertical);
        return buf;
    }

    @Override
    public void readFromBuf(PacketByteBuf buf) {
        horizontal = buf.readDouble();
        vertical = buf.readDouble();
    }

    @Override
    public Identifier getIdentifier() {
        return NetworkManager.MOUSE_SCROLL_ID;
    }

    public static void receiveC2S(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MouseScrollPacket packet = new MouseScrollPacket(buf);

        if(!ControlSwapper.isControlIn(player)) ControlSwapper.updateClientInfo(player);
        ServerPlayerEntity controlOut = ControlSwapper.getControlOut(player);

        NetworkManager.sendPacketToClient(controlOut, packet);
    }

    public static void receiveS2C(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        MouseScrollPacket packet = new MouseScrollPacket(buf);

        ControlSwapper.LOGGER.info("Received mouse scroll packet on client");
        InputManager.onMouseScroll(packet.horizontal, packet.vertical);
    }
}
