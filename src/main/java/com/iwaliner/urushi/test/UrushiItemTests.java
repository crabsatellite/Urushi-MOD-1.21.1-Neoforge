package com.iwaliner.urushi.test;

import com.iwaliner.urushi.BlockEntityRegister;
import com.iwaliner.urushi.EntityRegister;
import com.iwaliner.urushi.FluidRegister;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.MenuRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.ParticleRegister;
import com.iwaliner.urushi.RecipeTypeRegister;
import com.iwaliner.urushi.SoundRegister;
import com.iwaliner.urushi.block.FutonBlock;
import com.iwaliner.urushi.item.AbstractMagatamaItem;
import com.iwaliner.urushi.item.PlaceableFoodItem;
import com.iwaliner.urushi.item.WearableItem;
import com.iwaliner.urushi.util.interfaces.ElementItem;
import com.iwaliner.urushi.util.interfaces.HasReiryokuItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Startup-time integration validation for the entire Urushi registry.
 * Runs after FMLCommonSetupEvent (when all DeferredRegisters are populated).
 * Logs a per-registry summary and detailed failures. Optionally throws when
 * the system property {@code urushi.tests.failHard=true} is set, so CI can
 * fail the build on any registration regression.
 */
@EventBusSubscriber(modid = ModCoreUrushi.ModID, bus = EventBusSubscriber.Bus.MOD)
public final class UrushiItemTests {

    private static final String FAIL_HARD_PROP = "urushi.tests.failHard";
    private static final String AUTO_EXIT_PROP = "urushi.tests.autoExit";
    private static final String AUTO_EXIT_ENV = "URUSHI_TESTS_AUTO_EXIT";

