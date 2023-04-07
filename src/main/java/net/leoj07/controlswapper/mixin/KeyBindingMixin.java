package net.leoj07.controlswapper.mixin;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "updatePressedStates()V", at = @At("HEAD"), cancellable = true)
    private static void updatePressedStates(CallbackInfo ci) {
        // TODO: If receiving input from server than use that otherwise use KeyBinding.unpressAll();
        KeyBinding.unpressAll();
        ci.cancel();
    }
}
