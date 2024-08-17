package net.rodofire.easierworldcreator.mixin;

import com.google.common.collect.Lists;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(StructureTemplate.class)
public interface StructureTemplateMixin {
    @Accessor("blockInfoLists")
    void setBlockInfoLists(List<StructureTemplate.PalettedBlockInfoList> blockInfoLists);

    @Accessor
    List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists();

    @Invoker("combineSorted")
    static List<StructureTemplate.StructureBlockInfo> invokeCombineSorted(List<StructureTemplate.StructureBlockInfo> fullBlocks,
                                                                          List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
                                                                          List<StructureTemplate.StructureBlockInfo> otherBlocks) {
        throw new AssertionError();
    }
}
