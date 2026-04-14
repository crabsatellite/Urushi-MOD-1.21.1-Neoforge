package com.iwaliner.urushi.block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.mojang.serialization.MapCodec;


public class RiceCropBlock extends UrushiCropBlock {
    public static final MapCodec<RiceCropBlock> CODEC = simpleCodec(RiceCropBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }


    public RiceCropBlock(Properties p_i48421_1_) {
        super(p_i48421_1_);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState state2, boolean boo) {
        if(world.getBlockState(pos.below()).getBlock() instanceof FarmBlock){
            world.setBlockAndUpdate(pos.below(), ItemAndBlockRegister.paddy_field.get().defaultBlockState());
        }
    }




}
