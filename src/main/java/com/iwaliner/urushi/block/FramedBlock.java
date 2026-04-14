package com.iwaliner.urushi.block;

import net.minecraft.world.level.block.Block;
import com.mojang.serialization.MapCodec;

public class FramedBlock extends AbstractFramedBlock {
    public static final MapCodec<FramedBlock> CODEC = simpleCodec(FramedBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public FramedBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(UP, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)).setValue(VARIANT, Boolean.valueOf(false)));

    }
}
