package io.github.aadeg.autobroadcaster;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import io.github.aadeg.autobroadcaster.config.ConfigurationManager;
import io.github.aadeg.autobroadcaster.utils.TextUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.*;

import java.util.*;

public class BroadcasterManager {
    private static BroadcasterManager instance = new BroadcasterManager();

    public static BroadcasterManager getInstance(){
        return instance;
    }

    private Map<String, Broadcaster> broadcasters = new HashMap<String, Broadcaster>();

    public boolean addBroadcaster(String name){
        Map<Object, ? extends CommentedConfigurationNode> map = ConfigurationManager.getInstance().getBroadcastersConfig();
        if (map.containsKey(name))
            return addBroadcaster(name, map.get(name));
        return false;
    }

    private boolean addBroadcaster(String name, CommentedConfigurationNode config){
        if (broadcasters.containsKey(name))
            removeBroadcaster(name);

        int interval;
        try {
            interval = ConfigurationManager.parseInterval(config.getNode("interval").getString());
        } catch (IllegalArgumentException ex){
            AutoBroadcaster.getLogger().warn("Invalid interval in " + name + " broadcaster! Broadcaster ignored.");
            return false;
        }

        Broadcaster broadcaster = new Broadcaster(
                name,
                TextUtils.deserializeText(config.getNode("announcerName").getString()),
                config.getNode("enabled").getBoolean(),
                interval,
                config.getNode("broadcastToConsole").getBoolean(),
                config.getNode("worlds").getList(ConfigurationManager.STRING_LIST_TRANSFORMER),
                config.getNode("messages").getList(ConfigurationManager.TEXT_LIST_TRANSFORMER)
        );

        broadcasters.put(name, broadcaster);
        broadcaster.start();
        return true;
    }

    public boolean removeBroadcaster(String name){
        Broadcaster b = broadcasters.get(name);
        if (b != null) {
            b.stop();
            broadcasters.remove(name);
            return true;
        }
        return false;
    }

    public boolean enableBroadcaster(String name){
        Broadcaster b = broadcasters.get(name);

        if (b != null && b.enable()) {
            b.start();
            return true;
        }
        return false;
    }

    public boolean disableBroadcaster(String name){
        Broadcaster b = broadcasters.get(name);

        if (b != null && b.disable()) {
            b.stop();
            return true;
        }
        return false;
    }

    public void addBroadcasters(){
        removeBroadcasters();
        Map<Object, ? extends CommentedConfigurationNode> map = ConfigurationManager.getInstance().getBroadcastersConfig();

        AutoBroadcaster.getLogger().debug("Found " + map.size() + " broadcasters.");

        for (Object key : map.keySet()){
            addBroadcaster((String) key, map.get(key));
        }
    }

    public void removeBroadcasters(){
        for (String name : broadcasters.keySet())
            broadcasters.get(name).stop();
        broadcasters.clear();
    }

    public Set<Text> getBroadcastersToString(){
        Set<Text> out = new HashSet<Text>();
        broadcasters.forEach((name, b) -> {
            if (b.isEnabled())
                out.add(Text.of(b.toString()));
            else
                out.add(Text.of(TextStyles.STRIKETHROUGH, TextColors.DARK_GRAY, b.toString()));
        });

        return out;
    }

    public boolean hasBroadcaster(String name){
        return broadcasters.containsKey(name);
    }
}
