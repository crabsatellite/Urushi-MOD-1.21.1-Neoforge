package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

public class FlammableSlabBlock extends SlabBlock {
    public static final MapCodec<FlammableSlabBlock> CODEC = simpleCodec(FlammableSlabBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public FlammableSlabBlock(Properties p_56359_) {
        super(p_56359_);
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
