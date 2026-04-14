package com.iwaliner.urushi.entiity.food;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import com.iwaliner.urushi.EntityRegister;
import com.iwaliner.urushi.ItemAndBlockRegister;

public class GreenOnionAndRawEggGyudonFoodEntity extends FoodEntity {

    public GreenOnionAndRawEggGyudonFoodEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(ItemAndBlockRegister.green_onion_and_raw_egg_gyudon.get(), EntityRegister.GreenOnionAndRawEggGyudonFoodEntity.get(), p_i48580_2_);
    }


}
