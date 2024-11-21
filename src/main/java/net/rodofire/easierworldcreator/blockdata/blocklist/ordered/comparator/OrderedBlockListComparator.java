package net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.*;

/**
 * <p> Class to manage ordered BlockList.
 * <p> For memory saving, we use a link between states and {@link BlockPos}.
 * <p> Since that {@code BlockState} takes a lot of memory, we use a unique index represented by a short to make the link.
 * We also want the order of the BlockPos, so we can't compact the BlockPos into one BlockState easily and without having important performance losses.
 * <p>Since that it is highly improbable that there are more than 32 000 different {@code T} objects, we use the short, allowing us to save two Bytes of data per BlocPos.
 *
 * @param <T> the object that represents the state of the blocks usually a {@code BlockState}, but can include Nbt Compounds depending on the case.
 */
@SuppressWarnings("unused")
public class OrderedBlockListComparator<T> {
    /**
     * we're using BiMap to be able to get the short from the T objects and the other way around
     */
    private final BiMap<Short, T> statesMap = HashBiMap.create();

    /**
     * we're using the short to make the link between the T objects (blockStates in the most cases) and the BlockPos.
     */
    private LinkedHashMap<BlockPos, Short> posMap = new LinkedHashMap<>();


    /**
     * constructor to init a {@link OrderedBlockListComparator}.
     *
     * @param states  the {@code T} object that will init the {@code stateMap}.
     * @param posList the BlockPos List related to the {@code states} that will init the {@code posMap}
     */
    public OrderedBlockListComparator(T states, List<BlockPos> posList) {
        put(states, posList);
    }

    /**
     * constructor to init a {@link OrderedBlockListComparator}.
     *
     * @param info a map that provides a list of BlockPos and the {@code T} objects related to them.
     *             <p>That will be used to initialize the {@code posMap} as well as the {@code stateMap}.
     */
    public OrderedBlockListComparator(Map<T, List<BlockPos>> info) {
        List<T> states = info.keySet().stream().toList();
        List<List<BlockPos>> pos = info.values().stream().toList();
        for (int i = 0; i < info.size(); i++) {
            put(states.get(i), pos.get(i));
        }
    }

    /**
     * Constructor to init an empty {@link OrderedBlockListComparator}.
     */
    public OrderedBlockListComparator() {

    }

    /**
     * Method to add a {@code T} object as well as a list of BlockPos.
     * <p>- In the case there's already a {@code T} object, the method will only add the BlockPos list to the {@code posMap} with the related index.
     * <p>- In the other case, the {@code T} object will be added to the {@code stateMap} with a new index. And will put the blockPos List in the {@code posMap}
     *
     * @param state   the object that will be compared, added and assigned an index in the case it is not present
     * @param posList the List of BlockPos that you want to add
     */
    public void put(T state, List<BlockPos> posList) {
        //we put the blockPos depending on the short value
        if (this.statesMap.containsValue(state)) {
            //we compare the short with the blockState value
            short sho = this.statesMap.inverse().get(state);
            for (BlockPos pos : posList) {
                posMap.put(pos, sho);
            }
        }
        //we add the blockPos with a new index
        else {
            short statesSize = (short) this.statesMap.size();
            this.statesMap.put(statesSize, state);
            for (BlockPos pos : posList) {
                this.posMap.put(pos, statesSize);
            }
        }
    }

    /**
     * Method to add a {@code T} object as well as a list of BlockPos.
     * <p>- In the case there's already a {@code T} object, the method will only add the BlockPos list to the {@code posMap} with the related index.
     * <p>- In the other case, the {@code T} object will be added to the {@code stateMap} with a new index. And will put the blockPos List in the {@code posMap}
     * <p>We're calling the put method for all the objects in the map
     *
     * @param info a map of {@code T} object linked with a {@code List<BLockPos>}
     */
    public void putAll(Map<T, List<BlockPos>> info) {
        List<T> states = info.keySet().stream().toList();
        List<List<BlockPos>> pos = info.values().stream().toList();
        for (int i = 0; i < info.size(); i++) {
            put(states.get(i), pos.get(i));
        }
    }

    /**
     * Method to know if an object {@code T} is present in the comparator
     *
     * @param state the state that will be tested to know if it is present or not
     * @return <p>-true if the state is present.
     * <p>-false if not
     */
    public boolean isPresent(T state) {
        return this.statesMap.containsValue(state);
    }

