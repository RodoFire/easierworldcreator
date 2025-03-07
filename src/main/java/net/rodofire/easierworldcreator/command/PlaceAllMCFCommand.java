package net.rodofire.easierworldcreator.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListHelper;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.util.ChunkUtil;
import net.rodofire.easierworldcreator.util.file.EwcFolderData;
import net.rodofire.easierworldcreator.util.file.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PlaceAllMCFCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironmen) {

        dispatcher.register(CommandManager.literal("placeallmultichunkfeatures")
                .executes(context -> run(context, false))
                .then(CommandManager.argument("legacy", BoolArgumentType.bool())
                        .executes(context -> run(context, BoolArgumentType.getBool(context, "legacy")))
                )
        );
    }

    public static int run(CommandContext<ServerCommandSource> context, boolean legacy) throws CommandSyntaxException {
        Path path;

        if (legacy) path = EwcFolderData.getStructuresDirectory(context.getSource().getWorld());
        else path = EwcFolderData.Legacy.getLegacyStructureDir(context.getSource().getWorld());

        if (context.getSource().hasPermissionLevel(2)) {
            try (Stream<Path> paths = Files.list(path)) {
                paths.forEach(filePath -> {
                    String fileName = filePath.getFileName().toString();
                    Pattern pattern = Pattern.compile("chunk_(-?\\d+)_(-?\\d+)$");
                    Matcher matcher = pattern.matcher(fileName);
                    int chunkX;
                    int chunkZ;

                    if (matcher.matches()) {
                        chunkX = Integer.parseInt(matcher.group(1));
                        chunkZ = Integer.parseInt(matcher.group(2));
                    }
                    //initialize in the else because if not, intellij cries
                    else {
                        chunkZ = 0;
                        chunkX = 0;
                    }

                    if (ChunkUtil.isFeaturesGenerated(context.getSource().getWorld(), new ChunkPos(chunkX, chunkZ))) {
                        try (Stream<Path> files = Files.list(filePath)) {
                            files.forEach((jsonFiles) -> {
                                if (jsonFiles.toString().endsWith(".json")) {
                                    JsonArray jsonArray = new Gson().fromJson(FileUtil.loadJson(jsonFiles), JsonArray.class);
                                    BlockListManager manager = BlockListHelper.fromJson(context.getSource().getWorld(), jsonArray, new ChunkPos(chunkX, chunkZ));
                                    manager.placeAllNDelete(context.getSource().getWorld());
                                }
                                try {
                                    Files.delete(jsonFiles);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (Exception e) {
                            e.fillInStackTrace();
                        }
                        try {
                            Files.delete(filePath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        context.getSource().sendFeedback(() -> Text.translatable("multi_chunk_place"), true);

        return 1;
    }
}
