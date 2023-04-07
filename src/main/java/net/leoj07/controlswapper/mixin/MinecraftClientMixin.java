package net.leoj07.controlswapper.mixin;

import net.leoj07.controlswapper.utils.InputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "isWindowFocused", at = @At("HEAD"), cancellable = true)
    private void isWindowFocusedMixin(CallbackInfoReturnable<Boolean> cir) {
        if(InputManager.isInputFromServer()) cir.setReturnValue(true);
    }

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void setScreenHead(Screen screen, CallbackInfo ci) {
        InputManager.oldScreen = MinecraftClient.getInstance().currentScreen;
    }
}
