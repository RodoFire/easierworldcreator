package net.rodofire.easierworldcreator.util.file;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * class to load JSON files related to multi-chunk features
 */
public class LoadChunkShapeInfo {

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param pos   the {@link BlockPos} that will be converted into a {@link ChunkPos}
     * @return the list of the structure path to be placed later
     */
    public static List<Path> getWorldGenFiles(BlockPos pos) {
        return getWorldGenFiles(new ChunkPos(pos));
    }

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param chunk the chunk that needs to be verified
     * @return the list of the structure path to be placed later
     */
    public static List<Path> getWorldGenFiles(ChunkPos chunk) {
        List<Path> pathList = new ArrayList<>();
        int distance = EwcConfig.getFeaturesChunkDistance();
        Path generatedPath = EwcFolderData.getStructureDataDir(chunk);
        generatedPath = generatedPath.resolve(Ewc.MOD_ID).resolve("structures");

        if (Files.exists(generatedPath) && Files.isDirectory(generatedPath)) {
            for (int i = -distance; i <= distance; i++) {
                for (int j = -distance; j <= distance; j++) {
                    int chunkX = chunk.x + i;
                    int chunkZ = chunk.z + j;
                    String chunkDirPrefix = "chunk_" + chunkX + "_" + chunkZ;
                    Path newPath = generatedPath.resolve(chunkDirPrefix);
                    getPathFromChunk(newPath, pathList);
                }
            }
        }

        return pathList;
    }

    /**
     * method to get all multi-chunk JSON files of a block
     *
     * @param generatedPath the path of generated/ewc/chunk_[chunk.x]_[chunk.z]
     * @param pathList      the other resolved paths
     */
    private static void getPathFromChunk(Path generatedPath, List<Path> pathList) {
        try (Stream<Path> paths = Files.list(generatedPath)) {
            paths.forEach(filePath -> {
                if (filePath.toString().endsWith(".json")) {
                    pathList.add(filePath);
                }
            });

        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
