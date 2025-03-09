package net.rodofire.easierworldcreator.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.mixin.world.structure.StructureTemplateManagerInvoker;
import net.rodofire.easierworldcreator.util.ModUtil;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DebugStructureCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironmen) {

        dispatcher.register(CommandManager.literal("debugstructures")
                .executes(commandContext -> {
                    Entity entity = commandContext.getSource().getEntity();
                    if (entity == null) return 0;
                    BlockPos pos = entity.getBlockPos();
                    return DebugStructureCommand.runAll(commandContext, ModUtil.getModsList(), pos);
                })
                .then(CommandManager.argument("mod", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Set<String> mods = ModUtil.getModsList();

                            for (String mod : mods) {
                                builder.suggest(mod);
                            }
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            Entity entity = commandContext.getSource().getEntity();
                            if (entity == null) return 0;
                            BlockPos pos = entity.getBlockPos();
                            return DebugStructureCommand.runAll(commandContext, Set.of(StringArgumentType.getString(commandContext, "mod")), pos);
                        })
                        .then(CommandManager.argument("startPos", BlockPosArgumentType.blockPos())
                                .executes(commandContext -> DebugStructureCommand.runAll(commandContext, Set.of(StringArgumentType.getString(commandContext, "mod")), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos")))
                        )
                )
        );
    }

    public static int runAll(CommandContext<ServerCommandSource> context, Set<String> mod, BlockPos pos) throws CommandSyntaxException {
        StructureTemplateManager structureTemplateManager = context.getSource().getServer().getStructureTemplateManager();

        Map<Identifier, StructureTemplate> templates = new LinkedHashMap<>();
        for (StructureTemplateManager.Provider provider : ((StructureTemplateManagerInvoker) structureTemplateManager).getProviders()) {
            try {
                Stream<Identifier> structureIds = provider.lister().get();

                structureIds.forEach(id -> {
                    Optional<StructureTemplate> optionalTemplate = provider.loader().apply(id);
                    optionalTemplate.ifPresent(template -> templates.put(id, template));
                });
            } catch (Exception ignored) {
            }
        }

        int offsetZ = 0;
        int offsetX = 0;
        int maxX = 0;
        String lastFolder = Path.of(templates.keySet().iterator().next().getPath()).getParent().toString();
        for (Map.Entry<Identifier, StructureTemplate> entry : templates.entrySet()) {
            StructureTemplate template = entry.getValue();


            BlockPos offset = new BlockPos(offsetX, 0, offsetZ);

            if (mod.contains(entry.getKey().getNamespace())) {
                template.place(context.getSource().getWorld(), pos.add(offset), new BlockPos(0, 0, 0), new StructurePlacementData(), context.getSource().getWorld().random, Block.NOTIFY_LISTENERS);
                if (!Path.of(entry.getKey().getPath()).getParent().toString().equals(lastFolder)) {
                    offsetX += maxX + 3;
                    offsetZ = 0;
                    maxX = 0;
                    lastFolder = Path.of(entry.getKey().getPath()).getParent().toString();
                } else {
                    offsetZ += template.getSize().getZ() + 3;
                    maxX = Math.max(maxX, template.getSize().getX());
                }
            }
        }

        return 1;
    }
}