    /**
     * Method to know if a BlockPos is present in the comparator
     *
     * @param pos the state that will be tested to know if it is present or not
     * @return <p>-true if the pos is present.
     * <p>-false if not
     */
    public boolean isPresent(BlockPos pos) {
        return this.posMap.containsKey(pos);
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
    public short getStateIndex(T state) {
        return this.statesMap.containsValue(state) ? this.statesMap.inverse().get(state) : -1;
    }

    /**
     * method to remove a list of object from the {@code stateMap}
     *
     * @param states the objects that will be removed
     */
    public void removeState(List<T> states) {
        for (T state : states) {
            removeState(state);
        }
    }

    /**
     * <p>Method to remove a {@code T} object from the {@code stateMap}.
     * <p>Removing that object will also remove every {@link BlockPos} related to that object
     *
     * @param state the object that will be removed form the {@code stateMap}
     */
    public void removeState(T state) {
        //we check if the state is present
        if (this.statesMap.containsValue(state)) {
            short index = getStateIndex(state);
            this.statesMap.remove(index);
            for (BlockPos pos : this.posMap.keySet()) {
                if (this.posMap.get(pos) == index)
                    this.posMap.remove(pos);
            }
        }
    }


    /**
     * Replaces an old state with a new state in the states map.
     *
     * @param oldState the state to be replaced
     * @param newState the state to replace with
     */
    public void replaceState(T oldState, T newState) {
        if (this.statesMap.containsValue(oldState)) {
            short index = getStateIndex(oldState);
            this.statesMap.put(index, newState);
        }
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
        this.posMap.remove(pos);
    }

    /**
     * Retrieves a list of all states in the states map.
     *
     * @return a list of all states
     */
    protected List<T> getT() {
        return statesMap.values().stream().toList();
    }

    /**
     * Method to get the posMap related to the object
     *
     * @return the indexes (a Map of BlockPos and indexes)
     */
    public LinkedHashMap<BlockPos, Short> getPosMap() {
        return this.posMap;
    }

    /**
     * Method to set the posMap
     *
     * @param indexes the posMap that will be set (a Map of BlockPos and indexes)
     */
    public void setPosMap(LinkedHashMap<BlockPos, Short> indexes) {
        this.posMap = new LinkedHashMap<>(indexes);
    }

    /**
     * Method to gate all the BlockPos related to provided state
     *
     * @param state the parameter that will be used to get the related BlockPos
     * @return a Set of BlockPos that correspond to the provided state.
     */
    public Set<BlockPos> getBlockPos(T state) {
        short index = getStateIndex(state);
        Set<BlockPos> posSet = new HashSet<>();
        for (BlockPos pos : this.posMap.keySet()) {
            if (this.posMap.get(pos) == index)
                posSet.add(pos);
        }
        return posSet;
    }

    /**
     * method to get all the BlockPos related to a list of states
     *
     * @param states the states that will be used to get all the related blockPos
     * @return a set of BlockPos that are linked to the provided states.
     */
    public Set<BlockPos> getBlockPos(List<T> states) {
        Set<BlockPos> posSet = new HashSet<>();
        for (T state : states) {
            short index = getStateIndex(state);
            for (BlockPos pos : this.posMap.keySet()) {
                if (this.posMap.get(pos) == index)
                    posSet.add(pos);
            }
        }
        return posSet;
    }

    /**
     * Retrieves a set of all BlockPos entries in the position map.
     *
     * @return a set of all BlockPos objects
     */
    public Set<BlockPos> getBlockPosSet() {
        return posMap.keySet();
    }

    /**
     * Retrieves the BlockPos at a specified index.
     *
     * @param i the index of the BlockPos to retrieve
     * @return the BlockPos at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public BlockPos getBlockPos(int i) {
        return posMap.keySet().stream().toList().get(i);
    }

    /**
     * Retrieves the first BlockPos in the position map.
     *
     * @return the first BlockPos
     * @throws java.util.NoSuchElementException if the position map is empty
     */
    public BlockPos getFirstBlockPos() {
        return posMap.keySet().stream().toList().get(0);
    }

    /**
     * Retrieves the last BlockPos in the position map.
     *
     * @return the last BlockPos
     * @throws java.util.NoSuchElementException if the position map is empty
     */
    public BlockPos getLastBlockPos() {
        return posMap.keySet().stream().toList().get(size() - 1);
    }

    /**
     * Retrieves a random BlockPos from the position map.
     *
     * @return a random BlockPos
     * @throws IllegalStateException if the position map is empty
     */
    public BlockPos getRandomBlockPos() {
        return posMap.keySet().stream().toList().get(Random.create().nextInt(size() - 1));
    }

    /**
     * Retrieves a random BlockPos from the position map using a provided random generator.
     *
     * @param random the Random object to use for generating random indices
     * @return a random BlockPos
     * @throws IllegalStateException if the position map is empty
     */
    public BlockPos getRandomBlockPos(Random random) {
        return posMap.keySet().stream().toList().get(random.nextInt(size() - 1));
    }

    /**
     * Retrieves the number of entries in the position map.
     *
     * @return the size of the position map
     */
    public int size() {
        return posMap.size();
    }
}
