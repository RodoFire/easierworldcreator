package net.rodofire.easierworldcreator.blockdata.blocklist;

import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default Ordered BlockList comparator. The class provides the basic related to order blockList comparator
 */
@SuppressWarnings("unused")
public class OrderedBlockListManager {

    /**
     * blockData objects
     */
    List<Pair<BlockState, NbtCompound>> state = new ArrayList<>();
    Object2ShortArrayMap<Pair<BlockState, NbtCompound>> blockDataMap = new Object2ShortArrayMap<>();

    Short2ReferenceOpenHashMap<StructurePlacementRuleManager> ruler = new Short2ReferenceOpenHashMap<>();

    /**
     * Link between blockData and BlockPos
     */
    Short2ReferenceOpenHashMap<IntArrayList> statePosLink = new Short2ReferenceOpenHashMap<>();
    Int2ShortOpenHashMap posStateLink = new Int2ShortOpenHashMap();

    /**
     * BlockPos objects
     */
    Long2IntOpenHashMap posMap = new Long2IntOpenHashMap();
    LongArrayList posListOptimized = new LongArrayList();

    /**
     * constructor to init a {@link OrderedBlockListManager}.
     *
     * @param comparator the comparator to be fused
     */
    public OrderedBlockListManager(OrderedBlockListManager comparator) {
        put(comparator);
    }

