package com.iwaliner.urushi;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import com.iwaliner.urushi.ModCoreUrushi;

public class DimensionRegister {
    public static final ResourceKey<DimensionType> KakuriyoType=ResourceKey.create(Registries.DIMENSION_TYPE,ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo"));
    public static final ResourceKey<Level> KakuriyoKey=ResourceKey.create(Registries.DIMENSION,ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo"));

    public static void register(){

    }
}
