package net.rodofire.easierworldcreator.blockdata.sorter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.AbstractOrderedBlockListComparator;
import net.rodofire.easierworldcreator.util.ListUtil;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * method to sort BlockPos depending on a parameter
 */
@SuppressWarnings("unused")
public class BlockSorter {
    private BlockSorterType type;

    private BlockPos centerPoint = new BlockPos(0, 0, 0);
    private Vec3d axisDirection = new Vec3d(-1, -1, 0);

    /**
     * Constructor of the object
     *
     * @param type the type enum that decide how the List will get sorted
     */
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

    /**
     * method to get the type of sorter
     *
     * @return the type enum of the sorter
     */
    public BlockSorterType getType() {
        return type;
    }

    /**
     * Method to set the type of the sorter
     *
     * @param type the type of the sorter
     */
    public void setType(BlockSorterType type) {
        this.type = type;
    }

    /**
     * method to set a random sorter type
     */
    public void setRandomSorter() {
        this.type = BlockSorterType.values()[Random.create().nextInt(7)];
        if (this.type == BlockSorterType.ALONG_AXIS) {
            this.axisDirection = new Vec3d(2 * Random.create().nextFloat() - 1, 2 * Random.create().nextFloat() - 1, 2 * Random.create().nextFloat() - 1);
        }
    }

