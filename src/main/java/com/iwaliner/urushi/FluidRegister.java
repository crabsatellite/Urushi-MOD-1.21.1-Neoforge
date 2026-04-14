package com.iwaliner.urushi;


import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.block.HotSpringWaterBlock;

public class FluidRegister {

    public static final DeferredRegister<Fluid> FLUIDS=DeferredRegister.create(BuiltInRegistries.FLUID,ModCoreUrushi.ModID);


  public static final DeferredHolder<Fluid, FlowingFluid> HotSpringStill = FLUIDS.register("still_hot_spring_water",
          () -> new BaseFlowingFluid.Source(FluidRegister.HOT_SPRING_FLUID_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> HotSpringFlow = FLUIDS.register("flowing_hot_spring_water",
            () -> new BaseFlowingFluid.Flowing(FluidRegister.HOT_SPRING_FLUID_PROPERTIES));

    public static final BaseFlowingFluid.Properties HOT_SPRING_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            FluidTypeRegister.HOT_SPRING_FLUID_TYPE, HotSpringStill, HotSpringFlow)
            .slopeFindDistance(2).levelDecreasePerBlock(1).block(() -> (net.minecraft.world.level.block.LiquidBlock) ItemAndBlockRegister.HotSpringBlock.get())
            .bucket(ItemAndBlockRegister.hot_spring_bucket);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
