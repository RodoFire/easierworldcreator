package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ShapeGen {
    @NotNull
    private StructureWorldAccess world;

    @NotNull
    private BlockPos pos;

    @NotNull
    private List<BlockState> firstlayer;

    private List<BlockState> secondlayer;

    //thirdlayer will be ignored if second layer = null
    private List<BlockState> thirdlayer;
    private int firstlayerdepth;
    private int secondlayerdepth;

    private boolean force;
    private List<Block> blocksToForce;

    private int xrotation;
    private int yrotation;

    private double cosx;
    private double cosy;
    private double sinx;
    private double siny;

    /**
     * init the shape gen
     *
     * @param world            the world the shape will be generated
     * @param pos              the pos of the structure center
     *                         -------------------------------------------------------------------------------------
     * @param firstlayer       the list of blockstates that will be placed on top of the structure
     * @param secondlayer      the list of blockstates that will be placed in the second layer of the structure
     * @param thirdlayer       the list of blockstates tha will be placed in the third layer of the structure
     *                         these lists shouldn't have blocks in common, or you might run into generation problems
     *                         -------------------------------------------------------------------------------------
     * @param firstlayerdepth  int that represents the depth of the blockstates in the first layer
     * @param secondlayerdepth int that represents the depth of the blockstates in the second layer
     * @param force            force the blockPos if true (be careful if true, your structure will replace every block, you might destruct some constructions if it is bad used)
     * @param blocksToForce    list of blocks that the structure can still replace if @param force = false
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer, List<BlockState> secondlayer, List<BlockState> thirdlayer, int firstlayerdepth, int secondlayerdepth, boolean force, List<Block> blocksToForce, int xrotation, int yrotation) {
        this.world = world;
        this.pos = pos;
        this.firstlayer = firstlayer;
        this.secondlayer = secondlayer;
        this.thirdlayer = thirdlayer;
        this.firstlayerdepth = firstlayerdepth;
        this.secondlayerdepth = secondlayerdepth;
        this.force = force;
        this.blocksToForce = blocksToForce;
        getRotations(0, 0);
    }


    /**
     * init the shape gen
     *
     * @param world            the world the shape will be generated
     * @param pos              the pos of the structure center
     *                         -------------------------------------------------------------------------------------
     * @param firstlayer       the list of blockstates that will be placed on top of the structure
     * @param secondlayer      the list of blockstates that will be placed in the second layer of the structure
     * @param thirdlayer       the list of blockstates tha will be placed in the third layer of the structure
     *                         these lists shouldn't have blocks in common, or you might run into generation problems
     *                         -------------------------------------------------------------------------------------
     * @param firstlayerdepth  int that represents the depth of the blockstates in the first layer
     * @param secondlayerdepth int that represents the depth of the blockstates in the second layer
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer, List<BlockState> secondlayer, List<BlockState> thirdlayer, int firstlayerdepth, int secondlayerdepth, int xrotation, int yrotation) {
        this.world = world;
        this.pos = pos;
        this.firstlayer = firstlayer;
        this.secondlayer = secondlayer;
        this.thirdlayer = thirdlayer;
        this.firstlayerdepth = firstlayerdepth;
        this.secondlayerdepth = secondlayerdepth;
        this.force = false;
        this.blocksToForce = null;
        getRotations(0, 0);
    }


    /**
     * init the shape gen
     *
     * @param world       the world the shape will be generated
     * @param pos         the pos of the structure center
     *                    -------------------------------------------------------------------------------------
     * @param firstlayer  the list of blockstates that will be placed on top of the structure.
     * @param secondlayer the list of blockstates that will be placed in the second layer of the structure.
     * @param thirdlayer  the list of blockstates tha will be placed in the third layer of the structure.
     *                    these lists shouldn't have blocks in common, or you might run into generation problems.
     *                    -------------------------------------------------------------------------------------
     *                    first layers and second layers will use the default values ( 1 and 3 )
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer, List<BlockState> secondlayer, List<BlockState> thirdlayer) {
        this.world = world;
        this.pos = pos;
        this.firstlayer = firstlayer;
        this.secondlayer = secondlayer;
        this.thirdlayer = thirdlayer;
        this.firstlayerdepth = 1;
        this.secondlayerdepth = 3;
        this.force = false;
        this.blocksToForce = null;
        getRotations(0, 0);
    }


    /**
     * init the shape gen
     *
     * @param world           the world the shape will be generated
     * @param pos             the pos of the structure center
     *                        -------------------------------------------------------------------------------------
     * @param firstlayer      the list of blockstates that will be placed on top of the structure
     * @param secondlayer     the list of blockstates that will be placed in the second layer of the structure
     *                        these lists shouldn't have blocks in common, or you might run into generation problems
     *                        -------------------------------------------------------------------------------------
     * @param firstlayerdepth int that represents the depth of the blockstates in the first layer
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer, List<BlockState> secondlayer, int firstlayerdepth) {
        this.world = world;
        this.pos = pos;
        this.firstlayer = firstlayer;
        this.secondlayer = secondlayer;
        this.thirdlayer = null;
        this.firstlayerdepth = firstlayerdepth;
        this.secondlayerdepth = 3;
        this.force = false;
        this.blocksToForce = null;
        getRotations(0, 0);
    }


    /**
     * init the shape gen
     *
     * @param world       the world the shape will be generated
     * @param pos         the pos of the structure center
     *                    -------------------------------------------------------------------------------------
     * @param firstlayer  the list of blockstates that will be placed on top of the structure
     * @param secondlayer the list of blockstates that will be placed in the second layer of the structure
     *                    these lists shouldn't have blocks in common, or you might run into generation problems
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer, List<BlockState> secondlayer) {
        this.world = world;
        this.pos = pos;
        this.firstlayer = firstlayer;
        this.secondlayer = secondlayer;
        this.thirdlayer = null;
        this.firstlayerdepth = 1;
        this.secondlayerdepth = 3;
        this.force = false;
        this.blocksToForce = null;
        getRotations(0, 0);
    }


    /**
     * init the shape gen
     *
     * @param world      the world the shape will be generated
     * @param pos        the pos of the structure center
     *                   -------------------------------------------------------------------------------------
     * @param firstlayer the list of blockstates that will be placed on top of the structure
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer) {
        this.world = world;
        this.pos = pos;
        this.firstlayer = firstlayer;
        this.secondlayer = null;
        this.thirdlayer = null;
        this.firstlayerdepth = 1;
        this.secondlayerdepth = 3;
        this.force = false;
        this.blocksToForce = null;
    }

    public void getRotations(int xrotation, int yrotation) {
        this.xrotation = xrotation;
        this.yrotation = yrotation;
        this.cosx = FastMaths.getFastCos(xrotation);
        this.cosy = FastMaths.getFastCos(yrotation);
        this.sinx = FastMaths.getFastSin(xrotation);
        this.siny = FastMaths.getFastSin(yrotation);
    }

    /*---------- First Layer Related ----------*/
    public @NotNull List<BlockState> getFirstlayer() {
        return firstlayer;
    }

    public void setFirstlayer(@NotNull List<BlockState> firstlayer) {
        this.firstlayer = firstlayer;
    }

    public void setFirstlayer(BlockState firstlayer) {
        this.firstlayer = List.of(firstlayer);
    }

    public int getFirstlayerdepth() {
        return firstlayerdepth;
    }

    public void setFirstlayerdepth(int firstlayerdepth) {
        this.firstlayerdepth = firstlayerdepth;
    }

    public void addFirstlayer(BlockState firstlayer) {
        this.firstlayer.add(firstlayer);
    }

    public void addFirstLayer(List<BlockState> firstlayer) {
        this.firstlayer.addAll(firstlayer);
    }

    public void addFirstlayerdepth(int firstlayerdepth) {
        this.firstlayerdepth += firstlayerdepth;
    }


    /*---------- Second Layer Related ----------*/
    public List<BlockState> getSecondlayer() {
        return secondlayer;
    }

    public void setSecondlayer(List<BlockState> secondlayer) {
        this.secondlayer = secondlayer;
    }

    public void setSecondlayer(BlockState secondlayer) {
        this.secondlayer = List.of(secondlayer);
    }

    public int getSecondlayerdepth() {
        return secondlayerdepth;
    }

    public void setSecondlayerdepth(int secondlayerdepth) {
        this.secondlayerdepth = secondlayerdepth;
    }

    public void addSecondlayer(BlockState secondlayer) {
        this.secondlayer.add(secondlayer);
    }

    public void addSecondlayer(List<BlockState> secondlayer) {
        this.secondlayer.addAll(secondlayer);
    }

    public void addSecondlayerdepth(int secondlayerdepth) {
        this.secondlayerdepth += secondlayerdepth;
    }


    /*---------- Third Layer Related ----------*/
    public List<BlockState> getThirdlayer() {
        return thirdlayer;
    }

    public void setThirdlayer(List<BlockState> thirdlayer) {
        this.thirdlayer = thirdlayer;
    }

    public void setThirdlayer(BlockState thirdlayer) {
        this.thirdlayer = List.of(thirdlayer);
    }

    public void addThirdlayer(BlockState thirdlayer) {
        this.thirdlayer.add(thirdlayer);
    }

    public void addThirdlayer(List<BlockState> thirdlayer) {
        this.thirdlayer.addAll(thirdlayer);
    }

    public boolean getForce() {
        return force;
    }

    /*---------- Force Related ----------*/
    public void setForce(boolean force) {
        this.force = force;
    }

    public List<Block> getBlocksToForce() {
        return blocksToForce;
    }

    public void setBlocksToForce(List<Block> blocksToForce) {
        this.blocksToForce = blocksToForce;
    }

    public void addBlocksToForce(Block block) {
        this.blocksToForce.add(block);
    }

    public void addBlocksToForce(List<Block> blocks) {
        this.blocksToForce.addAll(blocks);
    }

    /*---------- Rotation related ----------*/
    public int getXrotation() {
        return xrotation;
    }

    public void setXrotation(int xrotation) {
        this.xrotation = xrotation;
    }

    public int getYrotation() {
        return yrotation;
    }

    public void setYrotation(int yrotation) {
        this.yrotation = yrotation;
    }

    public void addXrotation(int xrotation) {
        this.xrotation += xrotation;
    }

    public void addYrotation(int yrotation) {
        this.yrotation += yrotation;
    }

    /*----------- Pos Related ----------*/
    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public void addPosOffset(BlockPos pos1) {
        this.pos.add(pos1);
    }

    public StructureWorldAccess getWorld() {
        return world;
    }


    /*---------- Place Structure ----------*/
    public abstract void place();


    /**
     * place the layers of the structure starting from the first layer to the second to the third
     *
     * @param poslist list of BlockPos of the structure
     */
    public void placeLayers(List<BlockPos> poslist) {
        //you get a list of BlockPos that the first layer was able to place
        List<BlockPos> firstpos = this.placeFirstLayer(poslist);

        //if secondlayer == null, the entire structure will be composed of the firstlayers blocks
        //if thirdlayer != null, and secondlayer == null, the tird layer will not be placed
        if (this.secondlayer != null) {

            //this list is used for the second layer to avoid unnecessary calculations
            //it returns a list too
            List<BlockPos> secondpos = this.placeSecondLayer(firstpos);
            if (this.thirdlayer != null) {
                this.placeThirdLayer(secondpos);
            }
        }
    }

    public List<BlockPos> placeFirstLayer(List<BlockPos> poslist) {
        List<BlockPos> posedBlocks = new ArrayList<BlockPos>();
        for (BlockPos pos : poslist) {
            if (WorldGenUtil.verifyBlock(this.world, this.force, this.blocksToForce, this.firstlayer, pos)) {
                posedBlocks.add(pos);
            }
        }
        return posedBlocks;
    }

    public List<BlockPos> placeSecondLayer(List<BlockPos> firstpos) {
        List<BlockPos> secondpos = new ArrayList<BlockPos>();
        this.blocksToForce = WorldGenUtil.addBlockStateListtoBlockList(this.blocksToForce, this.firstlayer);
        for (BlockPos pos : firstpos) {
            if (WorldGenUtil.isBlockInBlockStateList(world.getBlockState(pos.up(this.firstlayerdepth)).getBlock(), firstlayer)) {
                WorldGenUtil.verifyBlock(this.world, this.force, this.blocksToForce, this.secondlayer, pos);
                secondpos.add(pos);
            }
        }
        return secondpos;
    }

    public void placeThirdLayer(List<BlockPos> seconpos) {
        this.blocksToForce = WorldGenUtil.addBlockStateListtoBlockList(this.blocksToForce, this.secondlayer);
        for (BlockPos pos : seconpos) {
            if (WorldGenUtil.isBlockInBlockStateList(world.getBlockState(pos.up(this.secondlayerdepth)).getBlock(), secondlayer)) {
                WorldGenUtil.verifyBlock(this.world, this.force, this.blocksToForce, this.thirdlayer, pos);
            }
        }
    }


    public BlockPos getCoordinatesRotation(float x, float y, float z, BlockPos pos) {
        float x_rot = (int) (x * cosy - y * siny);
        float y_rot = (int) (x * siny + y * cosy);

        float x_rot2 = (int) (x_rot * cosx + z * sinx);
        float z_rot = (int) (-x_rot * sinx + z * cosx);

        return new BlockPos.Mutable().set((Vec3i) pos, (int) x_rot2, (int) y_rot, (int) z_rot);
    }

    public void getGenTime(long startTime, boolean place) {
        long endTime = (System.nanoTime());
        long duration = (endTime - startTime) / 1000000;
        if (place) {
            Easierworldcreator.LOGGER.debug("structure placing took : " + duration + " ms");
        } else {
            Easierworldcreator.LOGGER.debug("structure coordinates calculations took : " + duration + " ms");
        }
    }


}
