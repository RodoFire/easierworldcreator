package net.rodofire.easierworldcreator.structure;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.shape.block.placer.animator.StructurePlaceAnimator;

import java.util.Optional;

/**
 * Class to place nbt structures in the world. To use a NbtPlacer, you can use this code:
 * <pre>{@code NbtPlacer placer = new NbtPlacer(world, new Identifier("Path_of_your_structure"));
 * placer.place(pos)
 * }</pre>
 */
@SuppressWarnings("unused")
public class NbtPlacer {
    private final StructureWorldAccess world;
    private final Identifier templateName;
    private StructurePlaceAnimator animator;

    /**
     * init a NbtPlacer
     *
     * @param world        the world the structure will spawn in
     * @param templateName the identifier of the structure
     */
    public NbtPlacer(StructureWorldAccess world, Identifier templateName) {
        this.world = world;
        this.templateName = templateName;
    }

    /**
     * init a NbtPlacer
     *
     * @param world        the world the structure will spawn in
     * @param templateName the identifier of the structure
     * @param animator     the animation of the structure place
     */
    public NbtPlacer(StructureWorldAccess world, Identifier templateName, StructurePlaceAnimator animator) {
        this.world = world;
        this.templateName = templateName;
        this.animator = animator;
    }

    /**
     * method to get the animator of the object
     *
     * @return the animator of the object
     */
    public StructurePlaceAnimator getAnimator() {
        return animator;
    }

    /**
     * method to change the animator
     *
     * @param animator the animator that will be changed
     */
    public void setAnimator(StructurePlaceAnimator animator) {
        this.animator = animator;
    }


    /**
     * this method allows you to place a structure in the world during world gen or not
     *
     * @param pos the pos of the structure
     */
    public void place(BlockPos pos) {
        this.place(1.0f, pos, new BlockPos(0, 0, 0), BlockMirror.NONE, BlockRotation.NONE, true);
    }

    /**
     * this method allows you to place a structure in the world during world gen or not
     *
     * @param integrity      the integrity of the structure must be between 0f and 1f
     * @param pos            the pos of the structure
     * @param offset         the offset of the pos structure
     * @param mirror         the block mirror if wanted
     * @param rotation       the structure rotation if wanted
     * @param ignoreEntities ignore entities of the structure
     */
    public void place(float integrity, BlockPos pos, BlockPos offset, BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities) {
        Pair<StructureTemplate, StructurePlacementData> data = processCommon(integrity, mirror, rotation, ignoreEntities);

        if (data != null) {
            data.getLeft().place(world, pos, offset, data.getRight(), world.getRandom(), 3);
        }
    }

    /**
     * method to get a {@link BlockListManager} based on a structure
     *
     * @param integrity      the integrity of the structure must be between 0f and 1f
     * @param pos            the pos of the structure
     * @param offset         the offset of the pos structure
     * @param mirror         the block mirror if wanted
     * @param rotation       the structure rotation if wanted
     * @param ignoreEntities ignore entities of the structure
     * @return the Structure converted into {@link BlockListManager}
     */
    public BlockListManager get(float integrity, BlockPos pos, BlockPos offset, BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities) {
        BlockListManager manager = new BlockListManager();
        Pair<StructureTemplate, StructurePlacementData> data = processCommon(integrity, mirror, rotation, ignoreEntities);

        if (data != null) {
            StructureUtil.convertNbtToManager(data.getLeft(), manager, data.getRight(), world, offset);
        }
        return manager;
    }



    private Pair<StructureTemplate, StructurePlacementData> processCommon(float integrity, BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            Ewc.LOGGER.error("cannot get structure template, MinecraftServer is null. Structure template: {}", templateName);
            return null;
        }
        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();
        if (structureTemplateManager == null) return null;

        Optional<StructureTemplate> optional;
        try {
            optional = structureTemplateManager.getTemplate(templateName);
        } catch (InvalidIdentifierException var6) {
            Ewc.LOGGER.error("cannot get Nbt from Identifier");
            var6.fillInStackTrace();
            return null;
        }

        if (optional.isEmpty()) return null;

        StructurePlacementData structurePlacementData = new StructurePlacementData()
                .setMirror(mirror)
                .setRotation(rotation)
                .setIgnoreEntities(ignoreEntities);

        if (integrity < 1.0F) {
            structurePlacementData.clearProcessors()
                    .addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(integrity, 0.0F, 1.0F)))
                    .setRandom(StructureBlockBlockEntity.createRandom(world.getSeed()));
        }

        return new Pair<>(optional.get(), structurePlacementData);
    }

}