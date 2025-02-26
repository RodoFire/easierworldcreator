package net.rodofire.easierworldcreator.structure;

import net.minecraft.block.Block;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.shape.block.placer.animator.StructurePlaceAnimator;

import java.util.Optional;
import java.util.Set;

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
        this.place(integrity, pos, offset, mirror, rotation, ignoreEntities, true, null, null);
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
     * @param force          determines if each block has to replace the already existing block if it exists
     * @param blockToForce   set of block that the structure can still force in the case force = false
     * @param blockToSkip    the list of blocks to skip during the pos
     */
    public void place(float integrity, BlockPos pos, BlockPos offset, BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities, boolean force, Set<Block> blockToForce, Set<Block> blockToSkip) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            Ewc.LOGGER.error("cannot get structure template, MinecraftServer is null. Structure template: {}", templateName);
            return;
        }
        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();
        if (structureTemplateManager == null) return;

        Optional<StructureTemplate> optional;
        try {
            optional = structureTemplateManager.getTemplate(templateName);
        } catch (InvalidIdentifierException var6) {
            return;
        }

        //if the structure exists, we initialize the structure placement
        if (optional.isPresent()) {
            StructurePlacementData structurePlacementData = new StructurePlacementData()
                    .setMirror(mirror)
                    .setRotation(rotation)
                    .setIgnoreEntities(ignoreEntities);
            if (integrity < 1.0F) {
                structurePlacementData.clearProcessors()
                        .addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(integrity, 0.0F, 1.0F)))
                        .setRandom(StructureBlockBlockEntity.createRandom(world.getSeed()));
            }
            StructureTemplate structureTemplate = optional.get();

            boolean bl2 = blockToForce == null || blockToForce.isEmpty();
            boolean bl3 = blockToSkip == null || blockToSkip.isEmpty();


            if (animator != null || !bl2 || !bl3) {
                    BlockListManager comparator = new BlockListManager();
                    StructureUtil.convertNbtToManager(structureTemplate, comparator, structurePlacementData, world, offset);
                    StructureUtil.place(world, animator, comparator, pos, force, blockToForce, blockToSkip, 1.0f);

            } else {
                structureTemplate.place(world, pos, offset, structurePlacementData, world.getRandom(), 3);
            }
        }

    }
}