package net.rodofire.easierworldcreator.shapeutil;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.worldgenutil.BlockStateUtil;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * class to put blocks with an animation
 */
@SuppressWarnings("unused")
public class StructurePlaceAnimator {
    private StructureWorldAccess world;

    private AnimatorType animatorType;
    private AnimatorTime animatorTime;
    private AnimatorSound animatorSound = AnimatorSound.DEFAULT;

    /**
     * you don't need to manually define each of theses since that they each belong to one animatorTime
     */
    private Pair<Integer, Integer> randomBlocksPerTickBound = new Pair<>(0, 100);
    private int blocksPerTick = 100;
    private int ticks = 500;

    int ticksRemaining;

    private BlockPos centerPoint = new BlockPos(0, 0, 0);
    private Vec3d axisDirection = new Vec3d(-1, -1, 0);


    private float soundPerTicks = 10f;

    /**
     * init a {@code StructurePlaceAnimator} object
     *
     * @param world        the world the animation will take place
     * @param animatorType the type of the animation
     * @param animatorTime the time of the animation
     */
    public StructurePlaceAnimator(StructureWorldAccess world, AnimatorType animatorType, AnimatorTime animatorTime) {
        this.world = world;
        this.animatorType = animatorType;
        this.animatorTime = animatorTime;
    }

    /**
     * Returns the random bounds for blocks placed per tick.
     *
     * @return a Pair representing the minimum and maximum number of blocks to be placed per tick.
     */
    public Pair<Integer, Integer> getRandomBlocksPerTickBound() {
        return randomBlocksPerTickBound;
    }

    /**
     * Sets the random bounds for blocks placed per tick.
     *
     * @param randomBlocksPerTickBound a Pair representing the minimum and maximum number of blocks to be placed per tick.
     */
    public void setRandomBlocksPerTickBound(Pair<Integer, Integer> randomBlocksPerTickBound) {
        this.randomBlocksPerTickBound = randomBlocksPerTickBound;
    }

