# Structure Tutorial Mod (Forge)
**How to register and generate jigsaw structures in 1.16.3 Minecraft Forge using nbt files!**

**Change the Github branch to see other versions of this tutorial including for Fabric!**
 
 This very small Forge mod is full of comments that will help you understand what a lot of the methods do and what needs to be done to get your structure to generate. In all, this mod is really just 4 java files, 2 JSON files, and 2 structure nbt files.  

If you don't know how to make a nbt file for structures, it's actually fairly simple and you can do it all inside minecraft itself! Here is a video on how to make and save a structure to nbt using structure blocks. If you want your jigsaw structure to have more than 1 piece, you will need to setup and save Jigsaw blocks with your structure to nbt (You'll have to find another video tutorial for jigsaw structures than the one below. Let me know if you find a great Jigsaw Block tutorial!): 
>https://www.youtube.com/watch?v=ylGFb4F4xVk&t=1s 

Once saved, the structure nbt file is stored in that world's save folder within the generated folder inside. Grab those files as you'll need to put it under your mod's resource folder inside data/mod_id/structures. NOTICE: This the data folder and not the asset folder inside resource! Then make a JSON file and put it in data/mod_id/worldgen/template_pool folder. Take a look at this tutorial's start_pool.json file for how to setup the JSON file itself.
>![Image of the folder layout for Structure Tutorial Mod which shows the structure nbt files are inside data.structure_tutorial.structures which is inside src/main/resources](https://i.imgur.com/Q4FLSOT.png)


Now you're ready to begin adding the structure to your mod! Take a look at StructureTutorialMain, start reading the comments, and follow the methods/classes. Don't just copy the code quickly or else you will get confuse. Take your time and try to understand how it all works before attempting to register and generate your structure in your own mod.

When making your own mod, make sure to include the accesstransformer file and to include it in your build.gradle. If your IDE still say that the methods listed in the access transformer are private and shows an error, then you may need to refresh gradle and generate the runs again so the accesstransformers takes effect.

Good luck and I hope this helps!

------------------

_MCPBot is dead. Do not use that to check for updates._

_if you want the absolute latest mapping, run ` gradlew -PUPDATE_MAPPINGS="20201028-1.16.3'" updateMappings `_

_command for the project and it'll remap everything to the cutting-edge mappings!_

_Otherwise, just use the mapping this mod uses for now:_

_mappings channel: 'snapshot', version: '20201028-1.16.3''_
 
