package com.iwaliner.urushi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import com.iwaliner.urushi.BlockEntityRegister;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.blockentity.SenryoubakoBlockEntity;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SenryoubakoBlock extends BaseEntityBlock {
    public static final MapCodec<SenryoubakoBlock> CODEC = simpleCodec(SenryoubakoBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final ResourceLocation CONTENTS = ShulkerBoxBlock.CONTENTS;

    public SenryoubakoBlock(Properties p_i49996_1_) {
        super(p_i49996_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)));
    }
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult p_60508_) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof SenryoubakoBlockEntity) {
                player.openMenu((SenryoubakoBlockEntity)blockentity);
                player.awardStat(Stats.OPEN_SHULKER_BOX);
            }

            return InteractionResult.CONSUME;
        }
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean boo) {
        if (!state.is(state2.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container) {
           //     Containers.dropContents(level, pos, (Container)blockentity);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, state2, boo);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource p_60465_) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof SenryoubakoBlockEntity) {
           // ((SenryoubakoBlockEntity)blockentity).recheckOpen();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return   new SenryoubakoBlockEntity(pos,state);
    }


    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }
    @Override
    public BlockState rotate(BlockState state, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING, OPEN);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }





    public BlockState playerWillDestroy(Level p_56212_, BlockPos p_56213_, BlockState p_56214_, Player p_56215_) {
        BlockEntity blockentity = p_56212_.getBlockEntity(p_56213_);
        if (blockentity instanceof SenryoubakoBlockEntity) {
            SenryoubakoBlockEntity shulkerboxblockentity = (SenryoubakoBlockEntity)blockentity;
            if (!p_56212_.isClientSide && p_56215_.isCreative() && !shulkerboxblockentity.isEmpty()) {
                ItemStack itemstack =new ItemStack(ItemAndBlockRegister.senryoubako.get());
                blockentity.saveToItem(itemstack, p_56212_.registryAccess());
                if (shulkerboxblockentity.hasCustomName()) {
                    itemstack.set(DataComponents.CUSTOM_NAME, shulkerboxblockentity.getCustomName());
                }

                ItemEntity itementity = new ItemEntity(p_56212_, (double)p_56213_.getX() + 0.5D, (double)p_56213_.getY() + 0.5D, (double)p_56213_.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                p_56212_.addFreshEntity(itementity);
            } else {
                shulkerboxblockentity.unpackLootTable(p_56215_);
            }
        }

        return super.playerWillDestroy(p_56212_, p_56213_, p_56214_, p_56215_);
    }

    public List<ItemStack> getDrops(BlockState p_56246_, LootParams.Builder p_56247_) {
        BlockEntity blockentity = p_56247_.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof SenryoubakoBlockEntity) {
            SenryoubakoBlockEntity shulkerboxblockentity = (SenryoubakoBlockEntity)blockentity;
            p_56247_ = p_56247_.withDynamicDrop(CONTENTS, (p_56219_) -> {
                for(int i = 0; i < shulkerboxblockentity.getContainerSize(); ++i) {
                    p_56219_.accept(shulkerboxblockentity.getItem(i));
                }

            });
        }

        return super.getDrops(p_56246_, p_56247_);
    }


    public PushReaction getPistonPushReaction(BlockState p_56265_) {
        return PushReaction.DESTROY;
    }
    public ItemStack getCloneItemStack(LevelReader p_56202_, BlockPos p_56203_, BlockState p_56204_) {
        ItemStack itemstack = super.getCloneItemStack(p_56202_, p_56203_, p_56204_);
        p_56202_.getBlockEntity(p_56203_, BlockEntityRegister.SenryoubakoBlockEntity.get()).ifPresent((p_187446_) -> {
            p_187446_.saveToItem(itemstack, p_56202_.registryAccess());
        });
        return itemstack;
    }

}
