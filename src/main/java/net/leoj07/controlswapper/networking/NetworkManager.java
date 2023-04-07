package net.leoj07.controlswapper.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.networking.packet.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class NetworkManager {
    public static final Identifier CONTROL_INFO_ID = new Identifier(ControlSwapper.MOD_ID, "control");

    public static final Identifier BUTTON_PRESSED_ID = new Identifier(ControlSwapper.MOD_ID, "button_pressed");
    public static final Identifier MOUSE_MOVE_ID = new Identifier(ControlSwapper.MOD_ID, "mouse_move");
    public static final Identifier MOUSE_SCROLL_ID = new Identifier(ControlSwapper.MOD_ID, "mouse_scroll");
    public static final Identifier MOUSE_LOCK_ID = new Identifier(ControlSwapper.MOD_ID, "mouse_lock");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(BUTTON_PRESSED_ID, ButtonPressedPacket::receiveC2S);
        ServerPlayNetworking.registerGlobalReceiver(MOUSE_SCROLL_ID, MouseScrollPacket::receiveC2S);
        ServerPlayNetworking.registerGlobalReceiver(MOUSE_MOVE_ID, MouseMovePacket::receiveC2S);
        ServerPlayNetworking.registerGlobalReceiver(MOUSE_LOCK_ID, MouseLockPacket::receiveC2S);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(CONTROL_INFO_ID, ControlInfoPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(BUTTON_PRESSED_ID, ButtonPressedPacket::receiveS2C);
        ClientPlayNetworking.registerGlobalReceiver(MOUSE_SCROLL_ID, MouseScrollPacket::receiveS2C);
        ClientPlayNetworking.registerGlobalReceiver(MOUSE_MOVE_ID, MouseMovePacket::receiveS2C);
        ClientPlayNetworking.registerGlobalReceiver(MOUSE_LOCK_ID, MouseLockPacket::receiveS2C);
    }

    public static void sendPacketToServer(Packet packet) {
        ClientPlayNetworking.send(packet.getIdentifier(), packet.createBuf());
    }


    public static void sendPacketToClient(ServerPlayerEntity player, Packet packet) {
        ServerPlayNetworking.send(player, packet.getIdentifier(), packet.createBuf());
    }

    public static void sendPacketToClient(ServerPlayerEntity player, Identifier identifier, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, identifier, buf);
    }
}
