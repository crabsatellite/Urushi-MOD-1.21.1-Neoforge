package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

public class FlammableRotatedPillarBlock extends RotatedPillarBlock {
    public static final MapCodec<FlammableRotatedPillarBlock> CODEC = simpleCodec(FlammableRotatedPillarBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public FlammableRotatedPillarBlock(Properties p_55926_) {
        super(p_55926_);
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
