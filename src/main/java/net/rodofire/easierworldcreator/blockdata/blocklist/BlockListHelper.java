package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.util.BlockStateUtil;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.*;

@SuppressWarnings("unused")
public class BlockListHelper {
    //TODO use divide to reign for better performance

    /**
     * method to combine a number of {@code List<BlockList>} superior to 2
     *
     * @param lists the list to combine
     * @return a {@code List<BlockList>} that correspond to the combined List
     */
    @SafeVarargs
    public static List<BlockList> combineNBlockList(List<BlockList>... lists) {
        if (lists.length == 0) {
            return new ArrayList<>();
        }
        if (lists.length == 1) {
            return new ArrayList<>(lists[0]);
        }
        return Arrays.stream(lists).parallel()
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    List<BlockList> result = new ArrayList<>(list1);
                    combine2BlockList(result, list2);
                    return result;
                });
    }


    /**
     * method to sort a BlockList
     *
     * @param blockList the blockList that will be sorted
     * @param sorter    the sorter object that will be used to get the sorted BlockPos
     * @return the sorted BlockList
     */
    public static BlockListManager getSorted(List<BlockList> blockList, BlockSorter sorter) {
        BlockListManager sortedList = new BlockListManager(blockList);
        sorter.sortInsideBlockList(sortedList);
        return sortedList;
    }

    /**
     * method to combine 2 {@code List<BlockList>}
     *
     * @param defaultBlockList1 the first list that will contain the modifications
     * @param defaultBlockList2 the second list that will get merged
     */
    public static void combine2BlockList(List<BlockList> defaultBlockList1, List<BlockList> defaultBlockList2) {
        Map<BlockState, Integer> blockStateIndexMap = new HashMap<>();
        int i = 0;
        for (BlockList list : defaultBlockList1) {
            blockStateIndexMap.put(list.getBlockState(), i++);
        }
        for (BlockList list : defaultBlockList2) {
            BlockState state = list.getBlockState();
            if (blockStateIndexMap.containsKey(state)) {
                int index = blockStateIndexMap.get(state);
                defaultBlockList1.get(index).addAll(list.getPosList());
            } else {
                defaultBlockList1.add(list);
            }

        }
    }

    public static BlockListManager fromJson(StructureWorldAccess worldAccess, JsonArray jsonArray, ChunkPos chunkPos) {
        BlockListManager manager = new BlockListManager();
        Gson gson = new Gson();

        System.out.println("from json");

        for (JsonElement jsonElement : jsonArray) {
            BlockList blockList = new BlockList();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            BlockState state = BlockStateUtil.parseBlockState(worldAccess, jsonObject.get("state").getAsString());

            blockList.setBlockState(state);


            if (jsonObject.has("force")) {
                boolean force = jsonObject.get("force").getAsBoolean();
                blockList.manager.setForce(force);
            }
            if (jsonObject.has("overriddenBlock")) {
                Set<Block> overriddenBlocks = gson.fromJson(jsonObject.get("overriddenBlock"), new TypeToken<Set<Block>>() {
                }.getType());
                blockList.manager.setOverriddenBlocks(overriddenBlocks);
            }

            // Récupération du tag (optionnel)
            if (jsonObject.has("tag")) {
                String tagString = jsonObject.get("tag").getAsString();
                try {
                    NbtCompound tag = StringNbtReader.parse(tagString);
                    blockList.setTag(tag);
                } catch (Exception e) {
                    Ewc.LOGGER.info("cannot parse NbtCompound");
                    e.fillInStackTrace();
                }
            }

            JsonArray positionsArray = jsonObject.getAsJsonArray("positions");
            int[] compactPositions = gson.fromJson(positionsArray, int[].class);

            int chunkMinX = chunkPos.x << 4;
            int chunkMinZ = chunkPos.z << 4;

            for (int compactPos : compactPositions) {
                int relX = ((compactPos >> 21) & 0x7FF);
                int relY = ((compactPos >> 11) & 0x3FF) - 512;
                int relZ = (compactPos & 0x7FF);

                // Ajustement pour les valeurs signées
                if (relX >= 1024) relX -= 2048;
                if (relZ >= 1024) relZ -= 2048;

                int x = relX + chunkMinX;
                int y = relY;
                int z = relZ + chunkMinZ;

                blockList.add(LongPosHelper.encodeBlockPos(x, y, z));
            }
            manager.put(blockList);

        }
        return manager;
    }
}
