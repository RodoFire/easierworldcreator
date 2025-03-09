package net.rodofire.easierworldcreator.mixin.world.gen.surfacebuilder;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnusedReturnValue")
@Mixin(MaterialRules.class)
public interface SurfaceRulesMixin {
    @Invoker("register")
    static <A> MapCodec<? extends A> register(Registry<MapCodec<? extends A>> registry, String id, CodecHolder<? extends A> codecHolder) {
        throw new AssertionError();
    }
}
