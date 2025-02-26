package net.rodofire.ewc_test.blockdata.blocklist;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BlockPosLongTest {

    /**
     * test result: 30% less RAM usage
     * about 70% faster when using `add()`
     * about 60% faster when using `get()`. This shouldn't impact global performance since that time took is negligeable compared to add()
     */
    @Test
    public void testPerformance() throws InterruptedException {
        System.gc();
        Thread.sleep(100);

        long beforeMemory = getUsedMemory();
        long startBlockPos = System.nanoTime();
        testBlockPosAllocation();
        long elapsedBlockPos = System.nanoTime() - startBlockPos;
        long afterBlockPosMemory = getUsedMemory();

        System.gc();
        Thread.sleep(100);

        long beforeLongMemory = getUsedMemory();
        long startLong = System.nanoTime();
        testLongAllocation();
        long elapsedLong = System.nanoTime() - startLong;
        long afterLongMemory = getUsedMemory();

        System.out.println("BlockPos: " + ((double) (elapsedBlockPos / 1000) / 1000) + " ms");
        System.out.println("Long: " + ((double) (elapsedLong / 1_000) / 1000) + " ms");
        System.out.println("Memory BlockPos: " + (afterBlockPosMemory - beforeMemory) / 1000000 + " ko");
        System.out.println("Memory Long: " + (afterLongMemory - beforeLongMemory) / 1000000 + " ko");

        Assertions.assertTrue(elapsedBlockPos >= elapsedLong, "BlockPos should be slower");
    }

    @Test
    public void testBlockPosAllocation() {
        List<BlockPos> posList = new ArrayList<>();
        for (int i = 0; i <= 600; i++) {
            for (int j = 0; j <= 320; j++) {
                for (int k = 0; k <= 500; k++) {
                    posList.add(new BlockPos(i, j, k));
                }
            }
        }
        for (int i = 0; i < 90_000_000; i++) {
            posList.get(i);
        }
    }

    @Test
    public void testLongAllocation() {
        LongArrayList longArrayList = new LongArrayList();
        for (int i = 0; i <= 600; i++) {
            for (int j = 0; j <= 320; j++) {
                for (int k = 0; k <= 500; k++) {
                    longArrayList.add(encodeBlockPos(i, j, k));
                }
            }
        }
        for (int i = 0; i < 90_000_000; i++) {
            longArrayList.getLong(i);
        }
    }

    public static long encodeBlockPos(int x, int y, int z) {
        long lx = (x & 0x1FFFFFFL);
        long ly = (y & 0xFFF);
        long lz = (z & 0x1FFFFFFL);

        return (lx << 37) | (lz << 12) | ly;
    }

    public static BlockPos decodeBlockPos(long encoded) {
        int x = (int) (encoded >> 37);
        int z = (int) ((encoded >> 12) & 0x1FFFFFF);
        int y = (int) (encoded & 0xFFF);

        if (x >= (1 << 24)) x -= (1 << 25);
        if (z >= (1 << 24)) z -= (1 << 25);
        if (y >= (1 << 11)) y -= (1 << 12);

        return new BlockPos(x, y, z);
    }


    private long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
