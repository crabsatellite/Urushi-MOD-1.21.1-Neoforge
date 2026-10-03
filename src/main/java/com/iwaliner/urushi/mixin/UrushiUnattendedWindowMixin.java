package com.iwaliner.urushi.mixin;

import com.iwaliner.urushi.test.UrushiClientTestMode;
import com.mojang.blaze3d.platform.Window;
import net.neoforged.fml.loading.ImmediateWindowHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@Mixin(Window.class)
public abstract class UrushiUnattendedWindowMixin {
    @Shadow private boolean fullscreen;
    @Shadow private boolean actuallyFullscreen;

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/fml/loading/ImmediateWindowHandler;setupMinecraftWindow(Ljava/util/function/IntSupplier;Ljava/util/function/IntSupplier;Ljava/util/function/Supplier;Ljava/util/function/LongSupplier;)J"))
    private long urushi$createHiddenWindow(IntSupplier width, IntSupplier height,
                                            Supplier<String> title, LongSupplier monitor) {
        if (!UrushiClientTestMode.enabled()) {
            return ImmediateWindowHandler.setupMinecraftWindow(width, height, title, monitor);
        }
        fullscreen = actuallyFullscreen = false;
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_FALSE);
        long window = GLFW.glfwCreateWindow(width.getAsInt(), height.getAsInt(), title.get(), 0, 0);
        if (window == 0) {
            throw new IllegalStateException("hidden Urushi client window creation failed");
        }
        return window;
    }

    @Inject(method = "setMode", at = @At("HEAD"))
    private void urushi$disableFullscreen(CallbackInfo callbackInfo) {
        if (UrushiClientTestMode.enabled()) {
            fullscreen = actuallyFullscreen = false;
        }
    }

    @Inject(method = "toggleFullScreen", at = @At("HEAD"), cancellable = true)
    private void urushi$blockFullscreenToggle(CallbackInfo callbackInfo) {
        if (UrushiClientTestMode.enabled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "onFocus", at = @At("HEAD"), cancellable = true)
    private void urushi$ignoreFocus(long window, boolean focused, CallbackInfo callbackInfo) {
        if (UrushiClientTestMode.enabled()) {
            callbackInfo.cancel();
        }
    }
}
