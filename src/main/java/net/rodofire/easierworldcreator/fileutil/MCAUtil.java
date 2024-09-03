package net.rodofire.easierworldcreator.fileutil;

import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.storage.RegionFile;
import net.rodofire.easierworldcreator.Easierworldcreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * util class for mca files
 */
public class MCAUtil {
    /**
     * method to get every region file
     * @param world the world of the file
     * @return the list of files (path to the file)
     */
    public static List<Path> getFiles(StructureWorldAccess world) {
        List<Path> files = new ArrayList<Path>();
        Path basePath = FileUtil.getWorldSavePathDirectory(world, WorldSavePath.ROOT).resolve("region");
        try (Stream<Path> stream = Files.list(basePath)) {
            stream.forEach(filePath -> {
                if (filePath.toString().endsWith(".mca")) {
                    files.add(filePath);
                    Easierworldcreator.LOGGER.info(filePath.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    /**
     * method to get every generated chunk in a mca file
     * @param path the path of the mca file
     * @return the list of every generated chunk
     */
    public static List<ChunkPos> getChunks(Path path) {
        File file = new File(path.toString());
        if (!file.exists()) {
            Easierworldcreator.LOGGER.error("{} does not exist", path.toString());
            return Collections.emptyList();
        }

        List<ChunkPos> posList = new ArrayList<>();
        String name = file.getName();
        String[] splittedName = name.split("\\.");
        int regionx = Integer.parseInt(splittedName[1]);
        int regionz = Integer.parseInt(splittedName[2]);

        try (RegionFile region = new RegionFile(file.toPath(), file.getParentFile().toPath(), true)) {
            for (int chunkX = 0; chunkX < 32; chunkX++) {
                for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                    if (region.hasChunk(new ChunkPos(chunkX, chunkZ)))
                        posList.add(new ChunkPos(chunkX + 32 * regionx, chunkZ + 32 * regionz));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return posList;
    }
}
