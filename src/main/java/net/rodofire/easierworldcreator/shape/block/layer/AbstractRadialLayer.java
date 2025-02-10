package net.rodofire.easierworldcreator.shape.block.layer;

import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

public abstract class AbstractRadialLayer extends AbstractRadialLikeLayer{
    AbstractRadialLayer(BlockLayerManager blockLayer) {
        super(blockLayer);
    }

    @Override
    protected float getDistance(int centerPosX, int centerPosY, int centerPosZ, int[] pos) {
        return WorldGenUtil.getDistance(centerPosX, centerPosY, centerPosZ, pos);
    }

    @Override
    protected float getDistance(int centerPosX, int centerPosY, int centerPosZ, int posX, int posY, int posZ) {
        return WorldGenUtil.getDistance(centerPosX, centerPosY, centerPosZ, posX, posY, posZ);
    }
}
