package net.leoj07.controlswapper.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface MouseAccessor {

    @Invoker
    void callOnMouseButton(long window, int button, int action, int mods);

    @Invoker
    void callOnMouseScroll(long window, double horizontal, double vertical);

    @Invoker
    void callOnCursorPos(long window, double x, double y);
}
