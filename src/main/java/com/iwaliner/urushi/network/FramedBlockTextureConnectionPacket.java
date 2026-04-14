package com.iwaliner.urushi.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.iwaliner.urushi.ModCoreUrushi;

public record FramedBlockTextureConnectionPacket(boolean isPressed) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FramedBlockTextureConnectionPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ModCoreUrushi.ModID, "framed_block_texture_connection"));

    public static final StreamCodec<FriendlyByteBuf, FramedBlockTextureConnectionPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL, FramedBlockTextureConnectionPacket::isPressed,
            FramedBlockTextureConnectionPacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FramedBlockTextureConnectionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            FramedBlockTextureConnectionData data = player.getData(FramedBlockTextureConnectionProvider.FRAMED_BLOCK_TEXTURE_CONNECTION.get());
            data.toggle();
        });
    }
}
