package com.iwaliner.urushi;


import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.iwaliner.urushi.ModCoreUrushi;

public class ParticleRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ModCoreUrushi.ModID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WoodElement = PARTICLES.register("wood_element", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FireElement = PARTICLES.register("fire_element", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EarthElement = PARTICLES.register("earth_element", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MetalElement = PARTICLES.register("metal_element", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WaterElement = PARTICLES.register("water_element", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FallingRedLeaves = PARTICLES.register("falling_red_leaves", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FallingOrangeLeaves = PARTICLES.register("falling_orange_leaves", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FallingYellowLeaves = PARTICLES.register("falling_yellow_leaves", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FallingSakuraLeaves = PARTICLES.register("falling_sakura_leaves", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WoodElementMedium = PARTICLES.register("wood_element_medium", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FireElementMedium = PARTICLES.register("fire_element_medium", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EarthElementMedium = PARTICLES.register("earth_element_medium", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MetalElementMedium = PARTICLES.register("metal_element_medium", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WaterElementMedium = PARTICLES.register("water_element_medium", () -> new SimpleParticleType(true));


    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
