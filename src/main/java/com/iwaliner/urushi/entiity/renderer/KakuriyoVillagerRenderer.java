package com.iwaliner.urushi.entiity.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.iwaliner.urushi.ClientSetUp;
import com.iwaliner.urushi.entiity.KakuriyoVillagerEntity;
import com.iwaliner.urushi.entiity.model.OniModel;
import com.iwaliner.urushi.util.KakuriyoVillagerProfessionType;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class KakuriyoVillagerRenderer extends MobRenderer<KakuriyoVillagerEntity, HumanoidModel<KakuriyoVillagerEntity>> {


    public KakuriyoVillagerRenderer(EntityRendererProvider.Context p_174131_) {
        super(p_174131_, new OniModel<>(p_174131_.bakeLayer(ClientSetUp.KAKURIYO_VILLAGER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, p_174131_.getItemInHandRenderer()));
    }


    public ResourceLocation getTextureLocation(KakuriyoVillagerEntity entity) {
        KakuriyoVillagerProfessionType professionType=entity.getProfessionType();
        switch (professionType){
            case Cook : return  ResourceLocation.parse("urushi:textures/entity/kakuriyo_villager/cook.png");
            case Miner: return  ResourceLocation.parse("urushi:textures/entity/kakuriyo_villager/miner.png");
            case Fisherman: return  ResourceLocation.parse("urushi:textures/entity/kakuriyo_villager/fisherman.png");
            case Lumberjack: return ResourceLocation.parse("urushi:textures/entity/kakuriyo_villager/lumberjack.png");
            case RiceDealer: return ResourceLocation.parse("urushi:textures/entity/kakuriyo_villager/rice_dealer.png");
        }
        return  ResourceLocation.parse("urushi:textures/entity/kakuriyo_villager/jobless.png");
    }
}
