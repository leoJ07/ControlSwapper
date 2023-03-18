package net.leoj07.controlswapper;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlSwapper implements ModInitializer {

    public static final String MOD_ID = "control-swapper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Enabling The Mod");
    }
}
