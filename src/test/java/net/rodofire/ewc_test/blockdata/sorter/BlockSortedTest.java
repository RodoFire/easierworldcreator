package net.rodofire.ewc_test.blockdata.sorter;

import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockSortedTest {
    public static void benchmarkSorting(List<BlockPos> list, BlockPos centerPoint) {
        double parallel = 0;
        double noParallel = 0;

        for (int i = 0; i < 150; i++) {
            List<BlockPos> cop1 = new ArrayList<>(list);
            long start = System.nanoTime();
            cop1.sort(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos)));
            long duration = System.nanoTime() - start;
            noParallel += (double) (duration / 1000) / 1000;

            List<BlockPos> copy = new ArrayList<>(cop1); // Réinitialiser
            start = System.nanoTime();
            List<BlockPos> sortedList = copy.parallelStream()
                    .sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos)))
                    .toList();
            duration = System.nanoTime() - start;
            parallel += (double) (duration / 1000) / 1000;
        }
        System.out.println("Modification en place : " + noParallel / 150 + " ms");
        System.out.println("Parallélisme avec allocation : " + parallel / 150 + " ms");
    }

    public static List<BlockPos> generateBlockPosList(int size) {
        List<BlockPos> posList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int x = i % 100;
            int y = i / 100;
            int z = (i / 10) % 10;
            posList.add(new BlockPos(x, y, z));
        }
        return posList;
    }

    public static void testSorting() {
        List<BlockPos> posList = generateBlockPosList(100);
        benchmarkSorting(posList, new BlockPos(200, 0, 0));
        posList = generateBlockPosList(1000);
        benchmarkSorting(posList, new BlockPos(200, 0, 0));
        posList = generateBlockPosList(5000);
        benchmarkSorting(posList, new BlockPos(200, 0, 0));
        posList = generateBlockPosList(10000);
        benchmarkSorting(posList, new BlockPos(200, 0, 0));

    }
}
