package net.rodofire.easierworldcreator.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(PlaceAllMCFCommand::register);
        CommandRegistrationCallback.EVENT.register(LegacyPlaceAllMCFCommand::register);
    }
}
