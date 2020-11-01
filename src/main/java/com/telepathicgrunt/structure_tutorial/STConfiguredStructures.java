package com.telepathicgrunt.structure_tutorial;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class STConfiguredStructures {
    /**
     * Static instance of our configured structure so we can reference it and add it to biomes easily.
     */
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_RUN_DOWN_HOUSE = STStructures.RUN_DOWN_HOUSE.configure(DefaultFeatureConfig.DEFAULT);

    /**
     * Registers the configured structure which is what gets added to the biomes.
     * You can use the same identifier for the configured structure as the regular structure
     * because the two fo them are registered to different registries.
     *
     * We can register configured structures at any time before a world is clicked on and made.
     * But the best time to register configured features by code is honestly to do it in onInitialize.
     */
    public static void registerConfiguredStructures() {
        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new Identifier(StructureTutorialMain.MODID, "configured_run_down_house"), CONFIGURED_RUN_DOWN_HOUSE);
    }
}