    private UrushiItemTests() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(UrushiItemTests::runAll);
    }

    public static void runAll() {
        ModCoreUrushi.logger.info("[UrushiItemTests] === Starting Urushi registry integration checks ===");

        Map<String, List<String>> failuresByRegistry = new LinkedHashMap<>();
        Map<String, Integer> countsByRegistry = new LinkedHashMap<>();

        runChecked("items", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItems);
        runChecked("blocks", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlocks);
        runChecked("blockItemMapping", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockItemMapping);
        runChecked("entities", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEntities);
        runChecked("blockEntities", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockEntities);
        runChecked("fluids", failuresByRegistry, countsByRegistry, UrushiItemTests::checkFluids);
        runChecked("sounds", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSounds);
        runChecked("menus", failuresByRegistry, countsByRegistry, UrushiItemTests::checkMenus);
        runChecked("particles", failuresByRegistry, countsByRegistry, UrushiItemTests::checkParticles);
        runChecked("recipeSerializers", failuresByRegistry, countsByRegistry, UrushiItemTests::checkRecipeSerializers);
        runChecked("creativeTabs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkCreativeTabContents);
        runChecked("uniqueIds", failuresByRegistry, countsByRegistry, UrushiItemTests::checkUniqueIds);
        runChecked("itemCategoryHierarchy", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItemCategoryHierarchy);
        runChecked("magatamaItems", failuresByRegistry, countsByRegistry, UrushiItemTests::checkMagatamaItems);
        runChecked("swordItems", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSwordItems);
        runChecked("equipableItems", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEquipableItems);
        runChecked("edibleItems", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEdibleItems);
        runChecked("elementItems", failuresByRegistry, countsByRegistry, UrushiItemTests::checkElementItems);
        runChecked("blockItemSubclasses", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockItemSubclasses);
        runChecked("placeableFoodItems", failuresByRegistry, countsByRegistry, UrushiItemTests::checkPlaceableFoodItems);
        runChecked("specialtyItemPresence", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSpecialtyItemPresence);
        runChecked("appendHoverTextSafety", failuresByRegistry, countsByRegistry, UrushiItemTests::checkAppendHoverTextSafety);
        runChecked("enUsTranslationCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEnUsTranslationCoverage);
        runChecked("jaJpTranslationParity", failuresByRegistry, countsByRegistry, UrushiItemTests::checkJaJpTranslationParity);
        runChecked("itemModelCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItemModelCoverage);
        runChecked("swordAttackStats", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSwordAttackStats);
        runChecked("spawnEggsValid", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSpawnEggsValid);
        runChecked("foodPropertyRanges", failuresByRegistry, countsByRegistry, UrushiItemTests::checkFoodPropertyRanges);
        runChecked("blockstateCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockstateCoverage);
        runChecked("blockLootTableCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockLootTableCoverage);
        runChecked("blockEntityBlockBinding", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockEntityBlockBinding);
        runChecked("attributeModifierIdsUnique", failuresByRegistry, countsByRegistry, UrushiItemTests::checkAttributeModifierIdsUnique);
        runChecked("blockPropertiesSane", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockPropertiesSane);
        runChecked("entityTypeSizeSane", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEntityTypeSizeSane);
        runChecked("magatamaElementBinding", failuresByRegistry, countsByRegistry, UrushiItemTests::checkMagatamaElementBinding);
        runChecked("itemTextureCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItemTextureCoverage);
        runChecked("swordAttackSpeedSign", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSwordAttackSpeedSign);
        runChecked("itemCreativeTabCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItemCreativeTabCoverage);
        runChecked("descriptionIdFormat", failuresByRegistry, countsByRegistry, UrushiItemTests::checkDescriptionIdFormat);
        runChecked("recipeItemReferences", failuresByRegistry, countsByRegistry, UrushiItemTests::checkRecipeItemReferences);
        runChecked("futonNoBlockEntity", failuresByRegistry, countsByRegistry, UrushiItemTests::checkFutonNoBlockEntity);
        runChecked("lootTablesParseable", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLootTablesParseable);
        runChecked("lootTableOrphans", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLootTableOrphans);
        runChecked("lootTablePatchouliGuard", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLootTablePatchouliGuard);
        runChecked("lootTableNoUnreachable", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLootTableNoUnreachable);
        runChecked("biomeTagReferences", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBiomeTagReferences);
        runChecked("commonTagReferences", failuresByRegistry, countsByRegistry, UrushiItemTests::checkCommonTagReferences);
        runChecked("botanypotsConditions", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBotanypotsConditions);
        runChecked("noLegacyPluralDataDirs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkNoLegacyPluralDataDirs);
        runChecked("senryoubakoContainerComponent", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSenryoubakoContainerComponent);
        runChecked("orphanBlockstates", failuresByRegistry, countsByRegistry, UrushiItemTests::checkOrphanBlockstates);
        runChecked("orphanItemModels", failuresByRegistry, countsByRegistry, UrushiItemTests::checkOrphanItemModels);
        runChecked("orphanLangKeys", failuresByRegistry, countsByRegistry, UrushiItemTests::checkOrphanLangKeys);
        runChecked("recipeCommonTagSingular", failuresByRegistry, countsByRegistry, UrushiItemTests::checkRecipeCommonTagSingular);
        runChecked("tagFilenameConsistency", failuresByRegistry, countsByRegistry, UrushiItemTests::checkTagFilenameConsistency);
        runChecked("nullTextureAdequate", failuresByRegistry, countsByRegistry, UrushiItemTests::checkNullTextureAdequate);
        runChecked("localeParityAll", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLocaleParityAll);
        runChecked("langValuesNonEmpty", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLangValuesNonEmpty);
        runChecked("langFilesValid", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLangFilesValid);
        runChecked("blockTranslationCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockTranslationCoverage);
        runChecked("packMcmetaValid", failuresByRegistry, countsByRegistry, UrushiItemTests::checkPackMcmetaValid);
        runChecked("soundsJsonCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSoundsJsonCoverage);
        runChecked("itemModelTextureLayers", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItemModelTextureLayers);
        runChecked("recipeResultIdsExist", failuresByRegistry, countsByRegistry, UrushiItemTests::checkRecipeResultIdsExist);
        runChecked("minecraftTagFilenamesValid", failuresByRegistry, countsByRegistry, UrushiItemTests::checkMinecraftTagFilenamesValid);
        runChecked("legacyForgeDataDirs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkLegacyForgeDataDirs);
        runChecked("botanyPotsRecipeItemsExist", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBotanyPotsRecipeItemsExist);
        runChecked("blockModelTextureRefs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockModelTextureRefs);
        runChecked("blockModelParentRefs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockModelParentRefs);
        runChecked("blockstateModelRefs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBlockstateModelRefs);
        runChecked("noBakFiles", failuresByRegistry, countsByRegistry, UrushiItemTests::checkNoBakFiles);
        runChecked("patchouliEntryItemRefs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkPatchouliEntryItemRefs);
        runChecked("patchouliBookModels", failuresByRegistry, countsByRegistry, UrushiItemTests::checkPatchouliBookModels);
        runChecked("biomeModifierFeatureRefs", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBiomeModifierFeatureRefs);
        runChecked("containerMenuTitlesTranslated", failuresByRegistry, countsByRegistry, UrushiItemTests::checkContainerMenuTitlesTranslated);
        runChecked("biomeResourceKeyNamespace", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBiomeResourceKeyNamespace);
        runChecked("biomeModifierNoForgeTags", failuresByRegistry, countsByRegistry, UrushiItemTests::checkBiomeModifierNoForgeTags);
        runChecked("entityLangCoverage", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEntityLangCoverage);
        runChecked("entityLangOrphans", failuresByRegistry, countsByRegistry, UrushiItemTests::checkEntityLangOrphans);
        runChecked("mixinClassesExist", failuresByRegistry, countsByRegistry, UrushiItemTests::checkMixinClassesExist);
        runChecked("creativeTabIdTypo", failuresByRegistry, countsByRegistry, UrushiItemTests::checkCreativeTabIdTypo);
        runChecked("noEmptyCatchBlocks", failuresByRegistry, countsByRegistry, UrushiItemTests::checkNoEmptyCatchBlocks);
        runChecked("soundSubtitleKeys", failuresByRegistry, countsByRegistry, UrushiItemTests::checkSoundSubtitleKeys);
        runChecked("itemPredicateOverridesRegistered", failuresByRegistry, countsByRegistry, UrushiItemTests::checkItemPredicateOverridesRegistered);
        runChecked("aprilFoolsRiceOverrides", failuresByRegistry, countsByRegistry, UrushiItemTests::checkAprilFoolsRiceOverrides);
        runChecked("aprilFoolsSakuraHeadHook", failuresByRegistry, countsByRegistry, UrushiItemTests::checkAprilFoolsSakuraHeadHook);
        runChecked("aprilFoolsCallSites", failuresByRegistry, countsByRegistry, UrushiItemTests::checkAprilFoolsCallSites);

        int totalFailures = 0;
        for (Map.Entry<String, Integer> e : countsByRegistry.entrySet()) {
            String registry = e.getKey();
            int count = e.getValue();
            List<String> fails = failuresByRegistry.getOrDefault(registry, List.of());
            totalFailures += fails.size();
            if (fails.isEmpty()) {
                ModCoreUrushi.logger.info("[UrushiItemTests] {}: OK ({} entries)", registry, count);
            } else {
                ModCoreUrushi.logger.error("[UrushiItemTests] {}: {} failure(s) out of {} entries",
                        registry, fails.size(), count);
                for (String f : fails) {
                    ModCoreUrushi.logger.error("[UrushiItemTests]   - {}", f);
                }
            }
        }

        if (totalFailures == 0) {
            ModCoreUrushi.logger.info("[UrushiItemTests] === All checks PASSED ===");
        } else {
            String msg = "UrushiItemTests detected " + totalFailures + " registry validation failure(s)";
            ModCoreUrushi.logger.error("[UrushiItemTests] === {} ===", msg);
            if (Boolean.getBoolean(FAIL_HARD_PROP)) {
                throw new IllegalStateException(msg);
            }
        }

        if (Boolean.getBoolean(AUTO_EXIT_PROP) || "1".equals(System.getenv(AUTO_EXIT_ENV))
                || "true".equalsIgnoreCase(System.getenv(AUTO_EXIT_ENV))) {
            int code = totalFailures == 0 ? 0 : 1;
            ModCoreUrushi.logger.info("[UrushiItemTests] autoExit requested — shutting down JVM with code {}", code);
            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                System.exit(code);
            }, "UrushiItemTests-AutoExit").start();
        }
    }

    @FunctionalInterface
    private interface RegistryCheck {
        int check(List<String> failuresOut);
    }

    private static void runChecked(String name,
                                   Map<String, List<String>> failuresByRegistry,
                                   Map<String, Integer> countsByRegistry,
                                   RegistryCheck check) {
        List<String> failures = new ArrayList<>();
        int count;
        try {
            count = check.check(failures);
        } catch (Throwable t) {
            count = 0;
            failures.add("CHECK CRASHED: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        countsByRegistry.put(name, count);
        if (!failures.isEmpty()) {
            failuresByRegistry.put(name, failures);
        }
    }

    private static int checkItems(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace, expected '" + ModCoreUrushi.ModID + "'");
            }
            try {
                Item item = holder.get();
                if (item == null) {
                    failures.add(id + ": holder.get() returned null");
                    continue;
                }
                ItemStack stack = new ItemStack(item);
                if (stack.isEmpty()) {
                    failures.add(id + ": new ItemStack(item) is empty");
                }
                String descId = item.getDescriptionId();
                if (descId == null || descId.isBlank()) {
                    failures.add(id + ": empty description id");
                } else if (!descId.contains(ModCoreUrushi.ModID)) {
                    failures.add(id + ": description id '" + descId + "' missing mod id");
                }
                int maxStack = item.getDefaultMaxStackSize();
                if (maxStack <= 0 || maxStack > 99) {
                    failures.add(id + ": invalid max stack size " + maxStack);
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkBlocks(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                Block block = holder.get();
                if (block == null) {
                    failures.add(id + ": holder.get() returned null");
                    continue;
                }
                BlockState state = block.defaultBlockState();
                if (state == null) {
                    failures.add(id + ": defaultBlockState() is null");
                    continue;
                }
                if (state.getBlock() != block) {
                    failures.add(id + ": defaultBlockState().getBlock() != block");
                }
                String descId = block.getDescriptionId();
                if (descId == null || descId.isBlank()) {
                    failures.add(id + ": empty description id");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    /**
     * Each block that has a corresponding BlockItem (same id in ITEMS) should
     * round-trip: Item.byBlock(block) == that BlockItem.
     */
    private static int checkBlockItemMapping(List<String> failures) {
        Map<ResourceLocation, Item> itemsById = new LinkedHashMap<>();
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            try {
                itemsById.put(holder.getId(), holder.get());
            } catch (Throwable t) {
                failures.add(holder.getId() + ": item holder threw " + t.getMessage());
            }
        }
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            ResourceLocation id = holder.getId();
            Item matched = itemsById.get(id);
            if (matched == null) continue;
            count++;
            if (!(matched instanceof BlockItem bi)) {
                ModCoreUrushi.logger.debug("[UrushiItemTests] {}: shares id with non-BlockItem {} (intentional)",
                        id, matched.getClass().getSimpleName());
                continue;
            }
            try {
                Block block = holder.get();
                if (bi.getBlock() != block) {
                    failures.add(id + ": BlockItem.getBlock() != registered block");
                }
            } catch (Throwable t) {
                failures.add(id + ": mapping check threw " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkEntities(List<String> failures) {
        int count = 0;
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> holder : EntityRegister.Entities.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                EntityType<?> type = holder.get();
                if (type == null) {
                    failures.add(id + ": holder.get() returned null");
                    continue;
                }
                String descId = type.getDescriptionId();
                if (descId == null || descId.isBlank()) {
                    failures.add(id + ": empty description id");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkBlockEntities(List<String> failures) {
        int count = 0;
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> holder : BlockEntityRegister.Tiles.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                BlockEntityType<?> type = holder.get();
                if (type == null) {
                    failures.add(id + ": holder.get() returned null");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkFluids(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Fluid, ? extends Fluid> holder : FluidRegister.FLUIDS.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                Fluid fluid = holder.get();
                if (fluid == null) {
                    failures.add(id + ": holder.get() returned null");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkSounds(List<String> failures) {
        int count = 0;
        for (DeferredHolder<SoundEvent, ? extends SoundEvent> holder : SoundRegister.SOUNDS.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                SoundEvent ev = holder.get();
                if (ev == null) {
                    failures.add(id + ": holder.get() returned null");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkMenus(List<String> failures) {
        int count = 0;
        for (DeferredHolder<MenuType<?>, ? extends MenuType<?>> holder : MenuRegister.MENUS.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                MenuType<?> type = holder.get();
                if (type == null) {
                    failures.add(id + ": holder.get() returned null");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkParticles(List<String> failures) {
        int count = 0;
        for (DeferredHolder<ParticleType<?>, ? extends ParticleType<?>> holder : ParticleRegister.PARTICLES.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                ParticleType<?> type = holder.get();
                if (type == null) {
                    failures.add(id + ": holder.get() returned null");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    private static int checkRecipeSerializers(List<String> failures) {
        int count = 0;
        for (DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>> holder : RecipeTypeRegister.RECIPE_SERIALIZER.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            if (!ModCoreUrushi.ModID.equals(id.getNamespace())) {
                failures.add(id + ": wrong namespace");
            }
            try {
                RecipeSerializer<?> ser = holder.get();
                if (ser == null) {
                    failures.add(id + ": holder.get() returned null");
                }
            } catch (Throwable t) {
                failures.add(id + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    /**
     * Verify every entry in the creative-tab content lists is a registered Item.
     */
    private static int checkCreativeTabContents(List<String> failures) {
        int count = 0;
        count += checkTabList("redstoneTab", ModCoreUrushi.redstoneTabContents, failures);
        count += checkTabList("urushiTab", ModCoreUrushi.urushiTabContents, failures);
        count += checkTabList("plasterTab", ModCoreUrushi.urushiPlasterTabContents, failures);
        count += checkTabList("woodTab", ModCoreUrushi.urushiWoodTabContents, failures);
        count += checkTabList("foodTab", ModCoreUrushi.urushiFoodTabContents, failures);
        count += checkTabList("magicTab", ModCoreUrushi.urushiMagicTabContents, failures);
        return count;
    }

    private static int checkTabList(String tabName,
                                    List<DeferredHolder<Item, Item>> contents,
                                    List<String> failures) {
        if (contents == null) {
            failures.add(tabName + ": tab list is null");
            return 0;
        }
        int idx = 0;
        for (DeferredHolder<Item, Item> holder : contents) {
            idx++;
            if (holder == null) {
                failures.add(tabName + "[" + idx + "]: null entry");
                continue;
            }
            try {
                Item item = holder.get();
                if (item == null) {
                    failures.add(tabName + "[" + idx + "] " + holder.getId() + ": item is null");
                }
            } catch (Throwable t) {
                failures.add(tabName + "[" + idx + "] " + holder.getId() + ": threw " + t.getMessage());
            }
        }
        return contents.size();
    }

    /**
     * Walk every registered item and bucket by class hierarchy. Logs a per-bucket
     * count and ensures the major buckets we expect from the original 1.20.1 mod
     * are non-empty after migration. A bucket dropping to zero is a smoking gun
     * for a category whose items got silently removed during the port.
     */
    private static int checkItemCategoryHierarchy(List<String> failures) {
        Map<String, Integer> buckets = new TreeMap<>();
        int total = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (item == null) continue;
            total++;
            ItemStack stack = new ItemStack(item);
            if (item instanceof AbstractMagatamaItem) buckets.merge("magatama", 1, Integer::sum);
            if (item instanceof SwordItem)            buckets.merge("sword", 1, Integer::sum);
            if (item instanceof BlockItem)            buckets.merge("blockItem", 1, Integer::sum);
            if (item instanceof Equipable)            buckets.merge("equipable", 1, Integer::sum);
            if (item instanceof PlaceableFoodItem)    buckets.merge("placeableFood", 1, Integer::sum);
            if (item instanceof ElementItem)          buckets.merge("element", 1, Integer::sum);
            if (item instanceof HasReiryokuItem)      buckets.merge("hasReiryoku", 1, Integer::sum);
            if (item instanceof WearableItem)         buckets.merge("wearable", 1, Integer::sum);
            if (stack.has(DataComponents.FOOD))       buckets.merge("edible", 1, Integer::sum);
            if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) buckets.merge("withAttributeModifiers", 1, Integer::sum);
        }
        ModCoreUrushi.logger.info("[UrushiItemTests] item category hierarchy across {} items:", total);
        buckets.forEach((k, v) -> ModCoreUrushi.logger.info("[UrushiItemTests]   {} = {}", k, v));

        // Buckets that the 1.20.1 ref guarantees to be non-empty.
        String[] required = { "magatama", "sword", "blockItem", "equipable", "placeableFood", "element", "hasReiryoku", "edible" };
        for (String r : required) {
            if (buckets.getOrDefault(r, 0) == 0) {
                failures.add("category bucket '" + r + "' is empty after migration");
            }
        }
        return total;
    }

    /**
     * Magatama spirit-stones: 5 elements (wood/fire/earth/metal/water). Each must
     * subclass AbstractMagatamaItem, implement HasReiryokuItem, expose a positive
     * reiryoku capacity, max-stack to 1 (single-charge tool), and tooltip-render
     * without throwing (regression check for the tooltip migration).
     */
    private static int checkMagatamaItems(List<String> failures) {
        int count = 0;
        Set<String> classNames = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof AbstractMagatamaItem mag)) continue;
            count++;
            ResourceLocation id = holder.getId();
            classNames.add(mag.getClass().getSimpleName());

            if (!(mag instanceof HasReiryokuItem hr)) {
                failures.add(id + ": magatama not HasReiryokuItem");
                continue;
            }
            if (hr.getReiryokuCapacity() <= 0) {
                failures.add(id + ": reiryoku capacity is " + hr.getReiryokuCapacity());
            }
            if (item.getDefaultMaxStackSize() != 1) {
                failures.add(id + ": magatama max stack should be 1, got " + item.getDefaultMaxStackSize());
            }
            try {
                List<Component> lines = new ArrayList<>();
                item.appendHoverText(new ItemStack(item), Item.TooltipContext.EMPTY, lines, TooltipFlag.NORMAL);
                if (lines.size() < 2) {
                    failures.add(id + ": magatama tooltip rendered " + lines.size() + " lines, expected >=2");
                }
            } catch (Throwable t) {
                failures.add(id + ": appendHoverText threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        if (count < 5) {
            failures.add("expected at least 5 magatama items (wood/fire/earth/metal/water), found " + count);
        }
        ModCoreUrushi.logger.info("[UrushiItemTests] magatama subclasses present: {}", classNames);
        return count;
    }

    /**
     * Katana / sword family: must be SwordItem, must carry attribute modifiers
     * (mainhand damage + speed) so the player actually does damage on swing.
     */
    private static int checkSwordItems(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof SwordItem sword)) continue;
            count++;
            ResourceLocation id = holder.getId();
            ItemStack stack = new ItemStack(item);
            ItemAttributeModifiers mods = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (mods == null || mods.modifiers().isEmpty()) {
                failures.add(id + ": sword has no attribute modifiers (damage/speed missing)");
            }
            if (sword.getDefaultMaxStackSize() != 1) {
                failures.add(id + ": sword max stack should be 1, got " + sword.getDefaultMaxStackSize());
            }
        }
        if (count < 1) {
            failures.add("expected at least 1 SwordItem (katana), found 0");
        }
        return count;
    }

    /**
     * Equipable items (head/chest/legs/feet wearables, plus bespoke parasols/fans
     * the mod treats as gear). Each must report a non-null EquipmentSlot via the
     * vanilla Equipable.get(stack) path so it actually equips on right-click.
     */
    private static int checkEquipableItems(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof Equipable)) continue;
            count++;
            ResourceLocation id = holder.getId();
            ItemStack stack = new ItemStack(item);
            Equipable eq = Equipable.get(stack);
            if (eq == null) {
                failures.add(id + ": Equipable.get(stack) returned null");
                continue;
            }
            EquipmentSlot slot = eq.getEquipmentSlot();
            if (slot == null) {
                failures.add(id + ": getEquipmentSlot() is null");
            }
        }
        return count;
    }

    /**
     * Edible items: anything with a FOOD data component. Verifies that every
     * food has positive nutrition and non-negative saturation modifier, so we
     * catch foods that lost their FoodProperties during the migrator's
     * Builder.meat()/.fast() removal sweep.
     */
    private static int checkEdibleItems(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            ItemStack stack = new ItemStack(item);
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food == null) continue;
            count++;
            ResourceLocation id = holder.getId();
            if (food.nutrition() <= 0) {
                failures.add(id + ": food nutrition is " + food.nutrition());
            }
            if (food.saturation() < 0F) {
                failures.add(id + ": food saturation is " + food.saturation());
            }
            if (food.eatDurationTicks() <= 0) {
                failures.add(id + ": food eat duration is " + food.eatDurationTicks());
            }
        }
        return count;
    }

    /**
     * ElementItem family (kitsunebi, jufu, amber igniter, wagasa, uchiwa).
     * Just confirms the marker interface still hooks up at runtime and items
     * with an element type aren't silently absent.
     */
    private static int checkElementItems(List<String> failures) {
        int count = 0;
        Set<String> classNames = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof ElementItem)) continue;
            count++;
            classNames.add(item.getClass().getSimpleName());
        }
        if (count < 1) {
            failures.add("expected at least 1 ElementItem (kitsunebi/jufu/amber_igniter/wagasa/uchiwa), found 0");
        }
        ModCoreUrushi.logger.info("[UrushiItemTests] element-item classes present: {}", classNames);
        return count;
    }

    /**
     * Custom BlockItem subclasses (UrushiBlockItem, the three Invisible*
     * placement items). Each must wrap a real, registered Block.
     * IronIngotItem exists in the source tree but its BIron() helper is never
     * called in either the ref or migrated register, so it is dead code in
     * both and intentionally excluded.
     */
    private static int checkBlockItemSubclasses(List<String> failures) {
        int count = 0;
        Set<String> classNames = new HashSet<>();
        Set<String> wantedClasses = Set.of(
                "UrushiBlockItem",
                "InvisibleButtonItem", "InvisibleLeverItem", "InvisiblePressurePlateItem");
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof BlockItem bi)) continue;
            String simple = item.getClass().getSimpleName();
            if (!wantedClasses.contains(simple)) continue;
            count++;
            classNames.add(simple);
            ResourceLocation id = holder.getId();
            Block block = bi.getBlock();
            if (block == null) {
                failures.add(id + ": " + simple + ".getBlock() returned null");
            }
        }
        for (String wanted : wantedClasses) {
            if (!classNames.contains(wanted)) {
                failures.add("missing custom BlockItem subclass: " + wanted);
            }
        }
        return count;
    }

    /**
     * PlaceableFoodItem (TeaItem, OchokoItem, food-entity throwables). Each must
     * carry a non-null entity-type supplier so its right-click placement works.
     */
    private static int checkPlaceableFoodItems(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof PlaceableFoodItem)) continue;
            count++;
        }
        if (count < 2) {
            failures.add("expected at least 2 PlaceableFoodItems (TeaItem + OchokoItem at minimum), found " + count);
        }
        return count;
    }

    /**
     * Specialty singleton item classes from the 1.20.1 ref. Each class should
     * have at least one registered instance after migration. A class missing
     * here means the migrator silently dropped the item registration when it
     * couldn't process the source file (the SakuraHeadItem failure mode).
     * MagonoteItem and HotKatanaBladeItem exist as source files but are dead
     * code in ref (commented out or never instantiated) and are therefore
     * intentionally omitted from this expected set.
     */
    private static int checkSpecialtyItemPresence(List<String> failures) {
        Set<String> wanted = new HashSet<>(List.of(
                "HammerItem", "NoodleKnifeItem", "ToolchipItem",
                "CushionItem", "ClimbingRopesItem",
                "BakedMochochoItem", "RiceBallWithFillingItem",
                "EmptyBambooCup", "FilledBambooCup",
                "TranslatableBookItem",
                "StampItem", "AdditionalHeartItem",
                "DrawstringBagItem",
                "VisualizationGogglesItem", "WearableItem",
                "JufuItem", "KitsunebiItem", "AmberIgniterItem",
                "WagasaItem", "UchiwaItem",
                "WoodMagatamaItem", "FireMagatamaItem", "EarthMagatamaItem",
                "MetalMagatamaItem", "WaterMagatamaItem",
                "NormalKatanaItem",
                "SakuraHeadItem"
        ));
        Set<String> seen = new HashSet<>();
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            String simple = item.getClass().getSimpleName();
            if (wanted.contains(simple) && seen.add(simple)) {
                count++;
            }
        }
        for (String w : wanted) {
            if (!seen.contains(w)) {
                failures.add("specialty item class '" + w + "' has no registered instance");
            }
        }
        ModCoreUrushi.logger.info("[UrushiItemTests] specialty classes seen: {}/{}", seen.size(), wanted.size());
        return count;
    }

    /**
     * Brute-force tooltip resilience: call appendHoverText on a default stack of
     * every registered item with the empty TooltipContext. If any item throws
     * here it would crash the inventory tooltip in-game.
     */
    private static int checkAppendHoverTextSafety(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            count++;
            ResourceLocation id = holder.getId();
            try {
                List<Component> lines = new ArrayList<>();
                item.appendHoverText(new ItemStack(item), Item.TooltipContext.EMPTY, lines, TooltipFlag.NORMAL);
            } catch (Throwable t) {
                failures.add(id + ": appendHoverText threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    /**
     * Dev-only / internal utility items that intentionally lack user-facing
     * translations, models, or textures. They exist to support worldgen /
     * structure placement and never appear in an inventory.
     */
    private static final Set<String> DEV_ONLY_ITEM_IDS = Set.of(
            "urushi:null_block", "urushi:marker", "urushi:filler");

    /**
     * Blocks that intentionally drop nothing: portal blocks, invisible puzzle
     * controllers, fluid-style blocks, spikes. These deliberately have no
     * loot-table JSON so the skeleton minecart-of-a-loot-table warning is
     * silent.
     */
    private static final Set<String> LOOT_EXEMPT_BLOCK_IDS = Set.of(
            "urushi:null_block",
            "urushi:visible_kitsunebi",
            "urushi:hot_spring_water",
            "urushi:kakuriyo_portal",
            "urushi:kakuriyo_portal_core",
            "urushi:ghost_kakuriyo_portal_core",
            "urushi:red_kakuriyo_portal_frame",
            "urushi:ghost_red_kakuriyo_portal_frame",
            "urushi:black_kakuriyo_portal_frame",
            "urushi:ghost_black_kakuriyo_portal_frame",
            "urushi:freezing_display",
            "urushi:spike",
            "urushi:wood_element_puzzle_block",
            "urushi:fire_element_puzzle_block",
            "urushi:earth_element_puzzle_block",
            "urushi:metal_element_puzzle_block",
            "urushi:random_element_puzzle_block",
            "urushi:water_element_puzzle_block",
            "urushi:element_puzzle_controller_a",
            "urushi:element_puzzle_controller_b",
            "urushi:element_puzzle_controller_c");

    private static Set<String> loadLangKeys(String path) {
        Set<String> keys = new HashSet<>();
        try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(path)) {
            if (in == null) return keys;
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            for (String k : obj.keySet()) keys.add(k);
        } catch (Throwable ignored) {}
        return keys;
    }

    /**
     * Every registered item's getDescriptionId() must appear as a key in en_us.json.
     * Catches the "raw translation key shown to the player" UX regression.
     */
    private static int checkEnUsTranslationCoverage(List<String> failures) {
        Set<String> enKeys = loadLangKeys("/assets/urushi/lang/en_us.json");
        if (enKeys.isEmpty()) {
            failures.add("en_us.json not found or empty on classpath");
            return 0;
        }
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (DEV_ONLY_ITEM_IDS.contains(holder.getId().toString())) continue;
            count++;
            String key = item.getDescriptionId();
            if (!enKeys.contains(key)) {
                failures.add(holder.getId() + ": missing en_us translation key '" + key + "'");
            }
        }
        return count;
    }

    /**
     * Every item-level key in en_us.json must also exist in ja_jp.json.
     * Catches incomplete localization when a new item lands.
     */
    private static int checkJaJpTranslationParity(List<String> failures) {
        Set<String> enKeys = loadLangKeys("/assets/urushi/lang/en_us.json");
        Set<String> jaKeys = loadLangKeys("/assets/urushi/lang/ja_jp.json");
        if (jaKeys.isEmpty()) {
            failures.add("ja_jp.json not found or empty on classpath");
            return 0;
        }
        int count = 0;
        for (String k : enKeys) {
            if (!k.startsWith("item.urushi.") && !k.startsWith("block.urushi.") && !k.startsWith("info.urushi.")) continue;
            count++;
            if (!jaKeys.contains(k)) {
                failures.add("ja_jp.json missing key present in en_us.json: '" + k + "'");
            }
        }
        return count;
    }

    /**
     * Every registered item id must have a corresponding model JSON file under
     * assets/urushi/models/item/<id>.json. Catches the silent "Unable to load
     * model" warning class (which shows a missing-texture cube in inventory).
     */
    private static int checkItemModelCoverage(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            ResourceLocation id = holder.getId();
            if (DEV_ONLY_ITEM_IDS.contains(id.toString())) continue;
            count++;
            String path = "/assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json";
            if (UrushiItemTests.class.getResource(path) == null) {
                failures.add(id + ": missing item model at " + path);
            }
        }
        return count;
    }

    /**
     * NormalKatanaItem (and any future SwordItem) must expose ATTRIBUTE_MODIFIERS
     * with a positive ATTACK_DAMAGE base and a non-zero ATTACK_SPEED entry.
     * Catches a silently-stubbed createAttributes() call.
     */
    private static int checkSwordAttackStats(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof SwordItem)) continue;
            count++;
            ResourceLocation id = holder.getId();
            ItemAttributeModifiers mods = item.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            boolean hasDamage = false, hasSpeed = false;
            StringBuilder seenKeys = new StringBuilder();
            for (ItemAttributeModifiers.Entry entry : mods.modifiers()) {
                String attrKey = entry.attribute().unwrapKey().map(k -> k.location().toString())
                        .orElseGet(() -> entry.attribute().getRegisteredName());
                double amount = entry.modifier().amount();
                if (seenKeys.length() > 0) seenKeys.append(",");
                seenKeys.append(attrKey).append("=").append(amount);
                if (attrKey.endsWith("attack_damage") && amount > 0) hasDamage = true;
                if (attrKey.endsWith("attack_speed")) hasSpeed = true;
            }
            if (!hasDamage) failures.add(id + ": sword missing positive attack_damage modifier (saw [" + seenKeys + "])");
            if (!hasSpeed) failures.add(id + ": sword missing attack_speed modifier (saw [" + seenKeys + "])");
        }
        return count;
    }

    /**
     * Every DeferredSpawnEggItem must reference a non-null EntityType.
     * A null supplier crashes the spawn-egg right-click interaction.
     */
    private static int checkSpawnEggsValid(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof net.neoforged.neoforge.common.DeferredSpawnEggItem egg)) continue;
            count++;
            ResourceLocation id = holder.getId();
            try {
                EntityType<?> et = egg.getType(new ItemStack(item));
                if (et == null) failures.add(id + ": spawn egg EntityType is null");
            } catch (Throwable t) {
                failures.add(id + ": spawn egg getType threw " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Food-component sanity: nutrition in [1,20], saturation in [0,20],
     * eatDurationTicks > 0. Catches migrator stubs that left .nutrition(0)
     * or .eatDurationTicks(0).
     */
    private static int checkFoodPropertyRanges(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            ItemStack stack = new ItemStack(item);
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food == null) continue;
            count++;
            ResourceLocation id = holder.getId();
            int nut = food.nutrition();
            float sat = food.saturation();
            int dur = (int) (food.eatSeconds() * 20);
            if (nut < 1 || nut > 20) failures.add(id + ": nutrition out of range [1,20]: " + nut);
            if (sat < 0f) failures.add(id + ": negative saturation: " + sat);
            if (dur <= 0) failures.add(id + ": non-positive eat duration ticks: " + dur);
        }
        return count;
    }

    /**
     * Every registered block must have a blockstate JSON. Missing blockstates
     * render the "purple-black cube" in world.
     */
    private static int checkBlockstateCoverage(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            count++;
            ResourceLocation id = holder.getId();
            String path = "/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json";
            if (UrushiItemTests.class.getResource(path) == null) {
                failures.add(id + ": missing blockstate at " + path);
            }
        }
        return count;
    }

    /**
     * Every registered block must have a loot table (1.21 canonical path is
     * data/<ns>/loot_table/blocks/, legacy is data/<ns>/loot_tables/blocks/).
     * Missing loot tables mean the block silently drops nothing when broken.
     */
    private static int checkBlockLootTableCoverage(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            ResourceLocation id = holder.getId();
            if (LOOT_EXEMPT_BLOCK_IDS.contains(id.toString())) continue;
            count++;
            String modern = "/data/" + id.getNamespace() + "/loot_table/blocks/" + id.getPath() + ".json";
            String legacy = "/data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json";
            if (UrushiItemTests.class.getResource(modern) == null
                    && UrushiItemTests.class.getResource(legacy) == null) {
                failures.add(id + ": missing loot table at " + modern);
            }
        }
        return count;
    }

    /**
     * Each registered BlockEntityType must report at least one valid block.
     * Catches BlockEntityType.Builder.of(supplier, ...blocks) regressions where
     * the vararg list was lost during migration.
     */
    private static int checkBlockEntityBlockBinding(List<String> failures) {
        int count = 0;
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> holder : BlockEntityRegister.Tiles.getEntries()) {
            BlockEntityType<?> type;
            try { type = holder.get(); } catch (Throwable t) { continue; }
            if (type == null) continue;
            count++;
            ResourceLocation id = holder.getId();
            boolean anyValid = false;
            for (DeferredHolder<Block, ? extends Block> bh : ItemAndBlockRegister.BLOCKS.getEntries()) {
                Block block;
                try { block = bh.get(); } catch (Throwable t) { continue; }
                if (block == null) continue;
                try {
                    if (type.isValid(block.defaultBlockState())) { anyValid = true; break; }
                } catch (Throwable ignored) {}
            }
            if (!anyValid) {
                failures.add(id + ": BlockEntityType.isValid() returns false for every registered block");
            }
        }
        return count;
    }

    /**
     * ItemAttributeModifiers entries each carry a ResourceLocation id. Within a
     * single item, those ids must be unique — otherwise vanilla silently drops
     * all-but-one modifier, so an item that claims damage+speed ends up with
     * only one of them applied in combat.
     */
    private static int checkAttributeModifierIdsUnique(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            ItemAttributeModifiers mods = item.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            if (mods.modifiers().isEmpty()) continue;
            count++;
            Set<String> seen = new HashSet<>();
            for (ItemAttributeModifiers.Entry entry : mods.modifiers()) {
                String key = entry.modifier().id().toString() + "@" + entry.slot().name();
                if (!seen.add(key)) {
                    failures.add(holder.getId() + ": duplicate attribute modifier id " + key);
                }
            }
        }
        return count;
    }

    /**
     * Block hardness and explosion resistance should be finite and non-negative.
     * Catches a migration regression where Properties.strength() lost its value
     * and fell back to -1f (unbreakable via creative only, never mines).
     */
    private static int checkBlockPropertiesSane(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            Block block;
            try { block = holder.get(); } catch (Throwable t) { continue; }
            if (block == null) continue;
            count++;
            ResourceLocation id = holder.getId();
            try {
                float resistance = block.getExplosionResistance();
                if (Float.isNaN(resistance) || Float.isInfinite(resistance)) {
                    failures.add(id + ": non-finite explosion resistance " + resistance);
                }
                if (resistance < 0f) {
                    failures.add(id + ": negative explosion resistance " + resistance);
                }
            } catch (Throwable t) {
                failures.add(id + ": property probe threw " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Entity-type bounding box sanity: width and height must be finite and
     * strictly positive. Mojang collision math divides by these.
     */
    private static int checkEntityTypeSizeSane(List<String> failures) {
        int count = 0;
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> holder : EntityRegister.Entities.getEntries()) {
            EntityType<?> et;
            try { et = holder.get(); } catch (Throwable t) { continue; }
            if (et == null) continue;
            count++;
            ResourceLocation id = holder.getId();
            float w = et.getWidth();
            float h = et.getHeight();
            if (!(w > 0f) || Float.isNaN(w) || Float.isInfinite(w)) {
                failures.add(id + ": non-positive width " + w);
            }
            if (!(h > 0f) || Float.isNaN(h) || Float.isInfinite(h)) {
                failures.add(id + ": non-positive height " + h);
            }
        }
        return count;
    }

    /**
     * Each magatama subclass must report its own element via getElementType().
     * A shared/null element would break the element-reiryoku routing.
     */
    private static int checkMagatamaElementBinding(List<String> failures) {
        int count = 0;
        Map<String, String> elementByClass = new LinkedHashMap<>();
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof AbstractMagatamaItem) || !(item instanceof HasReiryokuItem hri)) continue;
            count++;
            ResourceLocation id = holder.getId();
            com.iwaliner.urushi.util.ElementType type;
            try { type = hri.getElementType(); }
            catch (Throwable t) { failures.add(id + ": getElementType threw " + t.getClass().getSimpleName()); continue; }
            if (type == null || type == com.iwaliner.urushi.util.ElementType.FAIL) {
                failures.add(id + ": magatama reports null/FAIL element");
                continue;
            }
            String clazz = item.getClass().getSimpleName();
            String prior = elementByClass.put(clazz, type.name());
            if (prior != null && !prior.equals(type.name())) {
                failures.add(id + ": " + clazz + " previously mapped to " + prior + ", now " + type.name());
            }
        }
        return count;
    }

    /**
     * Item model JSONs of parent "item/generated" declare their texture via
     * layer0. That texture ResourceLocation must resolve to an actual PNG.
     * Catches the migrator-silent case where an item references a renamed or
     * missing texture and shows the purple-black placeholder.
     */
    private static int checkItemTextureCoverage(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            ResourceLocation id = holder.getId();
            if (DEV_ONLY_ITEM_IDS.contains(id.toString())) continue;
            String modelPath = "/assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json";
            try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(modelPath)) {
                if (in == null) continue;
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                String parent = obj.has("parent") ? obj.get("parent").getAsString() : "";
                if (!"item/generated".equals(parent) && !"minecraft:item/generated".equals(parent)
                        && !"item/handheld".equals(parent) && !"minecraft:item/handheld".equals(parent)) continue;
                if (!obj.has("textures")) continue;
                com.google.gson.JsonObject tex = obj.getAsJsonObject("textures");
                if (!tex.has("layer0")) continue;
                String layer0 = tex.get("layer0").getAsString();
                ResourceLocation texId = ResourceLocation.parse(layer0);
                // Vanilla textures ship in a different JAR; our classpath walk
                // can't see them reliably. Trust the Minecraft runtime to
                // fail-loudly if they're missing.
                if ("minecraft".equals(texId.getNamespace())) continue;
                count++;
                String texturePath = "/assets/" + texId.getNamespace() + "/textures/" + texId.getPath() + ".png";
                if (UrushiItemTests.class.getResource(texturePath) == null) {
                    failures.add(id + ": missing texture " + layer0 + " (at " + texturePath + ")");
                }
            } catch (Throwable t) {
                failures.add(id + ": texture probe threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    /**
     * Melee weapon attack speed modifiers must be negative. Vanilla treats the
     * value as a delta applied to the base 4.0 speed; a positive number would
     * let the item swing faster than bare hands — almost always a migration
     * bug where the sign was dropped.
     */
    private static int checkSwordAttackSpeedSign(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            if (!(item instanceof SwordItem)) continue;
            count++;
            ResourceLocation id = holder.getId();
            ItemAttributeModifiers mods = item.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            for (ItemAttributeModifiers.Entry entry : mods.modifiers()) {
                String attrKey = entry.attribute().unwrapKey().map(k -> k.location().toString())
                        .orElseGet(() -> entry.attribute().getRegisteredName());
                if (attrKey.endsWith("attack_speed") && entry.modifier().amount() >= 0) {
                    failures.add(id + ": attack_speed modifier must be negative, got " + entry.modifier().amount());
                }
            }
        }
        return count;
    }

    /**
     * At FMLCommonSetup time the vanilla CreativeModeTab display-item cache is
     * not yet populated (buildContents() runs later during tab-build events),
     * so iterating CreativeModeTab.getDisplayItems() here returns empty lists.
     * Instead we check the mod's own five tracked tab-content lists populated
     * by the B*() helpers in ItemAndBlockRegister. Items inline-accepted in
     * URUSHI_*_TAB.displayItems lambdas (and thus still reachable in survival)
     * are captured via INLINE_ACCEPTED_ITEM_IDS; items deliberately hidden via
     * TABLESS_ITEM_IDS are also exempt.
     */
    private static final Set<String> INLINE_ACCEPTED_ITEM_IDS = Set.of(
            // URUSHI_TAB inline-accepts (see ItemAndBlockRegister#URUSHI_TAB)
            "urushi:drawstring_bag",
            "urushi:white_cushion", "urushi:orange_cushion", "urushi:magenta_cushion",
            "urushi:light_blue_cushion", "urushi:yellow_cushion", "urushi:lime_cushion",
            "urushi:pink_cushion", "urushi:gray_cushion", "urushi:light_gray_cushion",
            "urushi:cyan_cushion", "urushi:purple_cushion", "urushi:blue_cushion",
            "urushi:brown_cushion", "urushi:green_cushion", "urushi:red_cushion",
            "urushi:black_cushion",
            "urushi:hammer", "urushi:iron_katana", "urushi:climbing_rope",
            // BlockItem/Item pairs below are registered under the block's id,
            // not the Java variable name (e.g. invisible_button_item registers
            // under id urushi:invisible_button).
            "urushi:hot_spring_water_bucket", "urushi:invisible_button",
            "urushi:invisible_lever", "urushi:invisible_pressure_plate",
            "urushi:shiitake_goggles", "urushi:lantern_plant_torch",
            "urushi:bamboo_cup", "urushi:water_bamboo_cup", "urushi:straw",
            // URUSHI_WOOD_TAB inline-accepts
            "urushi:raw_urushi_ball", "urushi:red_urushi_ball", "urushi:black_urushi_ball",
            // URUSHI_FOOD_TAB inline-accepts
            "urushi:tea_leaf", "urushi:pickled_japanese_apricot", "urushi:shiitake",
            "urushi:roasted_shiitake", "urushi:rice_malt", "urushi:raw_rice", "urushi:rice",
            "urushi:tkg", "urushi:sekihan", "urushi:butadon", "urushi:gyudon",
            "urushi:cheese_gyudon", "urushi:cheese_gyudon_with_onsen_egg",
            "urushi:green_onion_and_raw_egg_gyudon", "urushi:mustard_leaf_and_cod_caviar_gyudon",
            "urushi:rice_ball", "urushi:rice_cake", "urushi:roasted_rice_cake",
            "urushi:kusa_dango", "urushi:color_dango", "urushi:mitarashi_dango",
            "urushi:sakura_mochi", "urushi:kusa_mochi", "urushi:ohagi", "urushi:yokan",
            "urushi:sakura_yokan", "urushi:baked_mochocho", "urushi:karaage", "urushi:yakitori",
            "urushi:tofu", "urushi:miso", "urushi:miso_soup", "urushi:hiyayakko",
            "urushi:abura_age", "urushi:so", "urushi:onsen_egg", "urushi:noodle_knife",
            "urushi:kitsune_udon", "urushi:salt", "urushi:soy_source_ramen", "urushi:miso_ramen",
            "urushi:tonkotsu_ramen", "urushi:salt_ramen", "urushi:syari", "urushi:tsuna",
            "urushi:sweetfish", "urushi:sweetfish_with_salt", "urushi:cooked_sweetfish",
            "urushi:cooked_sweetfish_with_salt", "urushi:tsuna_sashimi", "urushi:tsuna_sushi",
            "urushi:salmon_sashimi", "urushi:salmon_sushi", "urushi:squid_sashimi",
            "urushi:squid_sushi", "urushi:egg_sushi", "urushi:salmon_roe", "urushi:salmon_roe_sushi",
            "urushi:shrimp", "urushi:fried_shrimp", "urushi:shrimp_sushi", "urushi:gravel_sushi",
            "urushi:inari", "urushi:minced_tuna_bowl", "urushi:yomotsuhegui_fruit",
            "urushi:lantern_plant_fruit", "urushi:milk_bamboo_cup", "urushi:soy_source_bamboo_cup",
            "urushi:green_tea", "urushi:sake", "urushi:tokkuri", "urushi:ochoko",
            "urushi:mandarin", "urushi:mandarin_slice",
            // URUSHI_MAGIC_TAB inline-accepts
            "urushi:visualization_goggles", "urushi:kitsunebi", "urushi:uchiwa",
            "urushi:open_wagasa", "urushi:shide",
            "urushi:wood_element_paper", "urushi:fire_element_paper", "urushi:earth_element_paper",
            "urushi:metal_element_paper", "urushi:water_element_paper",
            "urushi:wood_element_magatama", "urushi:fire_element_magatama",
            "urushi:earth_element_magatama", "urushi:metal_element_magatama",
            "urushi:water_element_magatama",
            "urushi:wood_amber", "urushi:fire_amber", "urushi:earth_amber",
            "urushi:metal_amber", "urushi:water_amber", "urushi:amber_igniter");

    /**
     * Items intentionally hidden from every creative tab. Mostly legacy/compat
     * blocks kept for old-world support, alternate block states (close_wagasa
     * is the "closed" variant obtained in-world, not crafted), and cycled
     * display blocks (kakejiku 1..19 cycle via right-click, only kakejiku_15
     * shows in the tab as the canonical entry).
     */
    private static final Set<String> TABLESS_ITEM_IDS = Set.of(
            // legacy tatami compat: IDs "green_tatami"/"brown_tatami"+slab+carpet
            // were kept to preserve old worlds; canonical is "*_mat" variant
            "urushi:green_tatami", "urushi:green_tatami_slab", "urushi:green_tatami_carpet",
            "urushi:brown_tatami", "urushi:brown_tatami_slab", "urushi:brown_tatami_carpet",
            // cycled kakejiku: kakejiku_15 is the tab entry; the other 18 cycle
            // via right-click on the block
            "urushi:kakejiku_1", "urushi:kakejiku_2", "urushi:kakejiku_3", "urushi:kakejiku_4",
            "urushi:kakejiku_5", "urushi:kakejiku_6", "urushi:kakejiku_7", "urushi:kakejiku_8",
            "urushi:kakejiku_9", "urushi:kakejiku_10", "urushi:kakejiku_11", "urushi:kakejiku_12",
            "urushi:kakejiku_13", "urushi:kakejiku_14", "urushi:kakejiku_16", "urushi:kakejiku_17",
            "urushi:kakejiku_18", "urushi:kakejiku_19",
            // alternate in-world state of open_wagasa (obtained from closing)
            "urushi:close_wagasa",
            // config-gated drop item; intentionally not placeable
            "urushi:additional_heart",
            // WIP book, commented out in URUSHI_TAB for now
            "urushi:kakuriyo_chronicles_1",
            // spawn eggs: picked up by vanilla SPAWN_EGGS tab via NeoForge's
            // DeferredSpawnEggItem hook — not tracked in urushi*TabContents.
            "urushi:ghost_spawn_egg", "urushi:giant_skeleton_spawn_egg",
            "urushi:kakuriyo_villager_spawn_egg",
            // Spawned by villager trades, not placeable from a tab.
            "urushi:coin",
            // Newly ported; pending tab assignment.
            "urushi:sakura_head");

    /**
     * Every registered non-dev, non-tabless item should be reachable through
     * at least one of: (a) the mod's five tab-content tracking lists, or (b)
     * the curated INLINE_ACCEPTED_ITEM_IDS set (items inline-accepted in a
     * CreativeModeTab.displayItems lambda).
     */
    private static int checkItemCreativeTabCoverage(List<String> failures) {
        Set<ResourceLocation> inAnyTab = new HashSet<>();
        for (DeferredHolder<Item, Item> h : ModCoreUrushi.urushiTabContents) if (h != null) inAnyTab.add(h.getId());
        for (DeferredHolder<Item, Item> h : ModCoreUrushi.urushiPlasterTabContents) if (h != null) inAnyTab.add(h.getId());
        for (DeferredHolder<Item, Item> h : ModCoreUrushi.urushiWoodTabContents) if (h != null) inAnyTab.add(h.getId());
        for (DeferredHolder<Item, Item> h : ModCoreUrushi.urushiFoodTabContents) if (h != null) inAnyTab.add(h.getId());
        for (DeferredHolder<Item, Item> h : ModCoreUrushi.urushiMagicTabContents) if (h != null) inAnyTab.add(h.getId());
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            ResourceLocation id = holder.getId();
            if (DEV_ONLY_ITEM_IDS.contains(id.toString())) continue;
            if (TABLESS_ITEM_IDS.contains(id.toString())) continue;
            count++;
            if (inAnyTab.contains(id)) continue;
            if (INLINE_ACCEPTED_ITEM_IDS.contains(id.toString())) continue;
            failures.add(id + ": not present in any creative tab (not in urushi*TabContents nor INLINE_ACCEPTED set)");
        }
        return count;
    }

    /**
     * Item.getDescriptionId() must look like "item.urushi.<path>" or
     * "block.urushi.<path>" (for BlockItems that mirror their block). Catches
     * accidental overrides that return raw identifiers to the player.
     */
    private static int checkDescriptionIdFormat(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Item, ? extends Item> holder : ItemAndBlockRegister.ITEMS.getEntries()) {
            Item item;
            try { item = holder.get(); } catch (Throwable t) { continue; }
            count++;
            ResourceLocation id = holder.getId();
            String desc = item.getDescriptionId();
            if (desc == null || desc.isBlank()) {
                failures.add(id + ": blank descriptionId");
                continue;
            }
            if (!desc.startsWith("item." + ModCoreUrushi.ModID + ".")
                    && !desc.startsWith("block." + ModCoreUrushi.ModID + ".")) {
                failures.add(id + ": descriptionId '" + desc + "' does not start with item." + ModCoreUrushi.ModID + ". or block." + ModCoreUrushi.ModID + ".");
            }
        }
        return count;
    }

    /**
     * Scan data/urushi/recipe(s)/*.json and verify that every referenced item
     * (result, ingredient, "item" fields) resolves to a registered ItemStack.
     * Catches recipes pointing at renamed/deleted items that would silently
     * fail to load.
     */
    private static int checkRecipeItemReferences(List<String> failures) {
        Set<ResourceLocation> knownItems = new HashSet<>();
        for (ResourceLocation vid : net.minecraft.core.registries.BuiltInRegistries.ITEM.keySet()) knownItems.add(vid);
        // Scan both 1.21 singular "recipe" and legacy plural "recipes"
        int count = 0;
        for (String dir : List.of("recipe", "recipes")) {
            java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/urushi/" + dir);
            if (rootUrl == null) continue;
            java.util.Deque<String> stack = new java.util.ArrayDeque<>();
            stack.push("/data/urushi/" + dir);
            // Resource-walk via filesystem path conversion (works for exploded classpath; jars would need a different approach)
            try {
                java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
                try (var walk = java.nio.file.Files.walk(rootPath)) {
                    for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                        if (!p.toString().endsWith(".json")) continue;
                        count++;
                        String name = p.getFileName().toString();
                        try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                            collectItemRefs(root, knownItems, name, failures);
                        } catch (Throwable t) {
                            failures.add(name + ": parse threw " + t.getClass().getSimpleName());
                        }
                    }
                }
            } catch (Throwable t) {
                // Non-fatal: recipe scan only works from an exploded classpath (runClient)
            }
        }
        return count;
    }

    private static void collectItemRefs(com.google.gson.JsonElement el,
                                        Set<ResourceLocation> knownItems,
                                        String recipeFile,
                                        List<String> failures) {
        if (el == null || el.isJsonNull()) return;
        if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectItemRefs(c, knownItems, recipeFile, failures);
            return;
        }
        if (!el.isJsonObject()) return;
        com.google.gson.JsonObject obj = el.getAsJsonObject();
        for (String k : obj.keySet()) {
            if (k.equals("item")) {
                if (obj.get(k).isJsonPrimitive()) {
                    String ref = obj.get(k).getAsString();
                    try {
                        ResourceLocation rl = ResourceLocation.parse(ref);
                        // Only hard-fail on our own + vanilla namespaces. Other
                        // mod refs (farmersdelight:, jei:, etc.) may be
                        // conditional compat recipes and aren't loaded during
                        // this test run.
                        String ns = rl.getNamespace();
                        if (!"minecraft".equals(ns) && !ModCoreUrushi.ModID.equals(ns)) continue;
                        if (!knownItems.contains(rl)) {
                            failures.add(recipeFile + ": references unregistered item '" + ref + "'");
                        }
                    } catch (Throwable t) {
                        failures.add(recipeFile + ": invalid item ref '" + ref + "'");
                    }
                }
            } else {
                collectItemRefs(obj.get(k), knownItems, recipeFile, failures);
            }
        }
    }

    /**
     * Every FutonBlock must override newBlockEntity() to return null.
     * Regression guard: BedBlock.newBlockEntity() creates a BedBlockEntity whose
     * validBlocks set does NOT include any FutonBlock subclass, and NeoForge 1.21
     * strict-validates in BlockEntity.validateBlockState(), crashing dimension
     * load when futon blocks appear in structures (e.g. Kakuriyo villages).
     */
    private static int checkFutonNoBlockEntity(List<String> failures) {
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            Block block;
            try { block = holder.get(); } catch (Throwable t) { continue; }
            if (!(block instanceof FutonBlock futon)) continue;
            count++;
            try {
                BlockState state = futon.defaultBlockState();
                Object be = futon.newBlockEntity(BlockPos.ZERO, state);
                if (be != null) {
                    failures.add(holder.getId() + ": FutonBlock.newBlockEntity must return null, got " + be.getClass().getName());
                }
            } catch (Throwable t) {
                failures.add(holder.getId() + ": FutonBlock.newBlockEntity threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    /**
     * Walk /data/urushi/loot_table/ and parse every JSON. Any parse failure is
     * a regression: 1.21 rewrote the loot-table codec (e.g. set_contents now
     * requires a {type:block_entity, ...} wrapper with components like
     * minecraft:container_loot). Catches the senryoubako "No key component"
     * failure class.
     */
    private static int checkLootTablesParseable(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/urushi/loot_table");
        if (rootUrl == null) return count;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        if (!root.isJsonObject()) {
                            failures.add("loot_table/" + rel + ": root is not a JSON object");
                            continue;
                        }
                        com.google.gson.JsonObject obj = root.getAsJsonObject();
                        // If it's a real (non-conditionally-gated) loot table, it must declare "type" or "pools".
                        boolean gated = obj.has("neoforge:conditions");
                        if (!gated && !obj.has("type") && !obj.has("pools")) {
                            failures.add("loot_table/" + rel + ": missing both 'type' and 'pools' (malformed loot table)");
                        }
                        // Regression guard for senryoubako: legacy set_contents without type: component will fail to load.
                        checkLegacySetContents(root, "loot_table/" + rel, failures);
                    } catch (Throwable t) {
                        failures.add("loot_table/" + rel + ": parse threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
                    }
                }
            }
        } catch (Throwable t) {
            // Non-fatal: only works from exploded classpath
        }
        return count;
    }

    private static void checkLegacySetContents(com.google.gson.JsonElement el, String file, List<String> failures) {
        if (el == null || el.isJsonNull()) return;
        if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) checkLegacySetContents(c, file, failures);
            return;
        }
        if (!el.isJsonObject()) return;
        com.google.gson.JsonObject obj = el.getAsJsonObject();
        com.google.gson.JsonElement fn = obj.get("function");
        if (fn != null && fn.isJsonPrimitive() && "minecraft:set_contents".equals(fn.getAsString())) {
            // 1.21 set_contents requires a "type" discriminator (e.g. minecraft:block_entity).
            if (!obj.has("type")) {
                failures.add(file + ": minecraft:set_contents without 'type' field (legacy schema, will fail to parse in 1.21)");
            }
        }
        for (String k : obj.keySet()) checkLegacySetContents(obj.get(k), file, failures);
    }

    /**
     * Safely extract the neoforge:conditions array and check for a
     * {type: neoforge:mod_loaded, modid: <id>} entry. Tolerates absent key,
     * non-array value, malformed entries. Returns null if no conditions block
     * is declared at all (caller decides whether that's OK).
     */
    private static Boolean hasModLoadedCondition(com.google.gson.JsonObject obj, String modid) {
        if (!obj.has("neoforge:conditions")) return null;
        com.google.gson.JsonElement cond = obj.get("neoforge:conditions");
        if (!cond.isJsonArray()) return Boolean.FALSE;
        for (com.google.gson.JsonElement c : cond.getAsJsonArray()) {
            if (c == null || !c.isJsonObject()) continue;
            com.google.gson.JsonObject co = c.getAsJsonObject();
            com.google.gson.JsonElement ty = co.get("type");
            com.google.gson.JsonElement mi = co.get("modid");
            if (ty != null && ty.isJsonPrimitive() && "neoforge:mod_loaded".equals(ty.getAsString())
                    && mi != null && mi.isJsonPrimitive() && modid.equals(mi.getAsString())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Recursively collect every string value anywhere in a JSON tree — used for
     * tolerant pattern matching (e.g. "does this file reference patchouli:X?")
     * without committing to a specific schema shape.
     */
    private static void collectAllStrings(com.google.gson.JsonElement el, Set<String> out) {
        if (el == null || el.isJsonNull()) return;
        if (el.isJsonArray()) { for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectAllStrings(c, out); return; }
        if (el.isJsonPrimitive()) {
            com.google.gson.JsonPrimitive p = el.getAsJsonPrimitive();
            if (p.isString()) out.add(p.getAsString());
            return;
        }
        if (el.isJsonObject()) {
            com.google.gson.JsonObject o = el.getAsJsonObject();
            for (String k : o.keySet()) collectAllStrings(o.get(k), out);
        }
    }

    /**
     * Every JSON in /data/urushi/loot_table/blocks/ must correspond to a
     * registered block. Regression guard against orphan loot tables referencing
     * items/blocks that were removed from ItemAndBlockRegister (e.g. the
     * 1.20.1 "falling_katana_block" which is an entity in 1.21).
     */
    private static int checkLootTableOrphans(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/urushi/loot_table/blocks");
        if (rootUrl == null) return count;
        Set<String> registeredBlockPaths = new HashSet<>();
        for (DeferredHolder<Block, ? extends Block> h : ItemAndBlockRegister.BLOCKS.getEntries()) {
            registeredBlockPaths.add(h.getId().getPath());
        }
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String name = p.getFileName().toString().replace(".json", "");
                    if (!registeredBlockPaths.contains(name)) {
                        failures.add("loot_table/blocks/" + name + ".json: orphan — no corresponding registered block");
                    }
                }
            }
        } catch (Throwable t) {
            // Non-fatal: exploded-classpath only
        }
        return count;
    }

    /**
     * Any data file under /data/urushi/{loot_table,recipe,advancement}/ that
     * references a patchouli:* resource (type, book, item, tag) MUST be wrapped
     * in neoforge:conditions with mod_loaded=patchouli. Otherwise datapack
     * loading errors on boot when patchouli is absent. Checks all three dirs
     * so advancement rewards and shapeless_book_recipe entries are covered.
     */
    private static int checkLootTablePatchouliGuard(List<String> failures) {
        int count = 0;
        for (String dir : List.of("loot_table", "recipe", "advancement")) {
            java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/urushi/" + dir);
            if (rootUrl == null) continue;
            try {
                java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
                try (var walk = java.nio.file.Files.walk(rootPath)) {
                    for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                        if (!p.toString().endsWith(".json")) continue;
                        String rel = dir + "/" + rootPath.relativize(p).toString().replace('\\', '/');
                        com.google.gson.JsonObject obj;
                        try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                            if (!root.isJsonObject()) continue;
                            obj = root.getAsJsonObject();
                        } catch (Throwable t) {
                            continue; // parseability is validated elsewhere
                        }
                        // Tolerant match: look at every string value anywhere in the JSON for a patchouli: prefix.
                        Set<String> allStrings = new HashSet<>();
                        collectAllStrings(obj, allStrings);
                        boolean refsPatchouli = false;
                        for (String s : allStrings) {
                            if (s.startsWith("patchouli:") || s.contains(":patchouli:")) {
                                refsPatchouli = true; break;
                            }
                        }
                        if (!refsPatchouli) continue;
                        count++;
                        Boolean guarded = hasModLoadedCondition(obj, "patchouli");
                        if (guarded == null) {
                            failures.add(rel + ": references patchouli but has no neoforge:conditions guard");
                        } else if (!guarded) {
                            failures.add(rel + ": has neoforge:conditions but missing mod_loaded=patchouli entry");
                        }
                    }
                }
            } catch (Throwable t) {
                // Non-fatal: exploded-classpath only
            }
        }
        return count;
    }

    /**
     * Within a minecraft:alternatives loot entry, only the LAST child may be
     * unconditional — earlier children must carry conditions, otherwise
     * Minecraft logs "Unreachable entry" warnings and silently drops the tail.
     * Regression guard for the limonite_ore / chalcopyrite_ore ordering bug.
     */
    private static int checkLootTableNoUnreachable(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/urushi/loot_table");
        if (rootUrl == null) return count;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        scanAlternativesForUnreachable(root, "loot_table/" + rel, failures);
                    } catch (Throwable ignored) {
                        // parseability is validated in checkLootTablesParseable
                    }
                }
            }
        } catch (Throwable t) {
            // Non-fatal
        }
        return count;
    }

    private static void scanAlternativesForUnreachable(com.google.gson.JsonElement el, String file, List<String> failures) {
        if (el == null || el.isJsonNull()) return;
        if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) scanAlternativesForUnreachable(c, file, failures);
            return;
        }
        if (!el.isJsonObject()) return;
        com.google.gson.JsonObject obj = el.getAsJsonObject();
        if (obj.has("type") && "minecraft:alternatives".equals(obj.get("type").getAsString())
                && obj.has("children") && obj.get("children").isJsonArray()) {
            com.google.gson.JsonArray kids = obj.getAsJsonArray("children");
            for (int i = 0; i < kids.size() - 1; i++) {
                if (!kids.get(i).isJsonObject()) continue;
                com.google.gson.JsonObject child = kids.get(i).getAsJsonObject();
                boolean hasConditions = child.has("conditions")
                        && child.get("conditions").isJsonArray()
                        && child.getAsJsonArray("conditions").size() > 0;
                if (!hasConditions) {
                    failures.add(file + ": alternatives child[" + i + "] is unconditional but not the last entry — subsequent entries are unreachable");
                }
            }
        }
        for (String k : obj.keySet()) scanAlternativesForUnreachable(obj.get(k), file, failures);
    }

    /**
     * Walk /data/*&#47;tags/worldgen/biome/*.json. Every referenced biome must
     * either be a known vanilla biome or have a corresponding JSON under
     * /data/urushi/worldgen/biome/. Catches typos like banboo_jungle, groove,
     * and stale urushi:eulalia_forest refs.
     */
    private static int checkBiomeTagReferences(List<String> failures) {
        Set<String> vanillaBiomes = Set.of(
                "minecraft:plains", "minecraft:sunflower_plains", "minecraft:snowy_plains", "minecraft:ice_spikes",
                "minecraft:desert", "minecraft:swamp", "minecraft:mangrove_swamp", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:birch_forest", "minecraft:dark_forest",
                "minecraft:old_growth_birch_forest", "minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga",
                "minecraft:taiga", "minecraft:snowy_taiga", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:windswept_savanna", "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:bamboo_jungle",
                "minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:meadow",
                "minecraft:grove", "minecraft:snowy_slopes", "minecraft:frozen_peaks", "minecraft:jagged_peaks",
                "minecraft:stony_peaks", "minecraft:beach", "minecraft:snowy_beach", "minecraft:stony_shore",
                "minecraft:mushroom_fields", "minecraft:dripstone_caves", "minecraft:lush_caves", "minecraft:deep_dark",
                "minecraft:cherry_grove", "minecraft:river", "minecraft:frozen_river", "minecraft:ocean",
                "minecraft:deep_ocean", "minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:the_end", "minecraft:end_highlands", "minecraft:end_midlands", "minecraft:small_end_islands",
                "minecraft:end_barrens", "minecraft:nether_wastes", "minecraft:soul_sand_valley", "minecraft:crimson_forest",
                "minecraft:warped_forest", "minecraft:basalt_deltas", "minecraft:the_void"
        );
        Set<String> urushiBiomes = new HashSet<>();
        java.net.URL biomeRoot = UrushiItemTests.class.getResource("/data/urushi/worldgen/biome");
        if (biomeRoot != null) {
            try {
                java.nio.file.Path rootPath = java.nio.file.Paths.get(biomeRoot.toURI());
                try (var walk = java.nio.file.Files.walk(rootPath)) {
                    for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                        if (!p.toString().endsWith(".json")) continue;
                        urushiBiomes.add("urushi:" + p.getFileName().toString().replace(".json", ""));
                    }
                }
            } catch (Throwable ignored) {}
        }

        int count = 0;
        for (String ns : List.of("urushi", "c")) {
            java.net.URL tagRoot = UrushiItemTests.class.getResource("/data/" + ns + "/tags/worldgen/biome");
            if (tagRoot == null) continue;
            try {
                java.nio.file.Path rootPath = java.nio.file.Paths.get(tagRoot.toURI());
                try (var walk = java.nio.file.Files.walk(rootPath)) {
                    for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                        if (!p.toString().endsWith(".json")) continue;
                        count++;
                        String rel = ns + "/tags/worldgen/biome/" + p.getFileName().toString();
                        try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                            if (!root.isJsonObject()) continue;
                            com.google.gson.JsonElement valsEl = root.getAsJsonObject().get("values");
                            if (valsEl == null || !valsEl.isJsonArray()) continue;
                            for (com.google.gson.JsonElement v : valsEl.getAsJsonArray()) {
                                String ref = extractTagEntryId(v);
                                if (ref == null) continue;
                                if (ref.startsWith("#")) continue; // another tag
                                if (ref.startsWith("minecraft:")) {
                                    if (!vanillaBiomes.contains(ref)) {
                                        failures.add(rel + ": references unknown vanilla biome '" + ref + "'");
                                    }
                                } else if (ref.startsWith("urushi:")) {
                                    if (!urushiBiomes.contains(ref)) {
                                        failures.add(rel + ": references unknown urushi biome '" + ref + "' (no JSON in /data/urushi/worldgen/biome/)");
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            failures.add(rel + ": parse threw " + t.getClass().getSimpleName());
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }
        return count;
    }

    /**
     * Walk /data/urushi/tags/ and inspect every #c:* tag reference. The NeoForge
     * 1.21 common-tag convention is PLURAL (c:stones, c:cobblestones, c:gravels,
     * c:sands). Sandstones is special — convention uses path-form c:sandstone/blocks,
     * c:sandstone/slabs, etc. Referencing a singular bare form resolves to an empty
     * tag and silently breaks downstream logic (e.g. element classification).
     */
    /** Deprecated singular common tag names whose plural form is the 1.21 NeoForge convention. */
    private static final Set<String> DEPRECATED_SINGULAR_COMMON_TAGS = Set.of(
            "c:stone", "c:cobblestone", "c:gravel", "c:sand", "c:sandstone",
            "c:netherrack", "c:end_stone", "c:ore", "c:wood", "c:log",
            "c:ingot", "c:nugget", "c:gem", "c:dust", "c:dye",
            "c:storage_block", "c:crop", "c:seed", "c:bucket",
            "c:raw_material", "c:shear", "c:spear", "c:hammer", "c:tool",
            "c:glass", "c:mushroom", "c:bone", "c:leather", "c:feather",
            "c:slimeball", "c:glass_pane", "c:chest", "c:string"
    );

    private static boolean isDeprecatedCommonTagRef(String ref) {
        if (ref == null) return false;
        // Accept both "#c:stone" (tag reference in another tag) and "c:stone" (tag id used directly)
        String stripped = ref.startsWith("#") ? ref.substring(1) : ref;
        return DEPRECATED_SINGULAR_COMMON_TAGS.contains(stripped);
    }

    /** Extract a tag-entry id from either a bare-string "minecraft:x" / "#c:x" or an object form {"id": "...", "required": false}. Returns null for un-extractable entries. */
    private static String extractTagEntryId(com.google.gson.JsonElement v) {
        if (v == null || v.isJsonNull()) return null;
        if (v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) return v.getAsString();
        if (v.isJsonObject()) {
            com.google.gson.JsonElement id = v.getAsJsonObject().get("id");
            if (id != null && id.isJsonPrimitive() && id.getAsJsonPrimitive().isString()) return id.getAsString();
        }
        return null;
    }

    private static int checkCommonTagReferences(List<String> failures) {
        int count = 0;
        java.net.URL tagsRoot = UrushiItemTests.class.getResource("/data/urushi/tags");
        if (tagsRoot == null) return count;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(tagsRoot.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        if (!root.isJsonObject()) continue;
                        com.google.gson.JsonElement valsEl = root.getAsJsonObject().get("values");
                        if (valsEl == null || !valsEl.isJsonArray()) continue;
                        for (com.google.gson.JsonElement v : valsEl.getAsJsonArray()) {
                            String ref = extractTagEntryId(v);
                            if (ref == null) continue;
                            if (isDeprecatedCommonTagRef(ref)) {
                                failures.add("tags/" + rel + ": uses deprecated singular common tag '" + ref + "' (use plural form, e.g. c:stones)");
                            }
                        }
                    } catch (Throwable t) {
                        failures.add("tags/" + rel + ": scan threw " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    /**
     * Every botanypots recipe under /data/botanypots/recipe/ must carry a
     * neoforge:conditions guard with mod_loaded=botanypots, otherwise datapack
     * loading errors on boot when BotanyPots isn't installed.
     */
    private static int checkBotanypotsConditions(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/botanypots/recipe");
        if (rootUrl == null) return count;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        if (!root.isJsonObject()) {
                            failures.add("botanypots/recipe/" + rel + ": root is not a JSON object");
                            continue;
                        }
                        com.google.gson.JsonObject obj = root.getAsJsonObject();
                        Boolean guarded = hasModLoadedCondition(obj, "botanypots");
                        if (guarded == null) {
                            failures.add("botanypots/recipe/" + rel + ": missing neoforge:conditions guard");
                        } else if (!guarded) {
                            failures.add("botanypots/recipe/" + rel + ": has neoforge:conditions but no mod_loaded=botanypots entry");
                        }
                    } catch (Throwable t) {
                        failures.add("botanypots/recipe/" + rel + ": parse threw " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    /**
     * 1.21 migrated data paths from plural to singular: recipes -> recipe,
     * loot_tables -> loot_table, advancements -> advancement, structures ->
     * structure, tags/blocks -> tags/block, tags/items -> tags/item. Having
     * legacy plural dirs sitting alongside the singular canonical forms creates
     * ambiguous resource loads — regression-guard against re-introduction.
     */
    private static int checkNoLegacyPluralDataDirs(List<String> failures) {
        String[][] legacy = {
                {"urushi", "loot_tables"},
                {"urushi", "recipes"},
                {"urushi", "advancements"},
                {"urushi", "structures"},
                {"urushi", "tags/blocks"},
                {"urushi", "tags/items"},
                {"minecraft", "tags/blocks"},
                {"minecraft", "tags/items"},
                {"minecraft", "recipes"},
                {"c", "tags/blocks"},
                {"c", "tags/items"},
                {"c", "tags/worldgen"},
                {"botanypots", "recipes"},
        };
        int count = 0;
        for (String[] pair : legacy) {
            count++;
            String path = "/data/" + pair[0] + "/" + pair[1];
            java.net.URL url = UrushiItemTests.class.getResource(path);
            if (url == null) continue;
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(url.toURI());
                if (java.nio.file.Files.isDirectory(p)) {
                    try (var walk = java.nio.file.Files.walk(p)) {
                        long files = walk.filter(f -> f.toString().endsWith(".json")).count();
                        if (files > 0) {
                            failures.add("legacy plural data dir still populated: " + path + " (" + files + " JSON files)");
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }
        return count;
    }

    /**
     * Senryoubako's loot table MUST include {@code minecraft:container} in its
     * copy_components include list, otherwise silk-touch/quick-replace loses
     * the stored items (senryoubako is a 27-slot RandomizableContainerBlockEntity
     * and its contents persist via DataComponents.CONTAINER in 1.21).
     */
    private static int checkSenryoubakoContainerComponent(List<String> failures) {
        String path = "/data/urushi/loot_table/blocks/senryoubako.json";
        java.net.URL url = UrushiItemTests.class.getResource(path);
        if (url == null) {
            failures.add("senryoubako.json missing at " + path);
            return 0;
        }
        try (java.io.InputStream in = url.openStream()) {
            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            Set<String> includes = new HashSet<>();
            collectCopyComponentsInclude(root, includes);
            for (String required : List.of("minecraft:container", "minecraft:container_loot", "minecraft:custom_name", "minecraft:lock")) {
                if (!includes.contains(required)) {
                    failures.add("senryoubako.json copy_components.include is missing '" + required + "' (stored contents will be lost on break)");
                }
            }
        } catch (Throwable t) {
            failures.add("senryoubako.json: scan threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return 1;
    }

    private static void collectCopyComponentsInclude(com.google.gson.JsonElement el, Set<String> out) {
        if (el == null || el.isJsonNull()) return;
        if (el.isJsonArray()) { for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectCopyComponentsInclude(c, out); return; }
        if (!el.isJsonObject()) return;
        com.google.gson.JsonObject o = el.getAsJsonObject();
        com.google.gson.JsonElement fn = o.get("function");
        if (fn != null && fn.isJsonPrimitive() && "minecraft:copy_components".equals(fn.getAsString())) {
            com.google.gson.JsonElement inc = o.get("include");
            if (inc != null && inc.isJsonArray()) {
                for (com.google.gson.JsonElement c : inc.getAsJsonArray()) {
                    if (c.isJsonPrimitive() && c.getAsJsonPrimitive().isString()) out.add(c.getAsString());
                }
            }
        }
        for (String k : o.keySet()) collectCopyComponentsInclude(o.get(k), out);
    }

    /**
     * Every blockstate JSON must correspond to a registered block. Catches
     * orphan blockstates left behind when a block was removed from the registry
     * (e.g. raw_urushi_layer whose block backing was dropped in the 1.21 port).
     */
    private static int checkOrphanBlockstates(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/assets/urushi/blockstates");
        if (rootUrl == null) return count;
        Set<String> registered = new HashSet<>();
        for (DeferredHolder<Block, ? extends Block> h : ItemAndBlockRegister.BLOCKS.getEntries()) {
            registered.add(h.getId().getPath());
        }
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String name = p.getFileName().toString().replace(".json", "");
                    if (!registered.contains(name)) {
                        failures.add("blockstates/" + name + ".json: orphan — no registered block '" + name + "'");
                    }
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    /**
     * Every item-model JSON under {@code assets/urushi/models/item/} must either
     * correspond to a registered item OR be referenced as a variant by another
     * item model (via {@code overrides[*].model} / {@code parent}). Catches stale
     * item-model files left behind when an item was removed, while tolerating
     * the common pattern of stateful predicate models (e.g. magatama_0/1/2...)
     * referenced by the base item's model.
     */
    private static int checkOrphanItemModels(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/assets/urushi/models/item");
        if (rootUrl == null) return count;
        Set<String> registeredItemPaths = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> h : ItemAndBlockRegister.ITEMS.getEntries()) {
            registeredItemPaths.add(h.getId().getPath());
        }
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());

            Set<String> referencedByOtherModels = new HashSet<>();
            try (var list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    if (!p.toString().endsWith(".json")) continue;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        if (!root.isJsonObject()) continue;
                        com.google.gson.JsonObject o = root.getAsJsonObject();
                        com.google.gson.JsonElement ov = o.get("overrides");
                        if (ov != null && ov.isJsonArray()) {
                            for (com.google.gson.JsonElement oe : ov.getAsJsonArray()) {
                                if (oe == null || !oe.isJsonObject()) continue;
                                com.google.gson.JsonElement m = oe.getAsJsonObject().get("model");
                                if (m != null && m.isJsonPrimitive() && m.getAsJsonPrimitive().isString()) {
                                    String mr = m.getAsString();
                                    if (mr.startsWith("urushi:item/")) {
                                        referencedByOtherModels.add(mr.substring("urushi:item/".length()));
                                    }
                                }
                            }
                        }
                        com.google.gson.JsonElement parent = o.get("parent");
                        if (parent != null && parent.isJsonPrimitive() && parent.getAsJsonPrimitive().isString()) {
                            String pr = parent.getAsString();
                            if (pr.startsWith("urushi:item/")) {
                                referencedByOtherModels.add(pr.substring("urushi:item/".length()));
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }

            java.net.URL booksUrl = UrushiItemTests.class.getResource("/data/urushi/patchouli_books/");
            if (booksUrl != null) {
                try {
                    java.nio.file.Path booksPath = java.nio.file.Paths.get(booksUrl.toURI());
                    try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(booksPath)) {
                        for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                            if (java.nio.file.Files.isDirectory(p)) continue;
                            if (!p.getFileName().toString().equals("book.json")) continue;
                            try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                                if (!obj.has("model") || !obj.get("model").isJsonPrimitive()) continue;
                                String m = obj.get("model").getAsString();
                                if (m.startsWith("urushi:")) {
                                    referencedByOtherModels.add(m.substring("urushi:".length()));
                                }
                            } catch (Throwable ignored) {}
                        }
                    }
                } catch (Throwable ignored) {}
            }

            try (var list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String name = p.getFileName().toString().replace(".json", "");
                    if (!registeredItemPaths.contains(name) && !referencedByOtherModels.contains(name)) {
                        failures.add("models/item/" + name + ".json: orphan — no registered item 'urushi:" + name + "' and not referenced as override/parent by another model");
                    }
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    /**
     * Every {@code block.urushi.X} / {@code item.urushi.X} key across all 4
     * locale files MUST correspond to a registered block/item. Catches stale
     * lang entries left behind when a block was removed (e.g. raw_urushi_layer).
     */
    private static int checkOrphanLangKeys(List<String> failures) {
        Set<String> registeredBlocks = new HashSet<>();
        for (DeferredHolder<Block, ? extends Block> h : ItemAndBlockRegister.BLOCKS.getEntries()) registeredBlocks.add(h.getId().getPath());
        Set<String> registeredItems = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> h : ItemAndBlockRegister.ITEMS.getEntries()) registeredItems.add(h.getId().getPath());

        int count = 0;
        for (String locale : List.of("en_us", "ja_jp", "zh_cn", "zh_tw")) {
            java.net.URL url = UrushiItemTests.class.getResource("/assets/urushi/lang/" + locale + ".json");
            if (url == null) continue;
            try (java.io.InputStream in = url.openStream()) {
                com.google.gson.JsonObject o = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                for (String k : o.keySet()) {
                    count++;
                    if (k.startsWith("block.urushi.")) {
                        String path = k.substring("block.urushi.".length());
                        int dot = path.indexOf('.');
                        String base = (dot >= 0) ? path.substring(0, dot) : path;
                        if (!registeredBlocks.contains(base) && !registeredItems.contains(base)) {
                            failures.add(locale + ".json: orphan lang key '" + k + "' — no registered block/item '" + base + "'");
                        }
                    } else if (k.startsWith("item.urushi.")) {
                        String path = k.substring("item.urushi.".length());
                        int dot = path.indexOf('.');
                        String base = (dot >= 0) ? path.substring(0, dot) : path;
                        if (!registeredItems.contains(base)) {
                            failures.add(locale + ".json: orphan lang key '" + k + "' — no registered item '" + base + "'");
                        }
                    }
                }
            } catch (Throwable t) {
                failures.add(locale + ".json: scan threw " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Every urushi recipe MUST use plural 1.21 common-tag names. Regression
     * guard against singular forms like c:stone/c:gravel/c:cobblestone
     * resolving to empty tags and silently breaking crafting.
     */
    private static int checkRecipeCommonTagSingular(List<String> failures) {
        int count = 0;
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/data/urushi/recipe");
        if (rootUrl == null) return count;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        scanForDeprecatedTagRefs(root, "recipe/" + rel, failures);
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    private static void scanForDeprecatedTagRefs(com.google.gson.JsonElement el, String file, List<String> failures) {
        if (el == null || el.isJsonNull()) return;
        if (el.isJsonArray()) { for (com.google.gson.JsonElement c : el.getAsJsonArray()) scanForDeprecatedTagRefs(c, file, failures); return; }
        if (!el.isJsonObject()) return;
        com.google.gson.JsonObject o = el.getAsJsonObject();
        com.google.gson.JsonElement tag = o.get("tag");
        if (tag != null && tag.isJsonPrimitive() && tag.getAsJsonPrimitive().isString()) {
            String ref = tag.getAsString();
            if (isDeprecatedCommonTagRef(ref)) {
                failures.add(file + ": uses deprecated singular common tag '" + ref + "' (must be plural, e.g. c:stones)");
            }
        }
        for (String k : o.keySet()) scanForDeprecatedTagRefs(o.get(k), file, failures);
    }

    /**
     * Tag filenames must NOT carry stale biome/feature names whose referenced
     * IDs were renamed. E.g. is_eulalia_forest.json was renamed to
     * is_eulalia_plains.json when the biome ID changed.
     */
    private static int checkTagFilenameConsistency(List<String> failures) {
        int count = 0;
        Set<String> bannedSubstrings = Set.of(
                "eulalia_forest",   // biome is urushi:eulalia_plains
                "banboo_jungle",    // typo, should be bamboo_jungle
                "groove"            // typo, should be grove
        );
        java.net.URL tagsRoot = UrushiItemTests.class.getResource("/data/urushi/tags");
        if (tagsRoot == null) return count;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(tagsRoot.toURI());
            try (var walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    String name = p.getFileName().toString();
                    for (String banned : bannedSubstrings) {
                        if (name.contains(banned)) {
                            failures.add("tag file '" + rootPath.relativize(p) + "' contains stale substring '" + banned + "' in filename");
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    /**
     * The block atlas loads urushi:block/null as a 1x1 sprite, which caps the
     * entire atlas's mipmap level from 4 to 0 (WARN: SpriteLoader "limits mip
     * level from 4 to 0"). That degrades distance-filter quality for every
     * block in the atlas, not just ours. Catch any regression where the null
     * texture shrinks back to 1x1.
     */
    private static int checkNullTextureAdequate(List<String> failures) {
        String[] texturePaths = {
                "/assets/urushi/textures/block/null.png"
        };
        int count = 0;
        for (String path : texturePaths) {
            count++;
            java.net.URL url = UrushiItemTests.class.getResource(path);
            if (url == null) {
                failures.add(path + ": texture is missing");
                continue;
            }
            try (java.io.InputStream in = url.openStream()) {
                // PNG header: 8-byte signature, then IHDR chunk: 4-byte length, 4-byte "IHDR", then 4+4 bytes for width+height.
                byte[] header = in.readNBytes(24);
                if (header.length < 24 || header[0] != (byte)0x89 || header[1] != 'P' || header[2] != 'N' || header[3] != 'G') {
                    failures.add(path + ": not a valid PNG file");
                    continue;
                }
                int w = ((header[16] & 0xff) << 24) | ((header[17] & 0xff) << 16) | ((header[18] & 0xff) << 8) | (header[19] & 0xff);
                int h = ((header[20] & 0xff) << 24) | ((header[21] & 0xff) << 16) | ((header[22] & 0xff) << 8) | (header[23] & 0xff);
                if (w < 16 || h < 16) {
                    failures.add(path + ": texture is " + w + "x" + h + " — must be at least 16x16 to avoid atlas mipmap degradation");
                }
            } catch (Throwable t) {
                failures.add(path + ": read threw " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Bidirectional locale parity across en_us, ja_jp, zh_cn, zh_tw for
     * item.urushi.*, block.urushi.*, info.urushi.* keys. A key present in
     * ANY locale must be present in EVERY locale — catches one-sided drift
     * (e.g., a new key landing only in en_us, or a key surviving in zh_tw
     * after being removed from en_us).
     */
    private static int checkLocaleParityAll(List<String> failures) {
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        Map<String, Set<String>> byLocale = new LinkedHashMap<>();
        for (String loc : locales) {
            byLocale.put(loc, loadLangKeys("/assets/urushi/lang/" + loc + ".json"));
        }
        Set<String> union = new HashSet<>();
        for (Set<String> s : byLocale.values()) union.addAll(s);
        int count = 0;
        for (String k : union) {
            if (!k.startsWith("item.urushi.") && !k.startsWith("block.urushi.") && !k.startsWith("info.urushi.")) continue;
            count++;
            for (String loc : locales) {
                if (!byLocale.get(loc).contains(k)) {
                    failures.add(loc + ".json missing key '" + k + "' (present in other locales)");
                }
            }
        }
        return count;
    }

    /**
     * Every urushi-namespaced translation value must be a non-empty string.
     * Catches the "tooltip shows nothing" regression where a bulk edit left
     * a value as "" (valid JSON, invisible to the user).
     */
    private static int checkLangValuesNonEmpty(List<String> failures) {
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        int count = 0;
        for (String loc : locales) {
            String path = "/assets/urushi/lang/" + loc + ".json";
            try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(path)) {
                if (in == null) continue;
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                    String k = e.getKey();
                    if (!k.startsWith("item.urushi.") && !k.startsWith("block.urushi.") && !k.startsWith("info.urushi.")) continue;
                    count++;
                    if (!e.getValue().isJsonPrimitive() || !e.getValue().getAsJsonPrimitive().isString()) {
                        failures.add(loc + ".json key '" + k + "' is not a string");
                        continue;
                    }
                    String v = e.getValue().getAsString();
                    if (v.isEmpty()) {
                        failures.add(loc + ".json key '" + k + "' has empty string value");
                    }
                }
            } catch (Throwable t) {
                failures.add(loc + ".json: unreadable (" + t.getClass().getSimpleName() + ": " + t.getMessage() + ")");
            }
        }
        return count;
    }

    /**
     * Every lang file on disk must parse cleanly as a JSON object.
     * loadLangKeys silently swallows parse errors; this check surfaces them
     * so a malformed JSON (trailing comma, bad escape, UTF-8 BOM) can't
     * silently degrade coverage checks into trivial-passes.
     */
    private static int checkLangFilesValid(List<String> failures) {
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        int count = 0;
        for (String loc : locales) {
            count++;
            String path = "/assets/urushi/lang/" + loc + ".json";
            try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(path)) {
                if (in == null) {
                    failures.add(path + ": missing on classpath");
                    continue;
                }
                com.google.gson.JsonElement el = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                if (!el.isJsonObject()) {
                    failures.add(path + ": root is not a JSON object");
                } else if (el.getAsJsonObject().size() == 0) {
                    failures.add(path + ": root object is empty");
                }
            } catch (Throwable t) {
                failures.add(path + ": JSON parse failed — " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        return count;
    }

    /**
     * Every registered block (including BN blocks with no BlockItem) must
     * have a block.urushi.X key in en_us.json. Blocks without translations
     * show the raw key in F3 overlays, /setblock output, and wherever a
     * Block appears without its BlockItem wrapper.
     */
    private static int checkBlockTranslationCoverage(List<String> failures) {
        Set<String> enKeys = loadLangKeys("/assets/urushi/lang/en_us.json");
        if (enKeys.isEmpty()) {
            failures.add("en_us.json missing/empty — cannot validate block coverage");
            return 0;
        }
        int count = 0;
        for (DeferredHolder<Block, ? extends Block> holder : ItemAndBlockRegister.BLOCKS.getEntries()) {
            Block block;
            try { block = holder.get(); } catch (Throwable t) { continue; }
            count++;
            String descId = block.getDescriptionId();
            if (!enKeys.contains(descId)) {
                failures.add(holder.getId() + ": missing en_us translation key '" + descId + "'");
            }
        }
        return count;
    }

    /**
     * pack.mcmeta must exist and declare the correct pack_format for the
     * target Minecraft version. 1.21.1 → format 34. A wrong format silently
     * causes the resource pack to be ignored at runtime.
     */
    private static int checkPackMcmetaValid(List<String> failures) {
        String path = "/pack.mcmeta";
        try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(path)) {
            if (in == null) {
                failures.add(path + ": missing on classpath");
                return 0;
            }
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            com.google.gson.JsonObject pack = root.getAsJsonObject("pack");
            if (pack == null) {
                failures.add(path + ": missing 'pack' object");
                return 1;
            }
            if (!pack.has("pack_format") || !pack.get("pack_format").isJsonPrimitive()) {
                failures.add(path + ": missing or non-numeric 'pack_format'");
                return 1;
            }
            int fmt = pack.get("pack_format").getAsInt();
            if (fmt != 34) {
                failures.add(path + ": pack_format=" + fmt + " (expected 34 for 1.21.1)");
            }
        } catch (Throwable t) {
            failures.add(path + ": parse failed — " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return 1;
    }

    /**
     * Every registered SoundEvent must have a matching entry in sounds.json,
     * and every "sounds" array entry must point to a real .ogg file under
     * assets/urushi/sounds/. Catches "sound plays nothing" regressions.
     */
    private static int checkSoundsJsonCoverage(List<String> failures) {
        Set<String> jsonKeys = new HashSet<>();
        Map<String, List<String>> refsByKey = new LinkedHashMap<>();
        String jsonPath = "/assets/urushi/sounds.json";
        try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(jsonPath)) {
            if (in == null) {
                failures.add(jsonPath + ": missing on classpath");
                return 0;
            }
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                jsonKeys.add(e.getKey());
                List<String> refs = new ArrayList<>();
                com.google.gson.JsonObject entry = e.getValue().getAsJsonObject();
                if (entry.has("sounds") && entry.get("sounds").isJsonArray()) {
                    for (com.google.gson.JsonElement s : entry.getAsJsonArray("sounds")) {
                        if (s.isJsonPrimitive()) {
                            refs.add(s.getAsString());
                        } else if (s.isJsonObject() && s.getAsJsonObject().has("name")) {
                            refs.add(s.getAsJsonObject().get("name").getAsString());
                        }
                    }
                }
                refsByKey.put(e.getKey(), refs);
            }
        } catch (Throwable t) {
            failures.add(jsonPath + ": parse failed — " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return 0;
        }

        int count = 0;
        for (DeferredHolder<SoundEvent, ? extends SoundEvent> holder : SoundRegister.SOUNDS.getEntries()) {
            count++;
            String name = holder.getId().getPath();
            if (!jsonKeys.contains(name)) {
                failures.add(holder.getId() + ": missing entry in sounds.json");
            }
        }
        for (Map.Entry<String, List<String>> e : refsByKey.entrySet()) {
            for (String ref : e.getValue()) {
                String ns = "urushi", oggPath = ref;
                int colon = ref.indexOf(':');
                if (colon >= 0) { ns = ref.substring(0, colon); oggPath = ref.substring(colon + 1); }
                if (!"urushi".equals(ns)) continue;
                count++;
                String resPath = "/assets/urushi/sounds/" + oggPath + ".ogg";
                if (UrushiItemTests.class.getResource(resPath) == null) {
                    failures.add("sounds.json['" + e.getKey() + "'] references missing sound file " + resPath);
                }
            }
        }
        return count;
    }

    /**
     * Every urushi item model's layer0..layerN texture refs must resolve to
     * an actual .png under assets/urushi/textures/. Catches purple-cube
     * regressions from mis-typed layer paths or deleted atlases.
     */
    private static int checkItemModelTextureLayers(List<String> failures) {
        String rootRes = "/assets/urushi/models/item/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) {
            failures.add(rootRes + ": not found on classpath");
            return 0;
        }
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        if (!obj.has("textures") || !obj.get("textures").isJsonObject()) continue;
                        com.google.gson.JsonObject tex = obj.getAsJsonObject("textures");
                        for (Map.Entry<String, com.google.gson.JsonElement> t : tex.entrySet()) {
                            String key = t.getKey();
                            if (!key.startsWith("layer")) continue;
                            if (!t.getValue().isJsonPrimitive()) continue;
                            String ref = t.getValue().getAsString();
                            String ns = "minecraft", texPath = ref;
                            int colon = ref.indexOf(':');
                            if (colon >= 0) { ns = ref.substring(0, colon); texPath = ref.substring(colon + 1); }
                            if (!"urushi".equals(ns)) continue;
                            String resPath = "/assets/urushi/textures/" + texPath + ".png";
                            if (UrushiItemTests.class.getResource(resPath) == null) {
                                failures.add(name + " " + key + " → missing texture " + resPath);
                            }
                        }
                    } catch (Throwable t) {
                        failures.add(name + ": parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        return count;
    }

    /**
     * Every urushi:X id appearing as a recipe result must be a registered
     * item. 1.21 moved to {"result": {"id": "..."}} in shaped recipes;
     * checkRecipeItemReferences only walks legacy {"item": "..."} — this
     * closes the new-format gap.
     */
    private static int checkRecipeResultIdsExist(List<String> failures) {
        Set<String> registeredItemIds = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> h : ItemAndBlockRegister.ITEMS.getEntries()) {
            registeredItemIds.add(h.getId().toString());
        }
        String rootRes = "/data/urushi/recipe/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) {
            return 0;
        }
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (!p.toString().endsWith(".json")) continue;
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        collectResultIdRefs(obj, registeredItemIds, p.getFileName().toString(), failures);
                        count++;
                    } catch (Throwable t) {
                        failures.add(p.getFileName() + ": parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    private static void collectResultIdRefs(com.google.gson.JsonElement el, Set<String> registered, String file, List<String> failures) {
        if (el == null) return;
        if (el.isJsonObject()) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            if (obj.has("result") && obj.get("result").isJsonObject()) {
                com.google.gson.JsonObject r = obj.getAsJsonObject("result");
                String idKey = r.has("id") ? "id" : (r.has("item") ? "item" : null);
                if (idKey != null && r.get(idKey).isJsonPrimitive()) {
                    String id = r.get(idKey).getAsString();
                    if (id.startsWith("urushi:") && !registered.contains(id)) {
                        failures.add(file + ": result." + idKey + " references unregistered '" + id + "'");
                    }
                }
            }
            for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                collectResultIdRefs(e.getValue(), registered, file, failures);
            }
        } else if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectResultIdRefs(c, registered, file, failures);
        }
    }

    /**
     * Typo-catcher: any JSON file placed under data/minecraft/tags/block/
     * or data/minecraft/tags/item/ whose filename doesn't appear in a
     * known-good whitelist of vanilla 1.21.1 tag names is almost certainly
     * a typo (e.g., wodden_doors.json for wooden_doors.json). Such typo
     * files silently add urushi blocks to a nonexistent tag, breaking
     * vanilla AI that looks up the real tag.
     */
    private static int checkMinecraftTagFilenamesValid(List<String> failures) {
        Set<String> knownBlockTags = new HashSet<>(java.util.Arrays.asList(
                "climbable", "copper_ores", "crops", "dirt", "fence_gates",
                "foxes_spawnable_on", "gold_ores", "iron_ores", "leaves",
                "logs_that_burn", "needs_diamond_tool", "needs_iron_tool",
                "needs_stone_tool", "planks", "rabbits_spawnable_on",
                "replaceable_plants", "saplings", "small_flowers", "trapdoors",
                "walls", "wooden_buttons", "wooden_doors", "wooden_fences",
                "wooden_pressure_plates", "wooden_slabs", "wooden_stairs",
                "wooden_trapdoors"));
        Set<String> knownItemTags = new HashSet<>(java.util.Arrays.asList(
                "coals", "copper_ores", "dyeable", "gold_ores", "iron_ores",
                "leaves", "logs_that_burn", "planks", "saplings",
                "small_flowers", "stone_crafting_materials",
                "stone_tool_materials", "straw", "trapdoors", "walls",
                "wooden_buttons", "wooden_doors", "wooden_fences",
                "wooden_pressure_plates", "wooden_slabs", "wooden_stairs",
                "wooden_trapdoors"));
        int count = 0;
        count += scanMinecraftTagDir("/data/minecraft/tags/block/", knownBlockTags, "block", failures);
        count += scanMinecraftTagDir("/data/minecraft/tags/item/", knownItemTags, "item", failures);
        return count;
    }

    private static int scanMinecraftTagDir(String resDir, Set<String> known, String kind, List<String> failures) {
        java.net.URL url = UrushiItemTests.class.getResource(resDir);
        if (url == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path root = java.nio.file.Paths.get(url.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(root)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    String stem = name.substring(0, name.length() - 5);
                    if (!known.contains(stem)) {
                        failures.add(resDir + name + ": '" + stem + "' is not a known vanilla " + kind + " tag (typo? check against 1.21.1 tag list)");
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(resDir + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * NeoForge 1.21 reads biome modifiers from data/&lt;ns&gt;/neoforge/biome_modifier/.
     * data/&lt;ns&gt;/forge/ is the legacy Forge 1.20 dir and is silently
     * ignored — any files left there indicate an incomplete port. Also
     * covers loot_modifiers, tags/biomes etc. under forge/.
     */
    private static int checkLegacyForgeDataDirs(List<String> failures) {
        String root = "/data/urushi/forge/";
        java.net.URL url = UrushiItemTests.class.getResource(root);
        if (url == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(url.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    count++;
                    failures.add("legacy Forge 1.20 file still present under data/urushi/forge/: " + rootPath.relativize(p));
                }
            }
        } catch (Throwable t) {
            failures.add(root + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every "item" field in a BotanyPots recipe JSON (soil inputs, pot
     * interaction ingredients, outputs, display.block) must be a registered
     * item/block. Catches regressions like urushi:lacquer_wood or
     * urushi:kakuriyo_grass referencing ids that were never registered.
     */
    private static int checkBotanyPotsRecipeItemsExist(List<String> failures) {
        Set<String> registered = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> h : ItemAndBlockRegister.ITEMS.getEntries()) {
            registered.add(h.getId().toString());
        }
        for (DeferredHolder<Block, ? extends Block> h : ItemAndBlockRegister.BLOCKS.getEntries()) {
            registered.add(h.getId().toString());
        }
        String rootRes = "/data/botanypots/recipe/urushi/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    if (!p.toString().endsWith(".json")) continue;
                    count++;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        collectBotanyPotsItemRefs(obj, registered, p.getFileName().toString(), failures);
                    } catch (Throwable t) {
                        failures.add(p.getFileName() + ": BP parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    private static void collectBotanyPotsItemRefs(com.google.gson.JsonElement el, Set<String> registered, String file, List<String> failures) {
        if (el == null) return;
        if (el.isJsonObject()) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            for (String k : java.util.Arrays.asList("item", "block")) {
                if (obj.has(k) && obj.get(k).isJsonPrimitive()) {
                    String id = obj.get(k).getAsString();
                    if (id.startsWith("urushi:") && !registered.contains(id)) {
                        failures.add(file + ": '" + k + "':'" + id + "' not a registered urushi item/block");
                    }
                }
            }
            for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                collectBotanyPotsItemRefs(e.getValue(), registered, file, failures);
            }
        } else if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectBotanyPotsItemRefs(c, registered, file, failures);
        }
    }

    /**
     * Walk every block model JSON recursively. Each textures[key] value
     * pointing to urushi:<path> must correspond to an existing PNG. Catches
     * regressions like wood_frame_base/plaster_acacia_1.json with stale
     * texture paths that would otherwise render as purple cubes at runtime.
     */
    private static int checkBlockModelTextureRefs(List<String> failures) {
        String rootRes = "/assets/urushi/models/block/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        if (!obj.has("textures") || !obj.get("textures").isJsonObject()) continue;
                        com.google.gson.JsonObject tex = obj.getAsJsonObject("textures");
                        for (Map.Entry<String, com.google.gson.JsonElement> t : tex.entrySet()) {
                            if (!t.getValue().isJsonPrimitive()) continue;
                            String ref = t.getValue().getAsString();
                            if (ref.startsWith("#")) continue;
                            String ns = "minecraft", texPath = ref;
                            int colon = ref.indexOf(':');
                            if (colon >= 0) { ns = ref.substring(0, colon); texPath = ref.substring(colon + 1); }
                            if (!"urushi".equals(ns)) continue;
                            String resPath = "/assets/urushi/textures/" + texPath + ".png";
                            if (UrushiItemTests.class.getResource(resPath) == null) {
                                failures.add(rel + " " + t.getKey() + " → missing texture " + resPath);
                            }
                        }
                    } catch (Throwable t) {
                        failures.add(rel + ": parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every block model's "parent" (if present and pointing to urushi:)
     * must resolve to an existing model JSON. Catches broken inheritance
     * chains — vanilla would silently fall back to a missing-model cube.
     */
    private static int checkBlockModelParentRefs(List<String> failures) {
        String rootRes = "/assets/urushi/models/block/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    String rel = rootPath.relativize(p).toString().replace('\\', '/');
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        if (!obj.has("parent") || !obj.get("parent").isJsonPrimitive()) continue;
                        String ref = obj.get("parent").getAsString();
                        String ns = "minecraft", mPath = ref;
                        int colon = ref.indexOf(':');
                        if (colon >= 0) { ns = ref.substring(0, colon); mPath = ref.substring(colon + 1); }
                        if (!"urushi".equals(ns)) continue;
                        String resPath = "/assets/urushi/models/" + mPath + ".json";
                        if (UrushiItemTests.class.getResource(resPath) == null) {
                            failures.add(rel + " parent → missing model " + resPath);
                        }
                    } catch (Throwable t) {
                        failures.add(rel + ": parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every blockstate's "model" field must resolve to an existing model
     * JSON. Supports both the variants-form and multipart-form of
     * blockstate JSON. Catches blockstate files that reference models
     * that were deleted or never created.
     */
    private static int checkBlockstateModelRefs(List<String> failures) {
        String rootRes = "/assets/urushi/blockstates/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        collectBlockstateModelRefs(obj, name, failures);
                    } catch (Throwable t) {
                        failures.add(name + ": parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    private static void collectBlockstateModelRefs(com.google.gson.JsonElement el, String file, List<String> failures) {
        if (el == null) return;
        if (el.isJsonObject()) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            if (obj.has("model") && obj.get("model").isJsonPrimitive()) {
                String ref = obj.get("model").getAsString();
                String ns = "minecraft", mPath = ref;
                int colon = ref.indexOf(':');
                if (colon >= 0) { ns = ref.substring(0, colon); mPath = ref.substring(colon + 1); }
                if ("urushi".equals(ns)) {
                    String resPath = "/assets/urushi/models/" + mPath + ".json";
                    if (UrushiItemTests.class.getResource(resPath) == null) {
                        failures.add(file + " model → missing " + resPath);
                    }
                }
            }
            for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                collectBlockstateModelRefs(e.getValue(), file, failures);
            }
        } else if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectBlockstateModelRefs(c, file, failures);
        }
    }

    /**
     * No .bak files should ship in src/main/resources/. They're migration
     * leftovers and bloat the mod jar.
     */
    private static int checkNoBakFiles(List<String> failures) {
        String[] roots = {"/assets/urushi/", "/data/"};
        int count = 0;
        for (String rootRes : roots) {
            java.net.URL url = UrushiItemTests.class.getResource(rootRes);
            if (url == null) continue;
            try {
                java.nio.file.Path rootPath = java.nio.file.Paths.get(url.toURI());
                try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                    for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                        if (java.nio.file.Files.isDirectory(p)) continue;
                        count++;
                        if (p.toString().endsWith(".bak")) {
                            failures.add(".bak file shipped: " + rootRes + rootPath.relativize(p).toString().replace('\\', '/'));
                        }
                    }
                }
            } catch (Throwable t) {
                failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Each Patchouli book entry's "icon" and pages[*].item/recipe refs
     * that start with urushi: must resolve to a registered item. Catches
     * entries referencing items that were renamed or removed.
     */
    private static int checkPatchouliEntryItemRefs(List<String> failures) {
        Set<String> registered = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> h : ItemAndBlockRegister.ITEMS.getEntries()) {
            registered.add(h.getId().toString());
        }
        String rootRes = "/assets/urushi/patchouli_books/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String s = p.toString().replace('\\', '/');
                    if (!s.endsWith(".json")) continue;
                    if (!s.contains("/entries/")) continue;
                    count++;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        collectPatchouliItemRefs(obj, registered, p.getFileName().toString(), failures);
                    } catch (Throwable t) {
                        failures.add(p.getFileName() + ": Patchouli parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every Patchouli book.json with `use_resource_pack:true` and
     * `model:"urushi:X"` must have a matching item model JSON at
     * assets/urushi/models/item/X.json. Without it the book's item icon
     * renders as the pink/black "missing texture" in inventory.
     */
    private static int checkPatchouliBookModels(List<String> failures) {
        String rootRes = "/data/urushi/patchouli_books/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    if (!p.getFileName().toString().equals("book.json")) continue;
                    count++;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        boolean useRP = obj.has("use_resource_pack") && obj.get("use_resource_pack").getAsBoolean();
                        if (!useRP) continue;
                        if (!obj.has("model") || !obj.get("model").isJsonPrimitive()) continue;
                        String model = obj.get("model").getAsString();
                        if (!model.startsWith("urushi:")) continue;
                        String path = "/assets/urushi/models/item/" + model.substring("urushi:".length()) + ".json";
                        if (UrushiItemTests.class.getResource(path) == null) {
                            failures.add(p.getParent().getFileName() + "/book.json model='" + model + "' but missing " + path);
                        }
                    } catch (Throwable t) {
                        failures.add(p.getFileName() + ": book.json parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    private static void collectPatchouliItemRefs(com.google.gson.JsonElement el, Set<String> registered, String file, List<String> failures) {
        if (el == null) return;
        if (el.isJsonObject()) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            for (String k : java.util.Arrays.asList("icon", "item", "main_output", "output")) {
                if (obj.has(k) && obj.get(k).isJsonPrimitive()) {
                    String ref = obj.get(k).getAsString();
                    int hash = ref.indexOf('#');
                    if (hash >= 0) ref = ref.substring(0, hash);
                    int brace = ref.indexOf('{');
                    if (brace >= 0) ref = ref.substring(0, brace);
                    ref = ref.trim();
                    if (ref.isEmpty()) continue;
                    if (ref.startsWith("urushi:") && !registered.contains(ref)) {
                        failures.add(file + " " + k + " → unregistered '" + ref + "'");
                    }
                }
            }
            for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                collectPatchouliItemRefs(e.getValue(), registered, file, failures);
            }
        } else if (el.isJsonArray()) {
            for (com.google.gson.JsonElement c : el.getAsJsonArray()) collectPatchouliItemRefs(c, registered, file, failures);
        }
    }

    /**
     * Every NeoForge biome_modifier JSON with "features":"urushi:xxx" or
     * similar placed_feature refs must point to an existing
     * worldgen/placed_feature/ JSON. Without this, biome modifiers
     * silently fail at world load — trees/features just don't spawn.
     */
    private static int checkBiomeModifierFeatureRefs(List<String> failures) {
        String rootRes = "/data/urushi/neoforge/biome_modifier/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                        if (obj.has("features")) {
                            com.google.gson.JsonElement feat = obj.get("features");
                            if (feat.isJsonPrimitive()) {
                                checkPlacedFeatureRef(feat.getAsString(), name, failures);
                            } else if (feat.isJsonArray()) {
                                for (com.google.gson.JsonElement e : feat.getAsJsonArray()) {
                                    if (e.isJsonPrimitive()) checkPlacedFeatureRef(e.getAsString(), name, failures);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        failures.add(name + ": parse failed — " + t.getClass().getSimpleName());
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    private static void checkPlacedFeatureRef(String ref, String file, List<String> failures) {
        if (ref == null || ref.isEmpty() || ref.startsWith("#")) return;
        String ns = "minecraft", fPath = ref;
        int colon = ref.indexOf(':');
        if (colon >= 0) { ns = ref.substring(0, colon); fPath = ref.substring(colon + 1); }
        if (!"urushi".equals(ns)) return;
        String resPath = "/data/urushi/worldgen/placed_feature/" + fPath + ".json";
        if (UrushiItemTests.class.getResource(resPath) == null) {
            failures.add(file + " features → missing placed_feature " + resPath);
        }
    }

    /**
     * Every translation key that a BlockEntity returns from
     * {@code getDisplayName()} must exist in every locale, or the GUI
     * title renders as the literal key. This hardcodes the known
     * container keys — add a new one here whenever a new BlockEntity
     * introduces a new container title.
     */
    private static int checkContainerMenuTitlesTranslated(List<String> failures) {
        String[] keys = {
            "container.bamboo_basket",
            "container.hokora",
            "container.plate",
            "container.ricecauldron",
            "container.sanbo",
            "container.senryoubako",
            "container.shichirin",
            "container.urushi.filler",
            "container.urushi.kettle",
            "container.urushihopper",
            "container.urushi_auto_crafting_table",
            "container.silkworm_farm",
            "container.woodencabinetry",
            "container.woodencabinetryslab",
            "container.doubledwoodencabinetry",
            "container.foxhopper",
            "container.fryer"
        };
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        int count = 0;
        for (String loc : locales) {
            String res = "/assets/urushi/lang/" + loc + ".json";
            java.net.URL u = UrushiItemTests.class.getResource(res);
            if (u == null) { failures.add(res + ": missing"); continue; }
            try (java.io.InputStream in = u.openStream()) {
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                for (String k : keys) {
                    count++;
                    if (!obj.has(k)) failures.add(loc + ": missing '" + k + "'");
                    else if (obj.get(k).isJsonPrimitive() && obj.get(k).getAsString().isEmpty())
                        failures.add(loc + ": '" + k + "' is empty");
                }
            } catch (Throwable t) {
                failures.add(res + ": parse failed — " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Verify biome ResourceKeys all use the urushi namespace — both the
     * code-defined {@link com.iwaliner.urushi.BiomeRegister} statics (loaded
     * via reflection so the static initializer fires even when the class is
     * otherwise dead code in this port) and the datapack JSONs at
     * {@code /data/urushi/worldgen/biome/}. The original regression was a
     * static initializer that built keys with {@code withDefaultNamespace}
     * (= minecraft:) instead of the mod id.
     */
    private static int checkBiomeResourceKeyNamespace(List<String> failures) {
        int count = 0;
        try {
            java.lang.reflect.Field[] fields = com.iwaliner.urushi.BiomeRegister.class.getDeclaredFields();
            for (java.lang.reflect.Field f : fields) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                if (f.getType() != net.minecraft.resources.ResourceKey.class) continue;
                f.setAccessible(true);
                @SuppressWarnings("unchecked")
                net.minecraft.resources.ResourceKey<?> key =
                        (net.minecraft.resources.ResourceKey<?>) f.get(null);
                if (key == null) continue;
                count++;
                ResourceLocation loc = key.location();
                if (!ModCoreUrushi.ModID.equals(loc.getNamespace())) {
                    failures.add("BiomeRegister." + f.getName() + " = " + loc
                            + " — wrong namespace; expected '" + ModCoreUrushi.ModID + "'");
                }
            }
        } catch (Throwable t) {
            failures.add("BiomeRegister reflection failed — "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        String rootRes = "/data/urushi/worldgen/biome/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl != null) {
            try {
                java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
                try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(rootPath)) {
                    for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                        if (java.nio.file.Files.isDirectory(p)) continue;
                        String name = p.getFileName().toString();
                        if (!name.endsWith(".json")) continue;
                        count++;
                    }
                }
            } catch (Throwable t) {
                failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Forbid {@code #forge:} tag refs in any biome modifier JSON. NeoForge 1.21
     * dropped the {@code forge} convention namespace for the common one
     * ({@code #c:}); leftover refs silently match nothing and break worldgen.
     */
    private static int checkBiomeModifierNoForgeTags(List<String> failures) {
        String rootRes = "/data/urushi/neoforge/biome_modifier/";
        java.net.URL rootUrl = UrushiItemTests.class.getResource(rootRes);
        if (rootUrl == null) return 0;
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    String name = p.getFileName().toString();
                    if (!name.endsWith(".json")) continue;
                    count++;
                    String content = new String(java.nio.file.Files.readAllBytes(p),
                            java.nio.charset.StandardCharsets.UTF_8);
                    if (content.contains("#forge:")) {
                        failures.add(name + " contains '#forge:' tag (use '#c:' on NeoForge 1.21+)");
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(rootRes + ": walk failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every registered EntityType must have an {@code entity.urushi.<id>}
     * translation in every locale. A missing key renders the entity name as
     * the literal key in tooltips, spawn eggs, and the F3 debug overlay.
     */
    private static int checkEntityLangCoverage(List<String> failures) {
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        java.util.LinkedHashMap<String, com.google.gson.JsonObject> objs = new java.util.LinkedHashMap<>();
        for (String loc : locales) {
            String res = "/assets/urushi/lang/" + loc + ".json";
            java.net.URL u = UrushiItemTests.class.getResource(res);
            if (u == null) { failures.add(res + ": missing"); continue; }
            try (java.io.InputStream in = u.openStream()) {
                objs.put(loc, com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject());
            } catch (Throwable t) {
                failures.add(res + ": parse failed — " + t.getClass().getSimpleName());
            }
        }
        int count = 0;
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> h
                : com.iwaliner.urushi.EntityRegister.Entities.getEntries()) {
            ResourceLocation id = h.getId();
            String key = "entity." + id.getNamespace() + "." + id.getPath();
            for (Map.Entry<String, com.google.gson.JsonObject> e : objs.entrySet()) {
                count++;
                com.google.gson.JsonObject obj = e.getValue();
                if (!obj.has(key)) {
                    failures.add(e.getKey() + ": missing '" + key + "'");
                } else if (obj.get(key).isJsonPrimitive() && obj.get(key).getAsString().isEmpty()) {
                    failures.add(e.getKey() + ": '" + key + "' is empty");
                }
            }
        }
        return count;
    }

    /**
     * Every {@code entity.urushi.X} translation key must map to a registered
     * EntityType. Orphan keys are dead weight and usually mean an entity was
     * removed but its lang entry was forgotten — translators waste effort on it.
     */
    private static int checkEntityLangOrphans(List<String> failures) {
        java.util.HashSet<String> registered = new java.util.HashSet<>();
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> h
                : com.iwaliner.urushi.EntityRegister.Entities.getEntries()) {
            ResourceLocation id = h.getId();
            registered.add("entity." + id.getNamespace() + "." + id.getPath());
        }
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        int count = 0;
        for (String loc : locales) {
            String res = "/assets/urushi/lang/" + loc + ".json";
            java.net.URL u = UrushiItemTests.class.getResource(res);
            if (u == null) continue;
            try (java.io.InputStream in = u.openStream()) {
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                for (String key : obj.keySet()) {
                    if (!key.startsWith("entity.urushi.")) continue;
                    count++;
                    if (!registered.contains(key)) {
                        failures.add(loc + ": orphan '" + key + "' has no matching registered EntityType");
                    }
                }
            } catch (Throwable t) {
                failures.add(res + ": parse failed — " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Every mixin name listed in {@code urushi.mixins.json} must resolve to a
     * compiled class on the classpath. A leftover entry for a deleted mixin
     * crashes the loader at startup with a hard-to-diagnose ClassNotFoundException.
     */
    private static int checkMixinClassesExist(List<String> failures) {
        String res = "/urushi.mixins.json";
        java.net.URL u = UrushiItemTests.class.getResource(res);
        if (u == null) { failures.add(res + ": missing"); return 0; }
        int count = 0;
        try (java.io.InputStream in = u.openStream()) {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            if (!obj.has("package")) { failures.add(res + ": missing 'package'"); return 0; }
            String pkg = obj.get("package").getAsString();
            String[] sections = {"mixins", "client", "server"};
            for (String section : sections) {
                if (!obj.has(section) || !obj.get(section).isJsonArray()) continue;
                for (com.google.gson.JsonElement el : obj.getAsJsonArray(section)) {
                    if (!el.isJsonPrimitive()) continue;
                    count++;
                    String simple = el.getAsString();
                    String classRes = "/" + pkg.replace('.', '/') + "/" + simple + ".class";
                    if (UrushiItemTests.class.getResource(classRes) == null) {
                        failures.add(section + " mixin '" + simple + "' has no class at " + classRes);
                    }
                }
            }
        } catch (Throwable t) {
            failures.add(res + ": parse failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Each registered creative-tab id must have an {@code itemGroup.<id>} key
     * in {@code en_us.json}. The original regression was a typo
     * ({@code "urushi_dood"} vs {@code "urushi_food"}) that compiled fine but
     * showed the literal key in the creative menu — this check would have
     * caught it the first time the test suite ran.
     */
    private static int checkCreativeTabIdTypo(List<String> failures) {
        java.util.LinkedHashSet<String> tabIds = new java.util.LinkedHashSet<>();
        for (DeferredHolder<net.minecraft.world.item.CreativeModeTab,
                ? extends net.minecraft.world.item.CreativeModeTab> h
                : com.iwaliner.urushi.ItemAndBlockRegister.CREATIVE_TABS.getEntries()) {
            tabIds.add(h.getId().getPath());
        }
        int count = 0;
        String res = "/assets/urushi/lang/en_us.json";
        java.net.URL u = UrushiItemTests.class.getResource(res);
        if (u == null) { failures.add(res + ": missing"); return 0; }
        try (java.io.InputStream in = u.openStream()) {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            for (String id : tabIds) {
                count++;
                String key = "itemGroup." + id;
                if (!obj.has(key)) {
                    failures.add("creative tab id '" + id + "' has no '" + key
                            + "' in en_us.json (possible typo or missing translation)");
                }
            }
        } catch (Throwable t) {
            failures.add(res + ": parse failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every {@code subtitle} ref in {@code sounds.json} must resolve to a key
     * present in every locale. Missing subtitles are an accessibility gap:
     * deaf/hard-of-hearing players with subtitles on see the raw key instead
     * of a sound description.
     */
    private static int checkSoundSubtitleKeys(List<String> failures) {
        String jsonPath = "/assets/urushi/sounds.json";
        java.util.LinkedHashSet<String> subtitleKeys = new java.util.LinkedHashSet<>();
        try (java.io.InputStream in = UrushiItemTests.class.getResourceAsStream(jsonPath)) {
            if (in == null) { failures.add(jsonPath + ": missing on classpath"); return 0; }
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                    new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, com.google.gson.JsonElement> e : obj.entrySet()) {
                com.google.gson.JsonObject entry = e.getValue().getAsJsonObject();
                if (entry.has("subtitle") && entry.get("subtitle").isJsonPrimitive()) {
                    subtitleKeys.add(entry.get("subtitle").getAsString());
                }
            }
        } catch (Throwable t) {
            failures.add(jsonPath + ": parse failed — " + t.getClass().getSimpleName());
            return 0;
        }
        String[] locales = {"en_us", "ja_jp", "zh_cn", "zh_tw"};
        int count = 0;
        for (String loc : locales) {
            String res = "/assets/urushi/lang/" + loc + ".json";
            java.net.URL u = UrushiItemTests.class.getResource(res);
            if (u == null) { failures.add(res + ": missing"); continue; }
            try (java.io.InputStream in = u.openStream()) {
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                for (String key : subtitleKeys) {
                    count++;
                    if (!obj.has(key)) failures.add(loc + ": missing subtitle '" + key + "'");
                    else if (obj.get(key).isJsonPrimitive() && obj.get(key).getAsString().isEmpty())
                        failures.add(loc + ": subtitle '" + key + "' is empty");
                }
            } catch (Throwable t) {
                failures.add(res + ": parse failed — " + t.getClass().getSimpleName());
            }
        }
        return count;
    }

    /**
     * Locate {@code src/main/java/com/iwaliner/urushi/} relative to wherever
     * the runtime classes live. Works in dev (classes under
     * {@code build/classes/java/main/}) and returns null in jar-only setups.
     */
    private static java.nio.file.Path resolveUrushiJavaSourceRoot() {
        // 1) Try classpath URL (dev: build/classes/java/main/com/iwaliner/urushi/test/UrushiItemTests.class)
        try {
            java.net.URL self = UrushiItemTests.class.getResource("UrushiItemTests.class");
            if (self != null) {
                String s = self.toString();
                if (s.startsWith("file:")) {
                    java.nio.file.Path p = java.nio.file.Paths.get(new java.net.URI(s));
                    while (p != null) {
                        java.nio.file.Path candidate = p.resolve("src").resolve("main").resolve("java")
                                .resolve("com").resolve("iwaliner").resolve("urushi");
                        if (java.nio.file.Files.isDirectory(candidate)) return candidate;
                        p = p.getParent();
                    }
                }
            }
        } catch (Throwable ignored) {}
        // 2) Try cwd walking up (runClient cwd = project/run/)
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("").toAbsolutePath();
            for (int i = 0; i < 6 && p != null; i++) {
                java.nio.file.Path candidate = p.resolve("src").resolve("main").resolve("java")
                        .resolve("com").resolve("iwaliner").resolve("urushi");
                if (java.nio.file.Files.isDirectory(candidate)) return candidate;
                p = p.getParent();
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * Forbid empty {@code catch (..) { }} blocks in the Urushi Java sources.
     * Silent exception swallowing hides bugs — a real implementation should
     * at minimum log the exception (at debug level if truly expected).
     * The test only runs in dev (the source tree must be visible); in a
     * jar-only classpath it short-circuits to 0 entries and passes.
     */
    private static int checkNoEmptyCatchBlocks(List<String> failures) {
        java.nio.file.Path srcRoot = resolveUrushiJavaSourceRoot();
        if (srcRoot == null || !java.nio.file.Files.isDirectory(srcRoot)) return 0;
        int count = 0;
        java.util.regex.Pattern empty = java.util.regex.Pattern.compile(
                "catch\\s*\\([^)]*\\)\\s*\\{\\s*\\}");
        try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(srcRoot)) {
            for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) walk::iterator) {
                String name = p.getFileName().toString();
                if (!name.endsWith(".java")) continue;
                if (name.equals("UrushiItemTests.java")) continue;
                count++;
                String content = new String(java.nio.file.Files.readAllBytes(p),
                        java.nio.charset.StandardCharsets.UTF_8);
                String stripped = content
                        .replaceAll("(?s)/\\*.*?\\*/", "")
                        .replaceAll("(?m)//.*$", "");
                java.util.regex.Matcher m = empty.matcher(stripped);
                while (m.find()) {
                    failures.add(srcRoot.relativize(p).toString().replace('\\', '/')
                            + ": empty catch block — '" + m.group() + "'");
                }
            }
        } catch (Throwable t) {
            failures.add("empty-catch scan failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * Every {@code "urushi:<name>"} predicate used by an item model override
     * (e.g. {@code raw_rice.json} → {@code "urushi:is_april_fools"}) must be
     * registered on the client via {@code ItemProperties.register(..., ResourceLocation.fromNamespaceAndPath("urushi","<name>"), ...)}
     * inside {@code ClientSetUp.java}. A missing registration silently leaves
     * the override inactive — the rainbow April Fools model would never appear
     * because the predicate always evaluates to 0.
     */
    private static int checkItemPredicateOverridesRegistered(List<String> failures) {
        java.net.URL rootUrl = UrushiItemTests.class.getResource("/assets/urushi/models/item");
        if (rootUrl == null) return 0;
        Set<String> predicatesFromModels = new java.util.TreeSet<>();
        int count = 0;
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get(rootUrl.toURI());
            try (var list = java.nio.file.Files.list(rootPath)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) list::iterator) {
                    if (java.nio.file.Files.isDirectory(p)) continue;
                    if (!p.toString().endsWith(".json")) continue;
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(p)) {
                        com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                                new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                        if (!root.isJsonObject()) continue;
                        com.google.gson.JsonElement ov = root.getAsJsonObject().get("overrides");
                        if (ov == null || !ov.isJsonArray()) continue;
                        for (com.google.gson.JsonElement oe : ov.getAsJsonArray()) {
                            if (oe == null || !oe.isJsonObject()) continue;
                            com.google.gson.JsonElement pred = oe.getAsJsonObject().get("predicate");
                            if (pred == null || !pred.isJsonObject()) continue;
                            for (String key : pred.getAsJsonObject().keySet()) {
                                count++;
                                if (key.startsWith("urushi:")) predicatesFromModels.add(key);
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable t) {
            failures.add("predicate-scan failed — " + t.getClass().getSimpleName());
            return count;
        }
        if (predicatesFromModels.isEmpty()) return count;

        java.nio.file.Path javaRoot = resolveUrushiJavaSourceRoot();
        if (javaRoot == null) return count;
        java.nio.file.Path clientSetup = javaRoot.resolve("ClientSetUp.java");
        if (!java.nio.file.Files.isRegularFile(clientSetup)) {
            failures.add("ClientSetUp.java not found under " + javaRoot);
            return count;
        }
        String rawSrc;
        try {
            rawSrc = new String(java.nio.file.Files.readAllBytes(clientSetup),
                    java.nio.charset.StandardCharsets.UTF_8);
        } catch (Throwable t) {
            failures.add("ClientSetUp.java read failed — " + t.getClass().getSimpleName());
            return count;
        }
        // Strip comments so a commented-out registration can't falsely satisfy the test.
        String src = rawSrc
                .replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)//.*$", "");
        // Only accept `ResourceLocation.fromNamespaceAndPath(<anything>, "<literal>")` form —
        // if someone refactors to a String variable, we want the test to flag missing
        // registrations rather than silently pass. The DOTALL flag lets the pattern span
        // lines inside a call.
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("ItemProperties\\s*\\.\\s*register\\s*\\([^;]*?ResourceLocation\\s*\\.\\s*fromNamespaceAndPath\\s*\\([^,)]*,\\s*\"([a-zA-Z_][a-zA-Z0-9_]*)\"\\s*\\)",
                        java.util.regex.Pattern.DOTALL)
                .matcher(src);
        Set<String> registered = new HashSet<>();
        while (m.find()) registered.add("urushi:" + m.group(1));
        // Diagnostic: if the scanner finds ItemProperties.register calls at all but zero
        // registered predicates, the regex shape has drifted (variable-based predicate,
        // new 1.22 API, etc.) — surface that instead of silently passing.
        if (registered.isEmpty() && src.contains("ItemProperties.register")) {
            failures.add("ItemProperties.register call(s) present in ClientSetUp.java but none matched 'fromNamespaceAndPath(.., \"X\")' — scanner drift, test is not enforcing anything");
        }
        for (String p : predicatesFromModels) {
            if (!registered.contains(p)) {
                failures.add("model predicate '" + p + "' has no matching ItemProperties.register(.., fromNamespaceAndPath(.., \"...\"), ..) call in ClientSetUp.java");
            }
        }
        return count;
    }

    /**
     * April Fools 2026 regression test — {@code raw_rice.json} and
     * {@code rice.json} MUST declare an {@code urushi:is_april_fools}
     * override pointing to the rainbow variant (and that target model must
     * exist). Catches accidental removal of the seasonal content when the
     * rice models are edited for unrelated reasons.
     */
    private static int checkAprilFoolsRiceOverrides(List<String> failures) {
        int count = 0;
        String[][] expected = {
                {"raw_rice", "urushi:item/masu_rice_raw_rainbow", "masu_rice_raw_rainbow"},
                {"rice", "urushi:item/rice_cup_rainbow", "rice_cup_rainbow"},
        };
        for (String[] row : expected) {
            count++;
            String base = row[0];
            String expectedModel = row[1];
            String targetFile = row[2];
            java.net.URL baseUrl = UrushiItemTests.class.getResource("/assets/urushi/models/item/" + base + ".json");
            if (baseUrl == null) { failures.add("models/item/" + base + ".json missing"); continue; }
            try (java.io.InputStream in = baseUrl.openStream()) {
                com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(
                        new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                if (!root.isJsonObject()) { failures.add(base + ".json: not a JSON object"); continue; }
                com.google.gson.JsonElement ov = root.getAsJsonObject().get("overrides");
                if (ov == null || !ov.isJsonArray()) { failures.add(base + ".json: missing 'overrides' array"); continue; }
                // Scan ALL overrides (don't break early) — a second/duplicate
                // is_april_fools override with the wrong target model would
                // otherwise slip through silently.
                int matchedOverrides = 0;
                for (com.google.gson.JsonElement oe : ov.getAsJsonArray()) {
                    if (!oe.isJsonObject()) continue;
                    com.google.gson.JsonObject o = oe.getAsJsonObject();
                    com.google.gson.JsonElement pred = o.get("predicate");
                    com.google.gson.JsonElement mdl = o.get("model");
                    if (pred == null || !pred.isJsonObject()) continue;
                    if (!pred.getAsJsonObject().has("urushi:is_april_fools")) continue;
                    matchedOverrides++;
                    if (mdl == null || !mdl.isJsonPrimitive()) {
                        failures.add(base + ".json: is_april_fools override #" + matchedOverrides + " has no 'model' string");
                        continue;
                    }
                    if (!expectedModel.equals(mdl.getAsString())) {
                        failures.add(base + ".json: is_april_fools override #" + matchedOverrides + " points at '" + mdl.getAsString() + "', expected '" + expectedModel + "'");
                    }
                }
                if (matchedOverrides == 0) {
                    failures.add(base + ".json: missing 'urushi:is_april_fools' override — April Fools rainbow rice model won't activate");
                } else if (matchedOverrides > 1) {
                    failures.add(base + ".json: " + matchedOverrides + " duplicate 'urushi:is_april_fools' overrides — expected exactly 1");
                }
            } catch (Throwable t) {
                failures.add(base + ".json: parse failed — " + t.getClass().getSimpleName());
                continue;
            }
            if (UrushiItemTests.class.getResource("/assets/urushi/models/item/" + targetFile + ".json") == null) {
                failures.add("target model 'urushi:item/" + targetFile + "' referenced by " + base + ".json does not exist");
            }
        }
        return count;
    }

    /**
     * April Fools 2026 regression test — {@code ModCoreUrushi.PlayerLoggedInEvent}
     * must contain the {@code UrushiUtils.isAprilFoolsDay()} hook that auto-equips
     * {@code sakura_head} on login. Catches accidental removal/commenting-out
     * when the event handler is refactored. Verifies both the conditional AND
     * the {@code sakura_head} reference inside it.
     */
    private static int checkAprilFoolsSakuraHeadHook(List<String> failures) {
        java.nio.file.Path javaRoot = resolveUrushiJavaSourceRoot();
        if (javaRoot == null) return 0;
        java.nio.file.Path modCore = javaRoot.resolve("ModCoreUrushi.java");
        if (!java.nio.file.Files.isRegularFile(modCore)) {
            failures.add("ModCoreUrushi.java not found under " + javaRoot);
            return 0;
        }
        String src;
        try {
            src = new String(java.nio.file.Files.readAllBytes(modCore),
                    java.nio.charset.StandardCharsets.UTF_8);
        } catch (Throwable t) {
            failures.add("ModCoreUrushi.java read failed — " + t.getClass().getSimpleName());
            return 0;
        }
        // strip comments so we don't match commented-out code
        String stripped = src
                .replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)//.*$", "");
        int count = 1;
        int idx = stripped.indexOf("PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent");
        if (idx < 0) {
            failures.add("PlayerLoggedInEvent handler not found in ModCoreUrushi.java");
            return count;
        }
        // Scope the search to the actual PlayerLoggedInEvent body by brace-matching:
        // find the first `{` after the signature, then walk to the matching `}` at
        // depth 0. This prevents the hook check from accidentally matching tokens in
        // later methods (e.g. AdvancementEvent) that happen to be within a fixed window.
        int braceStart = stripped.indexOf('{', idx);
        if (braceStart < 0) {
            failures.add("PlayerLoggedInEvent handler: opening brace not found");
            return count;
        }
        int depth = 0, braceEnd = -1;
        for (int i = braceStart; i < stripped.length(); i++) {
            char c = stripped.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') { depth--; if (depth == 0) { braceEnd = i; break; } }
        }
        if (braceEnd < 0) {
            failures.add("PlayerLoggedInEvent handler: closing brace not found (unbalanced braces after comment strip)");
            return count;
        }
        String body = stripped.substring(braceStart, braceEnd + 1);
        if (!body.contains("UrushiUtils.isAprilFoolsDay()")) {
            failures.add("ModCoreUrushi.PlayerLoggedInEvent is missing 'UrushiUtils.isAprilFoolsDay()' gate — April Fools sakura_head auto-equip hook is gone");
        }
        if (!body.contains("ItemAndBlockRegister.sakura_head")) {
            failures.add("ModCoreUrushi.PlayerLoggedInEvent is missing 'ItemAndBlockRegister.sakura_head' reference — auto-equip item missing");
        }
        if (!body.contains("EquipmentSlot.HEAD")) {
            failures.add("ModCoreUrushi.PlayerLoggedInEvent is missing 'EquipmentSlot.HEAD' assignment — sakura_head not going to head slot");
        }
        // also verify sakura_head is registered and Equipable so setItemSlot(HEAD) renders correctly
        try {
            Item sakuraHead = ItemAndBlockRegister.sakura_head.get();
            if (!(sakuraHead instanceof Equipable)) {
                failures.add("sakura_head is not Equipable — auto-equip on HEAD slot won't render as armor");
            } else {
                EquipmentSlot slot = ((Equipable) sakuraHead).getEquipmentSlot();
                if (slot != EquipmentSlot.HEAD) {
                    failures.add("sakura_head.getEquipmentSlot() == " + slot + ", expected HEAD");
                }
            }
        } catch (Throwable t) {
            failures.add("sakura_head lookup failed — " + t.getClass().getSimpleName());
        }
        return count;
    }

    /**
     * April Fools 2026 regression test — each non-ModCoreUrushi file that
     * branches on {@code UrushiUtils.isAprilFoolsDay()} in upstream MUST
     * retain that call. Catches silent removal (or comment-out) of the
     * seasonal branch in leaves, saplings, entities, or block items.
     * Complements {@link #checkAprilFoolsSakuraHeadHook}, which only
     * covers the ModCoreUrushi login handler.
     */
    private static int checkAprilFoolsCallSites(List<String> failures) {
        java.nio.file.Path javaRoot = resolveUrushiJavaSourceRoot();
        if (javaRoot == null) return 0;
        // path (relative to com/iwaliner/urushi/) → human reason for expecting the call
        String[][] sites = {
                {"ModCoreUrushi.java",                  "sakura_head auto-equip on login"},
                {"ClientSetUp.java",                    "rainbow rice ItemProperties is_april_fools predicate"},
                {"block/CutoutLeavesBlock.java",        "yellow pollen particles on cypress/cedar leaves"},
                {"block/FlammableSaplingBlock.java",    "yellow pollen particles on cypress/cedar saplings"},
                {"item/UrushiBlockItem.java",           "pollen trail when holding cypress/cedar block items"},
                {"entiity/CushionEntity.java",          "cushion bouncing"},
                {"entiity/food/FoodEntity.java",        "food entity bouncing"},
        };
        int count = 0;
        Set<java.nio.file.Path> expectedCallerPaths = new HashSet<>();
        for (String[] site : sites) {
            count++;
            java.nio.file.Path p = javaRoot.resolve(site[0].replace('/', java.io.File.separatorChar));
            expectedCallerPaths.add(p.toAbsolutePath().normalize());
            if (!java.nio.file.Files.isRegularFile(p)) {
                failures.add(site[0] + ": expected file not found — April Fools caller went missing (" + site[1] + ")");
                continue;
            }
            String raw;
            try {
                raw = new String(java.nio.file.Files.readAllBytes(p),
                        java.nio.charset.StandardCharsets.UTF_8);
            } catch (Throwable t) {
                failures.add(site[0] + ": read failed — " + t.getClass().getSimpleName());
                continue;
            }
            // Strip comments so we require a LIVE call, not a commented-out one.
            String stripped = raw
                    .replaceAll("(?s)/\\*.*?\\*/", "")
                    .replaceAll("(?m)//.*$", "");
            if (!stripped.contains("UrushiUtils.isAprilFoolsDay()")) {
                failures.add(site[0] + ": missing 'UrushiUtils.isAprilFoolsDay()' call — " + site[1] + " gate is gone");
            }
        }
        // Upper-bound guard: scan the whole urushi source tree for any LIVE caller
        // that isn't in the whitelist. Catches silent coverage divergence when
        // a new April Fools caller is added without also extending this test.
        // Excluded from caller set: UrushiUtils.java (the definition) and
        // anything under test/ (this harness + its own references).
        try {
            final java.util.List<String> extras = new java.util.ArrayList<>();
            final java.nio.file.Path utilDef = javaRoot.resolve("util").resolve("UrushiUtils.java").toAbsolutePath().normalize();
            final java.nio.file.Path testDir = javaRoot.resolve("test").toAbsolutePath().normalize();
            java.nio.file.Files.walkFileTree(javaRoot, new java.nio.file.SimpleFileVisitor<java.nio.file.Path>() {
                @Override
                public java.nio.file.FileVisitResult visitFile(java.nio.file.Path file, java.nio.file.attribute.BasicFileAttributes attrs) {
                    if (!file.toString().endsWith(".java")) return java.nio.file.FileVisitResult.CONTINUE;
                    java.nio.file.Path norm = file.toAbsolutePath().normalize();
                    if (norm.equals(utilDef)) return java.nio.file.FileVisitResult.CONTINUE;
                    if (norm.startsWith(testDir)) return java.nio.file.FileVisitResult.CONTINUE;
                    if (expectedCallerPaths.contains(norm)) return java.nio.file.FileVisitResult.CONTINUE;
                    String raw;
                    try {
                        raw = new String(java.nio.file.Files.readAllBytes(file),
                                java.nio.charset.StandardCharsets.UTF_8);
                    } catch (Throwable t) {
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }
                    String stripped = raw
                            .replaceAll("(?s)/\\*.*?\\*/", "")
                            .replaceAll("(?m)//.*$", "");
                    if (stripped.contains("UrushiUtils.isAprilFoolsDay()")) {
                        extras.add(javaRoot.relativize(file).toString().replace(java.io.File.separatorChar, '/'));
                    }
                    return java.nio.file.FileVisitResult.CONTINUE;
                }
            });
            if (!extras.isEmpty()) {
                failures.add("April Fools caller whitelist is out of date — "
                        + extras.size() + " file(s) reference UrushiUtils.isAprilFoolsDay() but are not in sites[][]: "
                        + String.join(", ", extras)
                        + " — add them to checkAprilFoolsCallSites sites[][] so their gate is regression-tested.");
            }
        } catch (Throwable t) {
            failures.add("upper-bound scan for April Fools callers failed: " + t.getClass().getSimpleName() + " — " + t.getMessage());
        }
        return count;
    }

    /**
     * Detect duplicate registry IDs across our DeferredRegisters
     * (which the registry would normally reject, but we surface explicitly).
     */
    private static int checkUniqueIds(List<String> failures) {
        int count = 0;
        Set<ResourceLocation> seenItems = new HashSet<>();
        for (DeferredHolder<Item, ? extends Item> h : ItemAndBlockRegister.ITEMS.getEntries()) {
            count++;
            if (!seenItems.add(h.getId())) {
                failures.add("duplicate item id: " + h.getId());
            }
        }
        Set<ResourceLocation> seenBlocks = new HashSet<>();
        for (DeferredHolder<Block, ? extends Block> h : ItemAndBlockRegister.BLOCKS.getEntries()) {
            count++;
            if (!seenBlocks.add(h.getId())) {
                failures.add("duplicate block id: " + h.getId());
            }
        }
        return count;
    }
}
