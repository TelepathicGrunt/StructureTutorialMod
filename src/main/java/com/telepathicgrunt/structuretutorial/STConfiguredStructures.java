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


        // Need to add this to superflat's special list so that superflat worldtype will not crash
        // when you are entering an existing superflat world. (Even if your structure isn't spawning it in).
        // Note: your structure cannot spawn in superflat without hacks because BiomeModificationEvent
        // does not fire for superflat's biomes that it makes when you switch to that worldtype.
        FlatGenerationSettings.STRUCTURES.put(STStructures.RUN_DOWN_HOUSE, CONFIGURED_RUN_DOWN_HOUSE);
    }
}
