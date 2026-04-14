package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class SakuraTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:sakura",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.SAKURA_KEY),
            Optional.empty()
    );
}
