package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.serialization.MapCodec;

import java.util.List;

public class DoubleEulaliaBlock extends DoublePlantBlock implements BonemealableBlock {
    public static final MapCodec<DoubleEulaliaBlock> CODEC = simpleCodec(DoubleEulaliaBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public DoubleEulaliaBlock(Properties p_52861_) {
        super(p_52861_);
    }
    public ItemStack getCloneItemStack(LevelReader p_152966_, BlockPos p_152967_, BlockState p_152968_) {
        return new ItemStack(ItemAndBlockRegister.eulalia.get());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader p_256559_, BlockPos p_50898_, BlockState p_50899_) {
        return true;
    }

    public boolean isBonemealSuccess(Level p_57308_, RandomSource p_57309_, BlockPos p_57310_, BlockState p_57311_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_57298_, RandomSource p_57299_, BlockPos p_57300_, BlockState p_57301_) {
        popResource(p_57298_, p_57300_, new ItemStack(ItemAndBlockRegister.eulalia.get()));
    }
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }
}
