package net.rodofire.easierworldcreator.structure;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.rodofire.easierworldcreator.mixin.StructureTemplateMixin;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import net.rodofire.easierworldcreator.shapeutil.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.util.MathUtil;
import net.rodofire.easierworldcreator.worldgenutil.BlockPlaceUtil;

import java.util.*;

@SuppressWarnings("unused")
public class StructureUtil {
    public static void placeNbtFiles(StructureWorldAccess world, List<Identifier> nbtlist, Chunk chunk) {
        for (Identifier nbt : nbtlist) {
            NbtPlacer placer = new NbtPlacer(world, nbt);
            placer.place(1f, chunk);
        }
    }

    public static void placeNbtFiles(StructureWorldAccess world, List<Identifier> nbtlist, ChunkPos chunk) {
        for (Identifier nbt : nbtlist) {
            NbtPlacer placer = new NbtPlacer(world, nbt);
            placer.place(1f, chunk);
        }
    }


    public static void convertNbtToBlockList(StructureTemplate structureTemplate, List<BlockList> blockLists) {
        List<StructureTemplate.PalettedBlockInfoList> blockInfoLists = ((StructureTemplateMixin) structureTemplate).getBlockInfoLists();
        Map<Pair<BlockState, NbtCompound>, List<BlockPos>> blockStateToPositionsMap = new HashMap<>();
        for (StructureTemplate.PalettedBlockInfoList palettedList : blockInfoLists) {
            for (StructureTemplate.StructureBlockInfo blockInfo : palettedList.getAll()) {
                BlockState blockState = blockInfo.state();
                BlockPos blockPos = blockInfo.pos();
                NbtCompound tag = blockInfo.nbt();

                Pair<BlockState, NbtCompound> stateAndTagPair = new Pair<>(blockState, tag);

                blockStateToPositionsMap.computeIfAbsent(stateAndTagPair, k -> new ArrayList<>()).add(blockPos);
            }
        }

        for (Map.Entry<Pair<BlockState, NbtCompound>, List<BlockPos>> entry : blockStateToPositionsMap.entrySet()) {
            BlockState blockState = entry.getKey().getFirst();
            NbtCompound tag = entry.getKey().getSecond();
            List<BlockPos> positions = entry.getValue();

            blockLists.add(new BlockList(positions, blockState, tag));
        }
    }

    public static void convertNbtToBlockList(StructureTemplate structureTemplate, List<BlockList> blockLists, StructurePlacementData data, StructureWorldAccess world, BlockPos pos) {
        List<StructureTemplate.StructureBlockInfo> list = data.getRandomBlockInfos(((StructureTemplateMixin) structureTemplate).getBlockInfoLists(), pos).getAll();
        List<StructureTemplate.StructureBlockInfo> blockInfoList = StructureTemplate.process(world, pos, new BlockPos(0, 0, 0), data, list);

        Map<Pair<BlockState, NbtCompound>, List<BlockPos>> blockStateToPositionsMap = new HashMap<>();
        //for (StructureTemplate.PalettedBlockInfoList palettedList : ((StructureTemplateMixin) structureTemplate).getBlockInfoLists()) {
        for (StructureTemplate.StructureBlockInfo blockInfo : blockInfoList) {
            BlockState blockState = blockInfo.state();
            BlockPos blockPos = blockInfo.pos();
            NbtCompound tag = blockInfo.nbt();

            Pair<BlockState, NbtCompound> stateAndTagPair = new Pair<>(blockState, tag);

            blockStateToPositionsMap.computeIfAbsent(stateAndTagPair, k -> new ArrayList<>()).add(blockPos);
        }
        //}

        for (Map.Entry<Pair<BlockState, NbtCompound>, List<BlockPos>> entry : blockStateToPositionsMap.entrySet()) {
            BlockState blockState = entry.getKey().getFirst();
            NbtCompound tag = entry.getKey().getSecond();
            List<BlockPos> positions = entry.getValue();

            blockLists.add(new BlockList(positions, blockState, tag));
        }
    }


    public static void place(StructureWorldAccess world, StructurePlaceAnimator animator, List<BlockList> blockLists, BlockPos block, boolean force, Set<Block> blockToForce, Set<Block> blockToSkip, float integrity) {
        //avoid errors due to aberrant values
        if (integrity < 0) integrity = 0;
        if (integrity > 1) integrity = 1;

        Iterator<BlockList> blockListIterator = blockLists.iterator();
        while (blockListIterator.hasNext()) {
            BlockList blockList = blockListIterator.next();
            BlockState blockState = blockList.getBlockState();

            if (blockState.isOf(Blocks.JIGSAW) || blockState.isOf(Blocks.STRUCTURE_BLOCK) || blockState.isOf(Blocks.STRUCTURE_VOID) || blockState.isOf(Blocks.AIR))
                blockListIterator.remove();

            if (blockToSkip != null && !blockToSkip.isEmpty())
                if (blockToSkip.contains(blockState.getBlock()))
                    blockListIterator.remove();

            NbtCompound tag = blockList.getTag();

            boolean bl1 = blockToForce == null || blockToForce.isEmpty();
            for (BlockPos pos : blockList.getPosList()) {
                blockList.replaceBlockPos(pos, pos.add(block));
                if (!bl1 || force) {
                    if (integrity < 1f) {
                        if (MathUtil.getRandomBoolean(integrity)) {
                            blockList.removeBlockPos(pos);
                        }
                    }
                    if (!BlockPlaceUtil.verifyBlock(world, force, blockToForce, pos.add(block))) {
                        blockList.removeBlockPos(pos);
                    }
                }
            }

        }
        //we remove or not the pos of the list
        /*for (BlockList blockList : blockLists) {
            BlockState blockState = blockList.getBlockState();
            if (blockState.isOf(Blocks.JIGSAW))
                continue;

            if (blockToSkip != null && !blockToSkip.isEmpty())
                if (blockToSkip.contains(blockState.getBlock()))
                    continue;

            NbtCompound tag = blockList.getTag();

            boolean bl1 = blockToForce == null || blockToForce.isEmpty();
            for (BlockPos pos : blockList.getPosList()) {
                blockList.replaceBlockPos(pos, pos.add(block));
                if (!bl1 || force) {
                    if (integrity < 1f) {
                        if (MathUtil.getRandomBoolean(integrity)) {
                            blockList.removeBlockPos(pos);
                        }
                    }
                    if (!BlockPlaceUtil.verifyBlock(world, force, blockToForce, pos.add(block))) {
                        blockList.removeBlockPos(pos);
                    }
                }
            }
        }*/
        //we place the structure depending on if the animator is present or not
        if (animator != null) {
            animator.placeFromBlockList(blockLists);
            System.out.println(blockLists);
        } else {
            for (BlockList blockList : blockLists) {
                BlockState blockState = blockList.getBlockState();
                NbtCompound tag = blockList.getTag();
                for (BlockPos pos : blockList.getPosList()) {
                    world.setBlockState(pos, blockState, 3);
                    if (tag != null) {
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        if (blockEntity != null) {
                            NbtCompound currentNbt = blockEntity.createNbtWithIdentifyingData();
                            currentNbt.copyFrom(tag);

                            blockEntity.readNbt(currentNbt);
                            blockEntity.markDirty();
                        }
                    }
                }
            }
        }

    }

}
