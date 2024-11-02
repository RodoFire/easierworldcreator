package net.rodofire.easierworldcreator.shapeutil;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.worldgenutil.BlockStateUtil;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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


    /* ------ Sound Related ----- */
    public AnimatorSound getAnimatorSound() {
        return animatorSound;
    }

    public void setAnimatorSound(AnimatorSound animatorSound) {
        this.animatorSound = animatorSound;
    }

    public float getSoundPerTicks() {
        return soundPerTicks;
    }

    public void setSoundPerTicks(float soundPerTicks) {
        this.soundPerTicks = soundPerTicks;
    }

    List<Pair<BlockState, BlockPos>> getSortedBlockList(List<Set<BlockList>> blockList) {
        List<Pair<BlockState, BlockPos>> sortedBlockList = new ArrayList<>();
        return switch (this.animatorType) {
            case ALONG_AXIS -> {
                blockList = blockList.parallelStream().sorted(Comparator.comparingDouble((set) -> {
                    Optional<BlockList> optionalPos = set.stream().findFirst();
                    if (optionalPos.isPresent()) {
                        BlockPos pos = optionalPos.get().getPosList().get(0);
                        return WorldGenUtil.getDistance(pos, new BlockPos(0, -60, 0));
                    }
                    return 0;
                })).collect(Collectors.toList());
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

    public void placeFromBlockList(List<Set<BlockList>> blockList) {
        System.out.println("animator");
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
        int totalBlocks = blocksToPlace.size();
        AtomicReference<Float> soundPlayed = new AtomicReference<>((float) 0);

        switch (animatorTime) {
            case TICKS -> {
                if (ticks == 0) {
                    Easierworldcreator.LOGGER.error("StructureBlockAnimator: ticks is zero");
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
                    Easierworldcreator.LOGGER.error("StructureBlockAnimator: blocksPerTick is zero or negative");
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
        Easierworldcreator.LOGGER.info("Starting placement with {} ticks and {} blocks per tick", placeTicks, blocksPerTick);
        Easierworldcreator.LOGGER.info("size: {}", blocksToPlace.size());

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
                Easierworldcreator.LOGGER.warn("All ticks completed, but {} blocks are still unplaced. Placing remaining blocks in final tick.", blocksToPlace.size());
                blocksToPlace.forEach(blockPair -> world.setBlockState(blockPair.getRight(), blockPair.getLeft(), 3));
                blocksToPlace.clear();
            }

            Easierworldcreator.LOGGER.debug("Tick {}: {} blocks remaining", placeTicks - ticksRemaining, blocksToPlace.size());
        });
    }


    public enum AnimatorType {
        ALONG_AXIS,
        RADIAL_AXIS,
        FROM_POINT,
        FROM_POINT_INVERTED,
        RANDOM,
        FROM_LIST
    }

    public enum AnimatorTime {
        BLOCKS_PER_TICK,
        /**
         * will place a random number blocks every tick
         */
        RANDOM_BLOCKS_PER_TICK,
        TICKS
    }

    public enum AnimatorSound {
        NO_SOUND,
        /**
         * will play one sound every tick
         */
        DEFAULT,
        NUMBER_PER_TICK
    }
}
