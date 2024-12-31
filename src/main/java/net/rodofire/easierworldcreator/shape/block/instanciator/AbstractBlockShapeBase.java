package net.rodofire.easierworldcreator.shape.block.instanciator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerComparator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic Class for Block Shape generation. This includes the basic needs for the shape generation.
 */
@SuppressWarnings("unused")
public abstract class AbstractBlockShapeBase {
    @NotNull
    private final StructureWorldAccess world;
    @NotNull
    private BlockPos pos;


    @NotNull
    private PlaceMoment placeMoment;
    private BlockLayerComparator blockLayer;


    /**
     * boolean used to determine if we have to use the custom chunk building provided by the mod or not
     */
    protected boolean multiChunk = false;

    /**
     * get the number of availible threads
     */
    protected static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();


    /**
     * init the ShapeBase
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public AbstractBlockShapeBase(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        this.world = world;
        this.pos = pos;
        this.placeMoment = placeMoment;
    }


    /**
     * Returns the {@code BlockLayerComparator} associated with this object.
     *
     * @return the current {@code BlockLayerComparator}
     */
    public BlockLayerComparator getBlockLayer() {
        return blockLayer;
    }

    /**
     * Sets the {@code BlockLayerComparator} for this object.
     *
     * @param blockLayer the {@code BlockLayerComparator} to set
     */
    public void setBlockLayer(BlockLayerComparator blockLayer) {
        this.blockLayer = blockLayer;
    }

    /*----------- Pos Related ----------*/

    /**
     * Retrieves the {@code BlockPos} associated with this object.
     *
     * @return the current {@code BlockPos}
     */
    public @NotNull BlockPos getPos() {
        return pos;
    }

    /**
     * Sets the {@code BlockPos} for this object.
     *
     * @param pos the {@code BlockPos} to set
     * @throws NullPointerException if the provided {@code pos} is null
     */
    public void setPos(@NotNull BlockPos pos) {
        this.pos = pos;
    }

    /**
     * Adds the given offset to the current {@code BlockPos}.
     *
     * @param pos1 the {@code BlockPos} to add as an offset
     */
    public void addPosOffset(BlockPos pos1) {
        this.pos.add(pos1);
    }

    /**
     * Retrieves the {@code StructureWorldAccess} associated with this object.
     *
     * @return the current {@code StructureWorldAccess}
     */
    public @NotNull StructureWorldAccess getWorld() {
        return world;
    }

    /**
     * Retrieves the {@code PlaceMoment} associated with this object.
     *
     * @return the current {@code PlaceMoment}
     */
    public @NotNull PlaceMoment getPlaceMoment() {
        return placeMoment;
    }

    /**
     * allow you to set and change the place moment
     *
     * @param placeMoment the changed parameter
     */
    public void setPlaceMoment(@NotNull PlaceMoment placeMoment) {
        this.placeMoment = placeMoment;
    }

    /**
     * method to get the list of a set of blockPos based on a Map with chunkPos
     *
     * @param posSetMap the map that will be converted
     * @return the converted collection
     */
    public List<Set<BlockPos>> getBlockPosList(Map<ChunkPos, Set<BlockPos>> posSetMap) {
        return posSetMap.values().stream().toList();
    }


    /**
     * Define the moment the shape will be placed.
     * It is really important that this does match what you want, or you will run into issues or even crash
     **/
    public enum PlaceMoment {
        /**
         * used during the world generation
         */
        WORLD_GEN,
        /**
         * used for any other moment than world gen and with animated blockPos
         */
        ANIMATED_OTHER,
        /**
         * used for any other moment than world gen
         */
        OTHER
    }

}
