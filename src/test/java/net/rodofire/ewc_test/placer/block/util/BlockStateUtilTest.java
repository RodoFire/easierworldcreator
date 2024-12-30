package net.rodofire.ewc_test.placer.block.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.ewc_test.EWCTest;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockStateUtilTest {

    public static void testGetBlockStatesFromWorld(StructureWorldAccess world) {
        EWCTest.LOGGER.info("starting BlockState collection Test");
        Instant start = Instant.now();
        List<BlockPos> posList = new ArrayList<BlockPos>();
        for(int i = -100; i <= 100; i++){
            for(int j = -64; j <= 150; j++){
                for(int k = -100; k <= 100; k++){
                    posList.add(new BlockPos(i,j,k));
                }
            }
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        EWCTest.LOGGER.info("Coordinates Generation Test took {} ms", ((double) (timeElapsed.toNanos() / 1000) / 1000));
        start = Instant.now();

        List<Set<BlockPos>> dividedPos = WorldGenUtil.divideBlockPosIntoChunk(posList);
        end = Instant.now();
        timeElapsed = Duration.between(start, end);
        EWCTest.LOGGER.info("Coordinates Generation Test took {} ms", ((double) (timeElapsed.toNanos() / 1000) / 1000));
        start = Instant.now();

        //BlockStateUtil.getThreadedBlockStatesFromWorld(dividedPos, world);
        end = Instant.now();
        timeElapsed = Duration.between(start, end);
        EWCTest.LOGGER.info("Coordinates Collection Test took {} ms", ((double) (timeElapsed.toNanos() / 1000) / 1000));
    }
}