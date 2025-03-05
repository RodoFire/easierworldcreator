package net.rodofire.easierworldcreator.blockdata.blocklist;

import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongShortImmutablePair;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.BlockDataKey;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.util.BlockPlaceUtil;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OrderedBlockListManager class.
 * It allows you to sort the positions no matter the {@code BlockData}.
 * <p>It represents a final state of the BlockListManager,
 * meaning that no modifications can be done after putting BlockData.
 * For example, you cannot change a state related to a blockPos.
 * You cannot get The BlockPos related to a `BlockData`.
 * If you want to do something like this, use a {@code BlockListManager}.
 * <p>While you can combine and put other Ordered / Base BlockListManager,
 * it is not recommended as it has some important performance.
 * You should combine BlockListManager then convert it to ordered.
 */
@SuppressWarnings("unused")
public class OrderedBlockListManager {

    /**
     * blockData objects
     */
    List<BlockDataKey> state = new ArrayList<>();
    Object2ShortOpenHashMap<BlockDataKey> blockDataMap = new Object2ShortOpenHashMap<>();

    Short2ReferenceOpenHashMap<StructurePlacementRuleManager> ruler = new Short2ReferenceOpenHashMap<>();

    /**
     * BlockPos objects.
     * <li> {@code long} represent the encoded {@link BlockPos} to save some memory and improve performance.
     * <li> {@code int} represents the index of the {@link List} that correponds to the encoded BlockPos
     */
    List<LongShortImmutablePair> posListOptimized = new ArrayList<>();


    /**
     * used for placing blocks
     */
    ServerChunkManager chunkManager;
    boolean markDirty = false;
    boolean init = false;

    /**
     * constructor to init a {@link OrderedBlockListManager}.
     *
     * @param manager the manager to be fused
     */
    public OrderedBlockListManager(OrderedBlockListManager manager) {
        this.state = manager.state;
        this.ruler = manager.ruler;
        this.blockDataMap = manager.blockDataMap;
        this.posListOptimized = manager.posListOptimized;
    }

    /**
     * constructor to init a {@link OrderedBlockListManager}.
     *
     * @param manager the manager to be fused
     */
    public OrderedBlockListManager(BlockListManager manager) {

        //we init at a good size to avoid computing intensive rehash
        this.posListOptimized = new ArrayList<>(manager.totalSize());


        for (BlockList blockList : manager.blockLists) {
            //we don't use put() to avoid temporary objects allocations.
            // These are done to avoid rehash done when adding into the data,
            // which is not required here thanks to the initialization
            BlockDataKey blockData = blockList.getBlockData();

            if (!this.blockDataMap.containsKey(blockData)) {
                short index = (short) this.blockDataMap.size();
                this.blockDataMap.put(blockData, index);
                this.state.add(blockData);
            }

            short index = this.blockDataMap.getShort(blockData);
            LongArrayList posList = blockList.getPosList();

            for (long pos : posList) {
                this.posListOptimized.add(new LongShortImmutablePair(pos, index));
            }
        }
    }

    /**
     * init a default ordered blockList comparator
     *
     * @param state   the state that will be tested and put (in the case it doesn't exist)
     * @param posList the blockPos that will be put related to the given state
     */
    public OrderedBlockListManager(BlockState state, List<BlockPos> posList) {
    }

    /**
     * init a comparator
     *
     * @param info the map that will be used to init the comparator
     */
    public OrderedBlockListManager(Map<BlockState, List<BlockPos>> info) {
    }

    /**
     * init an empty comparator
     */
    public OrderedBlockListManager() {
    }

    /**
     * Method to know if no BlockPos are present in the {@code posMap}
     *
     * @return <p>-true if no BlockPos are present in the map.
     * <p>-false if at least one BlockPos is present.
     */
    public boolean arePosEmpty() {
        return this.posListOptimized.isEmpty();
    }


