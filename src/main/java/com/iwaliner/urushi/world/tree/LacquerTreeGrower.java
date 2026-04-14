package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class LacquerTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:lacquer",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.LACQUER_KEY),
            Optional.empty()
    );
}
