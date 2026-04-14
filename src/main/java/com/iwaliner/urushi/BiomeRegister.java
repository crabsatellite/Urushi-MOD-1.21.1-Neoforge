package com.iwaliner.urushi;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.iwaliner.urushi.ModCoreUrushi;

import java.util.ArrayList;
import java.util.List;

public class BiomeRegister {

    public static final DeferredRegister<Biome> BIOMES;
    public static final ResourceKey<Biome> SakuraForest;
    public static final ResourceKey<Biome> EulaliaPlains;
    public static final ResourceKey<Biome> AutumnForest;
    public static final ResourceKey<Biome> CedarForest;



    public static final List<ResourceKey<Biome>> KakuriyoList = new ArrayList<>();

    public BiomeRegister() {
    }

    public static void register(IEventBus eventBus) {
        BIOMES.register(eventBus);
        KakuriyoList.add(SakuraForest);
        KakuriyoList.add(EulaliaPlains);
        KakuriyoList.add(AutumnForest);
        KakuriyoList.add(CedarForest);


    }



    static {
        BIOMES = DeferredRegister.create(Registries.BIOME, ModCoreUrushi.ModID);
        SakuraForest = ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("sakura_forest"));
        EulaliaPlains =ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("eulalia_plains"));
        AutumnForest =ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("autumn_forest"));
        CedarForest =ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("cedar_forest"));



    }



}