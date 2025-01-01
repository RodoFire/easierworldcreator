package net.rodofire.easierworldcreator.structure;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.CompoundBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.FullBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.BlockListComparator;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.CompoundBlockListComparator;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.FullBlockListComparator;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.OrderedBlockListComparator;
import net.rodofire.easierworldcreator.maths.MathUtil;
import net.rodofire.easierworldcreator.mixin.StructureTemplateMixin;
import net.rodofire.easierworldcreator.placer.blocks.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;

import java.util.*;

//TODO check if tag is present, if yes, cast CompoundBlockList
@SuppressWarnings("unused")
public class StructureUtil {
    public static void convertNbtToComparator(StructureTemplate structureTemplate, List<DefaultBlockList> defaultBlockLists) {
        List<StructureTemplate.PalettedBlockInfoList> blockInfoLists = ((StructureTemplateMixin) structureTemplate).getBlockInfoLists();
        Map<Pair<BlockState, NbtCompound>, List<BlockPos>> blockStateToPositionsMap = new HashMap<>();
        for (StructureTemplate.PalettedBlockInfoList palettedList : blockInfoLists) {
            for (StructureTemplate.StructureBlockInfo blockInfo : palettedList.getAll()) {
                BlockState blockState = blockInfo.state();
                BlockPos blockPos = blockInfo.pos();
                NbtCompound tag = blockInfo.nbt();

                //Pair<BlockState, NbtCompound> stateAndTagPair = new Pair<>(blockState/*, tag*/);

                //blockStateToPositionsMap.computeIfAbsent(stateAndTagPair, k -> new ArrayList<>()).add(blockPos);
            }
        }

        for (Map.Entry<Pair<BlockState, NbtCompound>, List<BlockPos>> entry : blockStateToPositionsMap.entrySet()) {
            BlockState blockState = entry.getKey().getFirst();
            NbtCompound tag = entry.getKey().getSecond();
            List<BlockPos> positions = entry.getValue();

            //defaultBlockLists.add(new DefaultBlockList(positions, blockState/*/*, tag*/*/));
        }
    }

    public static void convertNbtToComparator(StructureTemplate structureTemplate, CompoundBlockListComparator comparator, StructurePlacementData data, StructureWorldAccess world, BlockPos pos) {
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

            comparator.put(new CompoundBlockList(positions, blockState, tag));
        }
    }

    public static void convertNbtToComparator(StructureTemplate structureTemplate, FullBlockListComparator comparator, StructurePlacementData data, StructureWorldAccess world, BlockPos pos) {
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

            comparator.put(new FullBlockList(positions, blockState, tag));
        }
    }


    public static <T extends BlockListComparator<U, V, W, X>, U extends DefaultBlockList, V, W extends OrderedBlockListComparator<X>, X> void place(StructureWorldAccess world, StructurePlaceAnimator animator, T comparator, BlockPos block, boolean force, Set<Block> blockToForce, Set<Block> blockToSkip, float integrity) {
        //avoid errors due to aberrant values
        if (integrity < 0) integrity = 0;
        if (integrity > 1) integrity = 1;

        Iterator<U> iterator = comparator.get().iterator();
        while (iterator.hasNext()) {
            U defaultBlockList = iterator.next();
            BlockState blockState = defaultBlockList.getBlockState();

            if (blockState.isOf(Blocks.JIGSAW) || blockState.isOf(Blocks.STRUCTURE_BLOCK) || blockState.isOf(Blocks.STRUCTURE_VOID) || blockState.isOf(Blocks.AIR))
                iterator.remove();

            if (blockToSkip != null && !blockToSkip.isEmpty())
                if (blockToSkip.contains(blockState.getBlock()))
                    iterator.remove();

            //NbtCompound tag = defaultBlockList.getTag();

            boolean bl1 = blockToForce == null || blockToForce.isEmpty();
            for (BlockPos pos : defaultBlockList.getPosList()) {
                defaultBlockList.replacePos(pos, pos.add(block));
                if (!bl1 || force) {
                    if (integrity < 1f) {
                        if (MathUtil.getRandomBoolean(integrity)) {
                            defaultBlockList.removePos(pos);
                        }
                    }
                    if (!BlockPlaceUtil.verifyBlock(world, force, blockToForce, pos.add(block))) {
                        defaultBlockList.removePos(pos);
                    }
                }
            }

        }
        //we place the structure depending on if the animator is present or not
        if (animator != null) {
            animator.placeFromBlockList(comparator);
        } else {
            for (U blockList : comparator.get()) {
                BlockState blockState = blockList.getBlockState();
                for (BlockPos pos : blockList.getPosList()) {
                    world.setBlockState(pos, blockState, 3);
                    if (blockList instanceof CompoundBlockList cmp) {
                        NbtCompound tag = cmp.getTag();
                        if (tag != null) {
                            BlockEntity blockEntity = world.getBlockEntity(pos);
                            if (blockEntity != null) {
                                DynamicRegistryManager registry =  world.getRegistryManager();
                                NbtCompound currentNbt = blockEntity.createNbtWithIdentifyingData(registry);
                                currentNbt.copyFrom(tag);

                                blockEntity.read(currentNbt, registry);
                                blockEntity.markDirty();
                            }
                        }
                    }

                }
            }
        }

    }

}
