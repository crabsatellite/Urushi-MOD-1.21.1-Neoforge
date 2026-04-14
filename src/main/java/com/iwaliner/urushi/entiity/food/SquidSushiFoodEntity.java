package com.iwaliner.urushi.entiity.food;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import com.iwaliner.urushi.EntityRegister;
import com.iwaliner.urushi.ItemAndBlockRegister;

public class SquidSushiFoodEntity extends FoodEntity {

    public SquidSushiFoodEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(ItemAndBlockRegister.squid_sushi.get(), EntityRegister.SquidSushiFoodEntity.get(), p_i48580_2_);
    }


}
