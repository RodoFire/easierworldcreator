package net.rodofire.easierworldcreator.shape.block.layer;

import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;

abstract class AbstractLayer implements Layer {
    protected BlockLayerManager blockLayer;
    protected long centerPos;
    protected long directionVector;

    AbstractLayer(final BlockLayerManager blockLayer) {
        this.blockLayer = blockLayer;
    }

    @Override
    public long getCenterPos() {
        return centerPos;
    }

    @Override
    public void setCenterPos(long centerPos) {
        this.centerPos = centerPos;
    }

    @Override
    public long getDirectionVector() {
        return directionVector;
    }

    @Override
    public void setDirectionVector(long directionVector) {
        this.directionVector = directionVector;
    }
}
