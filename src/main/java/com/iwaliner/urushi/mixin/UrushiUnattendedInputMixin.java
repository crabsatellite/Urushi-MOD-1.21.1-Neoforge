package com.iwaliner.urushi.mixin;

import com.iwaliner.urushi.test.UrushiClientTestMode;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputConstants.class)
public abstract class UrushiUnattendedInputMixin {
    @Inject(method = "grabOrReleaseMouse", at = @At("HEAD"), cancellable = true)
    private static void urushi$keepCursorFree(long window, int mode, double x, double y,
                                               CallbackInfo callbackInfo) {
        if (UrushiClientTestMode.enabled()) {
            UrushiClientTestMode.suppressedMouseGrabs++;
            callbackInfo.cancel();
        }
    }

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private static void urushi$ignoreNativeKeyPoll(long window, int key,
                                                    CallbackInfoReturnable<Boolean> callbackInfo) {
        if (UrushiClientTestMode.enabled()) {
            callbackInfo.setReturnValue(false);
        }
    }
}
