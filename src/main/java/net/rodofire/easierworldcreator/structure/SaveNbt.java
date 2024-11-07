package net.rodofire.easierworldcreator.structure;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.mixin.PalettedBlockInfoListMixin;
import net.rodofire.easierworldcreator.mixin.StructureTemplateMixin;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SaveNbt {

    /**
     * This method converts the List of BlockList to a nbt file.
     * <p>
     * That is a way to generate a really large structure.
     * Since that the nbt is saved during world gen, no block entity should be present.
     * </p>
     * <p>
     * The methods receive a list of blockList.
     * The list represents all the blocks of the generated structure.
     * for every BlockPos and BlockStates, the method verify the chunk it belongs to and add it to the Map chunkBlockInfoMap.
     * This divides the structure into chunks that will be saved just after converting the first list into a {@link StructureTemplate.PalettedBlockInfoList}
     * The Structure will be located in the following path : [save_name]/generated/easierworldcreator/[chunk.x-chunk.z]/custom_feature_[Random long]
     * </p>
     *
     * @param blockLists a list of BlockList to save it into the nbt file
     */
    @SuppressWarnings("UnreachableCode")
    public static void saveNbtDuringWorldGen(StructureWorldAccess world, List<BlockList> blockLists, String featureName) {
        Map<ChunkPos, List<StructureTemplate.StructureBlockInfo>> chunkBlockInfoMap = new HashMap<>();

        List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
        List<StructureTemplate.StructureBlockInfo> list2 = Lists.newArrayList();
        List<StructureTemplate.StructureBlockInfo> list3 = Lists.newArrayList();

        for (BlockList blocks : blockLists) {
            BlockState blockState = blocks.getBlockState();
            for (BlockPos pos : blocks.getPosList()) {
                StructureTemplate.StructureBlockInfo structureBlockInfo = new StructureTemplate.StructureBlockInfo(pos, blockState, null);
                categorize(structureBlockInfo, list, list2, list3);

                ChunkPos chunkPos = new ChunkPos(pos);

                chunkBlockInfoMap.computeIfAbsent(chunkPos, k -> new ArrayList<>()).add(structureBlockInfo);
            }
        }
        MinecraftServer server = world.getServer();
        if (server == null) return;

        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();

        for (Map.Entry<ChunkPos, List<StructureTemplate.StructureBlockInfo>> entry : chunkBlockInfoMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            List<StructureTemplate.StructureBlockInfo> blockInfos = entry.getValue();

            List<StructureTemplate.StructureBlockInfo> combinedList = StructureTemplateMixin.invokeCombineSorted(list, list2, list3);

            List<StructureTemplate.PalettedBlockInfoList> blockInfoLists = new ArrayList<>();
            StructureTemplate.PalettedBlockInfoList palettedBlockInfoList = createPalettedBlockInfoList(combinedList);
            blockInfoLists.add(palettedBlockInfoList);

            Identifier templateName = new Identifier(Easierworldcreator.MOD_ID,
                    chunkPos.x + "-" + chunkPos.z + "/" + featureName);

            StructureTemplate structureTemplate;

            try {
                structureTemplate = structureTemplateManager.getTemplateOrBlank(templateName);
                ((StructureTemplateMixin) structureTemplate).getBlockInfoLists().clear();
                ((StructureTemplateMixin) structureTemplate).getBlockInfoLists().addAll(blockInfoLists);

                structureTemplateManager.saveTemplate(templateName);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }


    private static void categorize(
            StructureTemplate.StructureBlockInfo blockInfo,
            List<StructureTemplate.StructureBlockInfo> fullBlocks,
            List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
            List<StructureTemplate.StructureBlockInfo> otherBlocks
    ) {
        if (blockInfo.nbt() != null) {
            blocksWithNbt.add(blockInfo);
        } else if (!blockInfo.state().getBlock().hasDynamicBounds() && blockInfo.state().isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
            fullBlocks.add(blockInfo);
        } else {
            otherBlocks.add(blockInfo);
        }
    }



    /**
     * when you want, you can remove the file
     *
     * @param nbtList the list of the Identifier related to every structure that has to be removed
     */
    public static void removeNbtFiles(List<Identifier> nbtList) {
        for (Identifier nbt : nbtList) {
            try {
                Path filePath = Path.of(nbt.getPath());
                Files.delete(filePath);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }


    public static List<Identifier> loadNBTFiles(Chunk chunk) {
        return loadNBTFiles(chunk.getPos());
    }

    /**
     * gives you a list of "path" of nbt files related to a chunkPos
     *
     * @param chunk the chunk of the nbt
     * @return the list of "path"
     */
    public static List<Identifier> loadNBTFiles(ChunkPos chunk) {
        List<Identifier> nbtList = new ArrayList<>();
        String chunkFolderPath = new Identifier(Easierworldcreator.MOD_ID, "generated/structures/" + chunk.x + "_" + chunk.z + "/").getPath();
        try {
            Path path = Path.of(chunkFolderPath);
            if (Files.exists(path) && Files.isDirectory(path)) {
                Files.list(path).forEach(filePath -> {
                    if (filePath.toString().endsWith(".nbt")) {
                        nbtList.add(new Identifier(Easierworldcreator.MOD_ID, chunk.x + "_" + chunk.z + "/" + filePath.getFileName().toString()));
                    }
                });
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        return nbtList;
    }

    /**
     * avoid errors when using the mixin
     *
     * @param combinedList the list that will be converted to {@link StructureTemplate.PalettedBlockInfoList}
     * @return the {@link StructureTemplate.PalettedBlockInfoList} related to the list
     */
    @Unique
    public static StructureTemplate.PalettedBlockInfoList createPalettedBlockInfoList(List<StructureTemplate.StructureBlockInfo> combinedList) {
        return PalettedBlockInfoListMixin.invokeConstructor(combinedList);
    }

}
