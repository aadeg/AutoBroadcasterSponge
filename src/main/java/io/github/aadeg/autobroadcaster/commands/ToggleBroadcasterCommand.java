package io.github.aadeg.autobroadcaster.commands;

import io.github.aadeg.autobroadcaster.AutoBroadcaster;
import io.github.aadeg.autobroadcaster.BroadcasterManager;
import io.github.aadeg.autobroadcaster.config.ConfigurationManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class ToggleBroadcasterCommand implements CommandExecutor {
    private boolean enable;

    public ToggleBroadcasterCommand(boolean enable){
        this.enable = enable;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String broadcasterName = args.<String>getOne("broadcaster").get();

        if (!BroadcasterManager.getInstance().hasBroadcaster(broadcasterName)){
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Invalid broadcaster name!"));
            return CommandResult.empty();
        }

        if (enable)
            return enable(src, broadcasterName);
        else
            return disable(src, broadcasterName);
    }

    private CommandResult enable(CommandSource src, String broadcasterName){
        if (!BroadcasterManager.getInstance().enableBroadcaster(broadcasterName)){
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Broadcaster already enabled."));
            return CommandResult.empty();
        }

        AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Broadcaster enabled."));
        return CommandResult.success();
    }

    private CommandResult disable(CommandSource src, String broadcasterName){
        if (!BroadcasterManager.getInstance().disableBroadcaster(broadcasterName)){
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Broadcaster already disabled."));
            return CommandResult.empty();
        }

        AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Broadcaster disabled."));
        return CommandResult.success();
    }
}
