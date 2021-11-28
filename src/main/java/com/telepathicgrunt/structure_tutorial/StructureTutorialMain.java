package com.telepathicgrunt.structure_tutorial;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.telepathicgrunt.structure_tutorial.mixin.StructuresConfigAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.impl.structure.FabricStructureImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.chunk.StructureConfig;
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
     * The BIOME BASED STRUCTURE section below is needed to spawn structures in biomes in 1.18
     * until Fabric API updates and adds a new better way.
     */
    public static void addStructureSpawningToDimensionsAndBiomes() {
        // This is for making sure our ServerWorldEvents.LOAD event always fires after Fabric API's usage of the same event.
        // This is done so our changes don't get overwritten by Fabric API adding structure spacings to all dimensions.
        // Requires Fabric API v0.42.0  or newer.
        Identifier runAfterFabricAPIPhase = new Identifier(StructureTutorialMain.MODID, "run_after_fabric_api");
        ServerWorldEvents.LOAD.addPhaseOrdering(Event.DEFAULT_PHASE, runAfterFabricAPIPhase);

        ServerWorldEvents.LOAD.register(runAfterFabricAPIPhase, (MinecraftServer minecraftServer, ServerWorld serverWorld) -> {
            // We will need this a lot lol
            StructuresConfig worldStructureConfig = serverWorld.getChunkManager().getChunkGenerator().getStructuresConfig();

            //////////// BIOME BASED STRUCTURE SPAWNING ////////////
            /*
             * NOTE: BiomeModifications from Fabric API does not work in 1.18 currently.
             * Instead, we will use the below to add our structure to overworld biomes.
             * Remember, this is temporary until Fabric API finds a better solution for adding structures to biomes.
             */

            // Grab the map that holds what ConfigureStructures a structure has and what biomes it can spawn in.
            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> tempStructureToMultiMap = ImmutableMap.builder();
            ((StructuresConfigAccessor) worldStructureConfig).getConfiguredStructures().entrySet().forEach(tempStructureToMultiMap::put);


            // Create the multimap of Configured Structures to biomes we will need.
            ImmutableMultimap.Builder<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> tempConfiguredStructureBiomeMultiMap = ImmutableMultimap.builder();

            // Add the registrykey of all biomes that this Configured Structure can spawn in.
            for(Map.Entry<RegistryKey<Biome>, Biome> biomeEntry : minecraftServer.getRegistryManager().getMutable(Registry.BIOME_KEY).getEntries()) {
                // Skip all ocean, end, nether, and none category biomes.
                // You can do checks for other traits that the biome has.
                Biome.Category biomeCategory = biomeEntry.getValue().getCategory();
                if(biomeCategory != Biome.Category.OCEAN && biomeCategory != Biome.Category.THEEND && biomeCategory != Biome.Category.NETHER && biomeCategory != Biome.Category.NONE) {
                    tempConfiguredStructureBiomeMultiMap.put(STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE, biomeEntry.getKey());
                }
            }

            // Alternative way to add our structures to a fixed set of biomes by creating a set of biome registry keys.
            // To create a custom registry key that points to your own biome, do this:
            // RegistryKey.of(Registry.BIOME_KEY, new Identifier("modid", "custom_biome"))
//            ImmutableSet<RegistryKey<Biome>> overworldBiomes = ImmutableSet.<RegistryKey<Biome>>builder()
//                    .add(BiomeKeys.FOREST)
//                    .add(BiomeKeys.MEADOW)
//                    .add(BiomeKeys.PLAINS)
//                    .add(BiomeKeys.SAVANNA)
//                    .add(BiomeKeys.SNOWY_PLAINS)
//                    .add(BiomeKeys.SWAMP)
//                    .add(BiomeKeys.SUNFLOWER_PLAINS)
//                    .add(BiomeKeys.TAIGA)
//                    .build();
//            overworldBiomes.forEach(biomeKey -> tempConfiguredStructureBiomeMultiMap.put(STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE, biomeKey));

            // Add the base structure to associate with this new multimap of Configured Structures to biomes to spawn in.
            tempStructureToMultiMap.put(STStructures.RUN_DOWN_HOUSE, tempConfiguredStructureBiomeMultiMap.build());

            ((StructuresConfigAccessor) worldStructureConfig).setConfiguredStructures(tempStructureToMultiMap.build());


            //////////// DIMENSION BASED STRUCTURE SPAWNING (OPTIONAL) ////////////
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

        });
    }
}
