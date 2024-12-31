package net.rodofire.easierworldcreator.entity.trajectory;

import jdk.jfr.Experimental;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.rodofire.easierworldcreator.entity.EntityUtil;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockStateUtil;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to verify that an entity is able to jump without any collision.
 * It is recommended to only use one object for every verification done for better performance
 */
@SuppressWarnings("unused")
@Experimental
public class JumpVerification {
    int totalTime;
    float overshoot;

    Entity entity;

    Set<BlockPos> passing = new HashSet<>();
    Set<BlockPos> verified = new HashSet<>();
    Set<Block> allowed;
    EntityDimensions dimensions;
    BlockPos endPos;
    BlockPos startPos;

    Vec3d initial;


    public JumpVerification(Entity entity, BlockPos endPos, Vec3d initial, int totalTick, List<TagKey<Block>> allowed) {
        this.dimensions = entity.getDimensions(EntityPose.LONG_JUMPING);
        this.endPos = endPos;
        this.initial = initial;
        this.totalTime = totalTick;
        this.allowed = BlockStateUtil.convertBlockTagToBlockList(allowed);
    }

    public JumpVerification(Entity entity, BlockPos endPos, Vec3d initial, int totalTick, List<TagKey<Block>> allowed, Set<BlockPos> passing, Set<BlockPos> verified) {
        this.dimensions = entity.getDimensions(EntityPose.LONG_JUMPING);
        this.endPos = endPos;
        this.initial = initial;
        this.totalTime = totalTick;
        this.allowed = BlockStateUtil.convertBlockTagToBlockList(allowed);
        this.passing = new HashSet<>(passing);
        this.verified = new HashSet<>(verified);

    }

    public boolean verify() {
        Vec3d currentPos = this.startPos.toCenterPos();

        Vec3d velocity = this.initial;
        // we do 30 verifications per tick
        for (int i = 0; i < totalTime * 30; i++) {
            // change the velocity each tick
            if (i % 30 == 0) {
                velocity = new Vec3d(
                        velocity.x * EntityUtil.HORIZONTAL_DRAG,
                        (velocity.y - EntityUtil.GRAVITY) * EntityUtil.VERTICAL_DRAG,
                        velocity.z * EntityUtil.HORIZONTAL_DRAG
                );
            }

            // linear interpolation
            currentPos = currentPos.add(velocity.multiply((double) 1 / 30));

            if(WorldGenUtil.getDistance(currentPos, startPos.toCenterPos()) < 1)
                continue;

            // verifying the blocks that the entity Box is occupying
            Set<BlockPos> occupiedBlocks = getOccupiedBlocks(currentPos);
            for (BlockPos pos : occupiedBlocks) {
                if (passing.contains(pos)) {
                    continue;
                }
                if (verified.contains(pos)) {
                    return false;
                }

                BlockState state = entity.getWorld().getBlockState(pos);
                if (allowed.contains(state.getBlock())) {
                    passing.add(pos);
                } else {
                    verified.add(pos);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * determine which blocks are occupied by the entity
     * @param pos the position of the entity
     */
    private Set<BlockPos> getOccupiedBlocks(Vec3d pos) {
        Set<BlockPos> blocks = new HashSet<>();

        double halfWidth = dimensions.width / 2.0;
        double height = dimensions.height;

        int minX = (int) Math.floor(pos.x - halfWidth);
        int maxX = (int) Math.ceil(pos.x + halfWidth);
        int minY = (int) Math.floor(pos.y);
        int maxY = (int) Math.ceil(pos.y + height);
        int minZ = (int) Math.floor(pos.z - halfWidth);
        int maxZ = (int) Math.ceil(pos.z + halfWidth);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }

        return blocks;
    }
}
