package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class YellowTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:yellow",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.YELLOW_KEY),
            Optional.empty()
    );
}
