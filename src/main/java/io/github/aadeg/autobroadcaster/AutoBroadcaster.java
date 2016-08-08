package io.github.aadeg.autobroadcaster;

import com.google.inject.Inject;
import io.github.aadeg.autobroadcaster.commands.*;
import io.github.aadeg.autobroadcaster.config.ConfigurationManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Plugin(id = "autobroadcaster", name = "AutoBroadcaster", version = "0.1.0")
public class AutoBroadcaster {

    private static AutoBroadcaster instance;

    public final static Text messagePrefix = Text.of(
            TextColors.RED, "[", TextColors.GOLD, "AutoBroadcaster", TextColors.RED, "] "
    );

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configFile;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event){
        instance = this;
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        ConfigurationManager.getInstance().setup(configFile, configManager);
        registerCommands();
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event){
        BroadcasterManager.getInstance().addBroadcasters();
    }

    private void registerCommands() {
        CommandSpec reload = CommandSpec.builder()
                .description(Text.of("Reload the configuration files"))
                .permission("autobroadcaster.reload")
                .executor(new ReloadCommand())
                .build();

        CommandSpec listBroadcasters = CommandSpec.builder()
                .description(Text.of("List all the broadcasters"))
                .permission("autobroadcaster.list")
                .executor(new ListBroadcastersCommand())
                .build();

        Map<String, String> subCommands = new HashMap<>();
        subCommands.put("list", "list");
        subCommands.put("add", "add");
        subCommands.put("remove", "remove");
        subCommands.put("enable", "enable");
        subCommands.put("disable", "disable");

        CommandSpec cmd = CommandSpec.builder()
                .description(Text.of("Manage AutoBroadcaster plugin"))
                .child(reload, "reload", "r")
                .child(listBroadcasters, "list", "ls")
                .executor(new BroadcasterCommand())
                .arguments(
                        GenericArguments.onlyOne(new BroadcasterCommandElement(Text.of("broadcaster"))),
                        GenericArguments.onlyOne(GenericArguments.choices(Text.of("command"), subCommands)),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("params")))
                )
                .build();

        Sponge.getCommandManager().register(this, cmd, "autobroadcaster", "ab");
    }

    public static Logger getLogger(){
        return instance.logger;
    }

    public static AutoBroadcaster getInstance(){
        return instance;
    }

    public static void sendMessageWithPrefix(CommandSource src, Text msg) {
        src.sendMessage(AutoBroadcaster.messagePrefix.concat(Text.of(msg)));
    }
}
