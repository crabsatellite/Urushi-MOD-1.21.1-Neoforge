package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

public class FlammableStairBlock extends StairBlock {
    public static final MapCodec<FlammableStairBlock> CODEC = simpleCodec(__p -> new FlammableStairBlock(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), __p));

    @Override
    public MapCodec<? extends FlammableStairBlock> codec() { return CODEC; }
    public FlammableStairBlock(BlockState p_56862_, Properties p_56863_) {
        super(p_56862_, p_56863_);
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
