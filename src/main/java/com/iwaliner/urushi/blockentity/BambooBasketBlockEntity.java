package com.iwaliner.urushi.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.iwaliner.urushi.BlockEntityRegister;

public  class BambooBasketBlockEntity extends RandomizableContainerBlockEntity  {


    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    public BambooBasketBlockEntity(BlockPos p_155052_, BlockState p_155053_) {
        super(BlockEntityRegister.BambooBasket.get(), p_155052_, p_155053_);
    }
    public void loadAdditional(CompoundTag p_155025_, HolderLookup.Provider registries) {
        super.loadAdditional(p_155025_, registries);
        this.items.clear();
        ContainerHelper.loadAllItems(p_155025_, this.items, registries);
    }

    protected void saveAdditional(CompoundTag p_187452_, HolderLookup.Provider registries) {
        super.saveAdditional(p_187452_, registries);
       ContainerHelper.saveAllItems(p_187452_, this.items,true, registries);
    }
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag compoundtag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundtag, this.items, true, registries);
        return compoundtag;
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    protected Component getDefaultName() {
        return Component.translatable("container.bamboo_basket");
    }


    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
        return null;
    }


    public int getContainerSize() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }


    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return ContainerHelper.removeItem(this.items, i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.items, i);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.markUpdated();
        ItemStack itemstack = this.items.get(slot);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }




    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }


    public void setItems(NonNullList<ItemStack> p_199721_1_) {
        this.items = p_199721_1_;
    }


    public void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean canPlaceItem(int slot) {
            return this.items.get(slot).getCount()==0;

    }
    public ItemStack pickItem(int slot) {
        this.markUpdated();
        ItemStack stack= this.getItem(slot).copy();
        this.setItem(slot,ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    public int getItemsSize(){
        return items.size();
    }


}
