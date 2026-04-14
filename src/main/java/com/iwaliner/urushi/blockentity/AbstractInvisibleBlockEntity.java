package com.iwaliner.urushi.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.iwaliner.urushi.BlockEntityRegister;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.block.HiddenInvisibleButtonBlock;
import com.iwaliner.urushi.block.InvisibleButtonBlock;

public abstract class AbstractInvisibleBlockEntity extends BlockEntity {
    public int time;


    public AbstractInvisibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.time = tag.getInt("time");
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("time", this.time);
    }
    public static <T extends AbstractInvisibleBlockEntity> void tick(
        Level level, BlockPos pos, BlockState state, T blockEntity, Block targetBlock, Runnable onExpire) {
        if (state.getBlock() == targetBlock) {
            --blockEntity.time;
            if (!level.isClientSide() && blockEntity.time <= 0) {
                onExpire.run();
            }
        }
    }


}