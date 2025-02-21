package net.rodofire.easierworldcreator.shape.block.layer;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;

abstract class AbstractLayer implements Layer {
    protected BlockLayerManager blockLayer;
    protected Vec3d centerPos = new Vec3d(0,0,0);
    protected Vec3d directionVector = new Vec3d(0,1,0);


    AbstractLayer(BlockLayerManager blockLayer, Vec3d centerPos, Vec3i directionVector) {
        this.blockLayer = blockLayer;
        this.centerPos = centerPos;
        this.directionVector = Vec3d.of(directionVector);
    }

    AbstractLayer(BlockLayerManager blockLayer, Vec3d centerPos) {
        this.blockLayer = blockLayer;
        this.centerPos = centerPos;
    }

    AbstractLayer(BlockLayerManager blockLayer) {
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
    public void setDirectionVector(Vec3i directionVector) {
        this.directionVector = Vec3d.of(directionVector);
    }
}
