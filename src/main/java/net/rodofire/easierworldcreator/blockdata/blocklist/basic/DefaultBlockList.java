package net.rodofire.easierworldcreator.blockdata.blocklist.basic;

import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class used to connect BlockPos to a BlockState.</p>
 * <br>
 * <p>the class is composed of a {@code List<BlockPos>} related to a {@link BlockState}. </p>
 * <p>This means that all the {@link BlockPos} of the object are connected to the BlockState</p>
 * <br>
 * <p>this is an easier version of the {@link StructureTemplate.StructureBlockInfo}</p>
 * <p>It also allows for better manipulation of how blocks behave and allows to save memory since that there is no double blockStates.</p>
 */
@SuppressWarnings("unused")
public class DefaultBlockList implements BlockListManager {

    private List<BlockPos> posList;
    private BlockState blockState;


    /**
     * init a BlockShapeManager
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     */
    public DefaultBlockList(List<BlockPos> posList, BlockState blockState) {
        this.posList = new ArrayList<>(posList);
        this.blockState = blockState;
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public DefaultBlockList(BlockPos pos, BlockState state) {
        this.posList = new ArrayList<>();
        this.posList.add(pos);
        this.blockState = state;
    }

    /**
     * used to get the list of blockPos related to a layer
     *
     * @return the list of BlockPos
     */
    @Override
    public List<BlockPos> getPosList() {
        return posList;
    }

    /**
     * It uses a list of blockPos to allow multiple BlockPos to have a BlockState
     *
     * @param posList a list of BlockPos
     */
    @Override
    public void setPosList(List<BlockPos> posList) {
        this.posList = posList;
    }

    /**
     * allow you to add a BlockPos to the existing list
     *
     * @param pos the pos added
     */
    @Override
    public void addBlockPos(BlockPos pos) {
        this.posList.add(pos);
    }

    /**
     * allow you to add multiple BlockPos to the existing list
     *
     * @param pos the pos list added
     */
    @Override
    public void addBlockPos(List<BlockPos> pos) {
        this.posList.addAll(pos);
    }

    /**
     * allow you to remove a BlockPos to the existing list
     *
     * @param pos the pos removed
     */
    @Override
    public void removeBlockPos(BlockPos pos) {
        this.posList.remove(pos);
    }

    /**
     * allow you to remove a list of BlockPos to the existing list
     *
     * @param pos the list pos removed
     */
    @Override
    public void removeBlockPos(List<BlockPos> pos) {
        this.posList.removeAll(pos);
    }

    /**
     * method to replace one blockPos to another one
     *
     * @param oldPos the oldPos that will be replaced
     * @param newPos the newPos that will replace the other blockPos
     */
    @Override
    public void replaceBlockPos(BlockPos oldPos, BlockPos newPos) {
        this.posList.set(posList.indexOf(oldPos), newPos);
    }

    /**
     * method to get a certain pos from an index in the posList
     *
     * @param index the index of the BlockPos
     * @return the BlockPos of the index
     */
    @Override
    public BlockPos getPos(int index) {
        return this.posList.get(index);
    }

    /**
     * method to get the last blockPos of the posList
     *
     * @return the last blockPos of the posList
     */
    @Override
    public BlockPos getLastPos() {
        return this.posList.get(posList.size() - 1);
    }

    /**
     * method to get the first blockPos of the posList
     *
     * @return the first blockPos of the posList
     */
    @Override
    public BlockPos getFirstPos() {
        return this.posList.get(0);
    }

    /**
     * Method to get a random {@link BlockPos} in the {@code posList} by using a new minecraft Random
     *
     * @return a random {@link BlockPos}
     */
    @Override
    public BlockPos getRandomPos() {
        return this.posList.get(Random.create().nextInt(size()));
    }

    /**
     * Method to get a random {@link BlockPos} in the {@code posList} based on a provided random
     *
     * @return a random {@link BlockPos}
     */
    @Override
    public BlockPos getRandomPos(Random random) {
        return this.posList.get(random.nextInt(size()));
    }

    /**
     * used to get the blockState
     *
     * @return the blockState of the BlockShapeManager
     */
    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    /**
     * change the blockState of the BlockShapeManager
     *
     * @param blockState the blockState related to the BlockPos list
     */
    @Override
    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    /**
     * method to get the number of {@link BlockPos} present in the related BlockList
     *
     * @return the number of {@link BlockPos}
     */
    @Override
    public int size() {
        return this.posList.size();
    }

    @Override
    public String toString() {
        return "BlockShapeManager{" +
                "posList=" + posList +
                ", blockState=" + blockState +
                '}';
    }
}
