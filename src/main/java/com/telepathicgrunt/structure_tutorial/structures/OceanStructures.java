package com.telepathicgrunt.structure_tutorial.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.structure_tutorial.STStructures;
import com.telepathicgrunt.structure_tutorial.utilities.FilterHolderSet;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Map;
import java.util.Optional;

public class OceanStructures extends Structure {

    // A customized structure settings codec to allow us to expand the abilities of the biomes field.
    public static final MapCodec<Structure.Config> CUSTOM_STRUCTURE_SETTINGS_CODEC = RecordCodecBuilder.mapCodec(
            codecBuilder -> codecBuilder.group(
                            // This is where we swapped in our custom codec that will apply the exclude structure tag to remove entries from the has structure tag.
                            FilterHolderSet.codec(RegistryKeys.BIOME, Biome.REGISTRY_CODEC, false).fieldOf("biomes").forGetter(x -> x.biomes() instanceof FilterHolderSet<Biome> filterHolderSet ? filterHolderSet : new FilterHolderSet<>(x.biomes(), RegistryEntryList.empty())),
                            Codec.simpleMap(SpawnGroup.CODEC, StructureSpawns.CODEC, StringIdentifiable.toKeyable(SpawnGroup.values()))
                                    .fieldOf("spawn_overrides")
                                    .forGetter(Structure.Config::spawnOverrides),
                            GenerationStep.Feature.CODEC.fieldOf("step").forGetter(Structure.Config::step),
                            StructureTerrainAdaptation.CODEC.optionalFieldOf("terrain_adaptation", new Structure.Config(
                                    RegistryEntryList.of(), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.NONE
                            ).terrainAdaptation()).forGetter(Structure.Config::terrainAdaptation)
                    )
                    .apply(codecBuilder, Structure.Config::new)
    );

    // A custom codec that changes the size limit for our code_structure_sea_boat.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    public static final MapCodec<OceanStructures> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(CUSTOM_STRUCTURE_SETTINGS_CODEC.forGetter(structureInfo -> structureInfo.config),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    StructureLiquidSettings.codec.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
            ).apply(instance, OceanStructures::new));

    private final RegistryEntry<StructurePool> startPool;
    private final Optional<Identifier> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Type> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final DimensionPadding dimensionPadding;
    private final StructureLiquidSettings liquidSettings;

    public OceanStructures(Structure.Config config,
                         RegistryEntry<StructurePool> startPool,
                         Optional<Identifier> startJigsawName,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Type> projectStartToHeightmap,
                         int maxDistanceFromCenter,
                         DimensionPadding dimensionPadding,
                         StructureLiquidSettings liquidSettings)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    /*
     * This is where extra checks can be done to determine if the structure can spawn here.
     * This only needs to be overridden if you're adding additional spawn conditions.
     *
     * Fun fact, if you set your structure separation/spacing to be 0/1, you can use
     * isFeatureChunk to return true only if certain chunk coordinates are passed in
     * which allows you to spawn structures only at certain coordinates in the world.
     *
     * Basically, this method is used for determining if the land is at a suitable height,
     * if certain other structures are too close or not, or some other restrictive condition.
     *
     * For example, Pillager Outposts added a check to make sure it cannot spawn within 10 chunk of a Village.
     * (Bedrock Edition seems to not have the same check)
     *
     * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
     * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
     * Then loop through it and look for land with air above it and set blockpos's Y value to it.
     * Make sure to set the final boolean in JigsawPlacement.addPieces to false so
     * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
     *
     * Also, please for the love of god, do not do dimension checking here.
     * If you do and another mod's dimension is trying to spawn your structure,
     * the locate command will make minecraft hang forever and break the game.
     * Use the biome tags for where to spawn the structure and users can datapack
     * it to spawn in specific biomes that aren't in the dimension they don't like if they wish.
     */
    private static boolean extraSpawningChecks(Structure.Context context) {
        // Grabs the chunk position we are at
        ChunkPos chunkpos = context.chunkPos();

        // Get first non-air block.
        int occupiedYPos = context.chunkGenerator().getHeightInGround(
                chunkpos.getStartX(),
                chunkpos.getStartZ(),
                Heightmap.Type.WORLD_SURFACE_WG,
                context.world(),
                context.noiseConfig());

        // Get column of blocks at corner of the chunk. BEWARE, getBaseColumn is an expensive call. Call this as few times as possible for your checks.
        // Note, this column of blocks only has the raw terrain of the world which for the Overworld is Stone, Water, and Air.
        VerticalBlockSample columnOfBlocks = context.chunkGenerator().getColumnSample(chunkpos.getOffsetX(0), chunkpos.getOffsetZ(0), context.world(), context.noiseConfig());

        // Grab the block at the specified Y value.
        BlockState blockState = columnOfBlocks.getState(occupiedYPos);

        // Checks to make sure our structure only spawns if the spot has water.
        return blockState.getFluidState().isIn(FluidTags.WATER);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!OceanStructures.extraSpawningChecks(context)) {
            return Optional.empty();
        }

        // Set's our spawning blockpos's y offset
        int startY = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), startY, chunkPos.getStartZ());

        Optional<StructurePosition> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context, // Used for StructurePoolBasedGenerator to get all the proper behaviors done.
                        this.startPool, // The starting pool to use to create the structure layout from
                        this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                        this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                        // Here at projectStartToHeightmap, start_height's y value is -1 which means the structure spawn -1 blocks below terrain height if start_height and project_start_to_heightmap is defined in structure JSON.
                        // Set projectStartToHeightmap to be empty optional for structure to be place only at the passed in blockpos's Y value instead.
                        // Definitely keep this an empty optional when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        this.maxDistanceFromCenter, // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.
                        StructurePoolAliasLookup.EMPTY, // Optional thing that allows swapping a template pool with another per structure json instance. We don't need this but see vanilla JigsawStructure class for how to wire it up if you want it.
                        this.dimensionPadding, // Optional thing to prevent generating too close to the bottom or top of the dimension.
                        this.liquidSettings); // Optional thing to control whether the structure will be waterlogged when replacing pre-existing water in the world.

        /*
         * Note, you are always free to make your own StructurePoolBasedGenerator class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> getType() {
        return STStructures.SKY_STRUCTURES; // Helps the game know how to turn this structure back to json to save to chunks
    }
}
