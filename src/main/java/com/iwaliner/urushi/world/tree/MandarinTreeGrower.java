package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class MandarinTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:mandarin",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.MANDARIN_KEY),
            Optional.empty()
    );
}
