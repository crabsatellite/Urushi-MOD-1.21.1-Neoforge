package com.iwaliner.urushi.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.RecipeTypeRegister;
import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

public class OilExtractingRecipe implements Recipe<RecipeInput> {

    private final NonNullList<Ingredient> ingredient;
    private final ItemStack output;
    private final ResourceLocation location;
    public static ResourceLocation locationType=ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "frying");


    public OilExtractingRecipe(NonNullList<Ingredient> input, ItemStack output, ResourceLocation location) {
        this.ingredient = input;
        this.output = output;
        this.location = location;
    }
    public RecipeType<?> getType() {
        return RecipeTypeRegister.OilExtractingRecipe;
    }
    @Override
    public boolean matches(RecipeInput inventory, Level world) {

        return ingredient.get(0).test(inventory.getItem(0));

    }

    @Override
    public ItemStack assemble(RecipeInput p_44001_, HolderLookup.Provider p_267165_) {
        return output.copy();
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        return output.copy();
    }
    public ItemStack getResultItem() {
        return output.copy();
    }
    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    public NonNullList<Ingredient> getIngredient() {
        return ingredient;
    }




    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegister.OilExtractingSerializer.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ItemAndBlockRegister.oil_extractor.get());
    }

    public NonNullList<Ingredient> getIngredients(){
        return ingredient;
    }
    public static class OilExtractingRecipeType implements RecipeType<OilExtractingRecipe> {
        @Override
        public String toString() {
            return OilExtractingRecipe.locationType.toString();
        }
    }

    public static class OilExtractingSerializer implements RecipeSerializer<OilExtractingRecipe> {
        public static final MapCodec<OilExtractingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> r.ingredient.stream().toList()),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output)
        ).apply(inst, (ings, out) -> {
                NonNullList<Ingredient> __list = NonNullList.withSize(ings.size(), Ingredient.EMPTY);
                for (int __k = 0; __k < ings.size(); __k++) __list.set(__k, ings.get(__k));
                return new OilExtractingRecipe(__list, out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "frying"));
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, OilExtractingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> {
                        buf.writeVarInt(r.ingredient.size());
                        for (Ingredient __ing : r.ingredient) Ingredient.CONTENTS_STREAM_CODEC.encode(buf, __ing);
                        ItemStack.STREAM_CODEC.encode(buf, r.output);
                },
                (buf) -> {
                        int __sz = buf.readVarInt();
                        NonNullList<Ingredient> __list = NonNullList.withSize(__sz, Ingredient.EMPTY);
                        for (int __k = 0; __k < __sz; __k++) __list.set(__k, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        ItemStack __out = ItemStack.STREAM_CODEC.decode(buf);
                        return new OilExtractingRecipe(__list, __out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "frying"));
                }
        );

        @Override
        public MapCodec<OilExtractingRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OilExtractingRecipe> streamCodec() { return STREAM_CODEC; }
        // Original 1.20.1 fromJson - convert to codec()/streamCodec():
        //         @Override
        //         public OilExtractingRecipe fromJson(ResourceLocation location, JsonObject json) {
        //             ItemStack output= ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json,"result"));
        //             JsonArray ingredient=GsonHelper.getAsJsonArray(json,"ingredients");
        //             NonNullList<Ingredient> input=NonNullList.withSize(1,Ingredient.EMPTY);
        //             for(int i=0;i<input.size();i++){
        //                 input.set(i,Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, ingredient.get(0)).result().orElse(Ingredient.EMPTY));
        //             }
        //             return new OilExtractingRecipe(input,output,location);
        //         }


        // Original 1.20.1 fromNetwork - convert to codec()/streamCodec():
        //         @Nullable
        //         @Override
        //         public OilExtractingRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf buffer) {
        //             NonNullList<Ingredient> input=NonNullList.withSize(1,Ingredient.EMPTY);
        //             input.set(0,Ingredient.fromNetwork(buffer));
        //             ItemStack output=buffer.readItem();
        //             return new OilExtractingRecipe(input,output,location);
        //         }


        // Original 1.20.1 toNetwork - convert to codec()/streamCodec():
        //         @Override
        //         public void toNetwork(FriendlyByteBuf buffer, OilExtractingRecipe recipe) {
        //             for (Ingredient ingredient :recipe.getIngredient()){
        //                 ingredient.toNetwork(buffer);
        //             }
        //             buffer.writeItemStack(recipe.output,false);
        //         }

    }
}
