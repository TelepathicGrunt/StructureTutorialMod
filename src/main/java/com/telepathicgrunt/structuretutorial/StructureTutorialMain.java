package com.telepathicgrunt.structuretutorial;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StructureTutorialMain.MODID)
public class StructureTutorialMain {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    // mod ID to reference to from anywhere in mod
    public static final String MODID = "structure_tutorial";

    public StructureTutorialMain() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        /*
         * Notice how we aren't using any proxies. If you find another tutorial that uses proxies
         * or asks you to use them, ignore that. Proxies are nearly entirely useless and only end up
         * making your code look a bit worse. Instead, do all your setup in the FMLCommonSetupEvent,
         * FMLClientSetupEvent, and FMLDedicatedServerSetupEvent.
         *
         * But mainly FMLCommonSetupEvent will be used as doing stuff strictly client side or server
         * side depends on specific cases. For example, purely graphical stuff will be under client
         * setup because servers won't have any graphics stuff.
         */

        /*
         * Note: There are quite a lot of final or private fields we will need to access.
         * To get and set values in these fields, we will need to use Access Transformers (AT).
         * Once you setup your AT file in META-INF, copy the entries from this turtorial's AT into yours
         * and then refresh gradle (or rebuild project) so that the project will apply the AT entries.
         * Forge docs on AT's: https://mcforge.readthedocs.io/en/latest/advanced/accesstransformers/
         */
    }

    /*
     * This is where you do all the manipulation and startup things you need to do for your mod. What is actually done here will be different for every mod
     * depending on what the mod is doing.
     * 
     * Here, we will use this to add our structure to all biomes.
     */
    public void setup(final FMLCommonSetupEvent event) {
        // Add our structure to all biomes including other modded biomes
        //
        // You can filter to certain biomes based on stuff like temperature, scale, precipitation, mod id,
        // and if you use the BiomeDictionary, you can even check if the biome has certain tags like swamp or snowy.
        for (Biome biome : ForgeRegistries.BIOMES) {
            // All structures needs to be added to biomes by func_235063_a_ which replaces .addStructure and .addFeature from 1.15.2
            // The function does not have a mapping at time of writing, but the most likely name for it is .addStructureFeature
            //
            // This function determines which biomes your structure will be able to spawn in

            biome.func_235063_a_(STFeatures.RUN_DOWN_HOUSE.func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG));
        }

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
        public static void onRegisterFeatures(final RegistryEvent.Register<Feature<?>> event) {
            // registers the structures/features.
            // If you don't do this, you'll crash.
            STFeatures.registerFeatures(event);

            LOGGER.log(Level.INFO, "features/structures registered.");
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
