## 2.3.0 update:

### ! Mod using the old versions of this library won't work !

- reworked `BlockList`classes. They now are divided into parameters
- added BlockList Manager to manage a list pf blocksList
- added `OrderedBlockList, allowing you to order the blockPos of the BlockList
- performance imporvements for spiral and torus
- changed a lot of package
- changed names of some classes(the names are still close. For example: `Shape` -> `AbstractBlockShape`
- added some javadoc
- added some equations maths
- added `BlockSorter` class, allowing you to sort your BlockPos List.
- changed `StructurePlaceAnimator` to include `BlockSorter`
- added some `animationTime`
- removed dev test classes 