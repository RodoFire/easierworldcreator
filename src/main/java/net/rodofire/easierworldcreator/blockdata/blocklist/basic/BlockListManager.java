package net.rodofire.easierworldcreator.blockdata.blocklist.basic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * <p>Interface to define the base of the BlockList.
 * <p>BlockList are objects that are composed of at one {@link BlockState} and at least one {@link BlockPos},
 * <p>and eventually others parameters.
 * <p>This allows saving memory by removing any duplicate {@link BlockState}
 */
@SuppressWarnings("unused")
public interface BlockListManager {
    /**
     * used to get the list of {@link BlockPos} related to a layer
     *
     * @return the list of {@link BlockPos}
     */
    List<BlockPos> getPosList();

    /**
     * It uses a list of {@link BlockPos} to allow multiple BlockPos to have a {@link BlockState}
     *
     * @param posList a list of {@link BlockPos}
     */
    void setPosList(List<BlockPos> posList);

    /**
     * allow you to add a {@link BlockPos} to the existing list
     *
     * @param pos {@link BlockPos} pos added
     */
    void addBlockPos(BlockPos pos);

    /**
     * allow you to add multiple {@link BlockPos} to the existing list
     *
     * @param pos the {@link BlockPos} list added
     */
    void addBlockPos(List<BlockPos> pos);

    /**
     * allow you to remove a {@link BlockPos} to the existing list
     *
     * @param pos the pos removed
     */
    void removePos(BlockPos pos);

    /**
     * allow you to remove a list of {@link BlockPos} to the existing list
     *
     * @param pos the list pos removed
     */
    void removePos(List<BlockPos> pos);

    /**
     * method to remove the last blockPos of a blockList
     *
     * @return the BlockPos removed
     */
    BlockPos removeLastPos();

    /**
     * method to replace one blockPos to another one
     *
     * @param oldPos the oldPos that will be replaced
     * @param newPos the newPos that will replace the other {@link BlockPos}
     */
    void replacePos(BlockPos oldPos, BlockPos newPos);

    /**
     * method to get a certain {@link BlockPos} from an index in the posList
     *
     * @param index the index of the BlockPos
     * @return the {@link BlockPos} of the index
     */
    BlockPos getPos(int index);

    /**
     * method to get the last {@link BlockPos} of the posList
     *
     * @return the last {@link BlockPos} of the posList
     */
    BlockPos getLastPos();

    /**
     * method to get the first {@link BlockPos} of the posList
     *
     * @return the first {@link BlockPos} of the posList
     */
    BlockPos getFirstPos();

    /**
     * method to get a random {@link BlockPos} of the posList
     *
     * @return the first {@link BlockPos} of the posList
     */
    BlockPos getRandomPos();

    /**
     * method to get a random {@link BlockPos} of the posList
     *
     * @param random the random object that will be used to get the index
     * @return the first {@link BlockPos} of the posList
     */
    BlockPos getRandomPos(Random random);

    /**
     * used to get the {@link BlockState}
     *
     * @return the {@link BlockState} of the BlockShapeManager
     */
    BlockState getBlockState();

    /**
     * change the {@link BlockState} of the BlockShapeManager
     *
     * @param blockState the {@link BlockState} related to the {@link BlockPos} list
     */
    void setBlockState(BlockState blockState);

    /**
     * method to get the number of {@link BlockPos} present in the related BlockList
     *
     * @return the number of {@link BlockPos}
     */
    int size();
}
