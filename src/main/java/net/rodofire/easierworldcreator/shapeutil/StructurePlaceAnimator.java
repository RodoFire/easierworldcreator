package net.rodofire.easierworldcreator.shapeutil;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.worldgenutil.BlockStateUtil;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StructurePlaceAnimator {
    private StructureWorldAccess world;
    private AnimatorType animatorType;
    private AnimatorTime animatorTime;

    public StructurePlaceAnimator(StructureWorldAccess world, AnimatorType animatorType, AnimatorTime animatorTime) {
        this.world = world;
        this.animatorType = animatorType;
        this.animatorTime = animatorTime;
    }

    public Pair<Integer, Integer> getRandomBlocksPerTickBound() {
        return randomBlocksPerTickBound;
    }

    public void setRandomBlocksPerTickBound(Pair<Integer, Integer> randomBlocksPerTickBound) {
        this.randomBlocksPerTickBound = randomBlocksPerTickBound;
    }

    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = blocksPerTick;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public BlockPos getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(BlockPos centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Vec3d getAxisDirection() {
        return axisDirection;
    }

    public void setAxisDirection(Vec3d axisDirection) {
        this.axisDirection = axisDirection;
    }

    public AnimatorTime getAnimatorTime() {
        return animatorTime;
    }

    public void setAnimatorTime(AnimatorTime animatorTime) {
        this.animatorTime = animatorTime;
    }

    public AnimatorType getAnimatorType() {
        return animatorType;
    }

    public void setAnimatorType(AnimatorType animatorType) {
        this.animatorType = animatorType;
    }

    public StructureWorldAccess getWorld() {
        return world;
    }

    public void setWorld(StructureWorldAccess world) {
        this.world = world;
    }

    /**
     * you don't need to manually define each of theses since that they each belong to one animatorTime
     */
    private Pair<Integer, Integer> randomBlocksPerTickBound = new Pair<>(0, 100);
    private int blocksPerTick = 100;
    private int ticks = 500;

    int ticksRemaining;

    private BlockPos centerPoint = new BlockPos(0, 0, 0);
    private Vec3d axisDirection = new Vec3d(0, 1, 0);

    List<Pair<BlockState, BlockPos>> getSortedBlockList(List<Set<BlockList>> blockList) {
        List<Pair<BlockState, BlockPos>> sortedBlockList = new ArrayList<>();
        BlockStateUtil.convertBlockListToBlockStatePair(blockList, sortedBlockList);
        return switch (this.animatorType) {
            case ALONG_AXIS -> {
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
                sortedBlockList = sortedBlockList.parallelStream().sorted(
                        Comparator.comparingDouble((pair) -> WorldGenUtil.getDistance(centerPoint, pair.getRight())
                        )).collect(Collectors.toList());
                yield sortedBlockList;
            }
            case RANDOM -> {
                Collections.shuffle(sortedBlockList);
                yield sortedBlockList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + animatorType);
        };
    }

    public void placeFromBlockList(List<Set<BlockList>> blockList) {
        if (blockList == null || blockList.isEmpty()) {
            Easierworldcreator.LOGGER.warn("StructureBlockAnimator: blockList is null or empty");
            return;
        }
        long start = System.nanoTime();
        List<Pair<BlockState, BlockPos>> sortedBlockList = getSortedBlockList(blockList);
        long end = System.nanoTime();
        long diff = end - start;
        Easierworldcreator.LOGGER.info("Shape sorted list calculations took : {}ms", ((double) (diff / 1000)) / 1000);
        this.place(sortedBlockList);
    }

    /**
     * <p>Method to handle animated block placement. This has some advantages : </p>
     * <p> - Since that the placement take many ticks instead of one, it will improve user experience when placing huge structures
     * <p> - It may look better than just a structure spawning
     * <p>The method need a {@code List<Pair<BlockState, BlockPos>>} that will be used to place the blocks.
     * <p>The method calculates the number of ticks it will take to place the structure and will then place a part of the structure depending on how much blocks per ticks should be placed.
     * <p>To place them, it registers an event happening at the end of each world tick.
     *
     * @param blocksToPlace the {@code List<Pair<>>} that will be placed.
     */
    public void place(List<Pair<BlockState, BlockPos>> blocksToPlace) {
        int placeTicks;
        List<Integer> randomBlocks = new ArrayList<>();

        switch (animatorTime) {
            case TICKS -> placeTicks = blocksToPlace.size() / ticks;

            case RANDOM_BLOCKS_PER_TICK -> {
                int size = blocksToPlace.size();
                int blocksSelected = 0;
                while (blocksSelected < size) {
                    int randomInt = Random.create().nextBetween(Math.min(size - blocksSelected, randomBlocksPerTickBound.getLeft()), Math.min(size - blocksSelected, randomBlocksPerTickBound.getRight()));
                    blocksSelected += randomInt;
                    randomBlocks.add(randomInt);
                }
                placeTicks = randomBlocks.size();
            }
            case BLOCKS_PER_TICK -> placeTicks = ticks;
            default -> throw new IllegalStateException("Unexpected value: " + animatorTime);
        }

        this.ticksRemaining = placeTicks;
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (this.ticksRemaining <= 0 || blocksToPlace.isEmpty()) {
                return;
            }
            if (animatorType == AnimatorType.RANDOM) {
                for (int i = 0; i < randomBlocks.get(0) && !blocksToPlace.isEmpty(); i++) {
                    Pair<BlockState, BlockPos> blockPair = blocksToPlace.remove(0);
                    world.setBlockState(blockPair.getRight(), blockPair.getLeft(), 3);
                }
                randomBlocks.remove(0);
            } else {
                for (int i = 0; i < blocksPerTick && !blocksToPlace.isEmpty(); i++) {
                    Pair<BlockState, BlockPos> blockPair = blocksToPlace.remove(0);
                    world.setBlockState(blockPair.getRight(), blockPair.getLeft(), 3);
                }
            }
            this.ticksRemaining--;
        });


    }


    public enum AnimatorType {
        ALONG_AXIS,
        RADIAL_AXIS,
        FROM_POINT,
        RANDOM,
        FROM_LIST
    }

    public enum AnimatorTime {
        BLOCKS_PER_TICK,
        RANDOM_BLOCKS_PER_TICK,
        TICKS
    }
}
