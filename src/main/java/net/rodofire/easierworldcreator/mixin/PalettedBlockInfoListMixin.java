package net.rodofire.easierworldcreator.mixin;

import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

/**
 * mixin used for nbt file generation
 */
@Mixin(StructureTemplate.PalettedBlockInfoList.class)
public interface PalettedBlockInfoListMixin {

    /**
     * mixin allow us to access the constructor to generate the wanted nbt files
     * @param infosy the var to be initialized
     * @return the {@code StructureTemplate.PalettedBlockInfoList} wanted
     */
    @Invoker("<init>")
    public static StructureTemplate.PalettedBlockInfoList invokeConstructor(List<StructureTemplate.StructureBlockInfo> infosy) {
        throw new AssertionError();
    }
}
