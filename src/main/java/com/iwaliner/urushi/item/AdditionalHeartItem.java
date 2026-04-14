package com.iwaliner.urushi.item;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.network.AdditionalHeartProvider;

import java.util.Objects;

public class AdditionalHeartItem extends Item {
    public AdditionalHeartItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        var data = player.getData(AdditionalHeartProvider.ADDITIONAL_HEART.get());
        data.increaseHeart();
        Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(player.getAttribute(Attributes.MAX_HEALTH).getBaseValue()+2);
        stack.shrink(1);

        return InteractionResultHolder.success(stack);
    }
}
