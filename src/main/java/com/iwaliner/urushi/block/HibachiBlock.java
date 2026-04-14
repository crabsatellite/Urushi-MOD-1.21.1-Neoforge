package com.iwaliner.urushi.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.iwaliner.urushi.TagUrushi;
import com.iwaliner.urushi.util.UrushiUtils;
import com.mojang.serialization.MapCodec;

import java.util.List;

public class HibachiBlock extends HorizonalRotateBlock {
    public static final MapCodec<HibachiBlock> CODEC = simpleCodec(HibachiBlock::new);

    @SuppressWarnings("unchecked")
    @Override
    public MapCodec codec() {
        return CODEC;
    }



    public HibachiBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public void animateTick(BlockState p_180655_1_, Level p_180655_2_, BlockPos p_180655_3_, RandomSource randomSource) {
         double d0 = (double) p_180655_3_.getX() + 0.5D;
            double d1 = (double) p_180655_3_.getY() + 1.0625D;
            double d2 = (double) p_180655_3_.getZ() + 0.5D;
            p_180655_2_.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);


    }
    @Override
    public void appendHoverText(ItemStack p_49816_, Item.TooltipContext p_49817_, List<Component> list, TooltipFlag p_49819_) {
        UrushiUtils.setInfo(list,"hibachi");
         }



}
