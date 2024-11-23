package net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CompoundOrderedBlockListComparator. The class provides the ability to add Nbt Compounds to the order blockList comparator
 */
@SuppressWarnings("unused")
public class CompoundOrderedBlockListComparator extends OrderedBlockListComparator<Pair<BlockState, NbtCompound>> {

    /**
     * init a default ordered blockList comparator
     *
     * @param state   the pair of state and NbtCompound that will be tested and put (in the case it doesn't exist)
     * @param posList the blockPos that will be put related to the given state
     */
    public CompoundOrderedBlockListComparator(Pair<BlockState, NbtCompound> state, List<BlockPos> posList) {
        super(state, posList);
    }

    /**
     * init a comparator
     *
     * @param info the map that will be used to init the comparator
     */
    public CompoundOrderedBlockListComparator(Map<Pair<BlockState, NbtCompound>, List<BlockPos>> info) {
        super(info);
    }

    /**
     * init an empty Compound Comparator
     */
    public CompoundOrderedBlockListComparator() {
    }

    @Override
    public boolean place(StructureWorldAccess world, int index) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);
    }

    @Override
    public boolean placeFirst(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getFirstPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);
    }

    @Override
    public boolean placeFirstWithDeletion(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeFirstBlockPos();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);
    }


    /**
     * Method to get the Set of pair that are saved in the comparator
     *
     * @return the list of pair
     */
    public List<Pair<BlockState, NbtCompound>> getPairs() {
        return getT();
    }

    /**
     * method to get the Pair related to the index
     *
     * @param index the index of the Pair
     * @return the Pair related to the index
     */
    public Pair<BlockState, NbtCompound> getPair(short index) {
        return this.statesMap.get(index);
    }

    /**
     * Method to get the first Pair
     *
     * @return the first Pair
     */
    public Pair<BlockState, NbtCompound> getFirstPair() {
        return this.statesMap.get((short) 0);
    }

    /**
     * Method to get the last Pair
     *
     * @return the last Pair
     */
    public Pair<BlockState, NbtCompound> getLastPair() {
        return this.statesMap.get((short) (statesMap.size() - 1));
    }

    /**
     * Method to get the set of BlockStates that are saved in the comparator
     *
     * @return the set of blockStates
     */
    public Set<BlockState> getBlockStates() {
        Set<BlockState> states = new HashSet<>();
        List<Pair<BlockState, NbtCompound>> nbt = getT();
        for (Pair<BlockState, NbtCompound> infos : nbt) {
            states.add(infos.getLeft());
        }
        return states;
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public BlockState getBlockState(short index) {
        return this.statesMap.get(index).getLeft();
    }

    /**
     * Method to get the first BlockState
     *
     * @return the first BlockState
     */
    public BlockState getFirstBlockState() {
        return this.statesMap.get((short) 0).getLeft();
    }

    /**
     * Method to get the last BlockState
     *
     * @return the last BlockState
     */
    public BlockState getLastBlockState() {
        return this.statesMap.get((short) (statesMap.size() - 1)).getLeft();
    }

    /**
     * Method to get the set of compound that are saved in the comparator
     *
     * @return the set of compound
     */
    public Set<NbtCompound> getCompounds() {
        Set<NbtCompound> tags = new HashSet<>();
        List<Pair<BlockState, NbtCompound>> nbt = getT();
        for (Pair<BlockState, NbtCompound> infos : nbt) {
            tags.add(infos.getRight());
        }
        return tags;
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public NbtCompound getCompound(short index) {
        return this.statesMap.get(index).getRight();
    }

    /**
     * Method to get the first BlockState
     *
     * @return the first BlockState
     */
    public NbtCompound getFirstCompound() {
        return this.statesMap.get((short) 0).getRight();
    }

    /**
     * Method to get the last BlockState
     *
     * @return the last BlockState
     */
    public NbtCompound getLastCompound() {
        return this.statesMap.get((short) (statesMap.size() - 1)).getRight();
    }


}