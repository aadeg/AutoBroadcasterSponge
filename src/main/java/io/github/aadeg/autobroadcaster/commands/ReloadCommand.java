package io.github.aadeg.autobroadcaster.commands;

import io.github.aadeg.autobroadcaster.AutoBroadcaster;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import io.github.aadeg.autobroadcaster.config.ConfigurationManager;

public class ReloadCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        AutoBroadcaster.getLogger().debug("Reloading configuration...");

        ConfigurationManager.getInstance().loadConfig();
        AutoBroadcaster.getInstance().startBroadcasters();
        src.sendMessage(Text.of("Configuration reloaded."));

        AutoBroadcaster.getLogger().debug("configuration reloaded.");
        return CommandResult.success();
    }
}
