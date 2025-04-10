package com.telepathicgrunt.structuretutorial.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.structuretutorial.STStructures;
import com.telepathicgrunt.structuretutorial.utilities.FilterHolderSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Map;
import java.util.Optional;

public class OceanStructures extends Structure {

    // A customized structure settings codec to allow us to expand the abilities of the biomes field.
    public static final MapCodec<StructureSettings> CUSTOM_STRUCTURE_SETTINGS_CODEC = RecordCodecBuilder.mapCodec(
            codecBuilder -> codecBuilder.group(
                            // This is where we swapped in our custom codec that will apply the exclude structure tag to remove entries from the has structure tag.
                            FilterHolderSet.codec(Registries.BIOME, Biome.CODEC, false).fieldOf("biomes").forGetter(x -> new FilterHolderSet<>(x.biomes(), HolderSet.empty())),
                            Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values()))
                                    .fieldOf("spawn_overrides")
                                    .forGetter(StructureSettings::spawnOverrides),
                            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(StructureSettings::step),
                            TerrainAdjustment.CODEC
                                    .optionalFieldOf("terrain_adaptation", new StructureSettings(
                                            HolderSet.direct(), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE
                                    ).terrainAdaptation())
                                    .forGetter(StructureSettings::terrainAdaptation)
                    )
                    .apply(codecBuilder, StructureSettings::new)
    );

    // A custom codec that changes the size limit for our code_structure_sea_boat.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    public static final MapCodec<OceanStructures> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(CUSTOM_STRUCTURE_SETTINGS_CODEC.forGetter(structureInfo -> structureInfo.modifiableStructureInfo().getOriginalStructureInfo().structureSettings()),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
            ).apply(instance, OceanStructures::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public OceanStructures(StructureSettings config,
                           Holder<StructureTemplatePool> startPool,
                           Optional<ResourceLocation> startJigsawName,
                           int size,
                           HeightProvider startHeight,
                           Optional<Heightmap.Types> projectStartToHeightmap,
                           int maxDistanceFromCenter,
                           DimensionPadding dimensionPadding,
                           LiquidSettings liquidSettings)
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
     * extraSpawningChecks to return true only if certain chunk coordinates are passed in
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
    private static boolean extraSpawningChecks(Structure.GenerationContext context) {
        // Grabs the chunk position we are at
        ChunkPos chunkpos = context.chunkPos();

        // Get first non-air block.
        int occupiedYPos = context.chunkGenerator().getFirstOccupiedHeight(
                chunkpos.getMinBlockX(),
                chunkpos.getMinBlockZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState());

        // Get column of blocks at corner of the chunk. BEWARE, getBaseColumn is an expensive call. Call this as few times as possible for your checks.
        // Note, this column of blocks only has the raw terrain of the world which for the Overworld is Stone, Water, and Air.
        NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(chunkpos.getBlockX(0), chunkpos.getBlockZ(0), context.heightAccessor(), context.randomState());

        // Grab the block at the specified Y value.
        BlockState blockState = columnOfBlocks.getBlock(occupiedYPos);

        // Checks to make sure our structure only spawns if the spot has water.
        return blockState.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!OceanStructures.extraSpawningChecks(context)) {
            return Optional.empty();
        }

        // Set's our spawning blockpos's y offset
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<GenerationStub> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        this.startPool, // The starting pool to use to create the structure layout from
                        this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                        this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                        // Here at projectStartToHeightmap, blockPos's y value is -1 which means the structure spawn -1 blocks above terrain height.
                        // Set projectStartToHeightmap to be empty optional for structure to be place only at the passed in blockpos's Y value instead.
                        // Definitely keep this an empty optional when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        this.maxDistanceFromCenter, // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.
                        PoolAliasLookup.EMPTY, // Optional thing that allows swapping a template pool with another per structure json instance. We don't need this but see vanilla JigsawStructure class for how to wire it up if you want it.
                        this.dimensionPadding, // Optional thing to prevent generating too close to the bottom or top of the dimension.
                        this.liquidSettings); // Optional thing to control whether the structure will be waterlogged when replacing pre-existing water in the world.

        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> type() {
        return STStructures.OCEAN_STRUCTURES.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
