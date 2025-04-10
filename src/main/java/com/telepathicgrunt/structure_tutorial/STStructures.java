package com.telepathicgrunt.structure_tutorial;

import com.telepathicgrunt.structure_tutorial.structures.OceanStructures;
import com.telepathicgrunt.structure_tutorial.structures.SkyStructures;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureType;

public class STStructures {

    public static StructureType<SkyStructures> SKY_STRUCTURES;
    public static StructureType<OceanStructures> OCEAN_STRUCTURES;

    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the Identifier of structure_tutorial:sky_structures.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It's great for mod/datapacks compatibility.
     */
    public static void registerStructureFeatures() {
        SKY_STRUCTURES = Registry.register(Registries.STRUCTURE_TYPE, Identifier.of(StructureTutorialMain.MODID, "sky_structures"), () -> SkyStructures.CODEC);
        OCEAN_STRUCTURES = Registry.register(Registries.STRUCTURE_TYPE, Identifier.of(StructureTutorialMain.MODID, "ocean_structures"), () -> OceanStructures.CODEC);
    }
}
