package com.telepathicgrunt.structuretutorial.world.features.structures;

import org.apache.logging.log4j.Level;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.structuretutorial.StructureTutorialMain;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class RunDownHouseStructure extends Structure<NoFeatureConfig> {
    public RunDownHouseStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    /*
     * The structure name to show in the /locate command.
     * 
     * Make sure this matches what the resourcelocation of your structure will be because if you don't add the MODID: part, Minecraft will put minecraft: in
     * front of the name instead and we don't want that. We want our structure to have our mod's ID rather than Minecraft so people don't get confused.
     */
    @Override
    public String getStructureName() {
        return StructureTutorialMain.MODID + ":run_down_house";
    }

    /*
     * This is how the worldgen code will start the generation of our structure when it passes the checks.
     */
    @Override
    public Structure.IStartFactory getStartFactory() {
        return RunDownHouseStructure.Start::new;
    }

    /*
     * This is where all the checks will be done to determine if the structure can spawn here. This only needs to be overridden if you're adding additional
     * spawn conditions.
     * 
     * Notice how the biome is also passed in. Though, you are not going to do any biome checking here as you should've added this structure to the biomes you
     * wanted already with the .addStructure method.
     * 
     * Basically, this method is used for determining if the chunk coordinates are valid, if certain other structures are too close or not, or some other
     * restrictive condition.
     *
     * For example, Pillager Outposts added a check to make sure it cannot spawn within 10 chunk of a Village. (Bedrock Edition seems to not have the same
     * check)
     * 
     * 
     * Also, please for the love of god, do not do dimension checking here. If you do and another mod's dimension is trying to spawn your structure, the locate
     * command will make minecraft hang forever and break the game.
     * 
     * If you want to do dimension checking, I would do it in the Init method. It will make the locate command say the structure is spawning in the blacklisted
     * dimension but the structure won't actually spawn at all. This is much better than making the game become unresponsive completely. You can send a message
     * from the server to the players there saying the dimension cannot spawn the structure and to ignore the false positive from the locate command.
     * 
     * This may be called canBeGenerated instead of func_230363_a_ on newer mappings.
     */
//    @Override
//    public boolean func_230363_a_(ChunkGenerator chunkGen, BiomeProvider biomeManager, long seedModifier, SharedSeedRandom rand, int chunkPosX, int chunkPosZ,
//            Biome biome, ChunkPos chunkPos, NoFeatureConfig config) {
//        // This is very similar to the code for making pillager outposts unable to spawn near villages.
//        int i = chunkPosX >> 4;
//        int j = chunkPosZ >> 4;
//        rand.setSeed((long) (i ^ j << 4) ^ seedModifier);
//        for (int k = chunkPosX - 10; k <= chunkPosX + 10; ++k) {
//            for (int l = chunkPosZ - 10; l <= chunkPosZ + 10; ++l) {
//                ChunkPos chunkpos = Structure.field_236381_q_.func_236392_a_(chunkGen.func_235957_b_().func_236197_a_(Structure.field_236381_q_), seedModifier,
//                        rand, k, l);
//                if (k == chunkpos.x && l == chunkpos.z) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    /*
     * Handles calling up the structure's pieces class and height that structure will spawn at.
     */
    public static class Start extends StructureStart {
        public Start(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void func_230364_a_(ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, IFeatureConfig config) {
            // Check out vanilla's WoodlandMansionStructure for how they offset the x and z
            // so that they get the y value of the land at the mansion's entrance, no matter
            // which direction the mansion is rotated.
            //
            // However, for most purposes, getting the y value of land with the default x and z is good enough.
            Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];

            // Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
            int x = (chunkX << 4) + 7;
            int z = (chunkZ << 4) + 7;

            // Finds the y value of the terrain at location.
            int surfaceY = generator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);
            BlockPos blockpos = new BlockPos(x, surfaceY, z);

            // Now adds the structure pieces to this.components with all details such as where each part goes
            // so that the structure can be added to the world by worldgen.
            RunDownHousePieces.start(templateManagerIn, blockpos, rotation, this.components, this.rand);

            // Sets the bounds of the structure.
            this.recalculateStructureSize();

            // I use to debug and quickly find out if the structure is spawning or not and where it is.
            StructureTutorialMain.LOGGER.log(Level.DEBUG, "Rundown House at " + (blockpos.getX()) + " " + blockpos.getY() + " " + (blockpos.getZ()));
        }

    }
}