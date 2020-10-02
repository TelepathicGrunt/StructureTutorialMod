package com.telepathicgrunt.structuretutorial;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StructureTutorialMain.MODID)
public class StructureTutorialMain {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    // mod ID to reference to from anywhere in mod
    public static final String MODID = "structure_tutorial";

    public StructureTutorialMain() {
    }


    // You can use EventBusSubscriber to automatically subscribe events on the contained class
    // (this is subscribing to the MOD Event bus for receiving Registry Events)

    /*
     * You will use this to register anything for your mod. The most common things you will register are blocks, items, biomes, entities, features, and
     * dimensions.
     */
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        /**
         * This method will be called by Forge when it is time for the mod to register features.
         */
        @SubscribeEvent
        public static void onRegisterStructures(final RegistryEvent.Register<Structure<?>> event) {
            // Registers the structures.
            // If you don't do this, bad things might happen... very bad things... Spooky...
            STStructures.registerStructures(event);
            STConfiguredStructures.registerConfiguredStructures();

            LOGGER.log(Level.INFO, "structures registered.");
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        /*
         * This is the event you will use to add anything to any biome.
         * This includes spawns, changing the biome's looks, messing with its surfacebuilders,
         * adding carvers, spawning new features... etc
         *
         * Here, we will use this to add our structure to all biomes.
         */
        @SubscribeEvent
        public static void biomeModification(final BiomeLoadingEvent event) {
            // Add our structure to all biomes including other modded biomes
            //
            // You can filter to certain biomes based on stuff like temperature, scale, precipitation, mod id

            event.getGeneration().getStructures().add(() -> STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE);
        }

        /*
         * Will go into the world's chunkgenerator and manually add our structure spacing.
         * If the spacing is not added, the structure doesn't spawn.
         * Use this for dimension blacklists for your structure!
         */
        @SubscribeEvent
        public static void addDimensionalSpacing(final WorldEvent.Load event) {
            if(event.getWorld() instanceof ServerWorld){
                ServerWorld serverWorld = (ServerWorld)event.getWorld();
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
                tempMap.put(STStructures.RUN_DOWN_HOUSE, DimensionStructuresSettings.field_236191_b_.get(STStructures.RUN_DOWN_HOUSE));
                serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
            }
       }
    }

    /*
     * Helper method to quickly register features, blocks, items, structures, biomes, anything that can be registered.
     */
    public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T entry, String registryKey) {
        entry.setRegistryName(new ResourceLocation(StructureTutorialMain.MODID, registryKey));
        registry.register(entry);
        return entry;
    }
}
