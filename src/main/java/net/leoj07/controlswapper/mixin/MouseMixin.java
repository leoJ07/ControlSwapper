package net.leoj07.controlswapper.mixin;

import net.leoj07.controlswapper.ControlSwapper;
import net.leoj07.controlswapper.client.ControlSwapperClient;
import net.leoj07.controlswapper.networking.NetworkManager;
import net.leoj07.controlswapper.networking.packet.ButtonPressedPacket;
import net.leoj07.controlswapper.networking.packet.MouseLockPacket;
import net.leoj07.controlswapper.networking.packet.MouseMovePacket;
import net.leoj07.controlswapper.networking.packet.MouseScrollPacket;
import net.leoj07.controlswapper.utils.InputManager;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButtonMixin(long window, int button, int action, int mods, CallbackInfo ci) {
        InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(button);
        boolean pressed = action != GLFW.GLFW_RELEASE;

        if(InputManager.shouldIgnoreInput(key)) return;
        InputManager.setKey(key, pressed);

        ControlSwapper.LOGGER.info("Mouse button pressed. button: " + key);
        if(ControlSwapperClient.cancelInput) {
            ControlSwapper.LOGGER.info("Canceling button press");
            ci.cancel();
        }

        if(ControlSwapperClient.sendInput) {
            ControlSwapper.LOGGER.info("Sending input to server");
            NetworkManager.sendPacketToServer(new ButtonPressedPacket(key, pressed, mods));
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScrollMixin(long window, double horizontal, double vertical, CallbackInfo ci) {
        ControlSwapper.LOGGER.info("Mouse scrolled");
        if(InputManager.shouldIgnoreInput(null)) return;

        ControlSwapper.LOGGER.info("processing event");
        if(ControlSwapperClient.cancelInput) {
            ControlSwapper.LOGGER.info("Canceling scroll");
            ci.cancel();
        }

        if(ControlSwapperClient.sendInput) {
            ControlSwapper.LOGGER.info("Sending input to server");
            NetworkManager.sendPacketToServer(new MouseScrollPacket(horizontal, vertical));
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void onCursorPosMixin(long window, double x, double y, CallbackInfo ci) {
        if(InputManager.shouldIgnoreInput(null)) return;

        ControlSwapper.LOGGER.info("Mouse moved");
        if(ControlSwapperClient.cancelInput) {
            ControlSwapper.LOGGER.info("Canceling mouse movement");
            ci.cancel();
        }

        if(ControlSwapperClient.sendInput) {
            ControlSwapper.LOGGER.info("Sending input to server");
            NetworkManager.sendPacketToServer(new MouseMovePacket(x, y));
        }
    }

    @Redirect(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
    private void lockCursorSendMixin(long handler, int inputModeValue, double x, double y) {
        ControlSwapper.LOGGER.info("lockCursor event");
        if(InputManager.shouldIgnoreCursor() || !ControlSwapperClient.sendCursor) {
            InputUtil.setCursorParameters(handler, inputModeValue, x, y);
            return;
        }

        ControlSwapper.LOGGER.info("lockCursor packet send");
        NetworkManager.sendPacketToServer(new MouseLockPacket(true, x, y));
        if(!ControlSwapperClient.sendInput)
            InputUtil.setCursorParameters(handler, inputModeValue, x, y);
    }

    @Redirect(method = "unlockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
    private void unlockCursorSendMixin(long handler, int inputModeValue, double x, double y) {
        ControlSwapper.LOGGER.info("unlockCursor event");
        if(InputManager.shouldIgnoreCursor() || !ControlSwapperClient.sendCursor) {
            InputUtil.setCursorParameters(handler, inputModeValue, x, y);
            return;
        }

        ControlSwapper.LOGGER.info("unlockCursor packet send");
        NetworkManager.sendPacketToServer(new MouseLockPacket(false, x, y));
        if(!ControlSwapperClient.sendInput)
            InputUtil.setCursorParameters(handler, inputModeValue, x, y);
    }

//    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
//    private void onLockCursorMixin(CallbackInfo ci) {
//        if(InputManager.shouldIgnore(null)) return;
//
//        ControlSwapper.LOGGER.info("Mouse locked");
//        if(ControlSwapperClient.sendCursor) {
//            ControlSwapper.LOGGER.info("Canceling mouse lock and sending packet to server");
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "unlockCursor", at = @At("HEAD"), cancellable = true)
//    private void onUnlockCursorMixin(CallbackInfo ci) {
//        if(InputManager.shouldIgnore(null)) return;
//
//        ControlSwapper.LOGGER.info("Mouse unlocked");
//        if(ControlSwapperClient.cancelInput) {
//            ControlSwapper.LOGGER.info("Canceling mouse unlock");
//            ci.cancel();
//        }
//
//        if(ControlSwapperClient.sendInput) {
//            ControlSwapper.LOGGER.info("Sending input to server");
//            NetworkManager.sendPacketToServer(new MouseLockPacket(false));
//        }
//    }
}
