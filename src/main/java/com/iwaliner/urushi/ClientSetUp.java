package com.iwaliner.urushi;



import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.block.*;
import com.iwaliner.urushi.blockentity.menu.FillerMenu;
import com.iwaliner.urushi.blockentity.renderer.*;
import com.iwaliner.urushi.blockentity.screen.*;
import com.iwaliner.urushi.entiity.food.model.*;
import com.iwaliner.urushi.entiity.food.renderer.*;
import com.iwaliner.urushi.entiity.model.CushionModel;
import com.iwaliner.urushi.entiity.model.OniModel;
import com.iwaliner.urushi.entiity.renderer.*;
import com.iwaliner.urushi.item.menu.DrawstringBagMenu;
import com.iwaliner.urushi.item.screen.DrawstringBagScreen;
import com.iwaliner.urushi.json.*;
import com.iwaliner.urushi.particle.*;
import com.iwaliner.urushi.util.ElementUtils;
import com.iwaliner.urushi.util.ToggleKeyMappingPlus;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.Objects;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModCoreUrushi.ModID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetUp {
    public static final ModelLayerLocation RICE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "rice_food"), "rice_food");
    public static final ModelLayerLocation KARAAGE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "karaage_food"), "karaage_food");
    public static final ModelLayerLocation TOFU = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "tofu_food"), "tofu_food");
    public static final ModelLayerLocation ABURAAGE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "aburaage_food"), "aburaage_food");
    public static final ModelLayerLocation DANGO = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "dango_food"), "dango_food");
    public static final ModelLayerLocation RICE_CAKE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "rice_cake_food"), "rice_cake_food");
    public static final ModelLayerLocation ROASTED_RICE_CAKE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "roasted_rice_cake_food"), "roasted_rice_cake_food");
    public static final ModelLayerLocation CUSHION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "cushion"), "cushion");
    public static final ModelLayerLocation SUSHI = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "sushi_food"), "sushi_food");
    public static final ModelLayerLocation SALMON_ROE_SUSHI = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "salmon_roe_sushi_food"), "salmon_roe_sushi_food");
    public static final ModelLayerLocation SHRIMP_SUSHI = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "shrimp_sushi_food"), "shrimp_sushi_food");
    public static final ModelLayerLocation INARI = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "inari_food"), "inari_food");
    public static final ModelLayerLocation RAMEN = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "ramen_food"), "ramen_food");
    public static final ModelLayerLocation MISO_SOUP = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "miso_soup_food"), "miso_soup_food");
    public static final ModelLayerLocation KAKURIYO_VILLAGER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "kakuriyo_villager"), "kakuriyo_villager");
    public static final ModelLayerLocation GREEN_TEA = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "green_tea_food"), "green_tea_food");
    public static final ModelLayerLocation SAKE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "sake_food"), "sake_food");
    public static final ModelLayerLocation TOKKURI = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "tokkuri_food"), "tokkuri_food");
    public static final ModelLayerLocation OCHOKO = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "ochoko_food"), "ochoko_food");
    public static final ModelLayerLocation MANDARIN = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "mandarin_food"), "mandarin_food");


    public static KeyMapping connectionKey = new ToggleKeyMappingPlus("key.urushi.connectionKey", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.urushi.category");
    @SubscribeEvent
    public static void keyRegister(RegisterKeyMappingsEvent event) {
        event.register(ClientSetUp.connectionKey);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
            private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");
            private static final ResourceLocation OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");

            @Override
            public ResourceLocation getStillTexture() { return STILL; }

            @Override
            public ResourceLocation getFlowingTexture() { return FLOWING; }

            @Override
            public ResourceLocation getOverlayTexture() { return OVERLAY; }

            @Override
            public int getTintColor() { return 0xbf60c3c9; }
        }, FluidTypeRegister.HOT_SPRING_FLUID_TYPE.get());
    }

     /**エンティティの見た目を登録*/
    @SubscribeEvent
    public static void RegisterEntityRendererEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegister.RiceFoodEntity.get(), RiceFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.TKGFoodEntity.get(), TKGFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SekihanFoodEntity.get(), SekihanFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.GyudonFoodEntity.get(), GyudonFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.ButadonFoodEntity.get(), ButadonFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.KitsuneUdonFoodEntity.get(), KitsuneUdonFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.KaraageFoodEntity.get(), KaraageFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.TofuFoodEntity.get(), TofuFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.AburaageFoodEntity.get(), AburaageFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.KusaDangoFoodEntity.get(), KusaDangoFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.ColorDangoFoodEntity.get(), ColorDangoFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.MitarashiDangoFoodEntity.get(), MitarashiDangoFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.RiceCakeFoodEntity.get(), RiceCakeFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.RoastedRiceCakeFoodEntity.get(), RoastedRiceCakeFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.TsunaSushiFoodEntity.get(), TsunaSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SalmonSushiFoodEntity.get(), SalmonSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SquidSushiFoodEntity.get(), SquidSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.EggSushiFoodEntity.get(), EggSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SalmonRoeSushiFoodEntity.get(), SalmonRoeSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.ShrimpSushiFoodEntity.get(), ShrimpSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.GravelSushiFoodEntity.get(), GravelSushiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.InariFoodEntity.get(), InariFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.KitsunebiEntity.get(),  (p_174088_) -> {
            return new ThrownItemRenderer<>(p_174088_, 1.0F, true);
        });
        event.registerEntityRenderer(EntityRegister.Ghost.get(), GhostRenderer::new);
        event.registerEntityRenderer(EntityRegister.GianntSkeleton.get(), GiantSkeletonRenderer::new);
        event.registerEntityRenderer(EntityRegister.Cushion.get(), CushionRenderer::new);
        event.registerEntityRenderer(EntityRegister.Jufu.get(),  (p_174088_) -> {
            return new ThrownItemRenderer<>(p_174088_, 1.0F, true);
        });
        event.registerEntityRenderer(EntityRegister.JufuEffectDisplay.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(EntityRegister.MisoSoupFoodEntity.get(), MisoSoupFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.CheeseGyudonFoodEntity.get(), CheeseGyudonFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.CheeseGyudonWithOnsenEggFoodEntity.get(), CheeseGyudonWithOnsenEggFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.GreenOnionAndRawEggGyudonFoodEntity.get(), GreenOnionAndRawEggGyudonFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.MustardLeafAndCodCaviarGyudonFoodEntity.get(), MustardLeafAndCodCaviarGyudonFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SakuraMochiFoodEntity.get(), SakuraMochiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.OhagiFoodEntity.get(), OhagiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.KusaMochiFoodEntity.get(), KusaMochiFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SoySourceRamenFoodEntity.get(), SoySourceRamenFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SaltRamenFoodEntity.get(), SaltRamenFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.MisoRamenFoodEntity.get(), MisoRamenFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.TonkotsuRamenFoodEntity.get(), TonkotsuRamenFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.MincedTunaBowlFoodEntity.get(), MincedTunaBowlFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.KakuriyoVillager.get(), KakuriyoVillagerRenderer::new);
        event.registerEntityRenderer(EntityRegister.GreenTeaFoodEntity.get(), GreenTeaFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.MandarinFoodEntity.get(), MandarinFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.SakeFoodEntity.get(), SakeFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.OchokoFoodEntity.get(), OchokoFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.TokkuriFoodEntity.get(), TokkuriFoodRenderer::new);
        event.registerEntityRenderer(EntityRegister.ExperienceDroppableFallingAnvil.get(), ExperienceDroppableAnvilRenderer::new);


    }

    /**エンティティのレイヤーを指定*/
    @SubscribeEvent
    public static void registerLayerEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RICE, RiceFoodModel::createBodyLayer);
        event.registerLayerDefinition(KARAAGE, KaraageFoodModel::createBodyLayer);
        event.registerLayerDefinition(TOFU, TofuFoodModel::createBodyLayer);
        event.registerLayerDefinition(ABURAAGE, AburaageFoodModel::createBodyLayer);
        event.registerLayerDefinition(DANGO, DangoFoodModel::createBodyLayer);
        event.registerLayerDefinition(RICE_CAKE, RiceCakeFoodModel::createBodyLayer);
        event.registerLayerDefinition(ROASTED_RICE_CAKE, RoastedRiceCakeFoodModel::createBodyLayer);
        event.registerLayerDefinition(CUSHION, CushionModel::createBodyLayer);
        event.registerLayerDefinition(SUSHI, SushiFoodModel::createBodyLayer);
        event.registerLayerDefinition(SALMON_ROE_SUSHI, SalmonRoeSushiFoodModel::createBodyLayer);
        event.registerLayerDefinition(SHRIMP_SUSHI, ShrimpSushiFoodModel::createBodyLayer);
        event.registerLayerDefinition(INARI, InariFoodModel::createBodyLayer);
        event.registerLayerDefinition(RAMEN, RamenFoodModel::createBodyLayer);
        event.registerLayerDefinition(MISO_SOUP, MisoSoupFoodModel::createBodyLayer);
        event.registerLayerDefinition(KAKURIYO_VILLAGER, OniModel::createBodyLayer);
        event.registerLayerDefinition(GREEN_TEA, GreenTeaFoodModel::createBodyLayer);
        event.registerLayerDefinition(MANDARIN, MandarinFoodModel::createBodyLayer);
        event.registerLayerDefinition(SAKE, SakeFoodModel::createBodyLayer);
        event.registerLayerDefinition(TOKKURI, TokkuriFoodModel::createBodyLayer);
        event.registerLayerDefinition(OCHOKO, OchokoFoodModel::createBodyLayer);



    }
    @SubscribeEvent
    public static void registerItemColorEvent(RegisterColorHandlersEvent.Item event) {
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.grass_block_with_fallen_red_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.grass_block_with_fallen_orange_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.grass_block_with_fallen_yellow_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.grass_block_with_fallen_japanese_apricot_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.grass_block_with_fallen_sakura_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_red_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_orange_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_yellow_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_japanese_apricot_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_sakura_leaves.get());
        event.register((stack, i) -> {return 12300080;},ItemAndBlockRegister.kakuriyo_grass_block.get());
        event.register((stack, i) -> {return 13886461;},ItemAndBlockRegister.onsen_egg.get());
        event.register((stack, i) -> {return event.getItemColors().getColor(new ItemStack(Items.OAK_LEAVES),0);},ItemAndBlockRegister.mandarin_leaves.get());
        event.register((stack, i) -> {return i > 0 ? -1 : DyedItemColor.getOrDefault(stack, -1);},ItemAndBlockRegister.drawstring_bag.get());
    }
    @SubscribeEvent
    public static void registerBlockColorEvent(RegisterColorHandlersEvent.Block event) {
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null? BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.grass_block_with_fallen_red_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.grass_block_with_fallen_orange_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.grass_block_with_fallen_yellow_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.grass_block_with_fallen_japanese_apricot_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.grass_block_with_fallen_sakura_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_red_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_orange_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_yellow_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_japanese_apricot_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.kakuriyo_grass_block_with_fallen_sakura_leaves.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageGrassColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.kakuriyo_grass_block.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageWaterColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.rainwater_tank.get());
        event.register((state, reader, pos, i) -> reader!=null&&pos!=null?BiomeColors.getAverageFoliageColor(Objects.requireNonNull(reader), Objects.requireNonNull(pos)):12300080,ItemAndBlockRegister.mandarin_leaves.get());

    }
    /**パーティクルの見た目を指定*/
    @SubscribeEvent
    public static void registerParticlesEvent(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegister.FireElement.get(), ElementParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.WoodElement.get(), ElementParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.EarthElement.get(), ElementParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.MetalElement.get(), ElementParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.WaterElement.get(), ElementParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.FallingRedLeaves.get(), FallingRedLeavesParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.FallingOrangeLeaves.get(), FallingOrangeLeavesParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.FallingYellowLeaves.get(), FallingYellowLeavesParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.FallingSakuraLeaves.get(), FallingSakuraLeavesParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.WoodElementMedium.get(), MediumParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.FireElementMedium.get(), MediumParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.EarthElementMedium.get(), MediumParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.MetalElementMedium.get(), MediumParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegister.WaterElementMedium.get(), MediumParticle.Provider::new);

    }



    @Nullable
    @SubscribeEvent
    public static void RegisterRendererEvent(FMLClientSetupEvent event) {


        /**キーボード操作を登録*/
        /**ClientRegistry.registerKeyBinding(connectionKey);*/

        /**アイテムの状態を登録*/
        event.enqueueWork(() -> {
            ItemProperties.register(ItemAndBlockRegister.iron_katana.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "ishurting"), (itemStack, clientWorld, livingEntity,i) -> (livingEntity instanceof Player &&livingEntity.swinging&&livingEntity.getMainHandItem()==itemStack)?1:0);

            ItemProperties.register(ItemAndBlockRegister.wood_element_magatama.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "stored_amount"), (itemStack, clientWorld, livingEntity,i) -> (int)Mth.floor((float) ElementUtils.getStoredReiryokuAmount(itemStack)/400) );
            ItemProperties.register(ItemAndBlockRegister.fire_element_magatama.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "stored_amount"), (itemStack, clientWorld, livingEntity,i) -> (int)Mth.floor((float) ElementUtils.getStoredReiryokuAmount(itemStack)/400) );
            ItemProperties.register(ItemAndBlockRegister.earth_element_magatama.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "stored_amount"), (itemStack, clientWorld, livingEntity,i) -> (int)Mth.floor((float) ElementUtils.getStoredReiryokuAmount(itemStack)/400) );
            ItemProperties.register(ItemAndBlockRegister.metal_element_magatama.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "stored_amount"), (itemStack, clientWorld, livingEntity,i) -> (int)Mth.floor((float) ElementUtils.getStoredReiryokuAmount(itemStack)/400) );
            ItemProperties.register(ItemAndBlockRegister.water_element_magatama.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "stored_amount"), (itemStack, clientWorld, livingEntity,i) -> (int)Mth.floor((float) ElementUtils.getStoredReiryokuAmount(itemStack)/400) );

            ItemProperties.register(Item.byBlock(ItemAndBlockRegister.japanese_timber_bamboo.get()), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "event"), (itemStack, clientWorld, livingEntity,i) -> UrushiUtils.isShogatsu()? 1 : 0);
            ItemProperties.register(ItemAndBlockRegister.raw_rice.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "is_april_fools"), (itemStack, clientWorld, livingEntity,i) -> UrushiUtils.isAprilFoolsDay()? 1 : 0);
            ItemProperties.register(ItemAndBlockRegister.rice.get(), ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "is_april_fools"), (itemStack, clientWorld, livingEntity,i) -> UrushiUtils.isAprilFoolsDay()? 1 : 0);


        });

       /**コンテナにGUIを登録*/


       /**見た目が特殊なBlockEntityの見た目を登録*/
        BlockEntityRenderers.register(BlockEntityRegister.Sanbo.get(), SanboRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.Shichirin.get(), ShichirinRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.Hokora.get(), HokoraRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.Plate.get(), PlateRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.BambooBasket.get(), BambooBasketRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.DoubledWoodenCabinetryBlockEntity.get(), DoubledWoodenCabinetryRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.Marker.get(), MarkerRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegister.Filler.get(), FillerRenderer::new);



        ModCoreUrushi.underDevelopmentList.add(Item.byBlock(ItemAndBlockRegister.senryoubako.get()));
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.additional_heart.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.giant_skeleton_spawn_egg.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.white_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.orange_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.magenta_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.light_blue_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.yellow_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.lime_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.pink_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.gray_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.light_gray_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.cyan_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.purple_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.blue_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.brown_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.green_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.red_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.black_silk.get());
        ModCoreUrushi.underDevelopmentList.add(ItemAndBlockRegister.kakuriyo_chronicles_1.get());
        ModCoreUrushi.underDevelopmentList.add(Item.byBlock(ItemAndBlockRegister.marker.get()));
        ModCoreUrushi.underDevelopmentList.add(Item.byBlock(ItemAndBlockRegister.filler.get()));


        ModCoreUrushi.urushiTabContents.add(ItemAndBlockRegister.additional_heart);
        ModCoreUrushi.urushiTabContents.add(ItemAndBlockRegister.ghost_spawn_egg);
        ModCoreUrushi.urushiTabContents.add(ItemAndBlockRegister.giant_skeleton_spawn_egg);
        ModCoreUrushi.urushiTabContents.add(ItemAndBlockRegister.kakuriyo_villager_spawn_egg);


        /**jsonファイルを自動生成するために開発環境のパスを登録*/
        if(ModCoreUrushi.isDebug) {
        FMLPaths.GAMEDIR.get();
        ModCoreUrushi.assetsDirectory = new File(FMLPaths.GAMEDIR.get().getParent().toString() + "/src/main/resources/assets/urushi/");
        ModCoreUrushi.assetsInBuildDirectory = new File(FMLPaths.GAMEDIR.get().getParent().toString() + "/build/resources/main/assets/urushi/");
        ModCoreUrushi.dataDirectory = new File(FMLPaths.GAMEDIR.get().getParent().toString() + "/src/main/resources/data/");
        ModCoreUrushi.dataInBuildDirectory = new File(FMLPaths.GAMEDIR.get().getParent().toString() + "/build/resources/main/data/");





            ModCoreUrushi.blockSelfDropList.remove("black_kakuriyo_portal_frame");
            ModCoreUrushi.blockSelfDropList.remove("earth_element_puzzle_block");
            ModCoreUrushi.blockSelfDropList.remove("element_puzzle_controller_a");
            ModCoreUrushi.blockSelfDropList.remove("element_puzzle_controller_b");
            ModCoreUrushi.blockSelfDropList.remove("element_puzzle_controller_c");
            ModCoreUrushi.blockSelfDropList.remove("fire_element_puzzle_block");
            ModCoreUrushi.blockSelfDropList.remove("ghost_black_kakuriyo_portal_frame");
            ModCoreUrushi.blockSelfDropList.remove("ghost_kakuriyo_portal_core");
            ModCoreUrushi.blockSelfDropList.remove("ghost_red_kakuriyo_portal_frame");
            ModCoreUrushi.blockSelfDropList.remove("kakuriyo_portal");
            ModCoreUrushi.blockSelfDropList.remove("kakuriyo_portal_core");
            ModCoreUrushi.blockSelfDropList.remove("metal_element_puzzle_block");
            ModCoreUrushi.blockSelfDropList.remove("random_element_puzzle_block");
            ModCoreUrushi.blockSelfDropList.remove("red_kakuriyo_portal_frame");
            ModCoreUrushi.blockSelfDropList.remove("water_element_puzzle_block");
            ModCoreUrushi.blockSelfDropList.remove("wood_element_puzzle_block");

            MineableTagGenerator.INSTANCE.registerPickaxeMineableTag(ModCoreUrushi.pickaxeList);
            MineableTagGenerator.INSTANCE.registerAxeMineableTag(ModCoreUrushi.axeList);
            MineableTagGenerator.INSTANCE.registerShovelMineableTag(ModCoreUrushi.shovelList);
            MineableTagGenerator.INSTANCE.registerHoeMineableTag(ModCoreUrushi.hoeList);
            RequiredToolMaterialTagGenerator.INSTANCE.registerStoneToolTag(ModCoreUrushi.stoneToolList);
            RequiredToolMaterialTagGenerator.INSTANCE.registerIronToolTag(ModCoreUrushi.ironToolList);
            RequiredToolMaterialTagGenerator.INSTANCE.registerGoldenToolTag(ModCoreUrushi.goldenToolList);
            RequiredToolMaterialTagGenerator.INSTANCE.registerDiamondToolTag(ModCoreUrushi.diamondToolList);
            RequiredToolMaterialTagGenerator.INSTANCE.registerNetheriteToolTag(ModCoreUrushi.netheriteToolList);
            for(String name:ModCoreUrushi.blockSelfDropList){
                LootTableGenerator.INSTANCE.registerSimpleBlockLootTable(name);
            }




        }
    }
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegister.FryerMenu.get(), FryerScreen::new);
        event.register(MenuRegister.DoubledWoodenCabinetryMenu.get(), DoubledWoodenCabinetryScreen::new);
        event.register(MenuRegister.DrawstringBagMenu.get(), DrawstringBagScreen::new);
        event.register(MenuRegister.UrushiHopperMenu.get(), UrushiHopperScreen::new);
        event.register(MenuRegister.AutoCraftingTableMenu.get(), AutoCraftingTableScreen::new);
        event.register(MenuRegister.SilkwormFarmMenu.get(), SilkwormFarmScreen::new);
        event.register(MenuRegister.KettleMenu.get(), KettleScreen::new);
        event.register(MenuRegister.TranslatableBookMenu.get(), TranslatableBookScreen::new);
        event.register(MenuRegister.FillerMenu.get(), FillerScreen::new);
    }


}