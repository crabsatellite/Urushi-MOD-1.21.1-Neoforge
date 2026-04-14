package com.iwaliner.urushi;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforgespi.language.IConfigurable;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.ForgeFeature;
import net.neoforged.neoforgespi.locating.IModFile;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.block.ChiseledLacquerLogBlock;
import com.iwaliner.urushi.block.SenbakokiBlock;
import com.iwaliner.urushi.network.NetworkAccess;
import com.iwaliner.urushi.recipe.SenbakokiRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@EventBusSubscriber(modid = ModCoreUrushi.ModID, bus = EventBusSubscriber.Bus.MOD)
public class CommonSetUp {

    @SubscribeEvent
    public static void CommonSetUpEvent(FMLCommonSetupEvent event) {
        // Network registration is now event-driven in NeoForge 1.21
        // Register via RegisterPayloadHandlersEvent instead
        DispenserBlock.registerBehavior(ItemAndBlockRegister.empty_bamboo_cup.get().asItem(), new OptionalDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            private ItemStack takeLiquid(BlockSource p_123447_, ItemStack p_123448_, ItemStack p_123449_) {
                p_123448_.shrink(1);
                if (p_123448_.isEmpty()) {
                    p_123447_.level().gameEvent((Entity)null, GameEvent.FLUID_PICKUP, p_123447_.pos());
                    return p_123449_.copy();
                } else {
                    if (!net.minecraft.world.level.block.entity.HopperBlockEntity.addItem(null, (net.minecraft.world.level.block.entity.DispenserBlockEntity) p_123447_.blockEntity(), p_123449_.copy(), null).isEmpty()) {
                        this.defaultDispenseItemBehavior.dispense(p_123447_, p_123449_.copy());
                    }

                    return p_123448_;
                }
            }

            public ItemStack execute(BlockSource p_123444_, ItemStack p_123445_) {
                this.setSuccess(false);
                ServerLevel serverlevel = p_123444_.level();
                BlockPos blockpos = p_123444_.pos().relative(p_123444_.state().getValue(DispenserBlock.FACING));
                BlockState blockstate = serverlevel.getBlockState(blockpos);
                if (serverlevel.getFluidState(blockpos).is(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.takeLiquid(p_123444_, p_123445_,new ItemStack(ItemAndBlockRegister.water_bamboo_cup.get()));
                } else {
                    return super.execute(p_123444_, p_123445_);
                }
            }
        });
        DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
        DispenserBlock.registerBehavior(Items.BOWL, new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos blockpos = source.pos().relative(direction);
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.getBlock() instanceof ChiseledLacquerLogBlock) {
                    if (blockstate.getValue(ChiseledLacquerLogBlock.FILLED)) {
                        this.setSuccess(true);
                        stack.shrink(1);
                        level.setBlockAndUpdate(blockpos, ItemAndBlockRegister.chiseled_lacquer_log.get().defaultBlockState().setValue(ChiseledLacquerLogBlock.FILLED, Boolean.valueOf(false)).setValue(ChiseledLacquerLogBlock.FACING, blockstate.getValue(ChiseledLacquerLogBlock.FACING)));
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, blockpos);
                        defaultDispenseItemBehavior.dispense(source, new ItemStack(ItemAndBlockRegister.raw_urushi_ball.get()).copy());

                    }
                    return stack;
                }
                return super.execute(source, stack);
            }
        });


        DispenserBlock.registerBehavior(ItemAndBlockRegister.rice_crop.get(), new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos blockpos = source.pos().relative(direction);
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.getBlock() instanceof SenbakokiBlock) {
                    Optional<RecipeHolder<SenbakokiRecipe>> recipe = Optional.of(level.getRecipeManager() )
                            .flatMap(manager -> manager.getRecipeFor(RecipeTypeRegister.SenbakokiRecipe, new SingleRecipeInput(stack), level));
                    if (recipe.isPresent()) {
                        this.setSuccess(true);
                        stack.shrink(1);
                        defaultDispenseItemBehavior.dispense(source, recipe.get().value().getResultItem().copy());
                        for(int i=0;i<recipe.get().value().getSubResultItems().size();i++) {
                            defaultDispenseItemBehavior.dispense(source, recipe.get().value().getSubResultItems().get(i).copy());
                        }
                        return stack;
                    }
                }
                return super.execute(source, stack);
            }
        });




    }
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
         event.register(EntityRegister.Ghost.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }



}
