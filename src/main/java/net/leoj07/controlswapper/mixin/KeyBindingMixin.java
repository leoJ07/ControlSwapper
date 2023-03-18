package net.leoj07.controlswapper.mixin;

import net.leoj07.controlswapper.client.ControlSwapperClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressedHead(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        if(!ControlSwapperClient.getInstance().isControlled()) return;

        ControlSwapperClient.getInstance().sendKey(key, pressed);
        ci.cancel();
    }
}
