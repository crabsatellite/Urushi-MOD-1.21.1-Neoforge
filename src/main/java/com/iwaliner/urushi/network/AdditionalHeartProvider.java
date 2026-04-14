package com.iwaliner.urushi.network;

import com.iwaliner.urushi.ModCoreUrushi;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AdditionalHeartProvider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModCoreUrushi.ModID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AdditionalHeartData>> ADDITIONAL_HEART =
        ATTACHMENT_TYPES.register("additional_heart", () -> AttachmentType.builder(() -> new AdditionalHeartData()).build());
}
