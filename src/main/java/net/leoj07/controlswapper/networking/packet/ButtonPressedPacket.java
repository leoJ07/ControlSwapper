package net.leoj07.controlswapper.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.leoj07.controlswapper.utils.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public class ButtonPressedPacket extends Packet {

    public InputUtil.Key key;
    public boolean pressed;
    public int modifiers;

    public ButtonPressedPacket(InputUtil.Key key, boolean pressed, int modifiers) {
        this.key = key;
        this.pressed = pressed;
        this.modifiers = modifiers;
    }

    ButtonPressedPacket(PacketByteBuf buf) {
        readFromBuf(buf);
    }

    @Override
    public PacketByteBuf writeToBuf(PacketByteBuf buf) {
        buf.writeInt(key.getCode());
        buf.writeByte(typeToByte(key.getCategory()));

        buf.writeBoolean(pressed);
        buf.writeInt(modifiers);
        return buf;
    }

    @Override
    public void readFromBuf(PacketByteBuf buf) {
        key = createKey(buf.readInt(), buf.readByte());
        pressed = buf.readBoolean();
        modifiers = buf.readInt();
    }

    private byte typeToByte(InputUtil.Type type) {
        if(type == InputUtil.Type.KEYSYM) return 0;
        if(type == InputUtil.Type.SCANCODE) return 1;
        if(type == InputUtil.Type.MOUSE) return 2;
        return -1;
    }

    private InputUtil.Key createKey(int code, byte type) {
        switch (type) {
            case 0: return InputUtil.Type.KEYSYM.createFromCode(code);
            case 1: return InputUtil.Type.SCANCODE.createFromCode(code);
            case 2: return InputUtil.Type.MOUSE.createFromCode(code);
            default: return null;
        }
    }

    @Override
    public Identifier getIdentifier() {
        return NetworkManager.BUTTON_PRESSED_ID;
    }


    public static void receiveC2S(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ButtonPressedPacket packet = new ButtonPressedPacket(buf);

        if(!ControlSwapper.isControlIn(player)) ControlSwapper.updateClientInfo(player);
        ServerPlayerEntity controlOut = ControlSwapper.getControlOut(player);

        NetworkManager.sendPacketToClient(controlOut, packet);
    }

    public static void receiveS2C(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ButtonPressedPacket packet = new ButtonPressedPacket(buf);

        ControlSwapper.LOGGER.info("Received button packet on client");
        InputManager.onKey(packet.key, packet.pressed, packet.modifiers);
    }
}
