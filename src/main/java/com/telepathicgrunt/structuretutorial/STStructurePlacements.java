package com.telepathicgrunt.structuretutorial;

import com.mojang.serialization.MapCodec;
import com.telepathicgrunt.structuretutorial.structureplacement.DistanceBasedStructurePlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class STStructurePlacements {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on NeoForge.
     * This will handle registering the structure placement type for us at the correct time so we don't have to handle it ourselves.
     */
    public static final DeferredRegister<StructurePlacementType<?>> DEFERRED_REGISTRY_STRUCTURE_PLACEMENT_TYPE = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, StructureTutorialMain.MODID);

    /**
     * Registers the structure placement type  itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:distance_based_structure_placement.
     */
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<DistanceBasedStructurePlacement>> DISTANCE_BASED_STRUCTURE_PLACEMENT = DEFERRED_REGISTRY_STRUCTURE_PLACEMENT_TYPE.register("distance_based_structure_placement", () -> explicitStructureTypeTyping(DistanceBasedStructurePlacement.CODEC));

    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private static <T extends StructurePlacement> StructurePlacementType<T> explicitStructureTypeTyping(MapCodec<T> structurePlacementTypeCodec) {
        return () -> structurePlacementTypeCodec;
    }
}
