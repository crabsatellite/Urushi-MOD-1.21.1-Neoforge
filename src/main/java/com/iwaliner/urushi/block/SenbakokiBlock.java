package com.iwaliner.urushi.block;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.iwaliner.urushi.ItemAndBlockRegister;
import com.iwaliner.urushi.RecipeTypeRegister;
import com.iwaliner.urushi.recipe.SenbakokiRecipe;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.serialization.MapCodec;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SenbakokiBlock extends HorizonalRotateBlock{
    public static final MapCodec<SenbakokiBlock> CODEC = simpleCodec(SenbakokiBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }

    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2D, 14.0D, 9.0D, 14.0D);
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

    public SenbakokiBlock(Properties p_i48377_1_) {
        super(p_i48377_1_);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
      return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        ItemStack stack = player.getMainHandItem();

        Optional<RecipeHolder<SenbakokiRecipe>> recipe = Optional.ofNullable(level.getRecipeManager())
                .flatMap(manager -> manager.getRecipeFor(RecipeTypeRegister.SenbakokiRecipe, new SingleRecipeInput(stack), level));
        if (recipe.isPresent()) {
             stack.shrink(1);
                if (!player.getInventory().add(recipe.get().value().getResultItem().copy())) {
                    player.drop(recipe.get().value().getResultItem().copy(), false);
                }
                for(int i=0;i<recipe.get().value().getSubResultItems().size();i++) {
                    if (!player.getInventory().add(recipe.get().value().getSubResultItems().get(i).copy())) {
                        player.drop(recipe.get().value().getSubResultItems().get(i).copy(), false);
                    }
                }

            level.playSound((Player) null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F, 1F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }
    @Override
    public void appendHoverText(ItemStack p_49816_, Item.TooltipContext p_49817_, List<Component> list, TooltipFlag p_49819_) {
        UrushiUtils.setInfo(list,"senbakoki");
    }
}
