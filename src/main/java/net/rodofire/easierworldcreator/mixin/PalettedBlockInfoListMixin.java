package net.rodofire.easierworldcreator.mixin;

import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(StructureTemplate.PalettedBlockInfoList.class)
public interface PalettedBlockInfoListMixin {
    @Invoker("<init>")
    public static StructureTemplate.PalettedBlockInfoList invokeConstructor(List<StructureTemplate.StructureBlockInfo> infosy) {
        throw new AssertionError();
    }

}
