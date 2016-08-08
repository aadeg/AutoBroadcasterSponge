package io.github.aadeg.autobroadcaster.commands;

import io.github.aadeg.autobroadcaster.AutoBroadcaster;
import io.github.aadeg.autobroadcaster.BroadcasterManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Set;

public class ListBroadcastersCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Set<Text> broadcasters = BroadcasterManager.getInstance().getBroadcastersToString();
        if (broadcasters.isEmpty()) {
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("No broadcasters defined"));
            return CommandResult.success();
        }

        AutoBroadcaster.sendMessageWithPrefix(src, Text.of("List of the active broadcasters:"));

        for (Text t : broadcasters)
            AutoBroadcaster.sendMessageWithPrefix(src, Text.builder("- ").append(t).build());

        return CommandResult.success();
    }
}