    /**
     * Removes the BlockPos at the specified index from the posList and posMap.
     *
     * @param index the index of the BlockPos to remove.
     * @return the removed BlockPos.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public long removeBlockPos(int index) {
        return posListOptimized.remove(index).leftLong();
    }

    /**
     * Removes the BlockPos at the specified index from the posList and retrieves its associated state.
     *
     * @param index the index of the BlockPos to remove.
     * @return a Pair containing the removed BlockPos and its associated state.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Pair<Long, BlockState> removeBlockPosPair(int index) {
        LongShortImmutablePair pos = posListOptimized.remove(index);
        return new Pair<>(pos.leftLong(), this.state.get(pos.rightShort()).getState());
    }

    /**
     * Removes and returns the first BlockPos from the posList and posMap.
     *
     * @return the first BlockPos that was removed.
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public long removeFirstPos() {
        return removeBlockPos(0);
    }

    /**
     * Removes and returns the last BlockPos from the posList and posMap.
     *
     * @return the first BlockPos that was removed.
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public long removeLastPos() {
        return removeBlockPos(posSize() - 1);
    }

    /**
     * Removes and returns the first BlockPos and its associated state from the posList.
     *
     * @return a Pair containing the first BlockPos and its associated state.
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public Pair<Long, BlockState> removeFirstBlockPos() {
        return removeBlockPosPair(0);
    }

    /**
     * Removes and returns the last BlockPos and its associated state from the posList.
     *
     * @return a Pair containing the last BlockPos and its associated state.
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public Pair<Long, BlockState> removeLastBlockPosPair() {
        return removeBlockPosPair(posSize() - 1);
    }

    /**
     * Clears all elements from posList, posMap, and statesMap.
     * After this operation, all structures will be empty.
     */
    public void clear() {
        this.state.clear();
        this.posListOptimized.clear();
    }

    public List<LongShortImmutablePair> getPosList() {
        return this.posListOptimized;
    }

    /**
     * Retrieves the BlockPos at a specified index.
     *
     * @param index the index of the BlockPos to retrieve
     * @return the BlockPos at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public long getBlockPos(int index) {
        return posListOptimized.get(index).leftLong();
    }

    /**
     * Retrieves the first BlockPos in the position map.
     *
     * @return the first BlockPos
     * @throws java.util.NoSuchElementException if the position map is empty
     */
    public long getFirstBlockPos() {
        return posListOptimized.getFirst().leftLong();
    }

    /**
     * Retrieves the last BlockPos in the position map.
     *
     * @return the last BlockPos
     * @throws java.util.NoSuchElementException if the position map is empty
     */
    public long getLastBlockPos() {
        return posListOptimized.getLast().leftLong();
    }

    /**
     * Retrieves a random BlockPos from the position map.
     *
     * @return a random BlockPos
     * @throws IllegalStateException if the position map is empty
     */
    public long getRandomBlockPos() {
        return getBlockPos(Random.create().nextInt(posSize() - 1));
    }

    /**
     * Retrieves a random BlockPos from the position map using a provided random generator.
     *
     * @param random the Random object to use for generating random indices
     * @return a random BlockPos
     * @throws IllegalStateException if the position map is empty
     */
    public long getRandomBlockPos(Random random) {
        return getBlockPos(random.nextInt(posSize() - 1));
    }

    /**
     * Retrieves the number of entries in the position map.
     *
     * @return the size of the position map
     */
    public int posSize() {
        return posListOptimized.size();
    }

    /**
     * method to get the size of the stateMap
     *
     * @return the size of the state map
     */
    public int stateSize() {
        return this.state.size();
    }

    /**
     * method to know if no blockPos are present
     *
     * @return true is no blockPos are present, false if not
     */
    public boolean isPosEmpty() {
        return posSize() == 0;
    }