    /**
     * Method to sort the list depending on the {@code animatorType}.
     *
     * @param posList the list of BlockPos that will be sorted
     * @return the sorted list
     */
    public List<BlockPos> sortBlockPos(List<BlockPos> posList) {
        return switch (this.type) {
            case FROM_POINT -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos))).collect(Collectors.toList());
            }
            case FROM_POINT_INVERTED -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos))).collect(Collectors.toList());
            }
            case FROM_RANDOM_POINT -> {
                this.centerPoint = ListUtil.getRandomElement(posList);
                yield posList.parallelStream().sorted(
                        Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos)
                        )).collect(Collectors.toList());
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                this.centerPoint = ListUtil.getRandomElement(posList);
                yield posList.parallelStream().sorted(
                        Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos)
                        )).collect(Collectors.toList());
            }
            case RANDOM -> {
                List<BlockPos> copy = new ArrayList<>(posList);
                Collections.shuffle(copy);
                yield copy;
            }
            case ALONG_AXIS -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                Vec3d direction = this.axisDirection.normalize();
                yield posList.parallelStream().sorted(Comparator.comparingDouble(
                                (pos) -> pos.getX() * direction.x + pos.getY() * direction.y + pos.getZ() * direction.z))
                        .toList();
            }
            case RADIAL_AXIS -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3d axisPoint = this.centerPoint.toCenterPos();
                    Vec3d axisDirection = this.axisDirection.normalize();
                    Vec3d blockPosition = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    Vec3d pointToBlock = blockPosition.subtract(axisPoint);
                    double projectionLength = pointToBlock.dotProduct(axisDirection);
                    Vec3d closestPointOnAxis = axisPoint.add(axisDirection.multiply(projectionLength));
                    return -WorldGenUtil.getDistance(BlockPos.ofFloored(closestPointOnAxis), BlockPos.ofFloored(axisPoint));
                })).collect(Collectors.toList());
            }
            case FROM_LIST -> {
                yield posList;
            }
            case INVERSE -> {
                List<BlockPos> copy = new ArrayList<>(posList);
                Collections.reverse(copy);
                yield copy;
            }

            case FROM_PLANE -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    double distance = blockVec.subtract(axisPoint).dotProduct(axisDirection);

                    return -Math.abs(distance);
                })).toList();
            }
            case FROM_PLANE_INVERTED -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    double distance = blockVec.subtract(axisPoint).dotProduct(axisDirection);

                    return Math.abs(distance);
                })).toList();
            }
            default -> throw new IllegalStateException("Unexpected sorter value: " + this.type);
        };
    }


    /**
     * Method to sort the list depending on the {@code animatorType}.
     * The method will only modify each {@code List<BLockPos>} in every BlockList.
     * If you want to force the method so that every blockPos getSorted, see {@code sortInsideBlockList()}
     *
     * @param blockListShapeManager the list of BlockList that will be sorted
     * @param <T>                   the object extending defaultBlockList
     */
    public <T extends DefaultBlockList> void sortInsideBlockList(List<T> blockListShapeManager) {
        switch (this.type) {
            case FROM_POINT -> {
                for (T blockList : blockListShapeManager) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;

                    posList = posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos))).collect(Collectors.toList());
                    blockList.setPosList(posList);
                }
            }
            case FROM_POINT_INVERTED -> {
                for (T blockList : blockListShapeManager) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;

                    posList = posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos))).collect(Collectors.toList());
                    blockList.setPosList(posList);
                }
            }
            case FROM_RANDOM_POINT -> {
                for (T blockList : blockListShapeManager) {
                    this.centerPoint = blockList.getRandomPos();
                    blockList.setPosList(
                            blockList.getPosList().parallelStream().sorted(
                                    Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos)
                                    )).collect(Collectors.toList())
                    );
                }
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                for (T blockList : blockListShapeManager) {
                    this.centerPoint = blockList.getRandomPos();
                    blockList.setPosList(
                            blockList.getPosList().parallelStream().sorted(
                                    Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos)
                                    )).collect(Collectors.toList())
                    );
                }
            }
            case RANDOM -> {
                for (T blockList : blockListShapeManager) {
                    Collections.shuffle(blockList.getPosList());
                }
            }
            case ALONG_AXIS -> {
                for (T blockList : blockListShapeManager) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;
                    Vec3d direction = this.axisDirection.normalize();
                    blockList.setPosList(
                            posList.parallelStream().sorted(Comparator.comparingDouble(
                                            (pos) -> pos.getX() * direction.x + pos.getY() * direction.y + pos.getZ() * direction.z))
                                    .toList()
                    );
                }
            }
            case RADIAL_AXIS -> {
                for (T blockList : blockListShapeManager) {
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
                                return -WorldGenUtil.getDistance(BlockPos.ofFloored(closestPointOnAxis), BlockPos.ofFloored(axisPoint));
                            })).collect(Collectors.toList())
                    );
                }
            }
            case FROM_LIST -> {
            }
            case INVERSE -> {
                for (T blockList : blockListShapeManager) {
                    Collections.reverse(blockList.getPosList());
                }
            }
            case FROM_PLANE -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                for (T blockList : blockListShapeManager) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;
                    blockList.setPosList(
                            posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                                Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                                double distance = blockVec.subtract(axisPoint).dotProduct(axisDirection);

                                return -Math.abs(distance);
                            })).toList()
                    );
                }
            }
            case FROM_PLANE_INVERTED -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                for (T blockList : blockListShapeManager) {
                    List<BlockPos> posList = blockList.getPosList();
                    if (posList == null || posList.isEmpty() || posList.size() == 1)
                        continue;
                    blockList.setPosList(
                            posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                                Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                                double distance = blockVec.subtract(axisPoint).dotProduct(axisDirection);

                                return Math.abs(distance);
                            })).toList()
                    );
                }
            }
            default -> throw new IllegalStateException("Unexpected sorter value: " + this.type);
        }
    }

    /**
     * Method to sort the list depending on the {@code animatorType}. The method will sort every blockPos of the BlockList and will return the related class to
     *
     * @param defaultBlockList the list of BlockList that will be sorted
     * @param <U>              the data of the block, for example, the blockState or the NbtCompounds
     * @param <W>              the object related to the orderedBlockListComparator
     * @return a list of W of BlockStates and BlockPos
     */
    public <W extends AbstractOrderedBlockListComparator<U>, U> W sortBlockList(W defaultBlockList) {

        return switch (this.type) {
            case ALONG_AXIS -> {
                Vec3d direction = this.axisDirection.normalize();
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble((pos) -> pos.getX() * direction.x + pos.getY() * direction.y + pos.getZ() * direction.z)).toList());

                yield defaultBlockList;
            }
            case RADIAL_AXIS -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3d blockPosition = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    Vec3d pointToBlock = blockPosition.subtract(axisPoint);
                    double projectionLength = pointToBlock.dotProduct(axisDirection);
                    Vec3d closestPointOnAxis = axisPoint.add(axisDirection.multiply(projectionLength));
                    return -WorldGenUtil.getDistance(BlockPos.ofFloored(closestPointOnAxis), BlockPos.ofFloored(axisPoint));
                })).toList());
                yield defaultBlockList;
            }
            case FROM_POINT -> {
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble((pos) ->
                        -WorldGenUtil.getDistance(centerPoint, pos))
                ).toList());
                yield defaultBlockList;
            }
            case FROM_POINT_INVERTED -> {
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos))
                ).toList());
                yield defaultBlockList;
            }
            case FROM_RANDOM_POINT -> {
                this.centerPoint = defaultBlockList.getRandomBlockPos();
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos))
                ).toList());
                yield defaultBlockList;
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                this.centerPoint = defaultBlockList.getRandomBlockPos();
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos))
                ).toList());
                yield defaultBlockList;
            }
            case FROM_PLANE -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble(pos -> {
                    Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    double distance = blockVec.subtract(axisPoint).dotProduct(axisDirection);

                    return -Math.abs(distance);
                })).toList());
                yield defaultBlockList;
            }
            case FROM_PLANE_INVERTED -> {
                Vec3d axisPoint = this.centerPoint.toCenterPos();
                Vec3d axisDirection = this.axisDirection.normalize();
                defaultBlockList.setPosList(defaultBlockList.getPosList().parallelStream().sorted(Comparator.comparingDouble(pos -> {
                    Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                    double distance = blockVec.subtract(axisPoint).dotProduct(axisDirection);

                    return Math.abs(distance);
                })).toList());
                yield defaultBlockList;

            }
            case RANDOM -> {
                Collections.shuffle(defaultBlockList.getPosList());
                yield defaultBlockList;
            }
            case FROM_LIST -> defaultBlockList;
            case INVERSE -> {
                Collections.reverse(defaultBlockList.getPosList());
                yield defaultBlockList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * enum to decide how the order of the blocks
     */
    public enum BlockSorterType {
        /**
         * Will place the blocks on an orthogonal plan to an axis. To use, it, use the method {@code setAxisDirection()}
         */
        ALONG_AXIS,
        /**
         * will place the blocks closer to an axis first
         */
        RADIAL_AXIS,
        /**
         * Will place the blocks from the closer to a blockPos to the further.
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_POINT,
        /**
         * will place the blocks from the further to a blockPos to the closer
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_POINT_INVERTED,
        /**
         * will take a random point of the shape and will place the blocks from the closer to the further
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_RANDOM_POINT,
        /**
         * will take a random point of the shape and will place the blocks from the further to the closer
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_RANDOM_POINT_INVERTED,
        /**
         * Will sort the BlockPos depending on the distance between the plane and the BlockPos.
         * The closer BlockPos will be first while the further will be last
         * To create a plane, you have to use {@code setCenterPoint()} as well as {@code setAxisDirection()}.
         */
        FROM_PLANE,
        /**
         * Will sort the BlockPos depending on the distance between the plane and the BlockPos.
         * The further BlockPos will be first while the closer will be last
         * To create a plane, you have to use {@code setCenterPoint()} as well as {@code setAxisDirection()}.
         */
        FROM_PLANE_INVERTED,
        /**
         * will place the blocks in a random order
         */
        RANDOM,
        /**
         * Invert the BlockPos List
         */
        INVERSE,
        /**
         * will place the blocks depending on your input
         */
        FROM_LIST
    }
}