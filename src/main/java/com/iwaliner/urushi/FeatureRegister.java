package com.iwaliner.urushi;



import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.world.feature.BlockNearWaterReplaceFeature;
import com.iwaliner.urushi.world.feature.BlockReplaceFeature;
import com.iwaliner.urushi.world.feature.JapaneseTimberBambooFeature;
import com.iwaliner.urushi.world.feature.KakuriyoPortalFeature;

import java.util.function.Supplier;

public class FeatureRegister {
    public static final DeferredRegister<Feature<?>> Features = DeferredRegister.create(Registries.FEATURE, ModCoreUrushi.ModID);
    public static final DeferredHolder<Feature<?>, Feature<JapaneseTimberBambooFeature.Configuration>> Bamboo=Features.register("bamboo", () -> new JapaneseTimberBambooFeature(JapaneseTimberBambooFeature.Configuration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockReplaceFeature.Configuration>> BLOCK_REPLACE=Features.register("block_replace", () -> new BlockReplaceFeature(BlockReplaceFeature.Configuration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockNearWaterReplaceFeature.Configuration>> BLOCK_REPLACE_NEAR_WATER=Features.register("block_replace_near_water", () -> new BlockNearWaterReplaceFeature(BlockNearWaterReplaceFeature.Configuration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<KakuriyoPortalFeature.Configuration>> KakuriyoPortal=Features.register("kakuriyo_portal", () -> new KakuriyoPortalFeature(KakuriyoPortalFeature.Configuration.CODEC));
    public static <T extends FeatureConfiguration> DeferredHolder<Feature<?>, Feature<T>> register(String name, Supplier<Feature<T>> featureSupplier) {
        return Features.register(name, featureSupplier);
    }
    public static void register(IEventBus eventBus) {
        Features.register(eventBus);
    }
}