    /**
     * constructor to init a {@link OrderedBlockListManager}.
     *
     * @param manager the manager to be fused
     */
    public OrderedBlockListManager(BlockListManager manager) {
        for (BlockList blockList : manager.blockLists) {
            put(blockList);
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
     * Method to know if a BlockPos is present in the comparator
     *
     * @param pos the state that will be tested to know if it is present or not
     * @return <p>-true if the pos is present.
     * <p>-false if not
     */
    public boolean isPresent(BlockPos pos) {
        return this.posMap.containsKey(LongPosHelper.encodeBlockPos(pos));
    }

    /**
     * Method to know if no BlockPos are present in the {@code posMap}
     *
     * @return <p>-true if no BlockPos are present in the map.
     * <p>-false if at least one BlockPos is present.
     */
    public boolean arePosEmpty() {
        return this.posMap.isEmpty();
    }

    /**
     * method to get the index related to the {@code T} object.
     *
     * @param state the state that will be compared
     * @return The index related to the {@code T} if it is present.
     * <p>- We return -1 in case it is not present.
     * <p>Be careful when using this method.
     * <p>Always check that the value is not equals to -1
     */
    public short getStateIndex(BlockState state) {
        Pair<BlockState, NbtCompound> blockData = new Pair<>(state, null);
        return this.blockDataMap.containsKey(blockData) ? this.blockDataMap.getShort(blockData) : -1;
    }

    /**
     * method to get the index related to the {@code T} object.
     *
     * @param state the state that will be compared
     * @return The index related to the {@code T} if it is present.
     * <p>- We return -1 in case it is not present.
     * <p>Be careful when using this method.
     * <p>Always check that the value is not equals to -1
     */
    public short getStateIndex(Pair<BlockState, NbtCompound> state) {
        return this.blockDataMap.containsKey(state) ? this.blockDataMap.getShort(state) : -1;
    }

    /**
     * method to remove a list of object from the {@code stateMap}
     *
     * @param states the objects that will be removed
     */
    public void removeState(List<Pair<BlockState, NbtCompound>> states) {
        for (Pair<BlockState, NbtCompound> state : states) {
            removeState(state);
        }
    }

    /**
     * <p>Method to remove a {@code T} object from the {@code stateMap}.
     * <p>Removing that object will also remove every {@link BlockPos} related to that object
     *
     * @param state the object that will be removed form the {@code stateMap}
     */
    public OrderedBlockListManager removeState(Pair<BlockState, NbtCompound> state) {
        //we check if the state is present
        if (this.blockDataMap.containsKey(state)) {
            short index = getStateIndex(state);
            this.blockDataMap.removeShort(state);
            for (int link : this.statePosLink.get(index)) {
                this.posListOptimized.removeLong(link);
            }
            statePosLink.remove(index);
        }
        return this;
    }

    /**
     * Removes multiple BlockPos entries from the position map.
     *
     * @param posList the list of BlockPos objects to be removed
     * @throws NullPointerException if posList or any BlockPos in it is null
     */
    public void removeBlockPos(List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            removeBlockPos(pos);
        }
    }

    /**
     * Removes a single BlockPos entry from the position map.
     *
     * @param pos the BlockPos to be removed
     * @throws NullPointerException if pos is null
     */
    public void removeBlockPos(BlockPos pos) {
        int index = this.posMap.remove(LongPosHelper.encodeBlockPos(pos));
        this.posListOptimized.removeLong(index);
    }


    /**
     * Removes the BlockPos at the specified index from the posList and posMap.
     *
     * @param index the index of the BlockPos to remove.
     * @return the removed BlockPos.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public long removeBlockPos(int index) {
        long pos = posListOptimized.removeLong(index);
        this.posMap.remove(pos);
        return pos;
    }

    /**
     * Removes the BlockPos at the specified index from the posList and retrieves its associated state.
     *
     * @param index the index of the BlockPos to remove.
     * @return a Pair containing the removed BlockPos and its associated state.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Pair<Long, BlockState> removeBlockPosPair(int index) {
        long pos = posListOptimized.removeLong(index);
        short id = this.posStateLink.get(index);
        return new Pair<>(pos, this.state.get(id).getLeft());
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
        this.posMap.clear();
        this.posStateLink.clear();
        this.blockDataMap.clear();
        this.statePosLink.clear();
        this.posListOptimized.clear();
    }

    public LongArrayList getPosList() {
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
        return posListOptimized.getLong(index);
    }

    /**
     * Retrieves the first BlockPos in the position map.
     *
     * @return the first BlockPos
     * @throws java.util.NoSuchElementException if the position map is empty
     */
    public long getFirstBlockPos() {
        return posListOptimized.getFirst();
    }

    /**
     * Retrieves the last BlockPos in the position map.
     *
     * @return the last BlockPos
     * @throws java.util.NoSuchElementException if the position map is empty
     */
    public long getLastBlockPos() {
        return posListOptimized.getLast();
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
        return this.blockDataMap.size();
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
        return this.state.get(index).getLeft();
    }

    public Pair<BlockState, NbtCompound> get(int index) {
        return this.state.get(index);
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public NbtCompound getCompound(short index) {
        return this.state.get(index).getRight();
    }

    /**
     * Method to get the first BlockState
     *
     * @return the first BlockState
     */
    public BlockState getFirstBlockState() {
        return this.state.getFirst().getLeft();
    }

    public Pair<BlockState, NbtCompound> getFirst() {
        return this.state.getFirst();
    }

    public NbtCompound getFirstCompound() {
        return this.state.getFirst().getRight();
    }

    /**
     * Method to get the last BlockState
     *
     * @return the last BlockState
     */
    public BlockState getLastBlockState() {
        return this.state.getLast().getLeft();
    }

    public Pair<BlockState, NbtCompound> getLast() {
        return this.state.getLast();
    }

    public NbtCompound getLastCompound() {
        return this.state.getLast().getRight();
    }

    public void setPosList(LongArrayList posList) {
        this.posListOptimized = posList;
    }

    public void setPosListFromList(List<BlockPos> posList) {
        this.posListOptimized = LongPosHelper.encodeBlockPos(posList);
    }


    public void put(OrderedBlockListManager comparator) {
        for (Pair<BlockState, NbtCompound> blockData : comparator.state) {
            if (!this.blockDataMap.containsKey(blockData)) {
                short index = (short) this.blockDataMap.size();
                this.blockDataMap.put(blockData, index);
                this.state.add(blockData);
            }
            short index = this.blockDataMap.getShort(blockData);

            IntArrayList positions = comparator.statePosLink.get(index);
            if (positions != null) {
                for (int idx : positions) {
                    long pos = comparator.posListOptimized.getLong(idx);
                    int normalizedIndex = posSize();
                    if (posMap.containsKey(pos)) {
                        continue;
                    }
                    posMap.put(pos, normalizedIndex);
                    posListOptimized.add(pos);
                    posStateLink.put(normalizedIndex, index);
                    this.statePosLink.computeIfAbsent(index, k -> new IntArrayList()).add(normalizedIndex);
                }
            }

        }
    }

    public OrderedBlockListManager put(BlockList blockList) {
        return put(blockList.getBlockState(), blockList.getTag().orElse(null), blockList.getPosList());
    }

    public OrderedBlockListManager put(BlockState state, NbtCompound tag, LongArrayList posList) {
        Pair<BlockState, NbtCompound> blockData = new Pair<>(state, tag);
        if (!this.blockDataMap.containsKey(blockData)) {
            short index = (short) this.blockDataMap.size();
            this.blockDataMap.put(blockData, index);
            this.state.add(blockData);
        }
        short index = this.blockDataMap.getShort(blockData);

        for (long pos : posList) {
            int normalizedIndex = posSize();
            if (posMap.containsKey(pos)) {
                continue;
            }
            posMap.put(pos, normalizedIndex);
            posListOptimized.add(pos);
            posStateLink.put(normalizedIndex, index);
            this.statePosLink.computeIfAbsent(index, k -> new IntArrayList()).add(normalizedIndex);
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

    public Pair<BlockState, NbtCompound> get(long pos) {
        return this.state.get(this.posStateLink.get(this.posMap.get(pos)));
    }

    public BlockState getState(long pos) {
        return this.state.get(this.posStateLink.get(this.posMap.get(pos))).getLeft();
    }

    public NbtCompound getCompound(long pos) {
        return this.state.get(this.posStateLink.get(this.posMap.get(pos))).getRight();
    }

    public Optional<StructurePlacementRuleManager> getPlacementRule(long pos) {
        short index = this.posStateLink.get(this.posMap.get(pos));
        return this.ruler.containsKey(index) ? Optional.of(this.ruler.get(index)) : Optional.empty();
    }


    public boolean placeLast(StructureWorldAccess world) {
        return place(world, posListOptimized.getLast());
    }

    public boolean placeFirst(StructureWorldAccess world) {
        return place(world, posListOptimized.getFirst());
    }

    public boolean place(StructureWorldAccess world, int index) {
        return place(world, posListOptimized.getLong(index));
    }

    public boolean placeAll(StructureWorldAccess worldAccess) {
        boolean placed = true;
        for(long pos : this.posListOptimized){
            if(!place(worldAccess, pos)){
                placed = false;
            }
        }
        return placed;
    }

    public boolean placeLastNDelete(StructureWorldAccess world) {
        return place(world, posListOptimized.removeLast());
    }

    /**
     * for the most performance, it is recommended to not use this method where {@code placeLastNDelete()} can be applied
     */
    public boolean placeNDelete(StructureWorldAccess world, int index) {
        return place(world, posListOptimized.removeLong(index));
    }

    public boolean placeAllNDelete(StructureWorldAccess worldAccess) {
        boolean placed = true;
        for(long pos : this.posListOptimized){
            if(!place(worldAccess, pos)){
                placed = false;
            }
        }
        this.posListOptimized.clear();
        return placed;
    }

    public boolean place(StructureWorldAccess world, long pos) {
        BlockState state = world.getBlockState(LongPosHelper.decodeBlockPos(pos));
        Optional<StructurePlacementRuleManager> rule = getPlacementRule(pos);
        if (rule.isPresent()) {
            if (rule.get().canPlace(state)) {
                world.setBlockState(LongPosHelper.decodeBlockPos(pos), state, 2);
                return true;
            }
            return false;
        }
        return state.isAir() && world.setBlockState(LongPosHelper.decodeBlockPos(pos), state, 2);
    }
}
