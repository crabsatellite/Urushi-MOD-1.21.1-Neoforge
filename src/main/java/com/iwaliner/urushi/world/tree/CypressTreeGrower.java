package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class CypressTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:cypress",
            Optional.of(ConfiguredFeatureRegister.MEGA_CYPRESS_KEY),
            Optional.of(ConfiguredFeatureRegister.CYPRESS_KEY),
            Optional.empty()
    );
}
