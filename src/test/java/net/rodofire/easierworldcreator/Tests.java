package net.rodofire.easierworldcreator;

import net.rodofire.easierworldcreator.blockdata.sorter.BlockSortedTest;

public class Tests {
    public static void registerTests() {
        BlockSortedTest.testSorting();
        WorldTest.registerTest();
    }
}
