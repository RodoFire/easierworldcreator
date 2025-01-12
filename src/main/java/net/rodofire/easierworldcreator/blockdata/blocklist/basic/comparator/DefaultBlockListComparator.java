package net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.DefaultOrderedBlockListComparator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * class to manage a list of DefaultBlockList automatically
 */
@SuppressWarnings("unused")
public class DefaultBlockListComparator extends AbstractBlockListComparator<DefaultBlockList, Integer, DefaultOrderedBlockListComparator, BlockState> {
    /**
     * init a comparator
     *
     * @param comparator the comparator that will be fused
     */
    public DefaultBlockListComparator(DefaultBlockListComparator comparator) {
        super(comparator);
    }

    /**
     * init a comparator
     *
     * @param defaultBlockLists the list of blockList that will be indexed
     */
    public DefaultBlockListComparator(List<DefaultBlockList> defaultBlockLists) {
        super(defaultBlockLists);
    }

    /**
     * init a comparator
     *
     * @param defaultBlockList a blockList that will be indexed
     */
    public DefaultBlockListComparator(DefaultBlockList defaultBlockList) {
        super(defaultBlockList);
    }

    /**
     * init an empty comparator
     */
    public DefaultBlockListComparator() {
    }


    /**
     * method tu initialize the indexes.
     */
    @Override
    protected void initIndexes() {
        for (DefaultBlockList defaultBlockList : this.blockLists) {
            this.indexes.put(defaultBlockList.getBlockState(), this.indexes.size());
        }
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state the state that will be tested
     * @param pos   the pos that you want to use
     */
    public void put(BlockState state, BlockPos pos) {
        put(state, List.of(pos));
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state   the state that will be tested
     * @param posList the list of pos that you want to use
     */
    public void put(BlockState state, List<BlockPos> posList) {
        if (indexes.containsKey(state)) {
            blockLists.get(indexes.get(state)).addBlockPos(posList);
            return;
        }
        indexes.put(state, indexes.size());
        blockLists.add(new DefaultBlockList(posList, state));
    }


    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param type the BlockList that will be added in the comparator
     */
    @Override
    public void put(DefaultBlockList type) {
        BlockState state = type.getBlockState();
        if (this.indexes.containsKey(state)) {
            this.blockLists.get(this.indexes.get(state)).addBlockPos(type.getPosList());
            return;
        }
        this.blockLists.add(type);
    }

    /**
     * <p>Method to clean a blockList.
     * <p>In the case, there are multiple common BlockState.
     * <p>All the blockPos common of a BlockState will be fused in a single BlockState
     *
     * @param blockList the blockList that will bea cleaned
     * @return the cleaned version of the list
     */
    @Override
    public List<DefaultBlockList> getCleaned(List<DefaultBlockList> blockList) {
        List<DefaultBlockList> cleanedList = new ArrayList<>();
        for (DefaultBlockList defaultBlockList : blockList) {
            BlockState state = defaultBlockList.getBlockState();
            if (this.indexes.containsKey(state)) {
                cleanedList.get(this.indexes.get(state)).addBlockPos(defaultBlockList.getPosList());
                continue;
            }
            cleanedList.add(new DefaultBlockList(defaultBlockList.getPosList(), state));
            this.indexes.put(state, this.indexes.size());

        }
        return cleanedList;
    }

    /**
     * Method to get the ordered version of the comparator
     *
     * @return the ordered version
     */
    @Override
    public DefaultOrderedBlockListComparator getOrdered() {
        DefaultOrderedBlockListComparator comparator = new DefaultOrderedBlockListComparator();
        for (DefaultBlockList blockList : this.blockLists) {
            comparator.put(blockList.getBlockState(), blockList.getPosList());
        }
        return comparator;
    }

    public void toJson(Path path, BlockPos offset) {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject = new JsonObject();

        int offsetX = offset.getX();
        int offsetY = offset.getY();
        int offsetZ = offset.getZ();
        char chunkOffsetX = (char) (offsetX % 16);
        char chunkOffsetZ = (char) (offsetZ % 16);

        for (DefaultBlockList defaultBlockList : this.blockLists) {
            jsonObject.addProperty("state", defaultBlockList.getBlockState().toString());
            int size = defaultBlockList.getPosList().size();
            int[] compactPositions = new int[size];
            for (int i = 0; i < size; i++) {
                BlockPos pos = defaultBlockList.getPosList().get(i);
                char posX = (char) Math.floorMod(pos.getX() + chunkOffsetX, 16);
                char posZ = (char) Math.floorMod(pos.getZ() + chunkOffsetZ, 16);
                int compactPos = (posX << 24) | ((short) pos.getY() << 8) | posZ;
                compactPositions[i] = compactPos;
            }

            // Ajout toutes les données en une seule fois sous forme de JsonArray
            JsonArray compactJsonPositions = gson.toJsonTree(compactPositions).getAsJsonArray();
            jsonObject.add("positions", compactJsonPositions);
            jsonArray.add(jsonObject);
        }
        try {
            Files.writeString(path, gson.toJson(jsonArray));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
