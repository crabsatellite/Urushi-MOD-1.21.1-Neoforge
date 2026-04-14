package com.iwaliner.urushi;



import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.iwaliner.urushi.ModCoreUrushi;

public class SoundRegister {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ModCoreUrushi.ModID);
    public static final DeferredHolder<SoundEvent, SoundEvent> WindBell=SOUNDS.register("wind_bell",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "wind_bell")));
    public static final DeferredHolder<SoundEvent, SoundEvent> UrushiAdvancements=SOUNDS.register("urushi_advancements",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "urushi_advancements")));
    public static final DeferredHolder<SoundEvent, SoundEvent> KakuriyoVillagerAmbient=SOUNDS.register("kakuriyo_villager_ambient",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo_villager_ambient")));
    public static final DeferredHolder<SoundEvent, SoundEvent> KakuriyoVillagerHurt=SOUNDS.register("kakuriyo_villager_hurt",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo_villager_hurt")));
    public static final DeferredHolder<SoundEvent, SoundEvent> KakuriyoVillagerDeath=SOUNDS.register("kakuriyo_villager_death",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo_villager_death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> KakuriyoVillagerYes=SOUNDS.register("kakuriyo_villager_yes",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo_villager_yes")));


    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

}
