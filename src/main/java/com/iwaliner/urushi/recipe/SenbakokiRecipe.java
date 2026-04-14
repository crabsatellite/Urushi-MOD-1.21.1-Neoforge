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

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class SenbakokiRecipe implements Recipe<RecipeInput> {

    private final NonNullList<Ingredient> ingredient;
    private final ItemStack output;
    private final NonNullList<ItemStack> sub_output;
    private final ResourceLocation location;
    public static ResourceLocation locationType=ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "senbakoki");


    public SenbakokiRecipe(NonNullList<Ingredient> input, ItemStack output, ResourceLocation location) {
        this(input, output, location, NonNullList.create());
    }

    public SenbakokiRecipe(NonNullList<Ingredient> input, ItemStack output, ResourceLocation location, NonNullList<ItemStack> sub_output) {
        this.ingredient = input;
        this.output = output;
        this.location = location;
        this.sub_output = NonNullList.create();
        this.sub_output.addAll(sub_output);
    }

    public RecipeType<?> getType() {
        return RecipeTypeRegister.SenbakokiRecipe;
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
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    public NonNullList<Ingredient> getIngredient() {
        return ingredient;
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        return output.copy();
    }
    public ItemStack getResultItem() {
        return output.copy();
    }

    public NonNullList<ItemStack> getSubResultItems() {
        return sub_output.stream().map(ItemStack::copy).collect(Collectors.toCollection(NonNullList::create));
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegister.SenbakokiSerializer.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ItemAndBlockRegister.senbakoki.get());
    }

    public NonNullList<Ingredient> getIngredients(){
        return ingredient;
    }
    public static class SenbakokiRecipeType implements RecipeType<SenbakokiRecipe> {
        @Override
        public String toString() {
            return SenbakokiRecipe.locationType.toString();
        }
    }

    public static class SenbakokiSerializer implements RecipeSerializer<SenbakokiRecipe> {
        public static final MapCodec<SenbakokiRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> r.ingredient.stream().toList()),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output),
                ItemStack.CODEC.listOf().optionalFieldOf("sub_results", java.util.List.of()).forGetter(r -> r.sub_output.stream().toList())
        ).apply(inst, (ings, out, subs) -> {
                NonNullList<Ingredient> __list = NonNullList.withSize(ings.size(), Ingredient.EMPTY);
                for (int __k = 0; __k < ings.size(); __k++) __list.set(__k, ings.get(__k));
                NonNullList<ItemStack> __subs = NonNullList.create();
                __subs.addAll(subs);
                return new SenbakokiRecipe(__list, out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "senbakoki"), __subs);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, SenbakokiRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> {
                        buf.writeVarInt(r.ingredient.size());
                        for (Ingredient __ing : r.ingredient) Ingredient.CONTENTS_STREAM_CODEC.encode(buf, __ing);
                        ItemStack.STREAM_CODEC.encode(buf, r.output);
                        buf.writeVarInt(r.sub_output.size());
                        for (ItemStack __s : r.sub_output) ItemStack.STREAM_CODEC.encode(buf, __s);
                },
                (buf) -> {
                        int __sz = buf.readVarInt();
                        NonNullList<Ingredient> __list = NonNullList.withSize(__sz, Ingredient.EMPTY);
                        for (int __k = 0; __k < __sz; __k++) __list.set(__k, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        ItemStack __out = ItemStack.STREAM_CODEC.decode(buf);
                        int __subSz = buf.readVarInt();
                        NonNullList<ItemStack> __subs = NonNullList.create();
                        for (int __k = 0; __k < __subSz; __k++) __subs.add(ItemStack.STREAM_CODEC.decode(buf));
                        return new SenbakokiRecipe(__list, __out, ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "senbakoki"), __subs);
                }
        );

        @Override
        public MapCodec<SenbakokiRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SenbakokiRecipe> streamCodec() { return STREAM_CODEC; }
        // Original 1.20.1 fromJson - convert to codec()/streamCodec():
        //         @Override
        //         public SenbakokiRecipe fromJson(ResourceLocation location, JsonObject json) {
        //             ItemStack output;
        //             JsonArray ingredient=GsonHelper.getAsJsonArray(json,"ingredients");
        //             NonNullList<Ingredient> input=NonNullList.withSize(1,Ingredient.EMPTY);
        //             NonNullList<ItemStack> sub_output = NonNullList.create();
        //
        //             if (GsonHelper.isArrayNode(json, "result")) {
        //                 var itt = GsonHelper.getAsJsonArray(json, "result").iterator();
        //                 output = ShapedRecipe.itemStackFromJson(itt.next().getAsJsonObject());
        //                 sub_output.addAll(StreamSupport.stream(Spliterators.spliteratorUnknownSize(itt, Spliterator.ORDERED), false)
        //                         .map(j -> ShapedRecipe.itemStackFromJson(j.getAsJsonObject())).toList());
        //             } else {
        //                 output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        //             }
        //
        //             for(int i=0;i<input.size();i++){
        //                 input.set(i,Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, ingredient.get(0)).result().orElse(Ingredient.EMPTY));
        //             }
        //             return new SenbakokiRecipe(input,output,location,sub_output);
        //         }


        // Original 1.20.1 fromNetwork - convert to codec()/streamCodec():
        //         @Nullable
        //         @Override
        //         public SenbakokiRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf buffer) {
        //             NonNullList<Ingredient> input=NonNullList.withSize(1,Ingredient.EMPTY);
        //             input.set(0,Ingredient.fromNetwork(buffer));
        //             ItemStack output=buffer.readItem();
        //             NonNullList<ItemStack> sub_output = NonNullList.create();
        //             int size = buffer.readInt();
        //             for (int i = 0; i < size; i++) sub_output.add(buffer.readItem());
        //             return new SenbakokiRecipe(input, output, location, sub_output);
        //         }


        // Original 1.20.1 toNetwork - convert to codec()/streamCodec():
        //         @Override
        //         public void toNetwork(FriendlyByteBuf buffer, SenbakokiRecipe recipe) {
        //             for (Ingredient ingredient :recipe.getIngredient()){
        //                 ingredient.toNetwork(buffer);
        //             }
        //             buffer.writeItemStack(recipe.getResultItem(), false);
        //             buffer.writeInt(recipe.sub_output.size());
        //             for (ItemStack stack : recipe.sub_output) buffer.writeItemStack(stack, false);
        //         }

    }
}
