package net.rodofire.easierworldcreator.blockdata.blocklist.basic;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class used to connect BlockPos to a BlockState.</p>
 * <br>
 * <p>the class is composed of a {@code List<BlockPos>} related to a {@link BlockState}. </p>
 * <p>This means that all the {@link BlockPos} of the object are connected to the BlockState</p>
 * <br>
 * <p>this is an easier version of the {@link StructureTemplate.StructureBlockInfo}</p>
 * <p>It also allows for better manipulation of how blocks behave and allows to save memory since that there is no double blockStates.</p>
 */
@SuppressWarnings("unused")
public class DefaultBlockList implements BlockListManager {

    private List<BlockPos> posList;
    private BlockState blockState;


    /**
     * init a BlockShapeManager
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     */
    public DefaultBlockList(List<BlockPos> posList, BlockState blockState) {
        this.posList = new ArrayList<>(posList);
        this.blockState = blockState;
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public DefaultBlockList(BlockPos pos, BlockState state) {
        this.posList = new ArrayList<>();
        this.posList.add(pos);
        this.blockState = state;
    }

    /**
     * used to get the list of blockPos related to a layer
     *
     * @return the list of BlockPos
     */
    @Override
    public List<BlockPos> getPosList() {
        return posList;
    }

    /**
     * It uses a list of blockPos to allow multiple BlockPos to have a BlockState
     *
     * @param posList a list of BlockPos
     */
    @Override
    public void setPosList(List<BlockPos> posList) {
        this.posList = posList;
    }

    /**
     * allow you to add a BlockPos to the existing list
     *
     * @param pos the pos added
     */
    @Override
    public void addBlockPos(BlockPos pos) {
        this.posList.add(pos);
    }

    /**
     * allow you to add multiple BlockPos to the existing list
     *
     * @param pos the pos list added
     */
    @Override
    public void addBlockPos(List<BlockPos> pos) {
        this.posList.addAll(pos);
    }

    /**
     * allow you to remove a BlockPos to the existing list
     *
     * @param pos the pos removed
     */
    @Override
    public void removePos(BlockPos pos) {
        this.posList.remove(pos);
    }

    /**
     * allow you to remove a list of BlockPos to the existing list
     *
     * @param pos the list pos removed
     */
    @Override
    public void removePos(List<BlockPos> pos) {
        this.posList.removeAll(pos);
    }

    /**
     * Removes the BlockPos at the specified index from the posList and posMap.
     *
     * @param index the index of the BlockPos to remove.
     * @return the removed BlockPos.
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public BlockPos removePos(int index) {
        return posList.remove(index);
    }

    /**
     * Removes and returns the first BlockPos from the posList and posMap.
     *
     * @return the first BlockPos that was removed.
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public BlockPos removeFirstPos() {
        return removePos(0);
    }

    /**
     * Removes and returns the last BlockPos from the posList and posMap.
     *
     * @return the first BlockPos that was removed.
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public BlockPos removeLastPos() {
        return removePos(size() - 1);
    }

    /**
     * Clears all elements from posList, posMap, and statesMap.
     * After this operation, all structures will be empty.
     */
    public void removeAll() {
        this.posList.clear();
    }

    /**
     * method to replace one blockPos to another one
     *
     * @param oldPos the oldPos that will be replaced
     * @param newPos the newPos that will replace the other blockPos
     */
    @Override
    public void replacePos(BlockPos oldPos, BlockPos newPos) {
        this.posList.set(posList.indexOf(oldPos), newPos);
    }

    /**
     * method to get a certain pos from an index in the posList
     *
     * @param index the index of the BlockPos
     * @return the BlockPos of the index
     */
    @Override
    public BlockPos getPos(int index) {
        return this.posList.get(index);
    }

    /**
     * method to get the last blockPos of the posList
     *
     * @return the last blockPos of the posList
     */
    @Override
    public BlockPos getLastPos() {
        return this.posList.get(posList.size() - 1);
    }

    /**
     * method to get the first blockPos of the posList
     *
     * @return the first blockPos of the posList
     */
    @Override
    public BlockPos getFirstPos() {
        return this.posList.get(0);
    }

    /**
     * Method to get a random {@link BlockPos} in the {@code posList} by using a new minecraft Random
     *
     * @return a random {@link BlockPos}
     */
    @Override
    public BlockPos getRandomPos() {
        return this.posList.get(Random.create().nextInt(size()));
    }

    /**
     * Method to get a random {@link BlockPos} in the {@code posList} based on a provided random
     *
     * @return a random {@link BlockPos}
     */
    @Override
    public BlockPos getRandomPos(Random random) {
        return this.posList.get(random.nextInt(size()));
    }

    /**
     * used to get the blockState
     *
     * @return the blockState of the BlockShapeManager
     */
    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    /**
     * change the blockState of the BlockShapeManager
     *
     * @param blockState the blockState related to the BlockPos list
     */
    @Override
    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    /**
     * method to get the number of {@link BlockPos} present in the related BlockList
     *
     * @return the number of {@link BlockPos}
     */
    @Override
    public int size() {
        return this.posList.size();
    }

    /**
     * Method to place all the blocks in the comparator
     *
     * @param worldAccess the world where the blocks will be placed
     */
    public void placeAll(StructureWorldAccess worldAccess) {
        for (int i = 0; i < posList.size(); i++) {
            this.place(worldAccess, i);
        }
    }

    /**
     * Method to place all the blocks in the comparator with the BlockPos getting verified
     *
     * @param worldAccess the world where the blocks will be placed
     */
    public void placeAllWithVerification(StructureWorldAccess worldAccess) {
        for (int i = 0; i < posList.size(); i++) {
            this.placeWithVerification(worldAccess, i);
        }
    }

    /**
     * Method to place all the blocks in the comparator and removing the BlockPos
     *
     * @param worldAccess the world where the blocks will be placed
     */
    public void placeAllWithDeletion(StructureWorldAccess worldAccess) {
        for (int i = 0; i < posList.size(); i++) {
            this.placeLastWithDeletion(worldAccess);
        }
    }

    /**
     * Method to place all the blocks in the comparator with the BlockPos getting verified and the getting deleted
     *
     * @param worldAccess the world where the blocks will be placed
     */
    public void placeAllWithVerificationDeletion(StructureWorldAccess worldAccess) {
        for (int i = 0; i < posList.size(); i++) {
            this.placeLastWithVerificationDeletion(worldAccess);
        }
    }

    /**
     * method to place the Block related to the index
     *
     * @param world the world the block will be placed
     * @param index the index of the BlockPos
     */
    public void place(StructureWorldAccess world, int index) {
        BlockPlaceUtil.placeBlock(world, this.getPos(index), this.blockState);
    }


    /**
     * method to place the block with the deletion of the BlockPos
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     */
    public void placeWithDeletion(StructureWorldAccess world, int index) {
        BlockPlaceUtil.placeBlock(world, this.removePos(index), this.blockState);
    }

    /**
     * Method to place the block related to the index.
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    public boolean placeWithVerification(StructureWorldAccess world, int index) {
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, this.getPos(index), this.blockState);
    }

    /**
     * Method to place the block with the deletion of the BlockPos
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    public boolean placeWithVerificationDeletion(StructureWorldAccess world, int index) {
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, this.removePos(index), this.blockState);
    }

    /**
     * method to place the first Block
     *
     * @param world the world the block will be placed
     */
    public void placeFirst(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlock(world, this.getFirstPos(), this.blockState);
    }

    /**
     * Method to place the first Block and deleting it.
     * You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     */
    public void placeFirstWithDeletion(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlock(world, this.removeFirstPos(), this.blockState);

    }

    /**
     * Method to place the first Block.
     * <p>The method also performs verification to know if the block can be placed.
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    public boolean placeFirstWithVerification(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, this.getFirstPos(), this.blockState);
    }

    /**
     * <p>Method to place the first Block and deleting it.
     * <p>The method also performs verification to know if the block can be placed.
     * <p>You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * <p>Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    public boolean placeFirstWithVerificationDeletion(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, this.removeFirstPos(), this.blockState);

    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     */
    public void placeLastWithDeletion(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlock(world, this.removeLastPos(), this.blockState);
    }

    /**
     * Method to place the last Block of the comparator.
     *
     * @param world the world the last block will be placed
     */
    public void placeLast(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlock(world, this.getLastPos(), this.blockState);

    }

    /**
     * Method to place the last Block.
     *
     * @param world the world the last block will be placed
     *              The method also performs verification to know if the block can be placed.
     * @return true if the block was placed, false if not
     */
    public boolean placeLastWithVerification(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, this.getLastPos(), this.blockState);

    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * The method also performs verification to know if the block can be placed.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     * @return true if the block was placed, false if not
     */
    public boolean placeLastWithVerificationDeletion(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, this.removeLastPos(), this.blockState);
    }

    @Override
    public String toString() {
        return "[" +
                "blockState=" + blockState.toString() +
                "; posList=" + posList.stream().toString() +
                ']';
    }

    public void toJson(Path path, BlockPos offset) {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject;

        int offsetX = offset.getX();
        int offsetY = offset.getY();
        int offsetZ = offset.getZ();

        jsonObject = new JsonObject();
        jsonObject.addProperty("state", this.getBlockState().toString());
        for (BlockPos pos : this.posList) {
            JsonArray positions = new JsonArray();
            JsonObject posObject = new JsonObject();
            posObject.addProperty("x", pos.getX() + offsetX);
            posObject.addProperty("y", pos.getY() + offsetY);
            posObject.addProperty("z", pos.getZ() + offsetZ);
            positions.add(posObject);

            jsonObject.add("positions", positions);
            jsonArray.add(jsonObject);
        }

        try {
            Files.writeString(path, gson.toJson(jsonArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
