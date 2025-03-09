package net.rodofire.easierworldcreator.shape.block.layer;

import net.minecraft.util.math.Vec3d;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

public class OuterRadialLayer extends AbstractOuterLayer {
    OuterRadialLayer(BlockLayerManager blockLayer, Vec3d center) {
        super(blockLayer, center);
    }

    @Override
    protected int findLayerIndex(int[] layerDistance, float distance) {
        int left = 0, right = layerDistance.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (layerDistance[mid] == distance) {
                return mid;
            } else if (layerDistance[mid] < distance) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    @Override
    protected float getDistance(int[] pos) {
        return maxDistance - WorldGenUtil.getDistance(this.centerPos, new Vec3d(pos[0], pos[1], pos[2]));
    }

    @Override
    protected float getDistance(int posX, int posY, int posZ) {
        return maxDistance - WorldGenUtil.getDistance(this.centerPos, new Vec3d(posX, posY, posZ));
    }
}
