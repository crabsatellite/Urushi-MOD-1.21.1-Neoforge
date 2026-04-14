package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class OrangeTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:orange",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.ORANGE_KEY),
            Optional.empty()
    );
}
