package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/**
 * class to manage a list of DefaultBlockList automatically
 */
@SuppressWarnings("unused")
public class BlockListManager {
    /**
     * the List of BlockList that are managed
     */
    protected List<BlockList> blockLists = new ArrayList<>();

    /**
     * Map used for better performance:
     * <ul>
     * <li>Instead of searching for a {@code BlockSate} inside the list, we use {@code indexes}.
     * <li>The map allows us to get the place of the related {@code BlockState} in the {@code posList}.
     * <li>Instead of having a time complexity of O(n), we get O(1) by using only a small amount of memory in more.
     * </ul>
     * <p> We use a Shorts to make the link between the {@code Pair<BlockState, NbtCompound>} and the {@code List<BlockList>}.
     * In the case where no {@link NbtCompound} is present, we put a {@code null} value to it.
     * <p>
     * Using short as a link allow us to save two bytes of data for each {@link BlockState}. Since that it is highly unprobable that more than 32 000 {@link BlockState} are used, shorts are enough.
     * <p>
     * </p>
     */
    protected List<Pair<BlockState, NbtCompound>> stateIndexes = new ArrayList<>();

    protected Object2ShortArrayMap<Pair<BlockState, NbtCompound>> blockDataMap = new Object2ShortArrayMap<>();

    /**
     * init a comparator
     *
     * @param comparator the comparator that will be fused
     */
    public BlockListManager(BlockListManager comparator) {
        this.blockLists = comparator.blockLists;
        this.stateIndexes = comparator.stateIndexes;
        this.blockDataMap = comparator.blockDataMap;
    }

    /**
     * init a comparator
     *
     * @param blockList the list of blockList that will be indexed
     */
    public BlockListManager(List<BlockList> blockList) {
        put(blockList);
    }

    /**
     * init a comparator
     *
     * @param blockList a blockList that will be indexed
     */
    public BlockListManager(BlockList blockList) {
        put(blockList);
    }

    /**
     * init an empty comparator
     */
    public BlockListManager() {
    }

    public BlockList get(int index) {
        return blockLists.get(index);
    }

    public List<BlockList> getAll() {
        return blockLists;
    }

    public BlockList getFirst() {
        return blockLists.getFirst();
    }

    public BlockList getLast() {
        return blockLists.getLast();
    }

    public BlockState getBlockState(int index) {
        return stateIndexes.get(index).getLeft();
    }

    public short size() {
        return (short) blockLists.size();
    }

    public BlockListManager put(BlockState state, NbtCompound tag, LongArrayList pos) {
        Pair<BlockState, NbtCompound> blockData = new Pair<>(state, tag);
        if (this.blockDataMap.containsKey(blockData)) {
            short index = this.blockDataMap.getShort(blockData);
            this.blockLists.get(index).addAll(pos);
            return this;
        }
        short index = size();
        this.blockDataMap.put(blockData, index);
        this.blockLists.add(new BlockList(state, tag, pos));
        return this;
    }

    public BlockListManager put(BlockState state, NbtCompound tag, long pos) {
        return put(state, tag, LongArrayList.of(pos));
    }

    public BlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList) {
        return put(state, tag, LongPosHelper.encodeBlockPos(posList));
    }

    public BlockListManager put(BlockState state, NbtCompound tag, BlockPos pos) {
        return put(state, tag, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public BlockListManager put(BlockState state, LongArrayList pos) {
        return put(state, null, pos);
    }

    public BlockListManager put(BlockState state, long pos) {
        return put(state, null, LongArrayList.of(pos));
    }

    public BlockListManager put(BlockState state, List<BlockPos> posList) {
        return put(state, null, LongPosHelper.encodeBlockPos(posList));
    }

    public BlockListManager put(BlockState state, BlockPos pos) {
        return put(state, null, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public BlockListManager put(BlockList[] blockLists) {
        for (BlockList blockList : blockLists) {
            put(blockList);
        }
        return this;
    }

    public BlockListManager put(List<BlockList> blockLists) {
        for (BlockList blockList : blockLists) {
            put(blockList);
        }
        return this;
    }

    public BlockListManager put(BlockList blockList) {
        BlockState state = blockList.getBlockState();
        NbtCompound tag = blockList.getTag().isPresent() ? blockList.getTag().get() : null;
        Pair<BlockState, NbtCompound> blockData = new Pair<>(state, tag);

        if (this.blockDataMap.containsKey(blockData)) {
            short index = this.blockDataMap.getShort(blockData);
            this.blockLists.get(index).addAll(blockList.getPosList());
            return this;
        }
        short index = size();
        this.blockDataMap.put(blockData, index);
        this.stateIndexes.add(blockData);
        this.blockLists.add(blockList);
        return this;
    }

    public BlockListManager put(BlockListManager manager) {
        for (BlockList blockList : manager.blockLists) {
            put(blockList);
        }
        return this;
    }

    public OrderedBlockListManager getOrdered() {
        return new OrderedBlockListManager(this);
    }

    public OrderedBlockListManager getOrderedSorted(BlockSorter.BlockSorterType type) {
        return new BlockSorter(type).sortOrderedBlockList(new OrderedBlockListManager(this));
    }

    @Override
    public String toString() {
        return this.blockLists.toString();
    }

    public BlockListManager sort(BlockSorter.BlockSorterType type) {
        BlockSorter sorter = new BlockSorter(type);
        sorter.sortInsideBlockList(this);
        return this;
    }


    public JsonArray toJson(ChunkPos chunkPos) {
        return toJson(chunkPos, new BlockPos(0, 0, 0));
    }

    public JsonArray toJson(ChunkPos chunkPos, BlockPos offset) {
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CompletionService<JsonObject> completionService = new ExecutorCompletionService<>(executorService);

        List<Future<JsonObject>> futures = new ArrayList<>();

        for (BlockList blockList : blockLists) {
            futures.add(completionService.submit(() -> blockList.toJson(offset, chunkPos)));
        }

        for (Future<JsonObject> future : futures) {
            try {
                jsonArray.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.fillInStackTrace();
            }
        }
        executorService.shutdown();

        return jsonArray;
    }

    public void toJson(ChunkPos chunkPos, Path path) {
        toJson(chunkPos, new BlockPos(0, 0, 0));
    }

    public void toJson(ChunkPos chunkPos, Path path, BlockPos offset) {
        Gson gson = new Gson();
        JsonArray jsonArray = toJson(chunkPos, offset);
        try {
            Files.writeString(path, gson.toJson(jsonArray));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

}
