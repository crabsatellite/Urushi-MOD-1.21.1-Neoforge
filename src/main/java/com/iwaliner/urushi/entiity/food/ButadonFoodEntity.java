package com.iwaliner.urushi.entiity.food;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import com.iwaliner.urushi.EntityRegister;
import com.iwaliner.urushi.ItemAndBlockRegister;

public class ButadonFoodEntity extends FoodEntity {

    public ButadonFoodEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(ItemAndBlockRegister.butadon.get(), EntityRegister.ButadonFoodEntity.get(), p_i48580_2_);
    }


}
