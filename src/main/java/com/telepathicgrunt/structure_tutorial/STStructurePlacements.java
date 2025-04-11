package com.telepathicgrunt.structure_tutorial;

import com.telepathicgrunt.structure_tutorial.structureplacement.DistanceBasedStructurePlacement;
import com.telepathicgrunt.structure_tutorial.structures.EndIslandStructures;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.structure.StructureType;

public class STStructurePlacements {

    public static StructurePlacementType<DistanceBasedStructurePlacement> DISTANCE_BASED_STRUCTURE_PLACEMENT;

    /**
     * Registers the structure placement type itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:distance_based_structure_placement.
     */
    public static void registerStructurePlacementTypes() {
        DISTANCE_BASED_STRUCTURE_PLACEMENT  = Registry.register(Registries.STRUCTURE_PLACEMENT, Identifier.of(StructureTutorialMain.MODID, "distance_based_structure_placement"), () -> DistanceBasedStructurePlacement.CODEC);
    }
}