    /**
     * Returns the number of blocks to place per tick.
     *
     * @return the number of blocks placed each tick.
     */
    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    /**
     * Sets the number of blocks to place per tick.
     *
     * @param blocksPerTick the number of blocks placed each tick.
     */
    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = blocksPerTick;
    }

    /**
     * Returns the current tick count.
     *
     * @return the number of ticks.
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * Sets the current tick count.
     *
     * @param ticks the number of ticks.
     */
    public void setTicks(int ticks) {
        this.ticks = ticks;
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
     * Returns the AnimatorTime object, which manages timing for the animation.
     *
     * @return the AnimatorTime object.
     */
    public AnimatorTime getAnimatorTime() {
        return animatorTime;
    }

    /**
     * Sets the AnimatorTime object, which manages timing for the animation.
     *
     * @param animatorTime the AnimatorTime object.
     */
    public void setAnimatorTime(AnimatorTime animatorTime) {
        this.animatorTime = animatorTime;
    }

    /**
     * Returns the AnimatorType, representing the type of animation.
     *
     * @return the AnimatorType.
     */
    public AnimatorType getAnimatorType() {
        return animatorType;
    }

    /**
     * Sets the AnimatorType, representing the type of animation.
     *
     * @param animatorType the AnimatorType.
     */
    public void setAnimatorType(AnimatorType animatorType) {
        this.animatorType = animatorType;
    }

    /**
     * Returns the StructureWorldAccess for interacting with the world.
     *
     * @return the StructureWorldAccess object.
     */
    public StructureWorldAccess getWorld() {
        return world;
    }

    /**
     * Sets the StructureWorldAccess for interacting with the world.
     *
     * @param world the StructureWorldAccess object.
     */
    public void setWorld(StructureWorldAccess world) {
        this.world = world;
    }

    /* ------ Sound Related ----- */

    /**
     * Returns the AnimatorSound object, which manages sounds for the animation.
     *
     * @return the AnimatorSound object.
     */
    public AnimatorSound getAnimatorSound() {
        return animatorSound;
    }

    /**
     * Sets the AnimatorSound object, which manages sounds for the animation.
     *
     * @param animatorSound the AnimatorSound object.
     */
    public void setAnimatorSound(AnimatorSound animatorSound) {
        this.animatorSound = animatorSound;
    }

    /**
     * Returns the interval of ticks between sounds during the animation.
     *
     * @return the interval as a float.
     */
    public float getSoundPerTicks() {
        return soundPerTicks;
    }

    /**
     * Sets the interval of ticks between sounds during the animation.
     *
     * @param soundPerTicks the interval as a float.
     */
    public void setSoundPerTicks(float soundPerTicks) {
        this.soundPerTicks = soundPerTicks;
    }

    /**
     * method to sort the list depending on the {@code animatorType} and handling the divided List of BlockList
     *
     * @param blockList the list of BlockList that will be sorted
     * @return a list of pair of BlockStates and BlockPos
     */
    List<BlockList> convertFromDividedToUnified(List<Set<BlockList>> blockList) {
        List<BlockList> fusedList = new ArrayList<>();
        for (Set<BlockList> set : blockList) {
            fusedList.addAll(set);
        }
        return fusedList;
    }

    /**
     * method to sort the list depending on the {@code animatorType}
     *
     * @param blockList the list of BlockList that will be sorted
     * @return a list of pair of BlockStates and BlockPos
     */
    List<Pair<BlockState, BlockPos>> getSortedBlockList(List<BlockList> blockList) {
        List<Pair<BlockState, BlockPos>> sortedBlockList = new ArrayList<>();
        return switch (this.animatorType) {
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
            default -> throw new IllegalStateException("Unexpected value: " + animatorType);
        };
    }

    /**
     * method to place the structure by merging and then sorting the BlockList depending on the {@code animatorType}
     *
     * @param blockList the list of BlockList that will be placed
     */
    public void placeFromDividedBlockList(List<Set<BlockList>> blockList) {
        if (blockListVerification(blockList)) return;
        Instant start = Instant.now();
        List<BlockList> fusedList = convertFromDividedToUnified(blockList);
        placeFromBlockList(fusedList);
    }

    /**
     * method to place the structure by sorting the BlockList depending on the {@code animatorType}
     *
     * @param blockList the list of BlockList that will be placed
     */
    public void placeFromBlockList(List<BlockList> blockList) {
        if (blockListVerification(blockList)) return;
        Instant start = Instant.now();
        List<Pair<BlockState, BlockPos>> sortedBlockList = getSortedBlockList(blockList);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        EasierWorldCreator.LOGGER.info("Shape sorted list calculations took : {}ms", timeElapsed.toMillis());
        this.place(sortedBlockList);
    }

    /**
     * method to verify that the provided list is not Empty
     *
     * @param blockList the list to verify
     * @param <T>       allow to be used in the divided and unified context
     * @return true if it's empty or null or false in the other case
     */
    private static <T> boolean blockListVerification(List<T> blockList) {
        if (blockList == null || blockList.isEmpty()) {
            EasierWorldCreator.LOGGER.warn("StructureBlockAnimator: blockList is null or empty");
            return true;
        }
        return false;
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
        int totalBlocks = blocksToPlace.size();
        AtomicReference<Float> soundPlayed = new AtomicReference<>((float) 0);

        switch (animatorTime) {
            case TICKS -> {
                if (ticks == 0) {
                    EasierWorldCreator.LOGGER.error("StructureBlockAnimator: ticks is zero");
                    return;
                }
                blocksPerTick = Math.max(1, totalBlocks / ticks); // a minimum of one block per tick will be placed
                placeTicks = ticks;
            }
            case RANDOM_BLOCKS_PER_TICK -> {
                int blocksSelected = 0;
                while (blocksSelected < totalBlocks) {
                    int randomInt = Random.create().nextBetween(
                            Math.min(totalBlocks - blocksSelected, randomBlocksPerTickBound.getLeft()),
                            Math.min(totalBlocks - blocksSelected, randomBlocksPerTickBound.getRight())
                    );
                    blocksSelected += randomInt;
                    randomBlocks.add(randomInt);
                }
                placeTicks = randomBlocks.size();
            }
            case BLOCKS_PER_TICK -> {
                if (blocksPerTick <= 0) {
                    EasierWorldCreator.LOGGER.error("StructureBlockAnimator: blocksPerTick is zero or negative");
                    return;
                }
                placeTicks = (int) Math.ceil((double) totalBlocks / blocksPerTick);
            }
            default -> throw new IllegalStateException("Unexpected value: " + animatorTime);
        }


        switch (animatorSound) {
            case NO_SOUND -> this.soundPerTicks = 0;
            case DEFAULT -> this.soundPerTicks = 0.5f;
            case NUMBER_PER_TICK -> this.soundPerTicks = Math.min(blocksPerTick, this.soundPerTicks);
            default -> throw new IllegalStateException("Unexpected value: " + animatorSound);
        }

        ticksRemaining = placeTicks;
        EasierWorldCreator.LOGGER.info("Starting placement with {} ticks and {} blocks per tick", placeTicks, blocksPerTick);
        EasierWorldCreator.LOGGER.info("size: {}", blocksToPlace.size());

        //calling on end server tick because end world tick wouldn't place the blocks 2 times on 3.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ticksRemaining <= 0 || blocksToPlace.isEmpty()) {
                return;
            }
            soundPlayed.set(soundPlayed.get() + this.soundPerTicks);

            int blocksThisTick;
            if (animatorTime == AnimatorTime.RANDOM_BLOCKS_PER_TICK && !randomBlocks.isEmpty()) {
                blocksThisTick = randomBlocks.remove(0);
            } else {
                blocksThisTick = Math.min(blocksPerTick, blocksToPlace.size());
            }

            for (int i = 0; i < blocksThisTick && !blocksToPlace.isEmpty(); i++) {
                Pair<BlockState, BlockPos> blockPair = blocksToPlace.remove(0);
                BlockState state = blockPair.getLeft();
                BlockPos pos = blockPair.getRight();

                if (soundPlayed.get() >= 1) {
                    world.playSound(null, pos, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, (float) Random.create().nextBetween(20, 100) / 10, (float) Random.create().nextBetween(5, 20) / 10);
                    soundPlayed.set(soundPlayed.get() - 1);
                }
                world.setBlockState(blockPair.getRight(), state, 2);
            }
            ticksRemaining--;

            if (ticksRemaining == 0 && !blocksToPlace.isEmpty()) {
                EasierWorldCreator.LOGGER.warn("All ticks completed, but {} blocks are still unplaced. Placing remaining blocks in final tick.", blocksToPlace.size());
                blocksToPlace.forEach(blockPair -> world.setBlockState(blockPair.getRight(), blockPair.getLeft(), 3));
                blocksToPlace.clear();
            }

            EasierWorldCreator.LOGGER.debug("Tick {}: {} blocks remaining", placeTicks - ticksRemaining, blocksToPlace.size());
        });
    }

    /**
     * enum to decide how the order of the blocks
     */
    public enum AnimatorType {
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

    /**
     * enum to determine how much time the structure will be placed
     */
    public enum AnimatorTime {
        /**
         * determines a defined number of blocks per tick
         */
        BLOCKS_PER_TICK,
        /**
         * will place a random number blocks every tick
         */
        RANDOM_BLOCKS_PER_TICK,
        /**
         * determines a fixed number of ticks to place the structure
         */
        TICKS
    }

    /**
     * enum to decide how to play sounds
     */
    public enum AnimatorSound {
        /**
         * no sounds will be played
         */
        NO_SOUND,
        /**
         * will play one sound every tick
         */
        DEFAULT,
        /**
         * set a number of sounds per tick
         */
        NUMBER_PER_TICK
    }
}
