package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.serialization.MapCodec;

import java.util.List;

public class EulaliaBlock extends TallGrassBlock {
    public static final MapCodec<EulaliaBlock> CODEC = simpleCodec(EulaliaBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public EulaliaBlock(Properties p_57318_) {
        super(p_57318_);
    }
    @Override
    public void appendHoverText(ItemStack p_49816_, Item.TooltipContext p_49817_, List<Component> list, TooltipFlag p_49819_) {
        UrushiUtils.setInfo(list,"small_flower");
    }
    public void performBonemeal(ServerLevel p_57320_, RandomSource p_57321_, BlockPos p_57322_, BlockState p_57323_) {
        DoublePlantBlock doubleplantblock = (DoublePlantBlock) ItemAndBlockRegister.double_eulalia.get();
        if (doubleplantblock.defaultBlockState().canSurvive(p_57320_, p_57322_) && p_57320_.isEmptyBlock(p_57322_.above())) {
            DoublePlantBlock.placeAt(p_57320_, doubleplantblock.defaultBlockState(), p_57322_, 2);
        }
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
