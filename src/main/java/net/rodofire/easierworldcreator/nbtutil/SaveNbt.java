package net.rodofire.easierworldcreator.nbtutil;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
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
import java.nio.file.Paths;
import java.util.*;

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
            BlockState blockState = blocks.getBlockstate();
            for (BlockPos pos : blocks.getPoslist()) {
                StructureTemplate.StructureBlockInfo structureBlockInfo = new StructureTemplate.StructureBlockInfo(pos, blockState, null);
                categorize(structureBlockInfo, list, list2, list3);

                ChunkPos chunkPos = new ChunkPos(pos);

                chunkBlockInfoMap.computeIfAbsent(chunkPos, k -> new ArrayList<>()).add(structureBlockInfo);
            }
        }

        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();

        // Parcourir chaque ChunkPos et sauvegarder les blocs correspondants
        for (Map.Entry<ChunkPos, List<StructureTemplate.StructureBlockInfo>> entry : chunkBlockInfoMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            List<StructureTemplate.StructureBlockInfo> blockInfos = entry.getValue();

            // Combine les listes list, list2, et list3 pour ce chunk spécifique
            List<StructureTemplate.StructureBlockInfo> combinedList = StructureTemplateMixin.invokeCombineSorted(list, list2, list3);

            // Créez la liste PalettedBlockInfoList pour ce chunk
            List<StructureTemplate.PalettedBlockInfoList> blockInfoLists = new ArrayList<>();
            StructureTemplate.PalettedBlockInfoList palettedBlockInfoList = createPalettedBlockInfoList(combinedList);
            blockInfoLists.add(palettedBlockInfoList);

            // Créer un nom unique pour le template basé sur la position du chunk
            Identifier templateName = new Identifier(Easierworldcreator.MOD_ID,
                    chunkPos.x + "-" + chunkPos.z + "/" + featureName);

            StructureTemplate structureTemplate = new StructureTemplate();

            try {
                // Charger ou créer un template existant pour ce chunk
                structureTemplate = structureTemplateManager.getTemplateOrBlank(templateName);
                ((StructureTemplateMixin) structureTemplate).getBlockInfoLists().clear();
                ((StructureTemplateMixin) structureTemplate).getBlockInfoLists().addAll(blockInfoLists);

                // Sauvegarde du template pour ce chunk
                structureTemplateManager.saveTemplate(templateName);
            } catch (Exception e) {
                e.printStackTrace();  // Gestion des erreurs
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

    public static void generateNbtFiles(StructureWorldAccess world, List<Identifier> nbtlist, Chunk chunk) {
        for (Identifier nbt : nbtlist) {
            StructureUtil.place(world, nbt, 1f, chunk);
        }
    }

    public static void generateNbtFiles(StructureWorldAccess world, List<Identifier> nbtlist, ChunkPos chunk) {
        for (Identifier nbt : nbtlist) {
            StructureUtil.place(world, nbt, 1f, chunk);
        }
    }

    /**
     * when you want, you can remove the file
     * @param nbtlist
     */
    public static void removeNbtFiles(List<Identifier> nbtlist) {
        for (Identifier nbt : nbtlist) {
            try {
                Path filePath = Path.of(nbt.getPath());
                Files.delete(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<Identifier> loadNBTFiles(Chunk chunk) {
        return loadNBTFiles(chunk.getPos());
    }

    public static List<Identifier> loadNBTFiles(ChunkPos chunk) {
        List<Identifier> nbtList = new ArrayList<>();
        String chunkFolderPath = new Identifier(Easierworldcreator.MOD_ID, "generated/structures/" + chunk.x + "_" + chunk.z + "/").getPath();
        try {
            if (Files.exists(Path.of(chunkFolderPath)) && Files.isDirectory(Path.of(chunkFolderPath))) {
                Files.list(Paths.get(chunkFolderPath)).forEach(filePath -> {
                    if (filePath.toString().endsWith(".nbt")) {
                        nbtList.add(new Identifier(Easierworldcreator.MOD_ID, chunk.x + "_" + chunk.z + "/" + filePath.getFileName().toString()));
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
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
