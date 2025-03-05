package net.rodofire.ewc_test.blockdata.blocklist;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderedBlockListTest {


    @Test
    public void testIteratorVsGetLongPos() {
        LongArrayList list = new LongArrayList(1_000_000);
        for (long i = 0; i < 1_000_000; i++) {
            list.add(i);
        }

        long startIterator = System.nanoTime();
        for (long i = 0; i < 1_0000; i++) {
            for (long pos : list) {
            }
        }


        long elapsedIterator = System.nanoTime() - startIterator;

        long startGet = System.nanoTime();
        int size = list.size() * 10000;
        for (int i = 0; i < size; i++) {
            list.getLong(i);
        }

        long elapsedGet = System.nanoTime() - startGet;

        //Assertions.assertTrue(elapsedIterator >= elapsedGet);


    }
}
