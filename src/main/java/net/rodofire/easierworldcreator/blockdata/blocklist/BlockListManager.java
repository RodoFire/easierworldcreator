package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.*;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
     * <p> We use a Shorts to make the link between the {@code BlockState} and the {@code List<BlockList>}.
     * Sometimes, the {@link BlockList} would have {@link NbtCompound}, making the case possible where multiple {@code BlockState} are in common with different {@link NbtCompound}.
     * That's why we use {@link ShortArrayList}, that is used to link {@link BlockState} to {@link NbtCompound} and the {@code List<BlockList>}
     * <p>
     * Using short as a link allow us to save two bytes of data for each {@link BlockState}. Since that it is highly unprobable that more than 32 000 {@link BlockState} are used, shorts are enough.
     * <p> {@link NbtCompound} Share the same short as their {@link BlockState} to make the link. They are as {@code indexes} linked to a {@link ShortArrayList} in the case where 2 nbtCompounds are in common but for different blockState.
     * </p>
     */
    protected List<BlockState> stateIndexes = new ArrayList<>();
    protected final Map<BlockState, ShortSet> indexes = new HashMap<>();

    protected final Short2ObjectArrayMap<NbtCompound> tagValues = new Short2ObjectArrayMap<>();
    protected final Map<NbtCompound, ShortSet> invertedTagIndexes = new HashMap<>();

    /**
     * init a comparator
     *
     * @param comparator the comparator that will be fused
     */
    public BlockListManager(BlockListManager comparator) {
    }

    /**
     * init a comparator
     *
     * @param defaultBlockLists the list of blockList that will be indexed
     */
    public BlockListManager(List<BlockList> defaultBlockLists) {
    }

    /**
     * init a comparator
     *
     * @param defaultBlockList a blockList that will be indexed
     */
    public BlockListManager(BlockList defaultBlockList) {
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
        return stateIndexes.get(index);
    }

    public Set<BlockState> getBlockStateSet() {
        return this.indexes.keySet();
    }

    public int size() {
        return blockLists.size();
    }

    public BlockListManager put(BlockState state, LongArrayList pos, NbtCompound tag) {
        if (tag != null) {
            if (this.invertedTagIndexes.containsKey(tag) && this.indexes.containsKey(state)) {
                short index = getCommonIndex(this.invertedTagIndexes.get(tag), this.indexes.get(state));
                this.blockLists.get(index).addAll(pos);
                return this;
            }
            short index = (short) size();
            this.indexes.computeIfAbsent(state, k -> new ShortOpenHashSet()).add(index);
            this.invertedTagIndexes.computeIfAbsent(tag, k -> new ShortOpenHashSet()).add(index);
            return this;
        }

        if (this.indexes.containsKey(state)) {
            ShortSet set = this.indexes.get(state);
            short index = getIndexWithoutTag(set);
            if (index == -1) {
                index = (short) size();
                set.add(index);
                this.blockLists.add(new BlockList(state, pos));
                return this;
            }
            this.blockLists.get(index).addAll(pos);
            return this;
        }
        short index = (short) size();
        this.indexes.get(state).add(index);
        this.blockLists.add(new BlockList(state, pos));
        return this;
    }

    public BlockListManager put(BlockState state, NbtCompound tag, long pos) {
        return put(state, LongArrayList.of(pos), tag);
    }

    public BlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList) {
        return put(state, LongPosHelper.encodeBlockPos(posList), tag);
    }

    public BlockListManager put(BlockState state, NbtCompound tag, BlockPos pos) {
        return put(state, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)), tag);
    }

    public BlockListManager put(BlockState state, LongArrayList pos) {
        return put(state, pos, null);
    }

    public BlockListManager put(BlockState state, long pos) {
        return put(state, LongArrayList.of(pos), null);
    }

    public BlockListManager put(BlockState state, List<BlockPos> posList) {
        return put(state, LongPosHelper.encodeBlockPos(posList), null);
    }

    public BlockListManager put(BlockState state, BlockPos pos) {
        return put(state, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)), null);
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
        if (blockList.getTag().isPresent()) {
            NbtCompound tag = blockList.getTag().get();
            if (this.invertedTagIndexes.containsKey(tag) && this.indexes.containsKey(state)) {
                short index = getCommonIndex(this.invertedTagIndexes.get(tag), this.indexes.get(state));
                this.blockLists.get(index).addAll(blockList.getList());
                return this;
            }
            short index = (short) size();
            this.indexes.computeIfAbsent(state, k -> new ShortOpenHashSet()).add(index);
            this.invertedTagIndexes.computeIfAbsent(tag, k -> new ShortOpenHashSet()).add(index);
            return this;
        }

        if (this.indexes.containsKey(state)) {
            ShortSet set = this.indexes.get(state);
            short index = getIndexWithoutTag(set);
            if (index == -1) {
                index = (short) size();
                set.add(index);
                this.blockLists.add(blockList);
                return this;
            }
            this.blockLists.get(index).addAll(blockList.getList());
            return this;
        }
        short index = (short) size();
        this.indexes.get(state).add(index);
        this.blockLists.add(blockList);
        return this;
    }


    private short getCommonIndex(ShortSet set1, ShortSet set2) {
        if (set1 != null && set2 != null) {
            //we go through the smallest set, getting us a better performance.
            ShortSet smaller = set1.size() < set2.size() ? set1 : set2;
            ShortSet larger = set1.size() < set2.size() ? set2 : set1;

            for (ShortIterator it = smaller.iterator(); it.hasNext(); ) {
                short index = it.nextShort();
                if (larger.contains(index)) {
                    return index;
                }
            }
        }
        return -1;
    }

    private short getIndexWithoutTag(ShortSet set1) {
        for (short index : set1) {
            if (!this.tagValues.containsKey(index)) {
                return index;
            }
        }
        //in the case where every index of the BlockState is linked to a NbtCompound
        return -1;
    }


    @Override
    public String toString() {
        return this.blockLists.toString();
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
