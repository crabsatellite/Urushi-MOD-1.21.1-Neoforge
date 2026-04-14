package com.iwaliner.urushi.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.iwaliner.urushi.util.UrushiUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NormalKatanaItem extends SwordItem {
    public NormalKatanaItem(Tier tier,int i,float f, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, i, f)));
    }
    @Override
    public boolean canAttackBlock(BlockState p_41441_, Level p_41442_, BlockPos p_41443_, Player player) {
        return !player.isCreative();
    }
    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_41422_, List<Component> list, TooltipFlag p_41424_) {
        UrushiUtils.setInfo(list,"katana1");
        UrushiUtils.setInfo(list,"katana2");
    }
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        }
        return 1.0F;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity living, LivingEntity entity) {
        stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(2, living, EquipmentSlot.MAINHAND);
        }

        return true; }


    public boolean isCorrectToolForDrops(BlockState p_150897_1_) {
        return p_150897_1_.is(Blocks.COBWEB);
    }
   @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        boolean flag=player.onGround();
        float a=flag?2F:1F;
        double f = -Math.sin(player.getYRot() * ((float)Math.PI / 180F)) * Math.cos(player.getXRot() * ((float)Math.PI / 180F));
        double f1 = -Math.sin((player.getXRot() + 0f) * ((float)Math.PI / 180F));
        double f2 = Math.cos(player.getYRot() * ((float)Math.PI / 180F)) * Math.cos(player.getXRot() * ((float)Math.PI / 180F));
        Vec3 vector3d = (new Vec3((double)f*a, (double)f1*a*0.4D, (double)f2*a));
        player.getCooldowns().addCooldown(this, 10);
        player.startUsingItem(hand);
        player.setDeltaMovement(vector3d);
        player.getItemInHand(hand).hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        AABB axisalignedbb =player.getBoundingBox() .inflate(4.0D, 4.0D, 4.0D);
        List<LivingEntity> list = player.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if(!list.isEmpty()) {
            for (LivingEntity entity : list) {
                if(entity instanceof Player) {
                }else{
                    float base = ((float) player.getAttributeValue(Attributes.ATTACK_DAMAGE)) * 0.5F;
                    float damage = base;
                    if (level instanceof ServerLevel sl) {
                        damage = EnchantmentHelper.modifyDamage(sl, player.getItemInHand(hand), entity, entity.damageSources().playerAttack(player), base);
                    }
                    entity.hurt(entity.damageSources().playerAttack(player), damage);
                    player.level().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.5F, 1F);
                    int fireLvl = EnchantmentHelper.getEnchantmentLevel(
                            player.level().registryAccess()
                                    .registryOrThrow(Registries.ENCHANTMENT)
                                    .getHolderOrThrow(Enchantments.FIRE_ASPECT),
                            player);
                    if (fireLvl > 0) {
                        entity.igniteForSeconds(fireLvl * 4);
                    }
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }



    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity living, InteractionHand hand) {
        float base = ((float) player.getAttributeValue(Attributes.ATTACK_DAMAGE)) * 0.5F;
        float damage = base;
        if (player.level() instanceof ServerLevel sl) {
            damage = EnchantmentHelper.modifyDamage(sl, player.getItemInHand(hand), living, living.damageSources().playerAttack(player), base);
        }
        living.hurt(living.damageSources().playerAttack(player), damage);
        this.use(player.level(),player,hand);
            player.level().playSound((Player) null, living.getX(), living.getY(), living.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.5F, 1F);
            return InteractionResult.SUCCESS;
        }



}
