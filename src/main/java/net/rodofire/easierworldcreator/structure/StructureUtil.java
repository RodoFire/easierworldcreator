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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.maths.MathUtil;
import net.rodofire.easierworldcreator.mixin.world.structure.StructureTemplateMixin;
import net.rodofire.easierworldcreator.shape.block.placer.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.util.BlockPlaceUtil;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.*;

@SuppressWarnings("unused")
public class StructureUtil {
    public static void convertNbtToManager(StructureTemplate structureTemplate, BlockListManager manager, StructurePlacementData data, StructureWorldAccess world, BlockPos pos) {
        List<StructureTemplate.StructureBlockInfo> list = data.getRandomBlockInfos(((StructureTemplateMixin) structureTemplate).getBlockInfoLists(), pos).getAll();
        List<StructureTemplate.StructureBlockInfo> blockInfoList = StructureTemplate.process(world, pos, new BlockPos(0, 0, 0), data, list);

        Map<Pair<BlockState, NbtCompound>, List<BlockPos>> blockStateToPositionsMap = new HashMap<>();
        for (StructureTemplate.StructureBlockInfo blockInfo : blockInfoList) {
            BlockState blockState = blockInfo.state();
            BlockPos blockPos = blockInfo.pos();
            NbtCompound tag = blockInfo.nbt();

            Pair<BlockState, NbtCompound> stateAndTagPair = new Pair<>(blockState, tag);

            blockStateToPositionsMap.computeIfAbsent(stateAndTagPair, k -> new ArrayList<>()).add(blockPos);
        }

        for (Map.Entry<Pair<BlockState, NbtCompound>, List<BlockPos>> entry : blockStateToPositionsMap.entrySet()) {
            BlockState blockState = entry.getKey().getFirst();
            NbtCompound tag = entry.getKey().getSecond();
            List<BlockPos> positions = entry.getValue();

            manager.put(new BlockList(blockState, tag, positions));
        }
    }


    public static void place(StructureWorldAccess world, StructurePlaceAnimator animator, BlockListManager manager, BlockPos block, boolean force, Set<Block> blockToForce, Set<Block> blockToSkip, float integrity) {
        //avoid errors due to aberrant values
        if (integrity < 0) integrity = 0;
        if (integrity > 1) integrity = 1;


        clean(world, manager, block, force, blockToForce, blockToSkip, integrity);
        //we place the structure depending on if the animator is present or not
        if (animator != null) {
            animator.place(manager);
        } else {
            for (BlockList blockList : manager.getAllBlockList()) {
                BlockState blockState = blockList.getState();
                for (long pos : blockList.getPosList()) {
                    BlockPos convertedPos = LongPosHelper.decodeBlockPos(pos);
                    world.setBlockState(convertedPos, blockState, 3);
                    if (blockList.getTag().isPresent()) {
                        NbtCompound tag = blockList.getTag().get();
                        BlockEntity blockEntity = world.getBlockEntity(convertedPos);
                        if (blockEntity != null) {
                            DynamicRegistryManager registry = world.getRegistryManager();
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

    private static void clean(StructureWorldAccess world, BlockListManager manager, BlockPos block, boolean force, Set<Block> blockToForce, Set<Block> blockToSkip, float integrity) {
        if (integrity != 1.0f) {
            Random random = world.getRandom();
            Iterator<BlockList> iterator = manager.getAllBlockList().iterator();
            while (iterator.hasNext()) {
                BlockList blockList = iterator.next();
                BlockState blockState = blockList.getState();

                if (blockState.isOf(Blocks.JIGSAW) || blockState.isOf(Blocks.STRUCTURE_BLOCK) || blockState.isOf(Blocks.STRUCTURE_VOID) || blockState.isOf(Blocks.AIR))
                    iterator.remove();

                if (blockToSkip != null && !blockToSkip.isEmpty())
                    if (blockToSkip.contains(blockState.getBlock()))
                        iterator.remove();

                boolean bl1 = blockToForce == null || blockToForce.isEmpty();
                int size = blockList.size();
                for (int i = 0; i < size; i++) {
                    long posLong = blockList.getLongPos(i);
                    BlockPos pos = LongPosHelper.decodeBlockPos(posLong);
                    blockList.replacePos(i, pos.add(block));
                    if (!bl1 || force) {
                        if (integrity < 1f) {
                            if (MathUtil.getRandomBoolean(random, integrity)) {
                                blockList.removePos(i);
                            }
                        }
                        if (!BlockPlaceUtil.verifyBlock(world, force, blockToForce, pos.add(block))) {
                            blockList.removePos(i);
                        }
                    }
                }
            }
        } else {
            Random random = world.getRandom();
            Iterator<BlockList> iterator = manager.getAllBlockList().iterator();
            while (iterator.hasNext()) {
                BlockList blockList = iterator.next();
                BlockState blockState = blockList.getState();

                if (blockState.isOf(Blocks.JIGSAW) || blockState.isOf(Blocks.STRUCTURE_BLOCK) || blockState.isOf(Blocks.STRUCTURE_VOID) || blockState.isOf(Blocks.AIR))
                    iterator.remove();

                if (blockToSkip != null && !blockToSkip.isEmpty())
                    if (blockToSkip.contains(blockState.getBlock()))
                        iterator.remove();

                //NbtCompound tag = defaultBlockList.getTag();

                boolean bl1 = blockToForce == null || blockToForce.isEmpty();
                int size = blockList.size();
                for (int i = 0; i < size; i++) {
                    long posLong = blockList.getLongPos(i);
                    BlockPos pos = LongPosHelper.decodeBlockPos(posLong);
                    blockList.replacePos(i, pos.add(block));
                    if (!bl1 || force) {
                        if (!BlockPlaceUtil.verifyBlock(world, force, blockToForce, pos.add(block))) {
                            blockList.removePos(i);
                        }
                    }
                }
            }

        }
    }

}
