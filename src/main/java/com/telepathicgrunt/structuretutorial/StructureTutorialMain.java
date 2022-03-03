package com.telepathicgrunt.structuretutorial;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StructureTutorialMain.MODID)
public class StructureTutorialMain {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "structure_tutorial";

    public StructureTutorialMain() {
        // For registration and init stuff.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        STStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
    }
}
