package net.rodofire.easierworldcreator.mixin;

import com.google.common.collect.Lists;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

/**
 * mixin used for nbt file generation
 */
@Mixin(StructureTemplate.class)
public interface StructureTemplateMixin {
    @Accessor("blockInfoLists")
    void setBlockInfoLists(List<StructureTemplate.PalettedBlockInfoList> blockInfoLists);

    /**
     * getter mixin for nbt generation files
     * @return the {@code List<StructureTemplate.PalettedBlockInfoList>} of the class
     */
    @Accessor
    List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists();

    /**
     * mixin for nbt generation files to combine and sort th parameters
     * @return the {@code List<StructureTemplate.PalettedBlockInfoList>} combined and sorted
     */
    @Invoker("combineSorted")
    static List<StructureTemplate.StructureBlockInfo> invokeCombineSorted(List<StructureTemplate.StructureBlockInfo> fullBlocks,
                                                                          List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
                                                                          List<StructureTemplate.StructureBlockInfo> otherBlocks) {
        throw new AssertionError();
    }
}
