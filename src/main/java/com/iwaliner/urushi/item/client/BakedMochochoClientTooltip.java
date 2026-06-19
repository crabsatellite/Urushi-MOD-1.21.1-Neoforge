package com.iwaliner.urushi.item.client;

import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.util.UrushiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Client-only holder for BakedMochochoItem's animated tooltip. Kept in its
 * own class so the server never resolves {@link Minecraft}/{@link net.minecraft.client.multiplayer.ClientLevel}
 * while loading BakedMochochoItem during RegisterEvent.
 */
public final class BakedMochochoClientTooltip {
    private BakedMochochoClientTooltip() {}

    public static void append(List<Component> list) {
        try {
            Level clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return;
            long gametime = clientLevel.getGameTime() % 100;
            if (gametime < 20) {
                UrushiUtils.setInfoWithColor(list, "obanyaki", ChatFormatting.WHITE);
            } else if (gametime < 40) {
                UrushiUtils.setInfoWithColor(list, "kaitenyaki", ChatFormatting.WHITE);
            } else if (gametime < 60) {
                UrushiUtils.setInfoWithColor(list, "imagawayaki", ChatFormatting.WHITE);
            } else if (gametime < 80) {
                UrushiUtils.setInfoWithColor(list, "oyaki", ChatFormatting.WHITE);
            } else {
                UrushiUtils.setInfoWithColor(list, "gozasourou", ChatFormatting.WHITE);
            }
        } catch (Exception e) {
            ModCoreUrushi.logger.debug("BakedMochochoItem tooltip suppressed", e);
        }
    }
}
