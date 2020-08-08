package com.telepathicgrunt.structuretutorial.world.features.structures;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.telepathicgrunt.structuretutorial.StructureTutorialMain;
import com.telepathicgrunt.structuretutorial.world.features.FeatureInit;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

/*
 * This class is based off of the IglooPieces class which I am assuming is doing the proper way of generating
 * structures. If you look at SwampHutPiece or DesertPyramidPiece, you'll see they manually hardcoded every block by
 * hand which is tedious and time consuming. It is also difficult to visualize it which is why I highly encourage the
 * use of structure blocks.
 * 
 * Also, you might notice that some structures like Pillager Outposts or Woodland Mansions uses a special block called
 * Jigsaw Block to randomize which structure nbt to use to attach to other parts of the structure and still keep it
 * looking clean. This is somewhat complicated and I haven't looks into this yet. But once you're familiar with modding
 * and is pretty experienced with coding, go try and make a structure using Jigsaw blocks! (Look at
 * PillagerOutpostPieces and how it used JigsawPattern and JigsawManager. Once mastered, you will be able to generate
 * massive structures that are unique every time you find one.
 */
public class RunDownHousePieces {
    /*
     * Here is a video on how to save a structure with structure blocks. https://www.youtube.com/watch?v=ylGFb4F4xVk&t=1s Once saved, the structure nbt file is
     * store in that world's save folder within the generated folder inside.
     * 
     * Move the nbt file of your structure into asses.mod_id.structures folder under src/main/resources in your mod. Make sure the nbt file name is all
     * lowercase and uses no spaces.
     * 
     * Here, I have two structure nbt files named run_down_house_left_side.nbt and run_down_house_right_side.nbt and I access them with the following resource
     * locations below. The MODID and ':' are important too.
     */
    private static final ResourceLocation LEFT_SIDE = new ResourceLocation(StructureTutorialMain.MODID + ":run_down_house_left_side");
    private static final ResourceLocation RIGHT_SIDE = new ResourceLocation(StructureTutorialMain.MODID + ":run_down_house_right_side");
    private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.of(LEFT_SIDE, new BlockPos(0, 1, 0), RIGHT_SIDE, new BlockPos(0, 1, 0));

    /*
     * Begins assembling your structure and where the pieces needs to go.
     */
    public static void start(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<StructurePiece> pieceList, Random random) {
        int x = pos.getX();
        int z = pos.getZ();

        // This is how we factor in rotation for multi-piece structures.
        //
        // I would recommend using the OFFSET map above to have each piece at correct height relative of each other
        // and keep the X and Z equal to 0. And then in rotations, have the centermost piece have a rotation
        // of 0, 0, 0 and then have all other pieces' rotation be based off of the bottommost left corner of
        // that piece (the corner that is smallest in X and Z).
        //
        // Lots of trial and error may be needed to get this right for your structure.
        BlockPos rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
        BlockPos blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new RunDownHousePieces.Piece(templateManager, LEFT_SIDE, blockpos, rotation));

        rotationOffSet = new BlockPos(-10, 0, 0).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new RunDownHousePieces.Piece(templateManager, RIGHT_SIDE, blockpos, rotation));
    }

    /*
     * Here's where some voodoo happens. Most of this doesn't need to be touched but you do have to pass in the IStructurePieceType you registered into the
     * super constructors.
     * 
     * The method you will most likely want to touch is the handleDataMarker method.
     */
    public static class Piece extends TemplateStructurePiece {
        private ResourceLocation resourceLocation;
        private Rotation rotation;

        public Piece(TemplateManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos, Rotation rotationIn) {
            super(FeatureInit.RDHP, 0);
            this.resourceLocation = resourceLocationIn;
            BlockPos blockpos = RunDownHousePieces.OFFSET.get(resourceLocation);
            this.templatePosition = pos.add(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.rotation = rotationIn;
            this.setupPiece(templateManagerIn);
        }

        public Piece(TemplateManager templateManagerIn, CompoundNBT tagCompound) {
            super(FeatureInit.RDHP, tagCompound);
            this.resourceLocation = new ResourceLocation(tagCompound.getString("Template"));
            this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
            this.setupPiece(templateManagerIn);
        }

        private void setupPiece(TemplateManager templateManager) {
            Template template = templateManager.getTemplateDefaulted(this.resourceLocation);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
            this.setup(template, this.templatePosition, placementsettings);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.resourceLocation.toString());
            tagCompound.putString("Rot", this.rotation.name());
        }

        /*
         * If you added any data marker structure blocks to your structure, you can access and modify them here. In this case, our structure has a data maker
         * with the string "chest" put into it. So we check to see if the incoming function is "chest" and if it is, we now have that exact position.
         * 
         * So what is done here is we replace the structure block with a chest and we can then set the loottable for it.
         * 
         * You can set other data markers to do other behaviors such as spawn a random mob in a certain spot, randomize what rare block spawns under the floor,
         * or what item an Item Frame will have.
         */
        @Override
        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if ("chest".equals(function)) {
                worldIn.setBlockState(pos, Blocks.CHEST.getDefaultState(), 2);
                TileEntity tileentity = worldIn.getTileEntity(pos);

                // Just another check to make sure everything is going well before we try to set the chest.
                if (tileentity instanceof ChestTileEntity) {
                    // ((ChestTileEntity) tileentity).setLootTable(<resource_location_to_loottable>, rand.nextLong());

                }
            }
        }
    }

}
