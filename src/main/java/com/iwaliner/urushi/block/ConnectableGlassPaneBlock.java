package com.iwaliner.urushi.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.mojang.serialization.MapCodec;

public class ConnectableGlassPaneBlock extends FramedPaneBlock{
    public static final MapCodec<ConnectableGlassPaneBlock> CODEC = simpleCodec(ConnectableGlassPaneBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public ConnectableGlassPaneBlock(Properties p_i48355_2_) {
        super(p_i48355_2_);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_) {
        return true;
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean skipRendering(BlockState p_60532_, BlockState p_60533_, Direction p_60534_) {
        return p_60533_.is(this) ? true : false;
    }


}
