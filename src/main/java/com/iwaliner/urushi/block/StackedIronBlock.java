package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;

public class StackedIronBlock extends Block {
    public static final MapCodec<StackedIronBlock> CODEC = simpleCodec(StackedIronBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1D, 15.0D);
    public StackedIronBlock(Properties p_49795_) {
        super(p_49795_);
    }
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }
}
