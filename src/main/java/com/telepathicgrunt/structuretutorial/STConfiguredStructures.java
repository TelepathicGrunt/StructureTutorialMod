package com.telepathicgrunt.structuretutorial;

import net.minecraft.util.ResourceLocation;
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
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(StructureTutorialMain.MODID, "configured_run_down_house"), CONFIGURED_RUN_DOWN_HOUSE);


        // Ok so, this part may be hard to grasp but basically, just add your structure to this to
        // prevent any sort of crash or issue with people accessing superflat worldtype even when
        // your structure isn't spawning in it.
        //
        // In theory, you could remove this line and keep the FlatChunkGenerator check in
        // StructureTutorialMain.addDimensionalSpacing and superflat worlds should be ok.
        // But if another mod adds your structure's spacing to the world when you didn't
        // do this line below, it'll crash. Best to leave this line in for mod compat and
        // to prevent any weird edge case problems with superflat worldtype. (smh buggy mess Mojang)
        //
        // Note: If you want your structure to spawn in superflat, remove the FlatChunkGenerator check
        // in StructureTutorialMain.addDimensionalSpacing and then create a superflat world, exit it,
        // and re-enter it and your structures will be spawning. I could not figure out why it needs
        // the restart but honestly, superflat is really buggy and shouldn't be you main focus in my opinion.
        FlatGenerationSettings.STRUCTURES.put(STStructures.RUN_DOWN_HOUSE, CONFIGURED_RUN_DOWN_HOUSE);
    }
}
