package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;

import java.util.Collection;
import java.util.Map;

/**
 * Manager class to connect the layerType to the classes
 */
public class LayerManager implements Layer {
    Type layerType;
    private final BlockLayerManager blockLayerManager;
    protected Vec3d centerPos;
    protected Vec3d directionVector;

    public LayerManager(Type layerType, BlockLayerManager blockLayerManager) {
        this.layerType = layerType;
        this.blockLayerManager = blockLayerManager;
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().get(posMap);
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        getLayer().place(world, posMap);
    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().getVerified(world, posMap);
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().getDivided(posMap);
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().getVerifiedDivided(world, posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        return getLayer().get(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {
        getLayer().place(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        return getLayer().getVerified(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        return getLayer().getDivided(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        return getLayer().getVerifiedDivided(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        return getLayer().get(posList);
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {
        getLayer().place(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        return getLayer().getVerified(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        return getLayer().getDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        return getLayer().getVerifiedDivided(world, posList);
    }

    @Override
    public Vec3d getCenterPos() {
        return centerPos;
    }

    @Override
    public void setCenterPos(Vec3d centerPos) {
        this.centerPos = centerPos;
    }

    @Override
    public Vec3d getDirectionVector() {
        return directionVector;
    }

    @Override
    public void setDirectionVector(Vec3d directionVector) {
        this.directionVector = directionVector;
    }


    private Layer getLayer() {
        return switch (layerType) {
            case SURFACE -> new SurfaceLayer(blockLayerManager);
            case INNER_RADIAL -> {
                Layer layer = new InnerRadialLayer(blockLayerManager);
                layer.setCenterPos(this.centerPos);
                yield layer;
            }
            case OUTER_RADIAL -> {
                Layer layer = new OuterRadialLayer(blockLayerManager);
                layer.setCenterPos(this.centerPos);
                yield layer;
            }
            case INNER_CYLINDRICAL -> {
                Layer layer = new InnerCylindricalLayer(blockLayerManager);
                layer.setCenterPos(this.centerPos);
                yield layer;
            }
            case OUTER_CYLINDRICAL -> {
                Layer layer = new OuterCylindricalLayer(blockLayerManager);
                layer.setCenterPos(this.centerPos);
                yield layer;
            }
            case ALONG_DIRECTION -> {
                Layer layer = new DirectionalLayer(blockLayerManager);
                layer.setCenterPos(this.centerPos);
                layer.setDirectionVector(this.directionVector);
                yield layer;
            }
        };
    }

    /**
     * change how the blocks are put
     */
    public enum Type {
        /**
         * for a natural aspect,
         * Put the first BlockStates on top of the structure for a coordinate x and z,
         * and until it reaches the depth of the layer
         */
        SURFACE,
        /**
         * place the blocks in a sphere shape, first layer being placed at the center
         */
        INNER_RADIAL,
        /**
         * place the blocks in a sphere shape, the last layer being placed at the center
         */
        OUTER_RADIAL,
        /**
         * place the blocks in a cylindrical shape, the first layer being placed at the center
         */
        INNER_CYLINDRICAL,
        /**
         * place the blocks in a cylindrical shape, the last layer being placed near the center
         */
        OUTER_CYLINDRICAL,
        /**
         * place the blocks on a plan
         * the plan is defined by the vector "directionalLayerDirection"
         */
        ALONG_DIRECTION;
    }
}
