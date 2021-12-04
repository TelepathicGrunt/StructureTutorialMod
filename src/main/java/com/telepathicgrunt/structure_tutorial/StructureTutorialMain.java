package com.telepathicgrunt.structure_tutorial;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.telepathicgrunt.structure_tutorial.mixin.StructuresConfigAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class StructureTutorialMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "structure_tutorial";

    @Override
    public void onInitialize() {

        /*
         * We setup and register our structures here.
         * You should always register your stuff to prevent mod compatibility issue down the line.
         */
        STStructures.setupAndRegisterStructureFeatures();
        STConfiguredStructures.registerConfiguredStructures();
        addStructureSpawningToDimensionsAndBiomes();

        /*
         * NOTE: BiomeModifications from Fabric API does not work for Structures in 1.18 currently.
         * See addStructureSpawningToDimensionsAndBiomes method for how to spawn structures in biomes
         * The BiomeModifications API still works for Features tho and mobs and other stuff.
         *
         * This is the API you will use to add anything to any biome.
         * This includes spawns, changing the biome's looks, messing with its surfacebuilders,
         * adding carvers, spawning new features... etc
         *
         * Make sure you give this an identifier to make it clear later what mod did a change and why.
         * It'll help people look to see if your mod was removing something from biomes.
         * The biome modifier identifier might also be used by modpacks to disable mod's modifiers too for customization.
         */
//        BiomeModifications.create(new Identifier(MODID, "run_down_house_addition"))
//                .add(   // Describes what we are doing. SInce we are adding a structure, we choose ADDITIONS.
//                        ModificationPhase.ADDITIONS,
//
//                        // Add our structure to all biomes including other modded biomes.
//                        // You can filter to certain biomes based on stuff like temperature, scale, precipitation, mod id.
//                        BiomeSelectors.all(),
//
//                        // context is basically the biome itself. This is where you do the changes to the biome.
//                        // Here, we will add our ConfiguredStructureFeature to the biome.
//                        context -> {
//                            context.getGenerationSettings().addBuiltInStructure(STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE);
//                        });
    }

    /**
     * used for spawning our structures in biomes.
     * You can move the BiomeModification API anywhere you prefer it to be at.
     * Just make sure you call BiomeModifications.addStructure at mod init.
     */
    public static void addStructureSpawningToDimensionsAndBiomes() {

        /*
         * This is the API you will use to add anything to any biome.
         * This includes spawns, changing the biome's looks, messing with its surfacebuilders,
         * adding carvers, spawning new features... etc
         */
        BiomeModifications.addStructure(
                // Add our structure to all biomes that have any of these biome categories. This includes modded biomes.
                // You can filter to certain biomes based on stuff like temperature, scale, precipitation, mod id, etc.
                // See BiomeSelectors's methods for more options or write your own by doing `(context) -> context.whatever() == condition`
                BiomeSelectors.categories(
                        Biome.Category.DESERT,
                        Biome.Category.EXTREME_HILLS,
                        Biome.Category.FOREST,
                        Biome.Category.ICY,
                        Biome.Category.JUNGLE,
                        Biome.Category.PLAINS,
                        Biome.Category.SAVANNA,
                        Biome.Category.TAIGA),
                // The registry key of our ConfiguredStructure so BiomeModification API can grab it
                // later to tell the game which biomes that your structure can spawn within.
                RegistryKey.of(
                        Registry.CONFIGURED_STRUCTURE_FEATURE_KEY,
                        BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE))
        );

        //////////// DIMENSION BASED STRUCTURE SPAWNING (OPTIONAL) ////////////
//
//        // This is for making sure our ServerWorldEvents.LOAD event always fires after Fabric API's usage of the same event.
//        // This is done so our changes don't get overwritten by Fabric API adding structure spacings to all dimensions.
//        // Requires Fabric API v0.42.0  or newer.
//        Identifier runAfterFabricAPIPhase = new Identifier(StructureTutorialMain.MODID, "run_after_fabric_api");
//        ServerWorldEvents.LOAD.addPhaseOrdering(Event.DEFAULT_PHASE, runAfterFabricAPIPhase);
//
//        ServerWorldEvents.LOAD.register(runAfterFabricAPIPhase, (MinecraftServer minecraftServer, ServerWorld serverWorld) -> {
//            // Skip superflat to prevent issues with it. Plus, users don't want structures clogging up their superflat worlds.
//            if (serverWorld.getChunkManager().getChunkGenerator() instanceof FlatChunkGenerator && serverWorld.getRegistryKey().equals(World.OVERWORLD)) {
//                return;
//            }
//
//            StructuresConfig worldStructureConfig = serverWorld.getChunkManager().getChunkGenerator().getStructuresConfig();
//
//            // Controls the dimension blacklisting and/or whitelisting
//            // If the spacing or our structure is not added for a dimension, the structure doesn't spawn in that dimension.
//            // Note: due to a quirk with how Noise Settings are shared between dimensions, you need this mixin to make a
//            // deep copy of the noise setting per dimension for your dimension whitelisting/blacklisting to work properly:
//            // https://github.com/TelepathicGrunt/RepurposedStructures-Fabric/blob/1.18/src/main/java/com/telepathicgrunt/repurposedstructures/mixin/world/ChunkGeneratorMixin.java
//
//            // Need temp map as some mods use custom chunk generators with immutable maps in themselves.
//            Map<StructureFeature<?>, StructureConfig> tempMap = new HashMap<>(worldStructureConfig.getStructures());
//
//            // Make absolutely sure modded dimension can or cannot spawn our structures.
//            // New dimensions under the minecraft namespace will still get it (datapacks might do this)
//            if(serverWorld.getRegistryKey().equals(World.OVERWORLD)) {
//                tempMap.put(STStructures.RUN_DOWN_HOUSE, FabricStructureImpl.STRUCTURE_TO_CONFIG_MAP.get(STStructures.RUN_DOWN_HOUSE));
//            }
//            else {
//                tempMap.remove(STStructures.RUN_DOWN_HOUSE);
//            }
//
//            // Set the new modified map of structure spacing to the dimension's chunkgenerator.
//            ((StructuresConfigAccessor)worldStructureConfig).setStructures(tempMap);
//
//        });
    }

    /**
     * Helper method that handles setting up the map to multimap relationship to help prevent issues.
     */
    private static void associateBiomeToConfiguredStructure(Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> STStructureToMultiMap, ConfiguredStructureFeature<?, ?> configuredStructureFeature, RegistryKey<Biome> biomeRegistryKey) {
        STStructureToMultiMap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
        HashMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> configuredStructureToBiomeMultiMap = STStructureToMultiMap.get(configuredStructureFeature.feature);
        if(configuredStructureToBiomeMultiMap.containsValue(biomeRegistryKey)) {
            StructureTutorialMain.LOGGER.error("""
                    Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome. One will be prevented from spawning.
                    This issue happens with vanilla too and is why a Snowy Village and Plains Village cannot spawn in the same biome because they both use the Village base structure.
                    The two conflicting ConfiguredStructures are: {}, {}
                    The biome that is attempting to be shared: {}
                """,
                BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureFeature),
                BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureToBiomeMultiMap.entries().stream().filter(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()),
                biomeRegistryKey
            );
        }
        else{
            configuredStructureToBiomeMultiMap.put(configuredStructureFeature, biomeRegistryKey);
        }
    }
}
