package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;

import java.util.Collection;
import java.util.Map;

public class OuterCylindricalLayer extends AbstractCylinderLayer {
    OuterCylindricalLayer(BlockLayerManager blockLayer) {
        super(blockLayer);
    }

    @Override
    protected int findLayerIndex(int[] layerDistance, float distance) {
        int left = 0, right = layerDistance.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (layerDistance[mid] < distance) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return right;
    }


}
