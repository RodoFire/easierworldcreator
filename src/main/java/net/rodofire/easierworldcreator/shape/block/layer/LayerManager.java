package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.Collection;
import java.util.Map;

/**
 * Class to assign BlockStates based on coordinates.
 * For that, we need a {@link BlockLayerManager}.
 * It allows us to define which blocks will go where.
 * This class will choose which block to assign depending on {@link Type}.
 * For example based on this shape:
 * <pre>
 *     {@code
 *     BlockLayer layer1, layer2, layer3
 *
 *     BlockLayerManager manager = new BlockLayerManager(
 *          List.of(layer1, layer2, layer3),
 *          List.of((short)1, (short)2, (short)5
 *     );
 *
 *     LayerManager layerManager = new LayerManager(Type.SURFACE, manager);
 *     }
 *     <br>
 *     The layer assignement will result on this:
 * </pre>
 * <pre>
 *       {@code
 *             * * *                  1 1 1
 *         * * * * * *            1 1 2 2 2 1
 *       * * * * * * *          1 2 2 2 2 2 2
 *       * * * * * * *      ->  2 2 2 3 3 3 2
 *       * * * * * * * *        2 3 3 3 3 3 3 1
 *       * * * * * * * *        3 3 3 3 3 3 3 2
 *       }
 *       </pre>
 * Then, after choosing the layer, we choose which block of the layer will be placed.
 * For more information, see {@link BlockLayer}
 */
public class LayerManager implements Layer {
    Type layerType;
    private final BlockLayerManager blockLayerManager;
    protected Vec3d centerPos = new Vec3d(0, 0, 0);
    protected Vec3i directionVector = new Vec3i(0, 1, 0);

    public LayerManager(Type layerType, BlockLayerManager blockLayerManager) {
        this.layerType = layerType;
        this.blockLayerManager = blockLayerManager;
    }


    public LayerManager(Type layerType, BlockLayerManager blockLayerManager, Vec3d centerPos) {
        this.blockLayerManager = blockLayerManager;
        this.layerType = layerType;
        this.centerPos = centerPos;
    }

    public LayerManager(Type layerType, BlockLayerManager blockLayerManager, Vec3d centerPos, Vec3i directionVector) {
        this.layerType = layerType;
        this.blockLayerManager = blockLayerManager;
        this.centerPos = centerPos;
        this.directionVector = directionVector;
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) ->
                                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos)
                    ))
            );
            return manager;
        }
        return getLayer().get(posMap);
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) -> layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler())
                    ))
            );
        }
        getLayer().place(world, posMap);
    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) -> {
                                if (layer.getRuler().canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);

                            }
                    ))
            );
            return manager;
        }
        return getLayer().getVerified(world, posMap);
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) ->
                                    manager.putWithoutVerification(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos)
                    ))
            );
            return manager;
        }
        return getLayer().getDivided(posMap);
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) -> {
                                if (layer.getRuler().canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                                    manager.putWithoutVerification(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
                            }
                    ))
            );
            return manager;
        }
        return getLayer().getVerifiedDivided(world, posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos)));
            return manager;
        }
        return getLayer().get(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {
        if (blockLayerManager.size() == 1) {
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> layer.getPlacer().place(world, layer.getBlockStates(), pos, layer.getRuler())));
        }
        getLayer().place(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                if (layer.getRuler().canPlace(world.getBlockState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }));
            return manager;
        }
        return getLayer().getVerified(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }));
            return manager;
        }
        return getLayer().getDivided(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                if (layer.getRuler().canPlace(world.getBlockState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }));
            return manager;
        }
        return getLayer().getVerifiedDivided(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }));
            return manager;
        }
        return getLayer().get(posList);
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {
        if (blockLayerManager.size() == 1) {
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
            }));
        }
        getLayer().place(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                if (layer.getRuler().canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }));
            return manager;
        }
        return getLayer().getVerified(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }));
            return manager;
        }
        return getLayer().getDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            posList.forEach(((pos) -> {
                if (layer.getRuler().canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }));
            return manager;
        }
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
        return Vec3d.of(directionVector);
    }

    @Override
    public void setDirectionVector(Vec3i directionVector) {
        this.directionVector = directionVector;
    }


    private Layer getLayer() {
        return switch (layerType) {
            case SURFACE -> new SurfaceLayer(blockLayerManager);
            case INNER_RADIAL -> new InnerRadialLayer(blockLayerManager, centerPos);
            case OUTER_RADIAL -> new OuterRadialLayer(blockLayerManager, centerPos);
            case INNER_CYLINDRICAL -> new InnerCylindricalLayer(blockLayerManager, centerPos, directionVector);
            case OUTER_CYLINDRICAL -> new OuterCylindricalLayer(blockLayerManager, centerPos, directionVector);
            case ALONG_DIRECTION -> new DirectionalLayer(blockLayerManager, centerPos, directionVector);
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
