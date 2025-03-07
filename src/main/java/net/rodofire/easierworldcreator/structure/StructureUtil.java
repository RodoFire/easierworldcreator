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

}
