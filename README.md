# Structure Tutorial Mod (NeoForge)
**How to register and generate jigsaw structures in 1.21.11+ Minecraft NeoForge using nbt files! In fact, you can actually make structures using only json files in 1.18.2+ MC!**

**Change the Github branch to see other versions of this tutorial including for Fabric!**

 This very small NeoForge mod is full of comments that will help you understand what a lot of the json files and what needs to be done to get your structure to generate. In all, minimum number of files for a structure is 4 json files and 1 nbt file. The json_only_house structure is made using just json files while the other 3 structures are mostly json files but has custom java classes to do extra behavior for it.
---
 
There are 4 structures in this tutorial mod. Each one focusing on different setups you may want.

- JSON Only House
  - This structure uses only JSON files to be made. No code needed. 
  - Uses Jigsaw blocks in the NBT files and template_pool JSON files to combine two NBT pieces together. A 2 piece structure. 
  - Has an entity saved into NBT with persistence required. See the structure in-world for what the entity is and signs explaining it.
  - Has a chest with a Loot Table saved to it, so it has random loot on first opening. See the structure in-world for signs explaining it.
  - Overrides the natural biome creature and monster spawns with its own. See the worldgen/structure file for this.

- Sky Fan
  - This structure uses code to ensure it never spawns above land that is higher than y = 150. See worldgen/structure and SkyStructures.java for this.
  - Has blocks randomized throughout the structure by using a processor list. See worldgen/template_pool and worldgen/processor_list for this.
  - Has 4 entity saved into NBT with persistence required.
  - Overrides the natural biome monster spawns with its own. See the worldgen/structure file for this.

- Sea Boat
    - This structure uses code to allow using 2 biome tags. 1 to say what biomes to spawn in and another for biome to ignore and not spawn in. See worldgen/structure and OceanStructures.java for this.
    - This structure also checks to makes sure spawn location has water. See worldgen/structure and OceanStructures.java for this.

- End Phantom Balloon
  - This structure uses code to ensure it only spawns above the large end islands. See worldgen/structure and SkyStructures.java for this.
  - Uses a custom structure placement so it only spawns 1000 blocks or more from world center. See worldgen/structure_set and DistanceBasedStructurePlacement.java

---

All vanilla worldgen JSON and NBT files can be found here. Older Minecraft versions are under the commit history: https://github.com/misode/mcmeta/tree/data/data/minecraft
 
Some other tutorials you can check out for extra info that might be easier to understand:

- https://minecraft.wiki/w/Tutorials/Custom_structures

- https://www.planetminecraft.com/blog/custom-structure-gen-documentation/

- https://gist.github.com/GentlemanRevvnar/98a8f191f46d28f63592672022c41497

If you don't know how to make a nbt file for structures, it's actually fairly simple and you can do it all inside minecraft itself! Here is a video on how to make and save a structure to nbt using structure blocks. Here's a short video on how Structure Blocks work: 
>https://www.youtube.com/watch?v=umhuRXinD3o

If your structure is only a **single piece, then you do NOT need any Jigsaw Blocks** and can just use 1 pool file. If you want your jigsaw structure to have more than 1 piece, you will need to setup and save Jigsaw blocks into your structure's nbt files. The Jigsaw blocks acts as connectors between the nbt pieces and pool files. (Here's two videos about using Jigsaw Blocks in structures! The first one is very long but extremely detailed.): 
>https://www.youtube.com/watch?v=5a4DAkWW3JQ

Handy image for a shorthand way of how Jigsaw Blocks and Pools work together: 
>![Jigsaw Cheatsheet](https://github.com/TelepathicGrunt/StructureTutorialMod/assets/40846040/dc5eb44d-ddbf-4302-a4c9-e544a53f7981)


This picture shows how structure jigsaw pieces are only valid if the child piece fits entirely within the parent or entirely outside the parent piece. Partial intersections will prevent the piece from spawning.

<img src="https://github.com/TelepathicGrunt/StructureTutorialMod/assets/40846040/a445415a-1d68-4d47-a38c-fe1d1fe675f2" data-canonical-src="https://github.com/TelepathicGrunt/StructureTutorialMod/assets/40846040/a445415a-1d68-4d47-a38c-fe1d1fe675f2" height="300"/>

And here, if a piece fails to spawn, it will go to the fallback pool, pick a new piece from the fallback pool, and try to spawn that piece if there is room for it. Great for closing off the ends of hallways!

<img src="https://github.com/TelepathicGrunt/StructureTutorialMod/assets/40846040/fbf2a4d3-d197-4c08-80f1-5027e2a6ed08" data-canonical-src="https://github.com/TelepathicGrunt/StructureTutorialMod/assets/40846040/fbf2a4d3-d197-4c08-80f1-5027e2a6ed08" height="300"/>

Once saved, the structure nbt file is stored in that world's save folder within the generated folder inside. Grab those files as you'll need to put it under your mod's resource folder inside data/mod_id/structures. NOTICE: This the data folder and not the asset folder inside resource! Then make a JSON file and put it in data/mod_id/worldgen/template_pool folder. Take a look at this tutorial's start_pool.json file for how to setup the JSON file itself.
>![Image of the folder layout for Structure Tutorial Mod which shows the structure nbt files are inside data.structure_tutorial.structures which is inside src/main/resources](https://github.com/TelepathicGrunt/StructureTutorialMod/assets/40846040/182e07fb-8d91-4ea2-8152-97c5ad64ff41)



Now you're ready to begin adding the structure to your mod! Take a look at StructureTutorialMain, start reading the comments, and follow the json/methods/classes. Don't just copy the code quickly or else you will get confuse. Take your time and try to understand how it all works before attempting to register and generate your structure in your own mod. (Also, check out the house structure itself in the world! There's some info on signs in the structure itself about nbt files)

Also, if you get stuck on the template_pool json, here's a datapack of the entire vanilla worldgen. Including vanilla's template_pools. Use this if you want to see how vanilla setup their pools! https://github.com/TelepathicGrunt/StructureTutorialMod/releases/tag/0.0.0

Good luck and I hope this helps!

You can contact me through issue reports here or on discord. My Discord is TelepathicGrunt#7397 and my channel is: https://discord.gg/K8qRev3yKZ


------------------

My Patreon if you wish to support me! 
https://www.patreon.com/telepathicgrunt
 
