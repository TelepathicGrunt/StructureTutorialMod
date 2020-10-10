package com.telepathicgrunt.structuretutorial;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
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
        // For registering and other initialization stuff.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(Structure.class, this::onRegisterStructures);


        // For events that happen after initialization. This is probably going to be use a lot.
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);

        // The comments for BiomeLoadingEvent and StructureSpawnListGatherEvent says to do HIGH for additions.
        forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
    }


    /**
     * This method will be called by Forge when it is time for the mod to register features.
     */
    public void onRegisterStructures(final RegistryEvent.Register<Structure<?>> event) {
        // Registers the structures.
        // If you don't do this, bad things might happen... very bad things... Spooky...
        STStructures.registerStructures(event);
        STConfiguredStructures.registerConfiguredStructures();

        //LOGGER.log(Level.INFO, "structures registered.");
    }

    /**
     * This is the event you will use to add anything to any biome.
     * This includes spawns, changing the biome's looks, messing with its surfacebuilders,
     * adding carvers, spawning new features... etc
     *
     * Here, we will use this to add our structure to all biomes.
     */
    public void biomeModification(final BiomeLoadingEvent event) {
        // Add our structure to all biomes including other modded biomes
        //
        // You can filter to certain biomes based on stuff like temperature, scale, precipitation, mod id

        event.getGeneration().getStructures().add(() -> STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE);
    }

    /**
     * Will go into the world's chunkgenerator and manually add our structure spacing.
     * If the spacing is not added, the structure doesn't spawn.
     *
     * Use this for dimension blacklists for your structure.
     * (Don't forget to attempt to remove your structure too from
     *  the map if you are blacklisting that dimension! It might have
     *  your structure in it already.)
     *
     * Basically use this to mak absolutely sure the chunkgeneration
     * can or cannot spawn your structure.
     */
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld)event.getWorld();
            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
            tempMap.put(STStructures.RUN_DOWN_HOUSE, DimensionStructuresSettings.field_236191_b_.get(STStructures.RUN_DOWN_HOUSE));
            serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
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
