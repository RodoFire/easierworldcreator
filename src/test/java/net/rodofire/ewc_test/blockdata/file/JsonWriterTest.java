package net.rodofire.ewc_test.blockdata.file;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonWriterTest {
    public static Map<ChunkPos, Set<BlockPos>> blockPosList = getBlockPosList();


    @Test
    public void testWithCoordinatesReductionPerformance() {
        long startTime = System.nanoTime(); // Temps de début
        testWithCoordinatesReduction();
        long endTime = System.nanoTime(); // Temps de fin

        long elapsedTime = endTime - startTime;
        System.out.println("testWithCoordinatesReduction took: " + elapsedTime + " ns");
    }

    @Test
    public void testWithoutCoordinatesReductionPerformance() {
        long startTime = System.nanoTime(); // Temps de début
        testWithoutCoordinatesReduction();
        long endTime = System.nanoTime(); // Temps de fin

        long elapsedTime = endTime - startTime;
        System.out.println("testWithoutCoordinatesReduction took: " + elapsedTime + " ns");
    }

    @Test
    public void comparePerformance() {
        long startWithReduction = System.nanoTime();
        testWithCoordinatesReduction();
        long elapsedWithReduction = System.nanoTime() - startWithReduction;

        long startWithoutReduction = System.nanoTime();
        testWithoutCoordinatesReduction();
        long elapsedWithoutReduction = System.nanoTime() - startWithoutReduction;

        System.out.println("With Reduction: " + ((double) (elapsedWithReduction / 1000)) / 1000 + " ms");
        System.out.println("Without Reduction: " + ((double) (elapsedWithoutReduction / 1000)) / 1000 + " ms");

        Assertions.assertTrue(elapsedWithReduction < elapsedWithoutReduction,
                "Coordinate reduction should be faster than non-reduction");
    }


    /**
     * based on test, the method is 70-75% faster than the other
     */
    @Test
    public void testWithCoordinatesReduction() {

        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject = new JsonObject();

        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;
        jsonObject.addProperty("state", "banana");
        JsonArray positions = new JsonArray();
        int chunkOffsetX = offsetX % 16; // Précalcule le décalage pour le chunk
        int chunkOffsetZ = offsetZ % 16;

        List<Integer> compactPositions = new ArrayList<>();

        for (Map.Entry<ChunkPos, Set<BlockPos>> entry : blockPosList.entrySet()) {
            Set<BlockPos> blockPositions = entry.getValue();

            for (BlockPos pos : blockPositions) {
                char posX = (char) Math.floorMod(pos.getX() + chunkOffsetX, 16);
                char posZ = (char) Math.floorMod(pos.getZ() + chunkOffsetZ, 16);
                int compactPos = (posX << 24) | ((short) pos.getY() << 8) | posZ;

                // Collecte des positions compactées directement
                compactPositions.add(compactPos);
            }
        }

// Ajout toutes les données en une seule fois sous forme de JsonArray
        JsonArray compactJsonPositions = gson.toJsonTree(compactPositions).getAsJsonArray();
        jsonObject.add("positions", compactJsonPositions);
        jsonArray.add(jsonObject);

        try {
            Path path = Path.of("run/test/test1.json");
            System.out.println(path.toAbsolutePath());
            Files.writeString(path, gson.toJson(jsonArray));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @Test
    public void testWithoutCoordinatesReduction() {

        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject;

        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;

        jsonObject = new JsonObject();
        jsonObject.addProperty("state", "banana");

        JsonArray positions = new JsonArray();
        for (Set<BlockPos> set : blockPosList.values()) {
            for (BlockPos pos : set) {
                JsonObject posObject = new JsonObject();
                posObject.addProperty("x", pos.getX() + offsetX);
                posObject.addProperty("y", pos.getY());
                posObject.addProperty("z", pos.getZ() + offsetZ);
                positions.add(posObject);
            }

        }
        jsonObject.add("positions", positions);
        jsonArray.add(jsonObject);
        try {
            Path path = Path.of("run/test/test2.json");
            System.out.println(path.toAbsolutePath());
            Files.writeString(path, gson.toJson(jsonArray));
        } catch (IOException e) {
            e.fillInStackTrace();
        }

    }

    private static Map<ChunkPos, Set<BlockPos>> getBlockPosList() {
        Map<ChunkPos, Set<BlockPos>> blockPosList = new HashMap<>();
        for (int i = -0; i < 50; i++) {
            for (int j = -0; j < 80; j++) {
                ChunkPos chunkPos = new ChunkPos(i, j);
                Set<BlockPos> blockPosSet = new HashSet<>();
                for (int k = -0; k < 100; k++) {
                    blockPosSet.add(new BlockPos(i, k, j));
                }
                blockPosList.put(chunkPos, blockPosSet);
            }
        }
        return blockPosList;
    }
}
