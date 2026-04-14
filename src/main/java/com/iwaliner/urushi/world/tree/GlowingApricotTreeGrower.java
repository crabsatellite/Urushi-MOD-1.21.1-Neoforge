package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class GlowingApricotTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:glowing_apricot",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.GLOWING_APRICOT_KEY),
            Optional.empty()
    );
}
