package net.leoj07.controlswapper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class ControlSwapperClient implements ClientModInitializer {

    private static ControlSwapperClient instance;


    private boolean controlled = false;


    public static ControlSwapperClient getInstance() {
        return instance;
    }


    @Override
    public void onInitializeClient() {
        instance = this;
    }

    public void sendKey(InputUtil.Key key, boolean pressed) {

    }

    public boolean isControlled() {
        return controlled;
    }
}
