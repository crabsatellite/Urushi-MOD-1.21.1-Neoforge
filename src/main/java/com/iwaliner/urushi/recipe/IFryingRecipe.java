package com.iwaliner.urushi.recipe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import com.iwaliner.urushi.ModCoreUrushi;

public interface IFryingRecipe extends Recipe<RecipeInput> {
    ResourceLocation locationType=ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "frying");

  /*  @Override
    default RecipeType<?> getType(){
        return Registry.RECIPE_TYPE.getOptional(locationType).get();
    }*/

    @Override
    default boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_){
        return true;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }
}
