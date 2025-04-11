package com.telepathicgrunt.structure_tutorial.structureplacement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.structure_tutorial.STStructurePlacements;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

import java.util.Optional;

public class DistanceBasedStructurePlacement extends RandomSpreadStructurePlacement {

    // Special codec where we tacked on a "min_distance_from_world_origin" field so
    // we can now have structures spawn based on distance from world center.
    public static final MapCodec<DistanceBasedStructurePlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(DistanceBasedStructurePlacement::getLocateOffset),
            StructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(DistanceBasedStructurePlacement::getFrequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(DistanceBasedStructurePlacement::getFrequency),
            Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(DistanceBasedStructurePlacement::getSalt),
            StructurePlacement.ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(DistanceBasedStructurePlacement::getExclusionZone),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spacing").forGetter(DistanceBasedStructurePlacement::getSpacing),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("separation").forGetter(DistanceBasedStructurePlacement::getSeparation),
            SpreadType.CODEC.optionalFieldOf("spread_type", SpreadType.LINEAR).forGetter(DistanceBasedStructurePlacement::getSpreadType),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_distance_from_world_origin").forGetter(DistanceBasedStructurePlacement::minDistanceFromWorldOrigin)
    ).apply(instance, instance.stable(DistanceBasedStructurePlacement::new)));

    private final Optional<Integer> minDistanceFromWorldOrigin;

    public DistanceBasedStructurePlacement(Vec3i locationOffset,
                                           StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
                                           float frequency,
                                           int salt,
                                           Optional<ExclusionZone> exclusionZone,
                                           int spacing,
                                           int separation,
                                           SpreadType spreadType,
                                           Optional<Integer> minDistanceFromWorldOrigin
    ) {
        super(locationOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, spreadType);
        this.minDistanceFromWorldOrigin = minDistanceFromWorldOrigin;

        // Helpful validation to ensure that spacing value is always greater than separation value
        if (spacing <= separation) {
            throw new RuntimeException("""
                Spacing cannot be less or equal to separation.
                Please correct this error as there's no way to spawn this structure properly
                    Spacing: %s
                    Separation: %s.
            """.formatted(spacing, separation));
        }
    }

    public Optional<Integer> minDistanceFromWorldOrigin() {
        return this.minDistanceFromWorldOrigin;
    }

    // Override this method to add coordinate checking.
    // The x and z here is in chunk positions.
    // What we do is we check if the structure is too close to world center and if so, return false.
    // Otherwise, if far enough away, run the normal structure position choosing code.
    // When this returns true, the structure's type class will be called to see if the structure layout can be made.
    @Override
    protected boolean isStartChunk(StructurePlacementCalculator structurePlacementCalculator, int x, int z) {
        if (minDistanceFromWorldOrigin.isPresent()) {
            // Convert chunk position to block position.
            long xBlockPos = x * 16L;
            long zBlockPos = z * 16L;

            // Simple fast distance check without needing to do a square root. The threshold is circular around world origin.
            if ((xBlockPos * xBlockPos) + (zBlockPos * zBlockPos) < (((long) minDistanceFromWorldOrigin.get()) * minDistanceFromWorldOrigin.get())) {
                return false;
            }
        }

        ChunkPos chunkpos = this.getStartChunk(structurePlacementCalculator.getStructureSeed(), x, z);
        return chunkpos.x == x && chunkpos.z == z;
    }

    @Override
    public StructurePlacementType<?> getType() {
        return STStructurePlacements.DISTANCE_BASED_STRUCTURE_PLACEMENT;
    }

}
