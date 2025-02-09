package net.rodofire.easierworldcreator.shape.block.layer;

import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerComparator;

abstract class AbstractLayer implements Layer {
    private BlockLayerComparator blockLayer;

    AbstractLayer(final BlockLayerComparator blockLayer) {
        this.blockLayer = blockLayer;
    }
}
