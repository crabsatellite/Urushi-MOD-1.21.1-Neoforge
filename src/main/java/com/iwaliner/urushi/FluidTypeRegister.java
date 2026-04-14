package com.iwaliner.urushi;


import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.fluidtype.HotSpringWaterFluidType;
import com.mojang.math.Axis;

public class FluidTypeRegister {

    public static final DeferredRegister<FluidType> FLUID_TYPES=DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES,ModCoreUrushi.ModID);

    public static final DeferredHolder<FluidType, FluidType> HOT_SPRING_FLUID_TYPE = register("hot_spring_fluid",
            FluidType.Properties.create().lightLevel(2).density(1000).viscosity(1000).sound(SoundAction.get("drink"),
                    SoundEvents.BUCKET_FILL).canExtinguish(true).canConvertToSource(true));



    private static DeferredHolder<FluidType, FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new HotSpringWaterFluidType( properties));
    }
    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
