package net.leoj07.controlswapper.networking.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.client.ControlSwapperClient;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ControlInfoPacket extends Packet {

    private boolean cancelInput;
    private boolean sendInput;
    private boolean sendCursor;

    public ControlInfoPacket(boolean cancelInput, boolean sendInput, boolean sendCursor) {
        this.cancelInput = cancelInput;
        this.sendInput = sendInput;
        this.sendCursor = sendCursor;
    }

    ControlInfoPacket(PacketByteBuf buf) {
        readFromBuf(buf);
    }

    @Override
    public PacketByteBuf writeToBuf(PacketByteBuf buf) {
        buf.writeBoolean(cancelInput);
        buf.writeBoolean(sendInput);
        buf.writeBoolean(sendCursor);
        return buf;
    }

    @Override
    public void readFromBuf(PacketByteBuf buf) {
        cancelInput = buf.readBoolean();
        sendInput = buf.readBoolean();
        sendCursor = buf.readBoolean();
    }

    @Override
    public Identifier getIdentifier() {
        return NetworkManager.CONTROL_INFO_ID;
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ControlInfoPacket p = new ControlInfoPacket(buf);

        ControlSwapperClient.cancelInput = p.cancelInput;
        ControlSwapperClient.sendInput = p.sendInput;
        ControlSwapperClient.sendCursor = p.sendCursor;

        ControlSwapper.LOGGER.info("Received control info packet. cancel: " + p.cancelInput + ", sendInput: " + p.sendInput + ", sendCursor: " + p.sendCursor);
    }
}
