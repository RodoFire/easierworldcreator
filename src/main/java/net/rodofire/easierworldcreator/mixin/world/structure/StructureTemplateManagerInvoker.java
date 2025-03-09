package net.rodofire.easierworldcreator.mixin.world.structure;

import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.StructureTemplateManager.Provider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplateManager.class)
public interface StructureTemplateManagerInvoker {
    @Accessor("providers")
    List<Provider> getProviders();
}
