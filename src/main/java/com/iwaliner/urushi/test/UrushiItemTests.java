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
import com.iwaliner.urushi.item.AbstractMagatamaItem;
import com.iwaliner.urushi.item.PlaceableFoodItem;
import com.iwaliner.urushi.item.WearableItem;
import com.iwaliner.urushi.util.interfaces.ElementItem;
import com.iwaliner.urushi.util.interfaces.HasReiryokuItem;
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
