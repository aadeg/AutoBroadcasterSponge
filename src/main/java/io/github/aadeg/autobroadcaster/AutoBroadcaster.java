package io.github.aadeg.autobroadcaster;

import com.google.inject.Inject;
import io.github.aadeg.autobroadcaster.commands.ReloadCommand;
import io.github.aadeg.autobroadcaster.config.ConfigurationManager;
import io.github.aadeg.autobroadcaster.utils.TextUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Plugin(id = "autobroadcaster", name = "AutoBroadcaster", version = "0.1.0")
public class AutoBroadcaster {

    private static AutoBroadcaster instance;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configFile;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private List<Broadcaster> broadcasters = new ArrayList<Broadcaster>();

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
        startBroadcasters();
    }

    private void registerCommands() {
        CommandSpec reload = CommandSpec.builder()
                .description(Text.of("Reload the configuration files"))
                .permission("autobroadcaster.reload")
                .executor(new ReloadCommand())
                .build();

        CommandSpec cmd = CommandSpec.builder()
                .description(Text.of("Manage AutoBroadcaster plugin"))
                .child(reload, "reload", "r")
                .build();

        Sponge.getCommandManager().register(this, cmd, "autobroadcaster", "ab");
    }

    public void startBroadcasters(){
        if (!broadcasters.isEmpty()){
            broadcasters.forEach((b) -> b.stop());
            broadcasters.clear();
        }

        Map<Object, ? extends CommentedConfigurationNode> map = ConfigurationManager.getInstance().getBroadcastersConfig();

        logger.debug("Found " + map.size() + " broadcasters.");

        for(Object key : map.keySet()){
            CommentedConfigurationNode node = map.get(key);

            int interval;
            try {
                interval = ConfigurationManager.parseInterval(node.getNode("interval").getString());
            } catch (IllegalArgumentException ex){
                logger.warn("Invalid interval in " + key + " broadcaster! Broadcaster ignored.");
                continue;
            }

            Broadcaster broadcaster = new Broadcaster(
                    (String) key,
                    TextUtils.deserializeText(node.getNode("announcerName").getString()),
                    interval,
                    node.getNode("broadcastToConsole").getBoolean(),
                    node.getNode("worlds").getList(ConfigurationManager.STRING_LIST_TRANSFORMER),
                    node.getNode("messages").getList(ConfigurationManager.TEXT_LIST_TRANSFORMER)
            );

            broadcasters.add(broadcaster);
            broadcaster.start();
        }

    }

    public static Logger getLogger(){
        return instance.logger;
    }

    public static AutoBroadcaster getInstance(){
        return instance;
    }
}
