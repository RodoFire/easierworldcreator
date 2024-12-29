package net.rodofire.easierworldcreator;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.block.util.BlockStateUtilTest;

import java.util.concurrent.atomic.AtomicInteger;

public class WorldTest {
    public static void registerTest() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
            if (atomicInteger.compareAndSet(0, 1)) {
                tests(world);
            }
        });
    }

    public static void tests(StructureWorldAccess world) {
        //GenTest.tests(world);
        BlockStateUtilTest.testGetBlockStatesFromWorld(world);
    }
}
