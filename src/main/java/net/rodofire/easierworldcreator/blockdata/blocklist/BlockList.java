package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * <p>Class used to connect BlockPos to a BlockState.</p>
 * <br>
 * <p>the class is composed of a {@link LongArrayList} that store all the BlockPos related to a {@link BlockState}. </p>
 * <p>This means that all the {@link BlockPos} of the object are connected to the BlockState</p>
 * <br>
 * <p>this is an easier version of the {@link StructureTemplate.StructureBlockInfo}</p>
 * This approach allows for many improvements:
 * <li> BlockStates are not doubled, saving a lot of memory comparing to {@code Pair<BlockState, BlockPos>} since that no {@link BlockState} are duplicated</li>
 * <li> BlockPos are compressed into a {@link LongArrayList}, saving ~30% memory and allowing for ~70% more performance </li>
 * <li> Provides some describing on how should the Block be placed: {@code overrideBlocks} and {@code force}</li>
 * <li> provide some useful methods to simplify it's usage</li>
 */
@SuppressWarnings("unused")
public class BlockList {
    StructurePlacementRuleManager manager = new StructurePlacementRuleManager();

    /**
     * BlockPos are compressed into a {@link LongArrayList}, saving ~30% memory and allowing for ~70% more performance </li>
     */
    private final LongArrayList posList = new LongArrayList();
    private BlockState blockState;

    private NbtCompound tag;


    /**
     * init a BlockShapeManager
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     */
    public BlockList(BlockState blockState, NbtCompound tag, List<BlockPos> posList) {
        addAll(posList);
        this.blockState = blockState;
        this.tag = tag;
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, NbtCompound tag, BlockPos pos) {
        add(pos);
        this.blockState = state;
        this.tag = tag;
    }

    /**
     * init a BlockShapeManager
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     */
    public BlockList(BlockState blockState, NbtCompound tag, LongArrayList posList) {
        addAll(posList);
        this.blockState = blockState;
        this.tag = tag;
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, NbtCompound tag, long pos) {
        add(pos);
        this.blockState = state;
        this.tag = tag;
    }

    /**
     * init a BlockShapeManager
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     */
    public BlockList(BlockState blockState, List<BlockPos> posList) {
        addAll(posList);
        this.blockState = blockState;
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, BlockPos pos) {
        add(pos);
        this.blockState = state;
    }

    /**
     * init a BlockShapeManager
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     */
    public BlockList(BlockState blockState, LongArrayList posList) {
        addAll(posList);
        this.blockState = blockState;
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, long pos) {
        add(pos);
        this.blockState = state;
    }

    public BlockList() {
    }

    public int size() {
        return posList.size();
    }


    public BlockList replace(int index, BlockPos newPos) {
        this.posList.set(index, LongPosHelper.encodeBlockPos(newPos));
        return this;
    }

    public BlockList replace(int index, long newPos) {
        this.posList.set(index, newPos);
        return this;
    }

    public BlockList addAll(List<BlockPos> list) {
        for (BlockPos pos : list) {
            add(pos);
        }
        return this;
    }

    public BlockList addAll(LongArrayList list) {
        for (long pos : list) {
            add(pos);
        }
        return this;
    }

    public BlockList add(BlockPos pos) {
        posList.add(LongPosHelper.encodeBlockPos(pos));
        return this;
    }

    public BlockList add(long pos) {
        posList.add(pos);
        return this;
    }

    public BlockList setPosList(List<BlockPos> posList) {
        this.posList.clear();
        this.addAll(posList);
        return this;
    }

    public BlockList setPosList(LongArrayList posList) {
        this.posList.clear();
        this.addAll(posList);
        return this;
    }

    public BlockList set(int index, BlockPos newPos) {
        this.posList.set(index, LongPosHelper.encodeBlockPos(newPos));
        return this;
    }

    public List<BlockPos> getConvertedPosList() {
        List<BlockPos> posList = new ArrayList<BlockPos>();
        for (long pos : this.posList) {
            posList.add(LongPosHelper.decodeBlockPos(pos));
        }
        return posList;
    }

    public LongArrayList getPosList() {
        return posList;
    }

    public BlockPos getPos(int index) {
        return LongPosHelper.decodeBlockPos(this.posList.getLong(index));
    }

    public BlockPos getFirstPos() {
        return LongPosHelper.decodeBlockPos(this.posList.getFirst());
    }

