package net.rodofire.easierworldcreator.structure;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.mixin.StructureTemplateMixin;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import net.rodofire.easierworldcreator.worldgenutil.BlockPlaceUtil;

import java.util.*;

@SuppressWarnings("unused")
public class StructureUtil {
    /**
     * This method is used to get every block in a structure.
     *
     * @param world the world the structure is being used
     * @param templateName the identifier of the structure
     * @return the List of blockList that compose the structure
     */
    public static List<BlockList> convertNbtToBlockList(StructureWorldAccess world, Identifier templateName) {
        List<BlockList> blockLists = new ArrayList<>();

        MinecraftServer server = world.getServer();
        if(server == null) {
            return blockLists;
        }
        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();
        if (structureTemplateManager == null) {
            return null;
        }

        Optional<StructureTemplate> optionalTemplate = structureTemplateManager.getTemplate(templateName);

        if (optionalTemplate.isPresent()) {
            StructureTemplate structureTemplate = optionalTemplate.get();

            Map<Pair<BlockState, NbtCompound>, List<BlockPos>> blockStateToPositionsMap = new HashMap<>();

            List<StructureTemplate.PalettedBlockInfoList> blockInfoLists = ((StructureTemplateMixin) structureTemplate).getBlockInfoLists();

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
        } else {
            Easierworldcreator.LOGGER.error("cannot get structure template, wrong Identifier : {}", templateName);
        }
        return blockLists;
    }


    public static void place(StructureWorldAccess world, List<BlockList> blockLists, BlockPos block, boolean force, Set<Block> blockToForce, List<Block> blockToSkip, float integrity) {
        if (integrity < 0) integrity = 0;
        if (integrity > 1) integrity = 1;
        for (BlockList blockList : blockLists) {
            BlockState blockState = blockList.getBlockState();
            if (blockState.isOf(Blocks.JIGSAW) || blockToSkip.contains(blockState.getBlock())) continue;
            NbtCompound tag = blockList.getTag();
            for (BlockPos pos : blockList.getPosList()) {
                if (integrity <= 1f) {
                    if (Random.create().nextFloat() > integrity) {
                        continue;
                    }
                }
                if (BlockPlaceUtil.verifyBlock(world, force, blockToForce, pos.add(block))) {
                    world.setBlockState(pos.add(block), blockState, 3);
                    if (tag != null) {
                        BlockEntity blockEntity = world.getBlockEntity(pos.add(block));
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


    public static void place(StructureWorldAccess world, Identifier templateName, float integrity, Chunk chunk) {
        place(world, templateName, integrity, chunk.getPos().getStartPos(), new BlockPos(0, 0, 0), BlockMirror.NONE, BlockRotation.NONE, true);
    }
    public static void place(StructureWorldAccess world, Identifier templateName, float integrity, ChunkPos chunk) {
        place(world, templateName, integrity, chunk.getStartPos(), new BlockPos(0, 0, 0), BlockMirror.NONE, BlockRotation.NONE, true);
    }


    /**
     * this method allows you to place a structure in the world during world gen or not
     *
     * @param world          the world the structure will spawn in
     * @param templateName   the identifier of the structure
     * @param integrity      the integrity of the structure must be between 0f and 1f
     * @param pos            the pos of the structure
     * @param offset         the offset of the pos structure
     * @param mirror         the block mirror if wanted
     * @param rotation       the structure rotation if wanted
     * @param ignoreEntities ignore entities of the structure
     */
    public static void place(StructureWorldAccess world, Identifier templateName, float integrity, BlockPos pos, BlockPos offset, BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities) {
        MinecraftServer server = world.getServer();
        if(server == null) {
            Easierworldcreator.LOGGER.error("cannot get structure template, MinecraftServer is null. Structure template: {}", templateName);
            return;
        }
        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();
        if (structureTemplateManager == null) return;

        Optional<StructureTemplate> optional;
        try {
            optional = structureTemplateManager.getTemplate(templateName);
        } catch (InvalidIdentifierException var6) {
            return;
        }

        //if the structure exists, we initialize the structure placement
        if (optional.isPresent()) {
            StructurePlacementData structurePlacementData = new StructurePlacementData()
                    .setMirror(mirror)
                    .setRotation(rotation)
                    .setIgnoreEntities(ignoreEntities);
            if (integrity < 1.0F) {
                structurePlacementData.clearProcessors()
                        .addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(integrity, 0.0F, 1.0F)))
                        .setRandom(StructureBlockBlockEntity.createRandom(world.getSeed()));
            }
            StructureTemplate structureTemplate = optional.get();
            structureTemplate.place(world, pos, offset, structurePlacementData, world.getRandom(), 3);
        }

    }
}