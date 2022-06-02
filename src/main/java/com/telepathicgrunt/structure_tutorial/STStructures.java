package com.telepathicgrunt.structure_tutorial;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.structure_tutorial.structures.SkyStructures;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

public class STStructures {

    public static StructureType<?> SKY_STRUCTURES;

    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the Identifier of structure_tutorial:sky_structures.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It's great for mod/datapacks compatibility.
     */
    public static void registerStructureFeatures() {
        SKY_STRUCTURES = register(new Identifier(StructureTutorialMain.MODID, "sky_structures"), SkyStructures.CODEC);
    }

    // Helper method to register since compiler will complain about typing if we did () -> SkyStructures.CODEC directly.
    private static <S extends StructureFeature> StructureType<S> register(Identifier id, Codec<S> codec) {
        return Registry.register(Registry.STRUCTURE_TYPE, id, () -> codec);
    }
}
