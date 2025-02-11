package net.rodofire.easierworldcreator.shape.block.layer;

import net.minecraft.util.math.Vec3d;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;

abstract class AbstractLayer implements Layer {
    protected BlockLayerManager blockLayer;
    protected Vec3d centerPos;
    protected Vec3d directionVector;

    AbstractLayer(final BlockLayerManager blockLayer) {
        this.blockLayer = blockLayer;
    }

    @Override
    public Vec3d getCenterPos() {
        return centerPos;
    }

    @Override
    public void setCenterPos(Vec3d centerPos) {
        this.centerPos = centerPos;
    }

    @Override
    public Vec3d getDirectionVector() {
        return directionVector;
    }

    @Override
    public void setDirectionVector(Vec3d directionVector) {
        this.directionVector = directionVector;
    }
}
