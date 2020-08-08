package com.telepathicgrunt.structuretutorial.world.features;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.telepathicgrunt.structuretutorial.StructureTutorialMain;
import com.telepathicgrunt.structuretutorial.world.features.structures.RunDownHousePieces;
import com.telepathicgrunt.structuretutorial.world.features.structures.RunDownHouseStructure;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;

/*
 *  All methods in the FeatureInit class run during the forge mod event bus's register features event.
 */
public class FeatureInit {
    // Static instance of our structure so we can reference it and add it to biomes easily.
    public static Structure<NoFeatureConfig> RUN_DOWN_HOUSE = new RunDownHouseStructure(NoFeatureConfig.field_236558_a_);
    public static IStructurePieceType RDHP = RunDownHousePieces.Piece::new;

    /*
     * Registers the structure itself and sets what its path is. In this case, the structure will have the resourcelocation of
     * structure_tutorial:run_down_house.
     * 
     * This is also where the rarity of your structure is set. See the comments in below in new StructureSeparationSettings for details.
     * 
     * This is also where you would register normal Features.
     * 
     * It is always a good idea to register your regular features too so that other mods can use them too directly from the Forge Registry. It great for mod
     * compatibility.
     */
    public static void registerFeatures(Register<Feature<?>> event) {
        /*
         * You may notice we aren't using the Forge registry. This is because in 1.16+, Structure no longer
         * inherits from Feature. So we will register them directly into the Vanilla registry which is 100% okay!
         */


        /*
         * IMPORTANT: Once you have set the name for your structure below and distributed your mod, it should NEVER be changed or else it can cause worlds to
         * become corrupted if they generated any chunks with your mod with the old structure name. See MC-194811 in Mojang's bug tracker for details.
         */
        registerStructure(
                new ResourceLocation(StructureTutorialMain.MODID, "run_down_house"), /* the registry name of the structure. Always attach your mod's ID */
                RUN_DOWN_HOUSE, /* The instance of the structure */
                GenerationStage.Decoration.SURFACE_STRUCTURES, /* Generation stage for when to generate. This stage places the structure before plants are placed */
                new StructureSeparationSettings(10 /* maximum distance apart in chunks between spawn attempts */,
                        5 /* minimum distance apart in chunks between spawn attempts */,
                        1234567890 /* this modifies the seed of the structure so no two structures always spawn over each-other. Make this large and unique. */),
                true);


        FeatureInit.registerAllPieces();
    }

    /*
     * Adds the provided structure to the registry, and adds the separation settings.
     * The rarity of the structure is determined based on the values passed into
     * this method in the structureSeparationSettings argument. Called by registerFeatures.
     */
    public static <F extends Structure<NoFeatureConfig>> void registerStructure(
            ResourceLocation resourceLocation,
            F structure,
            GenerationStage.Decoration stage,
            StructureSeparationSettings structureSeparationSettings,
            boolean transformSurroundingLand)
    {
        // Registers the Structure and makes it able to be spawned in superflat mode with the customize string thing. (I haven't tried it)
        structure.setRegistryName(resourceLocation);
        addToStructureInfoMaps(resourceLocation.toString(), structure, stage);
        FlatGenerationSettings.STRUCTURES.put(structure, structure.func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG));


        /*
         * Will add land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically.
         */
        if(transformSurroundingLand){
            Structure.field_236384_t_ =
                    ImmutableList.<Structure<?>>builder()
                            .addAll(Structure.field_236384_t_)
                            .add(structure)
                            .build();
        }

        /*
         * Adds the structure's spacing into several places so that the structure's spacing remains
         * correct in any dimension or worldtype instead of defaulting to spawning every single chunk.
         */
        DimensionStructuresSettings.field_236191_b_ =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.field_236191_b_)
                        .put(structure, structureSeparationSettings)
                        .build();

        DimensionSettings.Preset.OVERWORLD.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings);
        DimensionSettings.Preset.NETHER.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings);
        DimensionSettings.Preset.END.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings);
        DimensionSettings.Preset.FLOATING_ISLANDS.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings);
        DimensionSettings.Preset.AMPLIFIED.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings);
        DimensionSettings.Preset.CAVES.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings);
        DimensionSettings.Preset.PRESET_MAP.forEach((presetResourceLocation, preset) -> preset.getSettings().getStructures().func_236195_a_().put(structure, structureSeparationSettings));
    }

    /*
     * The structure class keeps maps of all the structures and their generation stages. We need to add our structures into the maps along with the vanilla
     * structures or else it will cause errors. Called by registerStructure.
     */
    private static <F extends Structure<?>> F addToStructureInfoMaps(String name, F structure, GenerationStage.Decoration generationStage) {
        Structure.field_236365_a_.put(name.toLowerCase(Locale.ROOT), structure);
        Structure.field_236385_u_.put(structure, generationStage);
        return Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structure);
    }

    /*
     * If you have multiple structures it is helpful to break out the registering of the pieces. If you change the name you register the pieces with and load a
     * world from before the name was changed it will spam errors to the console, so pick a name you like before distributing your mod and don't change it.
     * Called by registerFeatures.
     */
    public static void registerAllPieces() {
        registerStructurePiece(RDHP, "RDHP");
    }

    /*
     * Registers the structures pieces themselves. If you don't do this part, Forge will complain to you in the Console. Called by registerPieces.
     */
    static IStructurePieceType registerStructurePiece(IStructurePieceType structurePiece, String key) {
        return Registry.register(Registry.STRUCTURE_PIECE, key.toLowerCase(Locale.ROOT), structurePiece);
    }
}
