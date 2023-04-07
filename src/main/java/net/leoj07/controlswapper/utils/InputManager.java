package net.leoj07.controlswapper.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.mixin.KeyBindingAccessor;
import net.leoj07.controlswapper.mixin.MouseAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.function.Predicate;

public class InputManager {
    private static final HashMap<InputUtil.Key, Boolean> keys = new HashMap<>();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean inputFromServer = false;
    private static boolean cursorFromServer = false;

    public static Screen oldScreen = null;

    private static final HashMap<InputUtil.Key, Predicate<InputUtil.Key>> BLACKLIST_KEYS = new HashMap<>() {{
        put(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_ESCAPE), key -> {
            if((client.currentScreen instanceof HandledScreen)) return false;

            return true;
        });
    }};

    private static final HashMap<KeyBinding, Predicate<KeyBinding>> BLACKLIST_KEYBINDINGS = new HashMap<>() {{
        put(client.options.screenshotKey, keybinding -> true);
        put(client.options.chatKey, keybinding -> true);
    }};

    public static void setKey(InputUtil.Key key, boolean pressed) {
        keys.put(key, pressed);
    }
    public static boolean getKey(InputUtil.Key key) {
        return keys.get(key);
    }

    public static void onKey(InputUtil.Key key, boolean pressed, int modifiers) {
        int action = pressed? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE;
        client.execute(() -> {
            inputFromServer = true;
            if(key.getCategory() == InputUtil.Type.KEYSYM)
                client.keyboard.onKey(client.getWindow().getHandle(), key.getCode(), -1, action, modifiers);
            else if(key.getCategory() == InputUtil.Type.SCANCODE)
                client.keyboard.onKey(client.getWindow().getHandle(), -1, key.getCode(), action, modifiers);
            else if(key.getCategory() == InputUtil.Type.MOUSE)
                ((MouseAccessor) client.mouse).callOnMouseButton(client.getWindow().getHandle(), key.getCode(), action, modifiers);
            inputFromServer = false;
        });
    }
    public static void onMouseScroll(double horizontal, double vertical) {
        client.execute(() -> {
            inputFromServer = true;
            ((MouseAccessor) client.mouse).callOnMouseScroll(client.getWindow().getHandle(), horizontal, vertical);
            inputFromServer = false;
        });
    }
    public static void onMouseMove(double x, double y) {
        client.execute(() -> {
            inputFromServer = true;
            ControlSwapper.LOGGER.info("running onCursorPos");
            ControlSwapper.LOGGER.info("x: " + x + ", y: " + y);
            ((MouseAccessor) client.mouse).callOnCursorPos(client.getWindow().getHandle(), x, y);
            inputFromServer = false;
        });
    }
    public static void onMouseLock(boolean lock, double x, double y) {
        InputUtil.setCursorParameters(client.getWindow().getHandle(), lock? 212995 : 212993, x, y);

        // TODO: Debug cursor behavior
        // TODO: Cursor doesn't lock when unpausing the game
        // TODO: The cursor when locking the cursor the game window looses focus
        // TODO: While in the inventory the shift and right click functions doesn't work
    }

    public static boolean isBlacklisted(InputUtil.Key key) {
        if(key == null) return false;
        KeyBinding binding = KeyBindingAccessor.getKeyToBindings().get(key);

        return (BLACKLIST_KEYS.get(key) != null && BLACKLIST_KEYS.get(key).test(key))
                || (binding != null && BLACKLIST_KEYBINDINGS.get(binding) != null && BLACKLIST_KEYBINDINGS.get(binding).test(binding));
    }
    public static boolean shouldIgnoreInput(InputUtil.Key key) {
        if(shouldIgnore()) return true;

        return inputFromServer || isBlacklisted(key);
    }
    public static boolean shouldIgnoreCursor() {
        if(!(oldScreen instanceof HandledScreen) && oldScreen != null) return true;

        return cursorFromServer;
    }
    private static boolean shouldIgnore() {
        if(!(client.currentScreen instanceof HandledScreen) && client.currentScreen != null) return true;
        // if(!inGame) return true;

        return false;
    }
    public static boolean isInputFromServer() {
        return inputFromServer;
    }
    public static boolean isCursorFromServer() {
        return cursorFromServer;
    }
}
