package com.telepathicgrunt.structure_tutorial;

import net.minecraft.structure.PlainsVillageData;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class STConfiguredStructures {
    /**
     * Static instance of our configured structure so we can reference it and add it to biomes easily.
     */
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_RUN_DOWN_HOUSE = STStructures.RUN_DOWN_HOUSE.configure(new StructurePoolFeatureConfig(

        // Dummy values for now. We will modify the pool at runtime since we cannot get json pool files here at mod init.
        // You can create and register your pools in code, pass in the code create pool here, and delete line 115 in RunDownHouseStructure
        () -> PlainsVillageData.STRUCTURE_POOLS,

        // We will set size at runtime too as StructurePoolFeatureConfig will not handle sizes above 7.
        // If your size is below 7, you can set the size here and delete line 130 in RunDownHouseStructure
        0

        /*
         * The only reason we are using StructurePoolFeatureConfig here is because in RunDownHouseStructure's createPiecesGenerator method,
         * we are using StructurePoolBasedGenerator.generate which requires StructurePoolFeatureConfig. However, if you create your own
         * StructurePoolBasedGenerator.generate, you could reduce the amount of workarounds like above that you need and give yourself more
         * opportunities and control over your structures.
         *
         * An example of a custom StructurePoolBasedGenerator.generate in action can be found here (warning, it is using Mojmap mappings):
         * https://github.com/TelepathicGrunt/RepurposedStructures-Fabric/blob/1.18/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/PieceLimitedJigsawManager.java
         */
    ));

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
