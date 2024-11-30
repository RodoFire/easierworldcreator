## 2.3.0 update:

### ! Mod using the old versions of this library won't work!

- reworked `BlockList`classes. They now are divided into parameters
- added `BlockListManager` to manage a list pf blocksList
- added `OrderedBlockList`, allowing you to order the blockPos of the BlockList.
  This replaces the previous `List<Pair<BonckPos, BlockState>>`,
  this allows us to save some memory by not keeping duplicated `BlockStates`
- performance improvements for spiral and torus
- changed a lot of packages, moved a lot of classes
- changed names of some classes (the names are still close. For example: `Shape` -> `AbstractBlockShape`
- added some javadoc
- solved some minor bugs
- added some equation maths
- added `BlockSorter` class, allowing you to sort your BlockPos List.
- changed `StructurePlaceAnimator` to include `BlockSorter`
- added some `animationTime`
- removed dev test classes
- added `ImageButtonWidget` class to put texture on buttons