package io.github.aadeg.autobroadcaster.commands;

import io.github.aadeg.autobroadcaster.BroadcasterManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BroadcasterCommandElement extends CommandElement {

    public BroadcasterCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
        String name = args.next();
        if (!BroadcasterManager.getInstance().hasBroadcaster(name))
            args.createError(Text.of("Invalid broadcaster name!"));

        return name;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        List<String> out = new ArrayList<>();
        String start = null;
        try {
            start = args.peek();
            for (String name : BroadcasterManager.getInstance().getBroadcasterNames())
                if (name.startsWith(start))
                    out.add(name);

            return out;
        } catch (ArgumentParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
