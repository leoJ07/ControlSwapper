package net.leoj07.controlswapper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.leoj07.controlswapper.commands.ControlCommand;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.leoj07.controlswapper.networking.packet.ControlInfoPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ControlSwapper implements ModInitializer {

    public static final String MOD_ID = "controlswapper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // "Player to receive data from": "Player to send data to"
    // "controlIn": "controlOut"
    private static final HashMap<ServerPlayerEntity, ServerPlayerEntity> controls = new HashMap<>();

    public static void control(ServerPlayerEntity controlIn, ServerPlayerEntity controlOut) {
        LOGGER.info("Control(" + controlIn.getName() + ", " + controlOut.getName() + ")");
        controls.put(controlIn, controlOut);

        // send packets to client
        updateClientInfo(controlIn);
        updateClientInfo(controlOut);
    }

    public static void stop(ServerPlayerEntity controlIn) {
        ServerPlayerEntity controlOut = controls.get(controlIn);
        controls.remove(controlIn);

        // send packets to client
        updateClientInfo(controlIn);
        updateClientInfo(controlOut);
    }

    public static void updateClientInfo(ServerPlayerEntity player) {
        NetworkManager.sendPacketToClient(player, new ControlInfoPacket(shouldCancelInput(player), shouldSendInput(player), shouldSendCursor(player)));
    }

    public static boolean isControlIn(ServerPlayerEntity player) {
        return controls.containsKey(player);
    }
    public static boolean isControlOut(ServerPlayerEntity player) {
        return controls.containsValue(player);
    }

    public static ServerPlayerEntity getControlIn(ServerPlayerEntity controlOut) {
        for (ServerPlayerEntity key : controls.keySet()) {
            if(controls.get(key).equals(controlOut)) return key;
        }
        return null;
    }
    public static ServerPlayerEntity getControlOut(ServerPlayerEntity controlIn) {
        return controls.get(controlIn);
    }

    private static boolean shouldSendInput(ServerPlayerEntity player) {
        return controls.containsKey(player);
    }
    private static boolean shouldCancelInput(ServerPlayerEntity player) {
        return controls.containsKey(player) || controls.containsValue(player);
    }
    private static boolean shouldSendCursor(ServerPlayerEntity player) {
        return controls.containsValue(player);
    }



    @Override
    public void onInitialize() {
        LOGGER.info("Enabling The Mod");

        NetworkManager.registerC2SPackets();
        registerCommands();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register(ControlCommand::register);
    }
}
