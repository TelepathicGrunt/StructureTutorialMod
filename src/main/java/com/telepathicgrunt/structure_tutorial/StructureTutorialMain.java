package com.telepathicgrunt.structure_tutorial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class StructureTutorialMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "structure_tutorial";

    @Override
    public void onInitialize(ModContainer mod) {

        /*
         * We setup and register our structures here.
         * You should always register your stuff to prevent mod compatibility issue down the line.
         */
        STStructures.registerStructureFeatures();
    }
}
