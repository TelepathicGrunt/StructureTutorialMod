package com.telepathicgrunt.structure_tutorial.mixin;

import com.telepathicgrunt.structure_tutorial.STStructures;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(
            method = "getEntitySpawnList(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void structureMobs(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos, CallbackInfoReturnable<List<SpawnSettings.SpawnEntry>> cir) {

        // Check if in our structure and grab mob list if so
        List<SpawnSettings.SpawnEntry> list = getStructureSpawns(biome, accessor, group, pos);

        // If not null, it was in our structure. Return the mob list and exit the method now.
        if(list != null) cir.setReturnValue(list);
    }

    /**
     * This mixin hooks into NoiseChunkGenerator's getEntitySpawnList which is where vanilla does the
     * mob spawning in structures over time. We have to check what the spawn group is for the structure
     * and then see if it is inside the structure. Then we grab the list of mobs from the structure that can spawn.
     *
     * This way of doing structure mob spawning will prevent biome's mobs from spawning in the structure.
     */
    private static List<SpawnSettings.SpawnEntry> getStructureSpawns(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos){

        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, true, STStructures.RUN_DOWN_HOUSE).hasChildren()) {
                return STStructures.RUN_DOWN_HOUSE.getMonsterSpawns();
            }
        }
        else if (group == SpawnGroup.CREATURE) {
            if (accessor.getStructureAt(pos, true, STStructures.RUN_DOWN_HOUSE).hasChildren()) {
                return STStructures.RUN_DOWN_HOUSE.getCreatureSpawns();
            }
        }

        return null;
    }
}