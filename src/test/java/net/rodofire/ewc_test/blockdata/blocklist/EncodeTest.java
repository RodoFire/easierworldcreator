package net.rodofire.ewc_test.blockdata.blocklist;

import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EncodeTest {

    @Test
    public void testPos() {
        for (int x = -1024; x < 1024; x += 10) {
            for (int y = -64; y < 64; y += 10) {
                for (int z = -1024; z < 1024; z += 10) {
                    int chunkMinX = 0;
                    int chunkMinZ = 0;

                    int offsetX = 0;
                    int offsetZ = 0;
                    int offsetY = 0;

                    BlockPos pos = new BlockPos(x, y, z);
                    // Positions relatives dans le chunk après application de l'offset
                    int relX = pos.getX() - chunkMinX - offsetX;
                    int relZ = pos.getZ() - chunkMinZ - offsetZ;
                    int relY = pos.getY() - offsetY;

                    BlockPos pos1 = new BlockPos(relX, relY, relZ);

                    int intX = ((relX) & 0x7FF) << 21;
                    int intY = (relY + 512) << 11;
                    int intZ = relZ & 0x7FF;


                    if (relX < -1024 || relX > 1023 || relZ < -1024 || relZ > 1023) {
                        throw new IllegalArgumentException("pos out of range: " + pos);
                    }
                    System.out.println(intX + " " + intY + " " + intZ);
                    System.out.println((intX | intY | intZ));
                    System.out.println((intX + intY + intZ));

                    int compactPos = ((relX & 0x7FF) << 21) | ((relY + 512) << 11) | (relZ & 0x7FF);


                    int relX1 = ((compactPos >> 21) & 0x7FF);
                    int relY1 = ((compactPos >> 11) & 0x3FF) - 512;
                    int relZ1 = (compactPos & 0x7FF);

                    // Ajustement pour les valeurs signées
                    if (relX1 >= 1024) relX1 -= 2048;
                    if (relZ1 >= 1024) relZ1 -= 2048;

                    int x1 = relX1 + chunkMinX;
                    int y1 = relY1;
                    int z1 = relZ1 + chunkMinZ;


                    BlockPos result = new BlockPos(x1, y1, z1);
                    System.out.println(pos + "   " + pos1 + "  " + compactPos + "   " + result);

                    Assertions.assertEquals(pos, result);
                }
            }
        }
    }

}
