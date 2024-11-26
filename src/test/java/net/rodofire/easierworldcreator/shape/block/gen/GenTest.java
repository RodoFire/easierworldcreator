package net.rodofire.easierworldcreator.shape.block.gen;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.StructureWorldAccess;

import java.util.concurrent.atomic.AtomicInteger;

public class GenTest {
    public static void registerTests() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (atomicInteger.compareAndSet(0, 1)) {
                tests(world);
            }
        });
    }

    public static void tests(StructureWorldAccess world) {
        TorusGenTest.testTorusGen(world);
    }
}
