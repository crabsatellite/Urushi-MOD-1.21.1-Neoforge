package com.iwaliner.urushi.test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.block.DirtFurnaceBlock;
import com.iwaliner.urushi.block.RiceCauldronBlock;
import com.iwaliner.urushi.blockentity.RiceCauldronBlockEntity;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Project-owned client acceptance gate for the rice-ear processing path.
 *
 * The test uses a real client interaction packet for each block use. The
 * integrated server is only used to arrange the fixture and inspect the
 * authoritative result; the local minecraft-mod-testing runtime remains an
 * execution dependency and is not part of this project test code.
 */
@EventBusSubscriber(modid = ModCoreUrushi.ModID, value = Dist.CLIENT)
public final class UrushiClientInteractionTest {
    private static final boolean ENABLED = Boolean.getBoolean("urushi.tests.clientInteraction");
    private static final int TIMEOUT_TICKS = 20 * 90;
    private static final String RECEIPT_ENV = "URUSHI_CLIENT_TEST_RECEIPT";
    private static final ResourceLocation RICE_EARS_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "trigger/trigger_rice_ears");
    private static final ResourceLocation ADVANCEMENT_SOUND =
            ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "urushi_advancements");
    private static final List<String> REQUESTED_SCENARIOS = List.of(
            "client_recipe_registry",
            "client_senbakoki_rice_ear_interaction",
            "client_rice_cauldron_cooking",
            "client_rice_ears_advancement");

    private static final Set<String> OBSERVED_SCENARIOS = new LinkedHashSet<>();
    private static final Map<String, Integer> SCENARIO_START_TICKS = new LinkedHashMap<>();
    private static final Map<String, Integer> SCENARIO_END_TICKS = new LinkedHashMap<>();
    private static Stage stage = Stage.WAIT_FOR_WORLD;
    private static int ticks;
    private static int stageTicks;
    private static int worldLoadCount;
    private static boolean setupQueued;
    private static volatile boolean setupReady;
    private static boolean serverVerificationQueued;
    private static volatile boolean serverVerificationReady;
    private static volatile boolean serverCookedRice;
    private static volatile boolean serverAdvancementDone;
    private static volatile boolean serverAdvancementReward;
    private static volatile boolean advancementSoundObserved;
    private static boolean finished;
    private static String failure;
    private static BlockPos senbakokiPos;
    private static BlockPos riceCauldronPos;

    private UrushiClientInteractionTest() {}

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (ENABLED && event.getOriginalSound().getLocation().equals(ADVANCEMENT_SOUND)) {
            advancementSoundObserved = true;
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!ENABLED || finished) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ticks++;
        try {
            UrushiClientTestMode.verify(minecraft);
            if (ticks > TIMEOUT_TICKS) {
                fail(minecraft, "client interaction gate timed out at stage " + stage);
                return;
            }

            switch (stage) {
                case WAIT_FOR_WORLD -> waitForWorld(minecraft);
                case WAIT_FOR_SETUP -> waitForSetup(minecraft);
                case WAIT_FOR_CLIENT_SYNC -> waitForClientSync(minecraft);
                case USE_SENBAKOKI -> useSenbakoki(minecraft);
                case WAIT_FOR_SENBAKOKI -> waitForSenbakoki(minecraft);
                case OPEN_RICE_CAULDRON -> openRiceCauldron(minecraft);
                case WAIT_FOR_RICE_CAULDRON_OPEN -> waitForRiceCauldronOpen(minecraft);
                case USE_RICE_CAULDRON -> useRiceCauldron(minecraft);
                case WAIT_FOR_COOKING -> waitForCooking(minecraft);
                case VERIFY -> verifyResult(minecraft);
                case COMPLETE -> succeed(minecraft);
            }
        } catch (Throwable throwable) {
            fail(minecraft, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }
    }

    private static void waitForWorld(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null || minecraft.gameMode == null
                || minecraft.getSingleplayerServer() == null || minecraft.screen != null) {
            return;
        }

        worldLoadCount++;
        queueFixtureSetup(minecraft);
        stage = Stage.WAIT_FOR_SETUP;
        stageTicks = 0;
    }

    private static void queueFixtureSetup(Minecraft minecraft) {
        if (setupQueued) {
            return;
        }
        setupQueued = true;
        startScenario("client_rice_ears_advancement");

        MinecraftServer server = minecraft.getSingleplayerServer();
        LocalPlayer clientPlayer = minecraft.player;
        if (server == null || clientPlayer == null) {
            throw new IllegalStateException("singleplayer client did not expose an integrated server");
        }

        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(clientPlayer.getUUID());
        if (serverPlayer == null) {
            setupQueued = false;
            return;
        }

        BlockPos playerPos = serverPlayer.blockPosition();
        int x = playerPos.getX() + 2;
        int y = playerPos.getY();
        int z = playerPos.getZ();
        senbakokiPos = new BlockPos(x, y, z + 2);
        riceCauldronPos = new BlockPos(x + 2, y, z + 2);
        BlockPos furnacePos = riceCauldronPos.below();

        server.execute(() -> {
            ServerLevel level = server.overworld();
            clearFixtureArea(level, new BlockPos(x, y, z));

            for (int dx = -1; dx <= 4; dx++) {
                for (int dz = 0; dz <= 5; dz++) {
                    level.setBlock(new BlockPos(x + dx, y - 1, z + dz),
                            Blocks.STONE.defaultBlockState(), 3);
                }
            }

            level.setBlock(senbakokiPos,
                    ItemAndBlockRegister.senbakoki.get().defaultBlockState(), 3);
            level.setBlock(furnacePos,
                    ItemAndBlockRegister.dirt_furnace.get().defaultBlockState()
                            .setValue(DirtFurnaceBlock.LIT, true), 3);
            level.setBlock(riceCauldronPos,
                    ItemAndBlockRegister.rice_cauldron.get().defaultBlockState(), 3);

            serverPlayer.teleportTo(level, x + 1.5D, y + 1.0D, z + 4.5D, 180.0F, 0.0F);
            serverPlayer.getInventory().clearContent();
            serverPlayer.getInventory().selected = 0;
            revokeRiceEarsAdvancement(server, serverPlayer);
            serverPlayer.getInventory().add(new ItemStack(
                    ItemAndBlockRegister.rice_crop.get().asItem(), 1));
            serverPlayer.inventoryMenu.broadcastChanges();
            setupReady = true;
        });
    }

    private static void clearFixtureArea(ServerLevel level, BlockPos origin) {
        for (int dx = -2; dx <= 5; dx++) {
            for (int dz = -1; dz <= 6; dz++) {
                for (int dy = 0; dy <= 4; dy++) {
                    level.setBlock(origin.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void revokeRiceEarsAdvancement(MinecraftServer server, ServerPlayer player) {
        AdvancementHolder holder = server.getAdvancements().get(RICE_EARS_ADVANCEMENT);
        if (holder == null) {
            return;
        }
        for (String criterion : holder.value().criteria().keySet()) {
            player.getAdvancements().revoke(holder, criterion);
        }
    }

    private static void waitForSetup(Minecraft minecraft) {
        if (!setupReady) {
            return;
        }
        stage = Stage.WAIT_FOR_CLIENT_SYNC;
        stageTicks = 0;
    }

    private static void waitForClientSync(Minecraft minecraft) {
        stageTicks++;
        if (minecraft.level == null || minecraft.player == null || senbakokiPos == null
                || riceCauldronPos == null || !minecraft.level.hasChunkAt(senbakokiPos)
                || countItem(minecraft.player, ItemAndBlockRegister.rice_crop.get().asItem()) < 1) {
            return;
        }
        if (stageTicks < 10) {
            return;
        }
        verifyRecipeRegistry(minecraft);
        stage = Stage.USE_SENBAKOKI;
        stageTicks = 0;
    }

    private static void verifyRecipeRegistry(Minecraft minecraft) {
        startScenario("client_recipe_registry");
        int expected = expectedLoadedRecipeCount();
        int actual = (int) minecraft.level.getRecipeManager().getRecipes().stream()
                .filter(holder -> ModCoreUrushi.ModID.equals(holder.id().getNamespace()))
                .count();
        if (expected < 0 || actual != expected) {
            fail(minecraft, "client recipe registry count mismatch: expected " + expected + ", actual " + actual);
            return;
        }
        for (String path : List.of("acacia_bars", "raw_rice_from_senbakoki", "baked_mochocho")) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, path);
            if (minecraft.level.getRecipeManager().byKey(id).isEmpty()) {
                fail(minecraft, "client recipe registry is missing " + id);
                return;
            }
        }
        observeScenario("client_recipe_registry");
    }

    private static int expectedLoadedRecipeCount() {
        URL rootUrl = UrushiClientInteractionTest.class.getResource("/data/urushi/recipe");
        if (rootUrl == null) {
            return -1;
        }
        try {
            Path rootPath = Paths.get(rootUrl.toURI());
            try (var walk = Files.walk(rootPath)) {
                int count = 0;
                for (Path path : (Iterable<Path>) walk::iterator) {
                    if (!path.toString().endsWith(".json")) {
                        continue;
                    }
                    JsonElement root;
                    try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                        root = JsonParser.parseReader(reader);
                    }
                    if (recipeConditionsAllow(root)) {
                        count++;
                    }
                }
                return count;
            }
        } catch (Exception exception) {
            ModCoreUrushi.logger.error("[UrushiClientInteractionTest] Could not count recipe resources", exception);
            return -1;
        }
    }

    private static boolean recipeConditionsAllow(JsonElement root) {
        if (!root.isJsonObject()) {
            return false;
        }
        JsonObject object = root.getAsJsonObject();
        JsonElement conditions = object.get("neoforge:conditions");
        if (conditions == null || !conditions.isJsonArray()) {
            return true;
        }
        for (JsonElement condition : conditions.getAsJsonArray()) {
            if (!condition.isJsonObject()) {
                continue;
            }
            JsonObject value = condition.getAsJsonObject();
            if ("neoforge:mod_loaded".equals(value.get("type").getAsString())
                    && !ModList.get().isLoaded(value.get("modid").getAsString())) {
                return false;
            }
        }
        return true;
    }

    private static void useSenbakoki(Minecraft minecraft) {
        startScenario("client_senbakoki_rice_ear_interaction");
        selectItem(minecraft.player, ItemAndBlockRegister.rice_crop.get().asItem());
        useBlock(minecraft, senbakokiPos);
        stage = Stage.WAIT_FOR_SENBAKOKI;
        stageTicks = 0;
    }

    private static void waitForSenbakoki(Minecraft minecraft) {
        stageTicks++;
        if (countItem(minecraft.player, ItemAndBlockRegister.rice_crop.get().asItem()) == 0
                && countItem(minecraft.player, ItemAndBlockRegister.raw_rice.get()) >= 1
                && countItem(minecraft.player, ItemAndBlockRegister.straw.get()) >= 1) {
            observeScenario("client_senbakoki_rice_ear_interaction");
            stage = Stage.OPEN_RICE_CAULDRON;
            stageTicks = 0;
            return;
        }
        if (stageTicks > 60) {
            fail(minecraft, "client senbakoki interaction did not produce raw rice and straw");
        }
    }

    private static void openRiceCauldron(Minecraft minecraft) {
        selectEmptyHotbarSlot(minecraft.player);
        useBlock(minecraft, riceCauldronPos);
        stage = Stage.WAIT_FOR_RICE_CAULDRON_OPEN;
        stageTicks = 0;
    }

    private static void waitForRiceCauldronOpen(Minecraft minecraft) {
        stageTicks++;
        if (minecraft.level != null
                && minecraft.level.getBlockState(riceCauldronPos)
                .getValue(RiceCauldronBlock.VARIANT) == 1) {
            stage = Stage.USE_RICE_CAULDRON;
            stageTicks = 0;
            return;
        }
        if (stageTicks > 60) {
            fail(minecraft, "client could not open the empty rice cauldron");
        }
    }

    private static void useRiceCauldron(Minecraft minecraft) {
        startScenario("client_rice_cauldron_cooking");
        selectItem(minecraft.player, ItemAndBlockRegister.raw_rice.get());
        useBlock(minecraft, riceCauldronPos);
        stage = Stage.WAIT_FOR_COOKING;
        stageTicks = 0;
    }

    private static void waitForCooking(Minecraft minecraft) {
        stageTicks++;
        if (stageTicks < 120 || serverVerificationQueued) {
            return;
        }

        serverVerificationQueued = true;
        MinecraftServer server = minecraft.getSingleplayerServer();
        if (server == null) {
            throw new IllegalStateException("integrated server disappeared during cooking");
        }
        UUID playerId = minecraft.player.getUUID();
        server.execute(() -> {
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            ServerLevel level = server.overworld();
            RiceCauldronBlockEntity blockEntity = level.getBlockEntity(riceCauldronPos) instanceof RiceCauldronBlockEntity value
                    ? value : null;
            serverCookedRice = blockEntity != null
                    && blockEntity.getItem(0).isEmpty()
                    && blockEntity.getItem(1).is(ItemAndBlockRegister.rice.get());
            serverAdvancementDone = player != null && advancementDone(server, player);
            serverAdvancementReward = player != null
                    && countServerItem(player, ItemAndBlockRegister.senbakoki.get().asItem()) >= 1;
            serverVerificationReady = true;
        });
        stage = Stage.VERIFY;
        stageTicks = 0;
    }

    private static void verifyResult(Minecraft minecraft) {
        stageTicks++;
        if (!serverVerificationReady) {
            if (stageTicks > 60) {
                fail(minecraft, "integrated server did not return cooking verification");
            }
            return;
        }
        if (!serverCookedRice) {
            fail(minecraft, "rice cauldron did not cook raw rice into rice");
            return;
        }
        if (!serverAdvancementDone) {
            fail(minecraft, "rice ears advancement did not complete from the client fixture");
            return;
        }
        if (!serverAdvancementReward) {
            fail(minecraft, "rice ears advancement did not grant a Senbakoki");
            return;
        }
        if (!advancementSoundObserved) {
            fail(minecraft, "rice ears advancement did not play urushi_advancements");
            return;
        }
        observeScenario("client_rice_cauldron_cooking");
        observeScenario("client_rice_ears_advancement");
        stage = Stage.COMPLETE;
    }

    private static boolean advancementDone(MinecraftServer server, ServerPlayer player) {
        AdvancementHolder holder = server.getAdvancements().get(RICE_EARS_ADVANCEMENT);
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static int countServerItem(ServerPlayer player, Item item) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void observeScenario(String id) {
        startScenario(id);
        OBSERVED_SCENARIOS.add(id);
        SCENARIO_END_TICKS.put(id, ticks);
    }

    private static void startScenario(String id) {
        SCENARIO_START_TICKS.putIfAbsent(id, ticks);
    }

    private static void selectItem(LocalPlayer player, Item item) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(item)) {
                player.getInventory().selected = slot < 9 ? slot : player.getInventory().selected;
                return;
            }
        }
        throw new IllegalStateException("client inventory is missing " + item);
    }

    private static void selectEmptyHotbarSlot(LocalPlayer player) {
        for (int slot = 0; slot < 9; slot++) {
            if (player.getInventory().getItem(slot).isEmpty()) {
                player.getInventory().selected = slot;
                return;
            }
        }
        throw new IllegalStateException("client hotbar has no empty slot for cauldron opening");
    }

    private static int countItem(LocalPlayer player, Item item) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void useBlock(Minecraft minecraft, BlockPos pos) {
        if (minecraft.gameMode == null || minecraft.player == null || minecraft.level == null) {
            throw new IllegalStateException("client interaction context is not ready");
        }
        if (!minecraft.level.getBlockState(pos).is(ItemAndBlockRegister.senbakoki.get())
                && !minecraft.level.getBlockState(pos).is(ItemAndBlockRegister.rice_cauldron.get())) {
            throw new IllegalStateException("client target block is not the expected Urushi block at " + pos);
        }
        minecraft.gameMode.useItemOn(minecraft.player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false));
    }

    private static void succeed(Minecraft minecraft) {
        if (finished) {
            return;
        }
        finished = true;
        writeReceipt(true, null);
        stopClient(minecraft, 0);
    }

    private static void fail(Minecraft minecraft, String message) {
        if (finished) {
            return;
        }
        finished = true;
        failure = message;
        writeReceipt(false, message);
        stopClient(minecraft, 1);
    }

    private static void stopClient(Minecraft minecraft, int exitCode) {
        minecraft.stop();
        Thread shutdown = new Thread(() -> {
            try {
                Thread.sleep(750L);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            System.exit(exitCode);
        }, "UrushiClientInteractionTest-Shutdown");
        shutdown.setDaemon(false);
        shutdown.start();
    }

    private static void writeReceipt(boolean passed, String failureMessage) {
        Path receiptPath = Paths.get(System.getenv().getOrDefault(
                RECEIPT_ENV, "build/minecraft-mod-testing/urushi-client-interaction-receipt.json"));
        Map<String, Object> receipt = new LinkedHashMap<>();
        receipt.put("schemaVersion", 1);
        receipt.put("sessionId", System.getenv().getOrDefault("MINECRAFT_MOD_TEST_SESSION_ID", ""));
        receipt.put("processStartCount", 1);
        receipt.put("worldLoadCount", worldLoadCount);
        receipt.put("scenarioCount", REQUESTED_SCENARIOS.size());
        receipt.put("failedCount", passed ? 0 : 1);
        receipt.put("cleanupFailureCount", 0);
        receipt.put("allScenariosPassed", passed);
        receipt.put("requestedScenarios", REQUESTED_SCENARIOS);
        receipt.put("observedScenarios", new ArrayList<>(OBSERVED_SCENARIOS));
        List<Map<String, Object>> scenarios = new ArrayList<>();
        for (String scenario : REQUESTED_SCENARIOS) {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("id", scenario);
            detail.put("passed", passed && OBSERVED_SCENARIOS.contains(scenario));
            detail.put("cleanupPassed", passed && OBSERVED_SCENARIOS.contains(scenario));
            detail.put("startedAtTick", SCENARIO_START_TICKS.getOrDefault(scenario, -1));
            detail.put("finishedAtTick", SCENARIO_END_TICKS.getOrDefault(scenario, -1));
            detail.put("artifacts", List.of());
            scenarios.add(detail);
        }
        receipt.put("scenarios", scenarios);
        receipt.put("ticks", ticks);
        receipt.put("unattended", UrushiClientTestMode.receipt());
        if (failureMessage != null) {
            receipt.put("failure", failureMessage);
        }
        try {
            Files.createDirectories(receiptPath.toAbsolutePath().getParent());
            Files.writeString(receiptPath,
                    new GsonBuilder().setPrettyPrinting().create().toJson(receipt));
        } catch (IOException exception) {
            ModCoreUrushi.logger.error("[UrushiClientInteractionTest] Could not write receipt", exception);
        }
    }

    private enum Stage {
        WAIT_FOR_WORLD,
        WAIT_FOR_SETUP,
        WAIT_FOR_CLIENT_SYNC,
        USE_SENBAKOKI,
        WAIT_FOR_SENBAKOKI,
        OPEN_RICE_CAULDRON,
        WAIT_FOR_RICE_CAULDRON_OPEN,
        USE_RICE_CAULDRON,
        WAIT_FOR_COOKING,
        VERIFY,
        COMPLETE
    }
}
