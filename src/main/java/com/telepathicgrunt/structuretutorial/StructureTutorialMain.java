package com.telepathicgrunt.structuretutorial;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StructureTutorialMain.MODID)
public class StructureTutorialMain {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "structure_tutorial";

    public StructureTutorialMain(IEventBus modEventBus) {
        // For registration and init stuff.
        STStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        STStructurePlacements.DEFERRED_REGISTRY_STRUCTURE_PLACEMENT_TYPE.register(modEventBus);
    }
}
