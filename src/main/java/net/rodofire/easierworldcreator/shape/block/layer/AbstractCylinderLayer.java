package net.rodofire.easierworldcreator.shape.block.layer;

import net.minecraft.util.math.Vec3d;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

public abstract class AbstractCylinderLayer extends AbstractRadialLikeLayer {
    AbstractCylinderLayer(BlockLayerManager blockLayer) {
        super(blockLayer);
    }

    @Override
    protected float getDistance(int centerPosX, int centerPosY, int centerPosZ, int[] pos) {
        return WorldGenUtil.getDistanceToAxis(this.centerPos, this.directionVector, new Vec3d(pos[0], pos[1], pos[2]));
    }

    @Override
    protected float getDistance(int centerPosX, int centerPosY, int centerPosZ, int posX, int posY, int posZ) {
        return WorldGenUtil.getDistanceToAxis(this.centerPos, this.directionVector, new Vec3d(posX, posY, posZ));
    }
}
