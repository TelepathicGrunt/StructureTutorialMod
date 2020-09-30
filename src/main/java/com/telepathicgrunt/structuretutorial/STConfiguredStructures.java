package com.telepathicgrunt.structuretutorial;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class STConfiguredStructures {
    // Static instance of our structure so we can reference it and add it to biomes easily.
    public static StructureFeature<?, ?> CONFIGURED_RUN_DOWN_HOUSE = STStructures.RUN_DOWN_HOUSE.func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG);

    /*
     * Registers the configured structure which is what gets added to the biomes.
     * Noticed we are not using a forge registry because there is none for configured structures
     */
    public static void registerConfiguredStructures() {
        MutableRegistry<StructureFeature<?, ?>> registry = (MutableRegistry<StructureFeature<?, ?>>) WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(StructureTutorialMain.MODID, "configured_run_down_house"), CONFIGURED_RUN_DOWN_HOUSE);


        // Make it so it can spawn in superflat mode with the customize string thing. (I haven't tried it)
        FlatGenerationSettings.STRUCTURES.put(STStructures.RUN_DOWN_HOUSE, CONFIGURED_RUN_DOWN_HOUSE);
    }
}
