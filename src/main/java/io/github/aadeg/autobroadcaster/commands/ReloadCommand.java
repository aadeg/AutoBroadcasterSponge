package io.github.aadeg.autobroadcaster.commands;

import io.github.aadeg.autobroadcaster.AutoBroadcaster;
import io.github.aadeg.autobroadcaster.BroadcasterManager;
import io.github.aadeg.autobroadcaster.config.ConfigurationManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class ReloadCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        AutoBroadcaster.getLogger().debug("Reloading configuration...");

        ConfigurationManager.getInstance().loadConfig();
        BroadcasterManager.getInstance().addBroadcasters();

        src.sendMessage(AutoBroadcaster.messagePrefix.concat(Text.of("Configuration reloaded.")));

        AutoBroadcaster.getLogger().debug("configuration reloaded.");
        return CommandResult.success();
    }
}
