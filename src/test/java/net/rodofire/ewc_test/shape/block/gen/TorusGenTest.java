package net.rodofire.ewc_test.shape.block.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shape.block.gen.TorusGen;
import net.rodofire.ewc_test.EWCTest;

import java.time.Duration;
import java.time.Instant;

public class TorusGenTest {


    public static void testTorusGen(StructureWorldAccess world) {
        EWCTest.LOGGER.info("starting Torus Test");
        Instant start = Instant.now();
        TorusGen torusGen = new TorusGen(new BlockPos(0, 0, 0), 30, 25);
        for (int i = 0; i < 10000; i++) {
            if (i % 100 == 0) EWCTest.LOGGER.info("torus progression {} %", i / 100);
            torusGen.getShapeCoordinates();
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        EWCTest.LOGGER.info("Torus Coordinates Generation Test took {} ms", ((double) (timeElapsed.toNanos() / 10000000) / 1000));

    }


}
