package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.mojang.serialization.MapCodec;

public class NoodleBlock extends TwoDirectionShapedBlock{
    public static final MapCodec<NoodleBlock> CODEC = simpleCodec(__p -> new NoodleBlock(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false, __p));

    @Override
    public MapCodec<? extends NoodleBlock> codec() { return CODEC; }
    public NoodleBlock(double d1, double d2, double d3, double d4, double d5, double d6, boolean canSurvive, Properties p_i48377_1_) {
        super(d1, d2, d3, d4, d5, d6, canSurvive, p_i48377_1_);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if(!player.isSuppressingBounce()){
            level.destroyBlock(pos,true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
