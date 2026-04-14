package com.iwaliner.urushi.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.RecipeTypeRegister;
import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

public class EarthElementTier2CraftingRecipe extends AbstractElementCraftingRecipe{
    public EarthElementTier2CraftingRecipe(NonNullList<Ingredient> input, ItemStack output, ResourceLocation location, int reiryoku) {
        super(input,output,location,reiryoku);
    }
    public RecipeType<?> getType() {
        return RecipeTypeRegister.EarthElementTier2CraftingRecipe;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegister.EarthElementTier2CraftingSerializer.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ItemAndBlockRegister.earth_element_crafting_table_tier2.get());
    }

    public static class EarthElementTier2CraftingRecipeType implements RecipeType<EarthElementTier2CraftingRecipe> {
        @Override
        public String toString() {
            return ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "earth_element_tier2_crafting").toString();
        }
    }

    public static class EarthElementTier2CraftingSerializer implements RecipeSerializer<EarthElementTier2CraftingRecipe> {
        public static final MapCodec<EarthElementTier2CraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> r.ingredient.stream().toList()),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output),
                Codec.INT.fieldOf("reiryoku").forGetter(r -> r.reiryoku)
        ).apply(inst, (ings, out, i) -> {
                NonNullList<Ingredient> __list = NonNullList.withSize(ings.size(), Ingredient.EMPTY);
                for (int __k = 0; __k < ings.size(); __k++) __list.set(__k, ings.get(__k));
                return new EarthElementTier2CraftingRecipe(__list, out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "earth_element_tier2_crafting"), i);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, EarthElementTier2CraftingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> {
                        buf.writeVarInt(r.ingredient.size());
                        for (Ingredient __ing : r.ingredient) Ingredient.CONTENTS_STREAM_CODEC.encode(buf, __ing);
                        ItemStack.STREAM_CODEC.encode(buf, r.output);
                        buf.writeVarInt(r.reiryoku);
                },
                (buf) -> {
                        int __sz = buf.readVarInt();
                        NonNullList<Ingredient> __list = NonNullList.withSize(__sz, Ingredient.EMPTY);
                        for (int __k = 0; __k < __sz; __k++) __list.set(__k, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        ItemStack __out = ItemStack.STREAM_CODEC.decode(buf);
                        int __i = buf.readVarInt();
                        return new EarthElementTier2CraftingRecipe(__list, __out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "earth_element_tier2_crafting"), __i);
                }
        );

        @Override
        public MapCodec<EarthElementTier2CraftingRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EarthElementTier2CraftingRecipe> streamCodec() { return STREAM_CODEC; }
        // Original 1.20.1 fromJson - convert to codec()/streamCodec():
        //         @Override
        //         public EarthElementTier2CraftingRecipe fromJson(ResourceLocation location, JsonObject json) {
        //             NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
        //                 ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        //                 int i = GsonHelper.getAsInt(json, "reiryoku");
        //                 return new EarthElementTier2CraftingRecipe(nonnulllist,itemstack,location,i);
        //
        //         }


        // Original 1.20.1 fromNetwork - convert to codec()/streamCodec():
        //         @Nullable
        //         @Override
        //         public EarthElementTier2CraftingRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf buffer) {
        //             NonNullList<Ingredient> input=NonNullList.withSize(4,Ingredient.EMPTY);
        //             for(int j = 0; j < input.size(); ++j) {
        //                 input.set(j, Ingredient.fromNetwork(buffer));
        //             }
        //             ItemStack output=buffer.readItem();
        //             int i = buffer.readVarInt();
        //             return new EarthElementTier2CraftingRecipe(input,output,location,i);
        //         }


        // Original 1.20.1 toNetwork - convert to codec()/streamCodec():
        //         @Override
        //         public void toNetwork(FriendlyByteBuf buffer, EarthElementTier2CraftingRecipe recipe) {
        //             for(Ingredient ingredient : recipe.getIngredients()) {
        //                 ingredient.toNetwork(buffer);
        //             }
        //             buffer.writeItem(recipe.output);
        //             buffer.writeVarInt(recipe.getReiryoku());
        //
        //         }

    }
}
