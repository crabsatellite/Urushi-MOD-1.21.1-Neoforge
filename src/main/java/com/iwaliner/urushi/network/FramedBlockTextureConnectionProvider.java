package com.iwaliner.urushi.network;

import com.iwaliner.urushi.ModCoreUrushi;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FramedBlockTextureConnectionProvider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModCoreUrushi.ModID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FramedBlockTextureConnectionData>> FRAMED_BLOCK_TEXTURE_CONNECTION =
        ATTACHMENT_TYPES.register("framed_block_texture_connection", () -> AttachmentType.builder(() -> new FramedBlockTextureConnectionData()).build());
}
