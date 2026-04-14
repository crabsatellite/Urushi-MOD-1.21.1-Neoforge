package com.iwaliner.urushi.world.tree;

import net.minecraft.world.level.block.grower.TreeGrower;
import com.iwaliner.urushi.ConfiguredFeatureRegister;

import java.util.Optional;

public class GlowingSakuraTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower(
            "urushi:glowing_sakura",
            Optional.empty(),
            Optional.of(ConfiguredFeatureRegister.GLOWING_SAKURA_KEY),
            Optional.empty()
    );
}