    /**
     * method to know if no state is present
     *
     * @return true is no blockPos are present, false if not
     */
    public boolean isStateEmpty() {
        return stateSize() == 0;
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public BlockState getBlockState(short index) {
        return this.state.get(index).getState();
    }

    public BlockDataKey get(int index) {
        return this.state.get(index);
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public NbtCompound getCompound(short index) {
        return this.state.get(index).getTag();
    }

    /**
     * Method to get the first BlockState
     *
     * @return the first BlockState
     */
    public BlockState getFirstBlockState() {
        return this.state.getFirst().getState();
    }

    public BlockDataKey getFirst() {
        return this.state.getFirst();
    }

    public NbtCompound getFirstCompound() {
        return this.state.getFirst().getTag();
    }

    /**
     * Method to get the last BlockState
     *
     * @return the last BlockState
     */
    public BlockState getLastBlockState() {
        return this.state.getLast().getState();
    }

    public BlockDataKey getLast() {
        return this.state.getLast();
    }

    public NbtCompound getLastCompound() {
        return this.state.getLast().getTag();
    }

    public void setPosList(List<LongShortImmutablePair> posList) {
        this.posListOptimized = posList;
    }

    public void put(OrderedBlockListManager comparator) {
        for (BlockDataKey blockData : comparator.state) {
            if (!this.blockDataMap.containsKey(blockData)) {
                short index = (short) this.blockDataMap.size();
                this.blockDataMap.put(blockData, index);
                this.state.add(blockData);
            }
            short index = this.blockDataMap.getShort(blockData);

            for (LongShortImmutablePair pos : comparator.posListOptimized) {
                posListOptimized.add(new LongShortImmutablePair(pos.leftLong(), index));
            }
        }
    }

    public OrderedBlockListManager put(BlockList blockList) {
        return put(blockList.getState(), blockList.getTag().orElse(null), blockList.getPosList());
    }

    public OrderedBlockListManager put(BlockState state, NbtCompound tag, LongArrayList posList) {
        BlockDataKey blockData = new BlockDataKey(state, tag);
        if (!this.blockDataMap.containsKey(blockData)) {
            short index = (short) this.blockDataMap.size();
            this.blockDataMap.put(blockData, index);
            this.state.add(blockData);
        }
        short index = this.blockDataMap.getShort(blockData);

        Int2ShortOpenHashMap tempStateLinkMap = new Int2ShortOpenHashMap(posList.size());

        int normalizedIndex = posSize();
        for (long pos : posList) {
            posListOptimized.add(new LongShortImmutablePair(pos, index));
        }

        return this;
    }

    public OrderedBlockListManager put(BlockState state, NbtCompound tag, long pos) {
        return put(state, tag, LongArrayList.of(pos));
    }

    public OrderedBlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList) {
        return put(state, tag, LongPosHelper.encodeBlockPos(posList));
    }

    public OrderedBlockListManager put(BlockState state, NbtCompound tag, BlockPos pos) {
        return put(state, tag, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public OrderedBlockListManager put(BlockState state, LongArrayList pos) {
        return put(state, null, pos);
    }

    public OrderedBlockListManager put(BlockState state, long pos) {
        return put(state, null, LongArrayList.of(pos));
    }

    public OrderedBlockListManager put(BlockState state, List<BlockPos> posList) {
        return put(state, null, LongPosHelper.encodeBlockPos(posList));
    }

    public OrderedBlockListManager put(BlockState state, BlockPos pos) {
        return put(state, null, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public BlockDataKey getFromPosIndex(int index) {
        return this.state.get(this.posListOptimized.get(index).rightShort());
    }

    public Optional<StructurePlacementRuleManager> getPlacementRuleFromPosIndex(int index) {
        return Optional.ofNullable(this.ruler.get(this.posListOptimized.get(index).rightShort()));
    }


    public boolean placeLast(StructureWorldAccess world) {
        return place(world, posSize() - 1);
    }

    public boolean placeFirst(StructureWorldAccess world) {
        return place(world, 0);
    }

    public boolean placeAll(StructureWorldAccess worldAccess) {
        boolean placed = true;
        boolean markdirty = false;
        ServerChunkManager chunkManager = null;
        MinecraftServer server = worldAccess.getServer();

        if (server != null) {
            if (worldAccess instanceof ServerWorld world) {
                chunkManager = world.getChunkManager();
                markdirty = true;
            }
        }

        for (int i = 0; i < this.posListOptimized.size(); i++) {
            if (!place(worldAccess, i)) {
                placed = false;
            } else if (markdirty) {
                chunkManager.markForUpdate(LongPosHelper.decodeBlockPos(this.posListOptimized.get(i).leftLong()));
            }
        }
        return placed;
    }

    public boolean placeLastNDelete(StructureWorldAccess world) {
        boolean placed = place(world, posSize() - 1);
        this.posListOptimized.removeLast();
        return placed;
    }

    /**
     * for the most performance, it is recommended to not use this method where {@code placeLastNDelete()} can be applied
     */
    public boolean placeNDelete(StructureWorldAccess world, int index) {
        return place(world, index);
    }

    public boolean placeAllNDelete(StructureWorldAccess worldAccess) {
        boolean placed = placeAll(worldAccess);
        this.posListOptimized.clear();
        return placed;
    }

    public boolean place(StructureWorldAccess world, int index) {
        if (!init)
            init(world);

        boolean placed;
        if ((placed = place(world, index, Block.FORCE_STATE)) && markDirty) {
            chunkManager.markForUpdate(LongPosHelper.decodeBlockPos(this.posListOptimized.get(index).leftLong()));
        }
        return placed;
    }

    public boolean place(StructureWorldAccess world, int index, int flag) {
        BlockPos pos = LongPosHelper.decodeBlockPos(this.posListOptimized.get(index).leftLong());
        BlockState worldState = world.getBlockState(pos);

        BlockDataKey data = getFromPosIndex(index);
        Optional<StructurePlacementRuleManager> rule = getPlacementRuleFromPosIndex(index);
        return worldState.isAir() && BlockPlaceUtil.place(world, pos, data, null, flag);
    }

    public void init(StructureWorldAccess world) {
        MinecraftServer server = world.getServer();

        if (server != null) {
            if (world instanceof ServerWorld serverWorld) {
                chunkManager = serverWorld.getChunkManager();
                markDirty = true;
            }
        }
    }
}
