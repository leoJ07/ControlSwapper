package net.leoj07.controlswapper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.leoj07.controlswapper.utils.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class ControlSwapperClient implements ClientModInitializer {

    private static ControlSwapperClient instance;

    public static boolean cancelInput = false;
    public static boolean sendInput = false;
    public static boolean sendCursor = false;


    public static ControlSwapperClient getInstance() {
        return instance;
    }
    public static void sendKey(InputUtil.Key key, boolean pressed) {
        ControlSwapper.LOGGER.info("Sending keybind information to server");
    }


    @Override
    public void onInitializeClient() {
        instance = this;

        NetworkManager.registerS2CPackets();
    }
}