    public BlockPos getLastPos() {
        return LongPosHelper.decodeBlockPos(this.posList.getLast());
    }

    public BlockPos getRandomPos() {
        return LongPosHelper.decodeBlockPos(this.posList.getLong(Random.create().nextBetween(0, this.size() - 1)));
    }

    public BlockPos getRandomPos(Random random) {
        return LongPosHelper.decodeBlockPos(this.posList.getLong(random.nextBetween(0, this.size() - 1)));
    }

    public long get(int index) {
        return this.posList.getLong(index);
    }

    public long getFirst() {
        return this.posList.getFirst();
    }

    public long getLast() {
        return this.posList.getLast();
    }

    public long getRandom() {
        return this.posList.getLong(Random.create().nextBetween(0, this.size() - 1));
    }

    public long getRandom(Random random) {
        return this.posList.getLong(random.nextBetween(0, this.size() - 1));
    }

    public BlockPos removePos(int index) {
        return LongPosHelper.decodeBlockPos(this.posList.removeLong(index));
    }

    public BlockPos removeLastPos() {
        return LongPosHelper.decodeBlockPos(this.posList.removeLong(posList.size() - 1));
    }

    public BlockPos removeFirstPos() {
        return LongPosHelper.decodeBlockPos(this.posList.removeLong(posList.size() - 1));
    }

    public Optional<NbtCompound> getTag() {
        return Optional.ofNullable(tag);
    }

    public void setTag(NbtCompound tag) {
        this.tag = tag;
    }

    /**
     * time complexity of O(n), avoid using this in performance crucial applications
     *
     * @return the removed pos
     */
    public BlockList remove(BlockPos pos) {
        this.posList.removeLong(posList.indexOf(LongPosHelper.encodeBlockPos(pos)));
        return this;
    }

    /**
     * time complexity of O(n * m), m being the length of the provided {@code List<>} avoid using it in performance crucial applications
     *
     * @return the instance of the object
     */
    public BlockList removeAll(List<BlockPos> list) {
        for (BlockPos pos : list) {
            this.posList.removeLong(posList.indexOf(LongPosHelper.encodeBlockPos(pos)));
        }
        return this;
    }


    public StructurePlacementRuleManager getManager() {
        return manager;
    }

