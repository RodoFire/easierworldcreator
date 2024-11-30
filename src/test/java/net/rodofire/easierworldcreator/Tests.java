package net.rodofire.easierworldcreator;

import net.rodofire.easierworldcreator.blockdata.sorter.BlockSortedTest;
import net.rodofire.easierworldcreator.shape.block.gen.GenTest;

public class Tests {
    public static void registerTests() {
        GenTest.registerTests();
        BlockSortedTest.testSorting();
    }
}
