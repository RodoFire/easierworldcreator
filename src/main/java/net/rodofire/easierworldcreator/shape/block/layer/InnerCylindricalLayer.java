package net.rodofire.easierworldcreator.shape.block.layer;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

public class InnerCylindricalLayer extends AbstractRadialLikeLayer {
    InnerCylindricalLayer(BlockLayerManager blockLayer, Vec3d center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    @Override
    protected int findLayerIndex(int[] layerDistance, float distance) {
        int left = 0, right = layerDistance.length-1;

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
        return  WorldGenUtil.getDistanceToAxis(this.centerPos, this.directionVector, new Vec3d(pos[0], pos[1], pos[2]));
    }

    @Override
    protected float getDistance(int posX, int posY, int posZ) {
        return  WorldGenUtil.getDistanceToAxis(this.centerPos, this.directionVector, new Vec3d(posX, posY, posZ));
    }


}
