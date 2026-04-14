package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class ApricotTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:apricot",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.APRICOT_KEY),
            Optional.empty()
    );
}
