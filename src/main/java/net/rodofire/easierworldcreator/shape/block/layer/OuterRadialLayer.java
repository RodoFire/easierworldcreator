package net.rodofire.easierworldcreator.shape.block.layer;

import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;

public class OuterRadialLayer extends AbstractRadialLayer {
    OuterRadialLayer(BlockLayerManager blockLayer) {
        super(blockLayer);
    }
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
