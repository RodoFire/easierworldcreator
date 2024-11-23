package net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

/**
 * Default Ordered BlockList comparator. The class provides the basic related to order blockList comparator
 */
@SuppressWarnings("unused")
public class DefaultOrderedBlockListComparator extends OrderedBlockListComparator<BlockState> {
    /**
     * init a default ordered blockList comparator
     *
     * @param state   the state that will be tested and put (in the case it doesn't exist)
     * @param posList the blockPos that will be put related to the given state
     */
    public DefaultOrderedBlockListComparator(BlockState state, List<BlockPos> posList) {
        super(state, posList);
    }

    /**
     * init a comparator
     *
     * @param info the map that will be used to init the comparator
     */
    public DefaultOrderedBlockListComparator(Map<BlockState, List<BlockPos>> info) {
        super(info);
    }

    /**
     * init an empty comparator
     */
    public DefaultOrderedBlockListComparator() {
    }

    /**
     * Method to get the List of BlockStates that are saved in the comparator
     *
     * @return the list of blockStates
     */
    public List<BlockState> getBlockStates() {
        return getT();
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public BlockState getBlockState(short index) {
        return this.statesMap.get(index);
    }

    /**
     * Method to get the first BlockState
     *
     * @return the first BlockState
     */
    public BlockState getFirstBlockState() {
        return this.statesMap.get((short) 0);
    }

    /**
     * Method to get the last BlockState
     *
     * @return the last BlockState
     */
    public BlockState getLastBlockState() {
        return this.statesMap.get((short) (statesMap.size() - 1));
    }

}
