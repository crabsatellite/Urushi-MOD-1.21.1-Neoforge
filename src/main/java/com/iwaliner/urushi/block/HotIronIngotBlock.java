package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.serialization.MapCodec;

public class HotIronIngotBlock extends HorizonalRotateBlock{
    public static final MapCodec<HotIronIngotBlock> CODEC = simpleCodec(HotIronIngotBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    protected static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 2D, 10D, 4D, 14.0D);

    public HotIronIngotBlock(Properties p_i48377_1_) {
        super(p_i48377_1_);
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext context) {
       return UrushiUtils.rotateSimpleBoxShapeHorizontally(SHAPE,state.getValue(FACING));}

}
