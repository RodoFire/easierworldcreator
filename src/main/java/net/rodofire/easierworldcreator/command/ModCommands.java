package net.rodofire.easierworldcreator.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.rodofire.easierworldcreator.Ewc;

public class ModCommands {
    public static void registerCommands() {
        Ewc.LOGGER.info("|\t- Registering Commands");
        CommandRegistrationCallback.EVENT.register(PlaceAllMCFCommand::register);
        CommandRegistrationCallback.EVENT.register(DebugStructureCommand::register);
    }
}
