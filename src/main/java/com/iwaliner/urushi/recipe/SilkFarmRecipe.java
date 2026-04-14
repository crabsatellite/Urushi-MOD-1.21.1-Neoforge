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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.RecipeTypeRegister;
import com.mojang.serialization.MapCodec;

import javax.annotation.Nullable;

public class SilkFarmRecipe implements Recipe<RecipeInput> {

    protected  NonNullList<Ingredient> ingredient;
    private final ItemStack result;
    private final ResourceLocation location;
    public static ResourceLocation locationType=ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "silkworm");


    public SilkFarmRecipe(NonNullList<Ingredient> ingredient, ItemStack result, ResourceLocation location) {
        this.ingredient = ingredient;
        this.result=result;
        this.location = location;
    }
    public RecipeType<?> getType() {
        return RecipeTypeRegister.SilkwormFarmRecipe;
    }
    public NonNullList<Ingredient> getIngredients(){
        return ingredient;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return  this.ingredient.get(0).test(container.getItem(0)) && this.ingredient.get(1).test(container.getItem(1));
    }

    public ItemStack assemble(RecipeInput p_267036_, HolderLookup.Provider p_266699_) {
        return this.result.copy();
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        return result.copy();
    }
    public ItemStack getResultItem() {
        return result.copy();
    }
    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegister.SilkwormFarmSerializer.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ItemAndBlockRegister.silkworm_farm.get());
    }

    public static class SilkFarmRecipeType implements RecipeType<SilkFarmRecipe> {
        @Override
        public String toString() {
            return SilkFarmRecipe.locationType.toString();
        }
    }

    public static class SilkFarmSerializer implements RecipeSerializer<SilkFarmRecipe> {
        public static final MapCodec<SilkFarmRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> r.ingredient.stream().toList()),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(inst, (ings, out) -> {
                NonNullList<Ingredient> __list = NonNullList.withSize(ings.size(), Ingredient.EMPTY);
                for (int __k = 0; __k < ings.size(); __k++) __list.set(__k, ings.get(__k));
                return new SilkFarmRecipe(__list, out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "silkworm"));
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, SilkFarmRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> {
                        buf.writeVarInt(r.ingredient.size());
                        for (Ingredient __ing : r.ingredient) Ingredient.CONTENTS_STREAM_CODEC.encode(buf, __ing);
                        ItemStack.STREAM_CODEC.encode(buf, r.result);
                },
                (buf) -> {
                        int __sz = buf.readVarInt();
                        NonNullList<Ingredient> __list = NonNullList.withSize(__sz, Ingredient.EMPTY);
                        for (int __k = 0; __k < __sz; __k++) __list.set(__k, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        ItemStack __out = ItemStack.STREAM_CODEC.decode(buf);
                        return new SilkFarmRecipe(__list, __out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "silkworm"));
                }
        );

        @Override
        public MapCodec<SilkFarmRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SilkFarmRecipe> streamCodec() { return STREAM_CODEC; }
        public NonNullList<Ingredient> itemsFromJson(JsonArray p_44276_) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < p_44276_.size(); ++i) {
                Ingredient ingredient = Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, p_44276_.get(i)).result().orElse(Ingredient.EMPTY);
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        // Original 1.20.1 fromJson - convert to codec()/streamCodec():
        //         @Override
        //         public SilkFarmRecipe fromJson(ResourceLocation location, JsonObject json) {
        //             NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
        //
        //             ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        //             return new SilkFarmRecipe(nonnulllist,itemstack,location);
        //         }


        // Original 1.20.1 fromNetwork - convert to codec()/streamCodec():
        //         @Nullable
        //         @Override
        //         public SilkFarmRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf buffer) {
        //             NonNullList<Ingredient> input=NonNullList.withSize(2,Ingredient.EMPTY);
        //             for(int j = 0; j < input.size(); ++j) {
        //                 input.set(j, Ingredient.fromNetwork(buffer));
        //             }
        //             ItemStack output=buffer.readItem();
        //             return new SilkFarmRecipe(input,output,location);
        //         }


        // Original 1.20.1 toNetwork - convert to codec()/streamCodec():
        //         @Override
        //         public void toNetwork(FriendlyByteBuf buffer, SilkFarmRecipe recipe) {
        //             for(Ingredient ingredient : recipe.getIngredients()) {
        //                 ingredient.toNetwork(buffer);
        //             }
        //             buffer.writeItem(recipe.result);
        //         }

    }
}
