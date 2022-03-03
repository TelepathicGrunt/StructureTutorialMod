package com.telepathicgrunt.structure_tutorial;

import com.telepathicgrunt.structure_tutorial.mixin.StructureFeatureAccessor;
import com.telepathicgrunt.structure_tutorial.structures.SkyStructures;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

public class STStructures {

    /**
    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the Identifier of structure_tutorial:sky_structures.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It great for mod/datapacks compatibility.
     */
    public static StructureFeature<?> SKY_STRUCTURES = new SkyStructures();

    public static void registerStructureFeatures() {
        // The generation step for when to generate the structure. there are 10 stages you can pick from!
        // This surface structure stage places the structure before plants and ores are generated.
        StructureFeatureAccessor.callRegister(StructureTutorialMain.MODID + ":sky_structures", SKY_STRUCTURES, GenerationStep.Feature.SURFACE_STRUCTURES);
    }
}
