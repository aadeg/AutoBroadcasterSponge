package io.github.aadeg.autobroadcaster.commands;

import io.github.aadeg.autobroadcaster.AutoBroadcaster;
import io.github.aadeg.autobroadcaster.BroadcasterManager;
import io.github.aadeg.autobroadcaster.utils.TextUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;


public class BroadcasterCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String broadcasterName = args.<String>getOne("broadcaster").get();
        String command = args.<String>getOne("command").get();
        String params = "";
        if (args.<String>getOne("params").isPresent())
            params = args.<String>getOne("params").get();

        switch (command){
            case "add":
                return add(src, broadcasterName, params);
            case "remove":
                return remove(src, broadcasterName, params);
            case "list":
                return list(src, broadcasterName);
        }

        return CommandResult.empty();
    }

    private CommandResult list(CommandSource src, String broadcasterName){
        AutoBroadcaster.sendMessageWithPrefix(src, Text.of("List of the messages:"));

        int i = 0;
        for (Text t : BroadcasterManager.getInstance().getBroadcasterMessages(broadcasterName)) {
            AutoBroadcaster.sendMessageWithPrefix(src, Text.builder("[" + i++ + "] ").append(t).build());
        }

        return CommandResult.success();
    }

    private CommandResult add(CommandSource src, String broadcasterName, String msg){
        if (msg.isEmpty()){
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Missing message!"));
            return CommandResult.empty();
        }

        BroadcasterManager.getInstance().addMessage(broadcasterName, TextUtils.deserializeText(msg));
        AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Message added."));
        return CommandResult.success();
    }

    private CommandResult remove(CommandSource src, String broadcasterName, String msg){
        int msgID = -1;

        try {
            msgID = Integer.parseInt(msg);
        } catch (NumberFormatException ex){
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Invalid message ID!"));
            return CommandResult.empty();
        }

        if (!BroadcasterManager.getInstance().removeMessage(broadcasterName, msgID)){
            AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Invalid message ID!"));
            return CommandResult.empty();
        }

        AutoBroadcaster.sendMessageWithPrefix(src, Text.of("Message removed."));
        return CommandResult.success();
    }
}
