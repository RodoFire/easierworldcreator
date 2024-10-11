## What is this mod about?
Create your world gen features easily with this library. It allow you to generate simple shapes that combined together can create some beautiful features. 

This mod also support multi-chunk features.

You can also manipulate structure and placing them in the world

One other functionnality that is not implemented for the moment is the support for custom particles shape

<center>
  <a href="https://modrinth.com/mod/ewc">
      <img src="https://imgur.com/9xU1uLq.png" alt="availible on modrinth" width="145">
    </a>
    <a href="https://www.curseforge.com/minecraft/mc-mods/easierworldcreator">
      <img src="https://imgur.com/0i2lDxQ.png" alt="availible on curseforge" width="150">
    </a>
</center>

### What Shapes can you create?
you can for the moment create :
  - Circles
  - Spheres
  - Cylinders
  - 2d and 3d ellipsoid
  - Lines between 2 points
  - Spirals and some variations
  - Tores

### Other features?
Yes there are a performance maths class as well as some utility world gen class that provide useful methods and a Class for perlin noise

<div class="center">
  <center>
    <a href="https://discord.gg/bAQRUxNyFj">
      <img src="https://imgur.com/sOXNu3x.png" alt="discord link" width="170">
    </a>
    <a>
      <img src="https://imgur.com/J4sja8y.png" alt="won't support forge" width="160">
    </a>
    <a href="https://github.com/McMellonTeam/easierworldcreator">
      <img src="https://imgur.com/bYpE7AK.png" alt="github link" width="145">
    </a>
  </center>
</div>


## Setup
if you are developping a mod and you want to use this library, you can put this in your `build.gradle`:
```gradle
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modImplementation "maven.modrinth:ewc:<version>"
}
```
you can find the version in the version tab (ex: 2.1.2-1.20.4)

## Info
### Wiki
I highly recommend you go to check the wiki to understand everything that is possible : [Mod-Wiki](https://github.com/RodoFire/easierworldcreator/wiki)
### Performance
this mod use is own maths that you can use For example calculating 1 000 000 cosinus :
gives an avarage calculating time of 476 ms when using `Math.cos()`, using the custom maths, you get an average time of 7ms when using `FatsMaths.getFastCos()` and an average of 14ms when using `FastMaths.getPreciseCos()`

### Using the library
the methods comes with common parameter : 

`StructureWorldAccess world` that is used to put the structure in the world. `StructureWorldAccess` allow you to use the feature either during world gen or not

`BlockPos pos` the position of the center of the structure

`List<BlockLayer> layers` a list of BlockLayer, see the wiki for more info : [Mod-Wiki](https://github.com/RodoFire/easierworldcreator/wiki)

The different size required for the shape to place


## Future?
I continue to develop actively this mod to support more and more possible things. You have any idea, suggestion on how to improve the mod? Let me know on the [github](https://github.com/RodoFire/easierworldcreator). 
<div class="center">
  <center>
    <a href="https://ko-fi.com/rodofire">
      <img src="https://imgur.com/H7hFYwW.png" alt="discord link" width="190">
    </a>
  </center>
</div>
