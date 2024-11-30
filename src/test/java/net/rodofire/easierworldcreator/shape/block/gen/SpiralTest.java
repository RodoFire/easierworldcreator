package net.rodofire.easierworldcreator.shape.block.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.EWCTest;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShapeBase;

import java.time.Duration;
import java.time.Instant;

public class SpiralTest {

    public static void generateEllipsoidSpiralTest(StructureWorldAccess world) {
        EWCTest.LOGGER.info("starting Ellipsoid Test");
        Instant start = Instant.now();
        SpiralGen spiral = new SpiralGen(world, new BlockPos(0, 0, 0), AbstractBlockShapeBase.PlaceMoment.OTHER, 30, 250);
        spiral.setSpiralType(SpiralGen.SpiralType.HELICOID);
        for (int i = 0; i < 10000; i++) {
            if (i % 10 == 0) EWCTest.LOGGER.info("spiral progression {} %", (float) (i / 10) / 10);
            spiral.getBlockPos();
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        EWCTest.LOGGER.info("Torus Coordinates Generation Test took {} ms", ((double) (timeElapsed.toNanos() / 10000000) / 1000));
    }
}
