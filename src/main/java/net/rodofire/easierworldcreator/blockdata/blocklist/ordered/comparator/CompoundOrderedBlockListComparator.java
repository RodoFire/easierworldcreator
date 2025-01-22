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
public class CompoundOrderedBlockListComparator extends AbstractOrderedBlockListComparator<Pair<BlockState, NbtCompound>> {
    /**
     * constructor to init a {@link CompoundOrderedBlockListComparator}.
     *
     * @param comparator the comparator to be fused
     */
    public CompoundOrderedBlockListComparator(CompoundOrderedBlockListComparator comparator) {
        super(comparator);
    }

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

    public void put(CompoundOrderedBlockListComparator comparator) {
        super.put(comparator);
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


    /**
     * method to place the Block related to the index
     *
     * @param world the world the block will be placed
     * @param index the index of the BlockPos
     */
    @Override
    public void place(StructureWorldAccess world, int index) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        BlockPlaceUtil.placeBlockWithNbt(world, pos, state, nbtCompound);
    }

    /**
     * method to place the block with the deletion of the BlockPos
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     */
    @Override
    public void placeWithDeletion(StructureWorldAccess world, int index) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeBlockPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        BlockPlaceUtil.placeBlockWithNbt(world, pos, state, nbtCompound);
    }


    /**
     * Method to place the block related to the index.
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeWithVerification(StructureWorldAccess world, int index) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);

    }

    /**
     * Method to place the block with the deletion of the BlockPos
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeWithVerificationDeletion(StructureWorldAccess world, int index) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeBlockPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);

    }

    /**
     * method to place the first Block
     *
     * @param world the world the block will be placed
     */
    @Override
    public void placeFirst(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getFirstPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        BlockPlaceUtil.placeBlockWithNbt(world, pos, state, nbtCompound);
    }

    /**
     * Method to place the first Block and deleting it.
     * You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     */
    @Override
    public void placeFirstWithDeletion(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeFirstBlockPos();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        BlockPlaceUtil.placeBlockWithNbt(world, pos, state, nbtCompound);
    }

    /**
     * Method to place the first Block.
     * <p>The method also performs verification to know if the block can be placed.
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    @Override
    public boolean placeFirstWithVerification(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getFirstPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);
    }

    /**
     * <p>Method to place the first Block and deleting it.
     * <p>The method also performs verification to know if the block can be placed.
     * <p>You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * <p>Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    @Override
    public boolean placeFirstWithVerificationDeletion(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeFirstBlockPos();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);
    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     */
    @Override
    public void placeLastWithDeletion(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeLastBlockPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        BlockPlaceUtil.placeBlockWithNbt(world, pos, state, nbtCompound);
    }

    /**
     * Method to place the last Block of the comparator.
     *
     * @param world the world the last block will be placed
     */
    @Override
    public void placeLast(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getLastPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        BlockPlaceUtil.placeBlockWithNbt(world, pos, state, nbtCompound);
    }

    /**
     * Method to place the last Block.
     *
     * @param world the world the last block will be placed
     *              The method also performs verification to know if the block can be placed.
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeLastWithVerification(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getLastPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);

    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * The method also performs verification to know if the block can be placed.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeLastWithVerificationDeletion(StructureWorldAccess world) {
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeLastBlockPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, pos, state, nbtCompound);
    }

}