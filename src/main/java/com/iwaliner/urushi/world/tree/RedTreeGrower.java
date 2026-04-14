package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class RedTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:red",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.RED_KEY),
            Optional.empty()
    );
}
