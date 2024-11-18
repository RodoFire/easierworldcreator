package net.rodofire.easierworldcreator.blockdata.sorter;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockList;
import net.rodofire.easierworldcreator.worldgenutil.BlockStateUtil;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BlockSorter {
    private BlockSorterType type;

    private BlockPos centerPoint = new BlockPos(0, 0, 0);
    private Vec3d axisDirection = new Vec3d(-1, -1, 0);

    public BlockSorter(BlockSorterType type) {
        this.type = type;
    }

    /**
     * Returns the central point of the structure.
     *
     * @return the center point as a BlockPos object.
     */
    public BlockPos getCenterPoint() {
        return centerPoint;
    }

    /**
     * Sets the central point of the structure.
     *
     * @param centerPoint the center point as a BlockPos object.
     */
    public void setCenterPoint(BlockPos centerPoint) {
        this.centerPoint = centerPoint;
    }

    /**
     * Returns the axis direction used in the animation.
     *
     * @return the axis direction as a Vec3d object.
     */
    public Vec3d getAxisDirection() {
        return axisDirection;
    }

    /**
     * Sets the axis direction used in the animation.
     *
     * @param axisDirection the axis direction as a Vec3d object.
     */
    public void setAxisDirection(Vec3d axisDirection) {
        this.axisDirection = axisDirection;
    }

    public BlockSorterType getType() {
        return type;
    }

    public void setType(BlockSorterType type) {
        this.type = type;
    }


    /**
     * Method to sort the list depending on the {@code animatorType}.
     * The method will only modify each {@code List<BLockPos>} in every BlockList.
     * If you want to force the method so that every blockPos getSorted, see {@code sortInsideBlockList()}
     *
     * @param blockListList the list of BlockList that will be sorted
     */
    public void sortInsideBlockList(List<BlockList> blockListList) {
        switch (this.type) {
            case FROM_POINT -> {
                for (BlockList blockList : blockListList) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;

                    posList = posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos))).collect(Collectors.toList());
                    blockList.setPosList(posList);
                }
            }
            case FROM_POINT_INVERTED -> {
                for (BlockList blockList : blockListList) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;

                    posList = posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos))).collect(Collectors.toList());
                    blockList.setPosList(posList);
                }
            }
            case RANDOM -> {
                for (BlockList blockList : blockListList) {
                    Collections.shuffle(blockList.getPosList());
                }
            }
            case ALONG_AXIS -> {
                for (BlockList blockList : blockListList) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;
                    Vec3d direction = this.axisDirection.normalize();
                    blockList.setPosList(
                            posList.parallelStream().sorted(Comparator.comparingDouble(
                                            (pos) -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z))
                                    .toList()
                    );
                }
            }
            case RADIAL_AXIS -> {
                for (BlockList blockList : blockListList) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;
                    blockList.setPosList(
                            posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                                Vec3d axisPoint = this.centerPoint.toCenterPos();
                                Vec3d axisDirection = this.axisDirection.normalize();
                                Vec3d blockPosition = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                                Vec3d pointToBlock = blockPosition.subtract(axisPoint);
                                double projectionLength = pointToBlock.dotProduct(axisDirection);
                                Vec3d closestPointOnAxis = axisPoint.add(axisDirection.multiply(projectionLength));
                                return WorldGenUtil.getDistance(BlockPos.ofFloored(closestPointOnAxis), BlockPos.ofFloored(axisPoint));
                            })).collect(Collectors.toList())
                    );
                }
            }
        }
    }


    /**
     * Method to sort the list depending on the {@code animatorType}. The method will sort every blockPos
     *
     * @param blockList the list of BlockList that will be sorted
     * @return a list of pair of BlockStates and BlockPos
     */
    public List<Pair<BlockState, BlockPos>> sortBlockList(List<BlockList> blockList) {
        List<Pair<BlockState, BlockPos>> sortedBlockList = new ArrayList<>();
        return switch (this.type) {
            case ALONG_AXIS -> {
                BlockStateUtil.convertBlockListToBlockStatePair(blockList, sortedBlockList);
                Vec3d direction = this.axisDirection.normalize();
                sortedBlockList = sortedBlockList.parallelStream().sorted(Comparator.comparingDouble((pair) -> {
                    BlockPos pos = pair.getRight();
                    return -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z;
                })).collect(Collectors.toList());

                yield sortedBlockList;
            }
            case RADIAL_AXIS -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                BlockStateUtil.convertBlockListToBlockStatePair(blockList, sortedBlockList);
                sortedBlockList = sortedBlockList.parallelStream().sorted(Comparator.comparingDouble((pair) -> {
                    BlockPos pos = pair.getRight();
                    Vec3d blockPosition = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    Vec3d pointToBlock = blockPosition.subtract(axisPoint);
                    double projectionLength = pointToBlock.dotProduct(axisDirection);
                    Vec3d closestPointOnAxis = axisPoint.add(axisDirection.multiply(projectionLength));
                    return WorldGenUtil.getDistance(BlockPos.ofFloored(closestPointOnAxis), BlockPos.ofFloored(axisPoint));
                })).collect(Collectors.toList());
                yield sortedBlockList;
            }
            case FROM_POINT -> {
                BlockStateUtil.convertBlockListToBlockStatePair(blockList, sortedBlockList);
                sortedBlockList = sortedBlockList.parallelStream().sorted(
                        Comparator.comparingDouble((pair) -> WorldGenUtil.getDistance(centerPoint, pair.getRight())
                        )).collect(Collectors.toList());
                yield sortedBlockList;
            }
            case FROM_POINT_INVERTED -> {
                BlockStateUtil.convertBlockListToBlockStatePair(blockList, sortedBlockList);
                sortedBlockList = sortedBlockList.parallelStream().sorted(
                        Comparator.comparingDouble((pair) -> -WorldGenUtil.getDistance(centerPoint, pair.getRight())
                        )).collect(Collectors.toList());
                yield sortedBlockList;
            }
            case RANDOM -> {
                BlockStateUtil.convertBlockListToBlockStatePair(blockList, sortedBlockList);
                Collections.shuffle(sortedBlockList);
                yield sortedBlockList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * enum to decide how the order of the blocks
     */
    public enum BlockSorterType {
        /**
         * will place the blocks on an orthogonal plan to an axis
         */
        ALONG_AXIS,
        /**
         * will place the blocks closer to an axis first
         */
        RADIAL_AXIS,
        /**
         * will place the blocks from the closer to a blockPos to the further
         */
        FROM_POINT,
        /**
         * will place the blocks from the further to a blockPos to the closer
         */
        FROM_POINT_INVERTED,
        /**
         * will place the blocks in a random order
         */
        RANDOM,
        /**
         * will place the blocks depending on your input
         */
        FROM_LIST
    }
}
