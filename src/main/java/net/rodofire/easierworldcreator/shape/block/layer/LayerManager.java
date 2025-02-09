package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerComparator;

import java.util.Collection;
import java.util.Map;

/**
 * Manager class to connect the layerType to the classes
 */
public class LayerManager implements Layer {
    Type layerType;
    private final BlockLayerComparator blockLayerComparator;

    public LayerManager(Type layerType, BlockLayerComparator blockLayerComparator) {
        this.layerType = layerType;
        this.blockLayerComparator = blockLayerComparator;
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().get(posMap);
    }

    @Override
    public void place(Map<ChunkPos, LongOpenHashSet> posMap) {
        getLayer().place(posMap);
    }

    @Override
    public BlockListManager getVerified(Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().getVerified(posMap);
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().getDivided(posMap);
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        return getLayer().getVerifiedDivided(posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        return getLayer().get(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> void place(T posList) {
        getLayer().place(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(T posList) {
        return getLayer().getVerified(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        return getLayer().getDivided(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(T posList) {
        return getLayer().getVerifiedDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        return getLayer().get(posList);
    }

    @Override
    public <U extends AbstractLongCollection> void place(U posList) {
        getLayer().place(posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(U posList) {
        return getLayer().getVerified(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        return getLayer().getDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(U posList) {
        return getLayer().getVerifiedDivided(posList);
    }


    private Layer getLayer() {
        return switch (layerType) {
            case SURFACE -> new SurfaceLayer(blockLayerComparator);
            case INNER_RADIAL -> new InnerRadialLayer(blockLayerComparator);
            case OUTER_RADIAL -> new OuterRadialLayer(blockLayerComparator);
            case INNER_CYLINDRICAL -> new InnerCylindricalLayer(blockLayerComparator);
            case OUTER_CYLINDRICAL -> new OuterCylindricalLayer(blockLayerComparator);
            case ALONG_DIRECTION -> new DirectionalLayer(blockLayerComparator);
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
