package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class CedarTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:cedar",
            Optional.of(ConfiguredFeatureRegister.MEGA_CEDAR_KEY),
            Optional.of(ConfiguredFeatureRegister.CEDAR_KEY),
            Optional.empty()
    );
}
