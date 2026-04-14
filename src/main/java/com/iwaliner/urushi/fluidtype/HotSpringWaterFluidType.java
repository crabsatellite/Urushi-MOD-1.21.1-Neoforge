package com.iwaliner.urushi.fluidtype;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Consumer;
import javax.annotation.Nullable;

public class HotSpringWaterFluidType extends FluidType {
    /**
     * Default constructor.
     *
     * @param properties the general properties of the fluid type
     */
    public HotSpringWaterFluidType(Properties properties) {
        super(properties);
    }
    //   Example: event.registerFluidType(new IClientFluidTypeExtensions() { ... }, YOUR_FLUID_TYPE.get());
    //   Original rendering configuration:
    //
    //
    //
    //
    //     @Override
    //     public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    //         consumer.accept(new IClientFluidTypeExtensions() {
    //             private static final ResourceLocation HotSpringStillTex = ResourceLocation.withDefaultNamespace("block/water_still");
    //             private static final ResourceLocation HotSpringFlowingTex = ResourceLocation.withDefaultNamespace("block/water_flow");
    //             private static final ResourceLocation HotSpringOverrayTex = ResourceLocation.withDefaultNamespace("block/water_overlay");
    //
    //             @Override
    //             public ResourceLocation getStillTexture() {
    //                 return HotSpringStillTex;
    //             }
    //
    //             @Override
    //             public ResourceLocation getFlowingTexture() {
    //                 return HotSpringFlowingTex;
    //             }
    //
    //             @Override
    //             public int getTintColor() {
    //                 return 0xbf60c3c9;
    //             }
    //         });
    //     }

}
