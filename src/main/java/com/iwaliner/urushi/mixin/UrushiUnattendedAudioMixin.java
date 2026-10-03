package com.iwaliner.urushi.mixin;

import com.iwaliner.urushi.test.UrushiClientTestMode;
import com.mojang.blaze3d.audio.Listener;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Listener.class)
public abstract class UrushiUnattendedAudioMixin {
    @Redirect(method = "setGain", at = @At(value = "INVOKE",
            target = "Lorg/lwjgl/openal/AL10;alListenerf(IF)V"))
    private void urushi$muteOpenAl(int parameter, float gain) {
        boolean quiet = UrushiClientTestMode.enabled();
        AL10.alListenerf(parameter, quiet ? 0.0F : gain);
        if (quiet) {
            UrushiClientTestMode.deviceGain = AL10.alGetListenerf(AL10.AL_GAIN);
            UrushiClientTestMode.gainWrites++;
        }
    }
}
