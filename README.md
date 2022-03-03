# Structure Tutorial Mod (Fabric)
**How to register and generate jigsaw structures in 1.18.2+ Minecraft Fabric using nbt files! In fact, you can actually make structures using only json files in 1.18.2+ MC!**

**Change the Github branch to see other versions of this tutorial including Forge.**

 This very small Fabric mod is full of comments that will help you understand what a lot of the json files and what needs to be done to get your structure to generate. In all, minimum number of files for a structure is 4 json files and 1 nbt file. The json_only_house structure is made using just json files while the code_structure_sky_fan structure is mostly json files but has a custom java structure class to do extra behavior for it.

If you don't know how to make a nbt file for structures, it's actually fairly simple and you can do it all inside minecraft itself! Here is a video on how to make and save a structure to nbt using structure blocks. Here's a short video on how Structure Blocks work: 
>https://www.youtube.com/watch?v=umhuRXinD3o

If your structure is only a **single piece, then you do NOT need any Jigsaw Blocks** and can just use 1 pool file. If you want your jigsaw structure to have more than 1 piece, you will need to setup and save Jigsaw blocks into your structure's nbt files. The Jigsaw blocks acts as connectors between the nbt pieces and pool files. (Here's two videos about using Jigsaw Blocks in structures! The first one is very long but extremely detailed.): 
>https://www.youtube.com/watch?v=5a4DAkWW3JQ

Handy image for a shorthand way of how Jigsaw Blocks and Pools work together: 
>![Jigsaw Cheatsheet](https://cdn.discordapp.com/attachments/686973568872873996/782006962979602432/jigsaws.png)

This picture shows how structure jigsaw pieces are only valid if the child piece fits entirely within the parent or entirely outside the parent piece. Partial intersections will prevent the piece from spawning.
>[Jigsaw Boundaries](https://cdn.discordapp.com/attachments/754531543309090817/947099493004894228/80C7594C-31ED-42C6-8DD8-656FEA984E0B.png)

And here, if a piece fails to spawn, it will go to the fallback pool, pick a new piece from the fallback pool, and try to spawn that piece if there is room for it. Great for closing off the ends of hallways!
>[Jigsaw Boundaries](https://cdn.discordapp.com/attachments/754531543309090817/947099941531168798/5201EEE1-1564-439C-A862-41DC6855609D.png)

Once saved, the structure nbt file is stored in that world's save folder within the generated folder inside. Grab those files as you'll need to put it under your mod's resource folder inside data/mod_id/structures. NOTICE: This the data folder and not the asset folder inside resource! Now after you place the nbt file at that spot, we need to make a new JSON file and put it in data/mod_id/worldgen/template_pool folder as now we need to make the pool that will spawn our nbt file. Take a look at this tutorial's start_pool.json file for how to setup the JSON file itself.
>![Image of the folder layout for Structure Tutorial Mod which shows the structure nbt files are inside data.structure_tutorial.structures which is inside src/main/resources](https://i.imgur.com/Q4FLSOT.png)


Now you're ready to begin adding the structure to your mod! Take a look at StructureTutorialMain, start reading the comments, and follow the json/methods/classes. Don't just copy the code quickly or else you will get confuse. Take your time and try to understand how it all works before attempting to register and generate your structure in your own mod. (Also, check out the house structure itself in the world! There's some info on signs in the structure itself about nbt files)

Also, if you get stuck on the template_pool json, here's a datapack of the entire vanilla worldgen. Including vanilla's template_pools. Use this if you want to see how vanilla setup their pools! https://github.com/TelepathicGrunt/StructureTutorialMod/releases/tag/0.0.0

Good luck and I hope this helps!
 
You can contact me through issue reports here or on discord. My Discord is TelepathicGrunt#7397 and my channel is: https://discord.gg/K8qRev3yKZ

------------------

My Patreon if you wish to support me! 
https://www.patreon.com/telepathicgrunt
 
