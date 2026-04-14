package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import com.iwaliner.urushi.util.ElementType;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.serialization.MapCodec;

public class ElementPuzzleBlock extends Block {
    public static final MapCodec<ElementPuzzleBlock> CODEC = simpleCodec(__p -> new ElementPuzzleBlock(com.iwaliner.urushi.util.ElementType.WoodElement, __p));

    @Override
    public MapCodec<? extends ElementPuzzleBlock> codec() { return CODEC; }
    private final ElementType elementType;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public ElementPuzzleBlock(ElementType type, Properties p_49795_) {
        super(p_49795_);
        elementType=type;
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        for(int i=0;i<6;i++){
            Direction direction=UrushiUtils.getDirectionFromInt(i);
            BlockState state2=level.getBlockState(pos.relative(direction));
            if(state2.getBlock() instanceof ElementPuzzleBlock){
                level.setBlockAndUpdate(pos.relative(direction),state2.cycle(LIT));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
