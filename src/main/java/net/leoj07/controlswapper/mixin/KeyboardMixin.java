package net.leoj07.controlswapper.mixin;

import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.client.ControlSwapperClient;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.leoj07.controlswapper.networking.packet.ButtonPressedPacket;
import net.leoj07.controlswapper.utils.InputManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyMixin(long window, int keycode, int scancode, int action, int modifiers, CallbackInfo ci) {
        InputUtil.Key key = InputUtil.fromKeyCode(keycode, scancode);
        boolean pressed = action != GLFW.GLFW_RELEASE;


        if(InputManager.shouldIgnoreInput(key)) return;
        InputManager.setKey(key, pressed);

        ControlSwapper.LOGGER.info("Received Key: " + key + ", pressed: " + pressed);
        if(ControlSwapperClient.cancelInput) {
            ControlSwapper.LOGGER.info("Canceling key press");
            ci.cancel();
        }

        if(ControlSwapperClient.sendInput) {
            ControlSwapper.LOGGER.info("Sending input to server");
            // TODO: Send keyboard input packet
            NetworkManager.sendPacketToServer(new ButtonPressedPacket(key, pressed, modifiers));
        }
    }
}
