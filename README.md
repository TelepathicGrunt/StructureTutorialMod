# Structure Tutorial Mod
**How to register and generate structures in 1.16.1 Minecraft using nbt files!**
**Change the Github branch to see previous MC versions of this tutorial.**
 
 This very small Forge mod is full of comments that will help you understand what a lot of the methods do and what needs to be done to get your structure to generate. In all, this mod is really just 5 files plus 2 structure nbt files.  

If you don't know how to make an nbt file for structures, it's actually fairly simple and you can do it all inside minecraft itself! Here is a video on how to make and save a structure to nbt using structure blocks: 
>https://www.youtube.com/watch?v=ylGFb4F4xVk&t=1s 

Once saved, the structure nbt file is stored in that world's save folder within the generated folder inside. Grab those files as you'll need to put it under your mod's resource folder inside data.mod_id.structures.
>![Image of the folder layout for Structure Tutorial Mod which shows the structure nbt files are inside data.structure_tutorial.structures which is inside src/main/resources](https://i.imgur.com/hNZoCql.png)

Now you're ready to begin adding the structure to your mod! Take a look at StructureTutorialMain, start reading the comments, and follow the methods/classes. Don't just copy the code quickly or else you will get confuse. Take your time and try to understand how it all works before attempting to register and generate your structure in your own mod.

When making your own mod, make sure to include the accesstransformer file and to include it in your build.gradle. Your IDE may still say that the methods listed in the access transformer are private and indicate so as an error, but if the access transformer is set up properly it won't actually throw any errors when run.

Good luck and I hope this helps!

------------------

_[This is using MCP mapping version of: '20200723-1.16.1' but try to use the latest_
 _mappings from http://export.mcpbot.bspk.rs/. Don't download any files but just_
 _get the number like this '20200521-1.15.1' of the latest mapping release._
 _Put that number into your mapping channel in build.gradle and rebuild your project_
 _to be able to use the latest mappings.]_
 