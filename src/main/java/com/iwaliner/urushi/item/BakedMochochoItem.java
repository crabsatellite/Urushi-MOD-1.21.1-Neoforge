package com.iwaliner.urushi.item;

import com.iwaliner.urushi.item.client.BakedMochochoClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

public class BakedMochochoItem extends Item {
    public BakedMochochoItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            BakedMochochoClientTooltip.append(list);
        }
    }
}
