package net.rodofire.ewc_test;

import net.rodofire.ewc_test.blockdata.sorter.BlockSortedTest;

public class Tests {
    public static void registerTests() {
        BlockSortedTest.testSorting();
        WorldTest.registerTest();
    }
}
