package com.iwaliner.urushi.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import com.iwaliner.urushi.ModCoreUrushi;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ModCoreUrushi.ModID, bus = EventBusSubscriber.Bus.MOD)
public class UrushiDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

      generator.addProvider(event.includeServer(), new UrushiWorldGenProvider(packOutput, lookupProvider));
    }
}