    public void setManager(StructurePlacementRuleManager manager) {
        this.manager = manager;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public Pair<BlockState, NbtCompound> getBlockData() {
        return new Pair<>(blockState, tag);
    }

    public void getBlockData(Pair<BlockState, NbtCompound> blockState) {
        this.blockState = blockState.getLeft();
        this.tag = blockState.getRight();
    }

    public boolean placeLast(StructureWorldAccess world) {
        return place(world, this.posList.getLast());
    }

    public boolean placeFirst(StructureWorldAccess world) {
        return place(world, this.posList.getFirst());
    }

    public boolean place(StructureWorldAccess world, int index) {
        return place(world, this.posList.getLong(index));
    }

    public boolean placeAll(StructureWorldAccess worldAccess) {
        boolean placed = true;
        for (long pos : this.posList) {
            if (!place(worldAccess, pos)) {
                placed = false;
            }
        }
        return placed;
    }

    public boolean placeLast(StructureWorldAccess world, int flag) {
        return place(world, this.posList.getLast(), flag);
    }

    public boolean placeFirst(StructureWorldAccess world, int flag) {
        return place(world, this.posList.getFirst(), flag);
    }

    public boolean place(StructureWorldAccess world, int index, int flag) {
        return place(world, this.posList.getLong(index), flag);
    }

    public boolean placeAll(StructureWorldAccess worldAccess, int flag) {
        boolean placed = true;
        boolean markdirty = false;
        ServerChunkManager chunkManager = null;
        MinecraftServer server = worldAccess.getServer();
        if (server != null) {
            if (worldAccess instanceof ServerWorld world) {
                chunkManager = world.getChunkManager();
                markdirty = true;
            }
        }

        for (long pos : this.posList) {
            if (!place(worldAccess, pos, flag)) {
                placed = false;
            } else if (markdirty) {
                chunkManager.markForUpdate(LongPosHelper.decodeBlockPos(pos));
            }
        }
        return placed;
    }

    public boolean placeLastNDelete(StructureWorldAccess world) {
        return place(world, this.posList.removeLast());
    }

    /**
     * for the most performance, it is recommended to not use this method where {@code placeLastNDelete()} can be applied
     */
    public boolean placeNDelete(StructureWorldAccess world, int index) {
        return place(world, this.posList.removeLong(index));
    }

    /**
     * for the most performance,
     * it is recommended to not use this method where {@code placeLastNDelete()} can be applied
     */
    public boolean placeNDelete(StructureWorldAccess world, int index, int flag) {
        return place(world, this.posList.removeLong(index), flag);
    }

    public boolean placeAllNDelete(StructureWorldAccess worldAccess) {
        boolean placed = placeAll(worldAccess);
        this.posList.clear();
        return placed;
    }

    public boolean placeAllNDelete(StructureWorldAccess worldAccess, int flag) {
        boolean placed = placeAll(worldAccess, flag);
        this.posList.clear();
        return placed;
    }

    /**
     * <p>When placing huge structures, {@link Block#NOTIFY_ALL} takes up 80% of the placement.
     * <p>It not used in this method for this reason.
     * <p>You can however still modify the flags using {@link #place(StructureWorldAccess, long, int)}
     */
    private boolean place(StructureWorldAccess world, long pos) {
        return place(world, pos, Block.FORCE_STATE);
    }

    private boolean place(StructureWorldAccess world, long pos, int flags) {
        BlockState state = world.getBlockState(LongPosHelper.decodeBlockPos(pos));
        if (this.manager != null) {
            if (this.manager.canPlace(state)) {
                world.setBlockState(LongPosHelper.decodeBlockPos(pos), this.blockState, flags);
                return true;
            }
            return false;
        }
        return state.isAir() && world.setBlockState(LongPosHelper.decodeBlockPos(pos), this.blockState, flags);
    }

    public JsonObject toJson(ChunkPos chunkPos) {
        return toJson(new BlockPos(0, 0, 0), chunkPos);
    }

    public JsonObject toJson(BlockPos offset, ChunkPos chunkPos) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        int offsetX = offset.getX();
        int offsetY = offset.getY();
        int offsetZ = offset.getZ();

        // Calcul du coin inférieur gauche du chunk
        int chunkMinX = chunkPos.x << 4;
        int chunkMinZ = chunkPos.z << 4;

        jsonObject.addProperty("type", blockState.toString());
        jsonObject.addProperty("state", blockState.toString());
        if (manager != null) {
            jsonObject.addProperty("force", manager.isForce());
            jsonObject.add("overriddenBlock", gson.toJsonTree(manager.getOverriddenBlocks()).getAsJsonArray());
        }
        if (tag != null) {
            jsonObject.addProperty("tag", tag.toString());
        }
        addCustomProperty(jsonObject);

        //we allocate 11 bits to z and x axis, going from -1024 to 1024 blocks relative to the BlockPos. 10 bits to y.
        int size = size();
        int[] compactPositions = new int[size];

        for (int i = 0; i < size; i++) {
            BlockPos pos = LongPosHelper.decodeBlockPos(posList.getLong(i));

            // Positions relatives dans le chunk après application de l'offset
            int relX = pos.getX() - chunkMinX - offsetX;
            int relZ = pos.getZ() - chunkMinZ - offsetZ;
            int relY = pos.getY() - offsetY;

            // Vérification des limites
            if (relX < 0 || relX >= 2048 || relZ < 0 || relZ >= 2048 || relY < -512 || relY > 512) {
                throw new IllegalArgumentException("Position hors limites: " + pos);
            }

            // Encodage des positions (2*11 bits pour X et Z, 10 bits pour Y)
            int compactPos = (relX << 21) | ((relY + 512) << 11) | relZ;
            compactPositions[i] = compactPos;
        }

        // Ajout des positions compactées au JSON
        jsonObject.add("positions", gson.toJsonTree(compactPositions).getAsJsonArray());

        return jsonObject;
    }

    public void addCustomProperty(JsonObject json) {

    }

    public void toJson(Path path, ChunkPos chunkPos) {
        toJson(new BlockPos(0, 0, 0), chunkPos);
    }

    public void toJson(Path path, BlockPos offset, ChunkPos chunkPos) {
        Gson gson = new Gson();
        JsonObject jsonObj = toJson(offset, chunkPos);
        try {
            Files.writeString(path, gson.toJson(jsonObj));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
