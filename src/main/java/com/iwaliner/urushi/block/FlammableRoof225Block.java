package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

public class FlammableRoof225Block extends Roof225Block{
    public static final MapCodec<FlammableRoof225Block> CODEC = simpleCodec(FlammableRoof225Block::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public FlammableRoof225Block(Properties p_i48331_1_) {
        super(p_i48331_1_);
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
