package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.TagUrushi;
import com.mojang.serialization.MapCodec;

import java.util.function.ToIntFunction;

public class YomiVinesBlock extends CaveVinesBlock {
    public static final MapCodec<YomiVinesBlock> CODEC = simpleCodec(YomiVinesBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public YomiVinesBlock(Properties p_152959_) {
        super(p_152959_);
    }
    protected Block getBodyBlock() {
        return ItemAndBlockRegister.yomi_vines_plant.get();
    }
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (state) -> {
       return state.getValue(BERRIES)? 5:0;
    };
    public ItemStack getCloneItemStack(LevelReader p_152966_, BlockPos p_152967_, BlockState p_152968_) {
        return new ItemStack(ItemAndBlockRegister.yomotsuhegui_fruit.get());
    }
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (state.getValue(BERRIES)) {
            Block.popResource(level, pos, new ItemStack(ItemAndBlockRegister.yomotsuhegui_fruit.get(), 1));
            float f = Mth.randomBetween(level.random, 0.8F, 1.2F);
            level.playSound((Player)null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
            level.setBlock(pos, state.setValue(BERRIES, Boolean.valueOf(false)), 2);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
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
