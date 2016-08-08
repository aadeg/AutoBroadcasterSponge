package io.github.aadeg.autobroadcaster.config;

import io.github.aadeg.autobroadcaster.AutoBroadcaster;
import io.github.aadeg.autobroadcaster.config.upgraders.ConfigurationUpgrader;
import io.github.aadeg.autobroadcaster.config.upgraders.Version2Upgrader;
import io.github.aadeg.autobroadcaster.utils.TextUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationManager {
    public static final int CONFIG_VERSION = 2;

    private static final List<ConfigurationUpgrader> UPGRADERS = new ArrayList<>();
    static {
        UPGRADERS.add(new Version2Upgrader());
    }

    private static ConfigurationManager instance = new ConfigurationManager();

    public static ConfigurationManager getInstance(){
        return instance;
    }

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode config;

    public void setup(File configFile, ConfigurationLoader<CommentedConfigurationNode> configLoader){
        this.configLoader = configLoader;

        if (!configFile.exists()){
            try {
                configFile.createNewFile();
                loadConfig();
                initDefaultConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadConfig();
            updateConfig();
        }
    }

    public CommentedConfigurationNode getConfig(){
        return config;
    }
    public Map<Object, ? extends CommentedConfigurationNode> getBroadcastersConfig() {
        return config.getNode("autobroadcaster", "broadcasters").getChildrenMap();
    }

    public void saveConfig(){
        try {
            configLoader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        try {
            config = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDefaultConfig() {
        getConfig().getNode("autobroadcaster", "version").setValue(2)
                .setComment("Do not touch this value");

        CommentedConfigurationNode broadcaster = config.getNode("autobroadcaster", "broadcasters")
                .setComment("Here you can define all the broadcaster that you need.");

        CommentedConfigurationNode defaultBroadcaster = broadcaster.getNode("default")
                .setComment("This is a default broadcaster. You can rename it and modified as you want.");

        Text announcerName = Text.builder("[").color(TextColors.RED)
                .append(Text.of(TextColors.GOLD, "AutoBroadcaster"),
                        Text.of(TextColors.RED, "]")).build();

        defaultBroadcaster.getNode("announcerName").setValue(TextUtils.serializeText(announcerName))
                .setComment("This is the name that will be display in the chat.");

        defaultBroadcaster.getNode("enabled").setValue(true)
                .setComment("Set it to false to temporarily disable the broadcaster.");

        defaultBroadcaster.getNode("interval").setValue("60s")
                .setComment("Interval between two announcement. Example: 10h30m5s -> 10 hours, 30 minutes and 5 seconds");

        defaultBroadcaster.getNode("worlds").setValue(new Vector<String>())
                .setComment("List of worlds where will be broadcast the messages. Leave it blank to broadcast to all the worlds.");

        defaultBroadcaster.getNode("broadcastToConsole").setValue(false)
                .setComment("Set it to true if you what to broadcast messages in console.");

        Vector<String> msgs = new Vector<String>();
        msgs.add("&6Test Message");
        defaultBroadcaster.getNode("messages").setValue(msgs)
                .setComment("Messages to broadcast.");

        saveConfig();

    }

    private void updateConfig(){
        boolean changed = false;

        for (ConfigurationUpgrader updater : UPGRADERS) {
            if (!updater.isAlreadyUpgrader(config)) {
                AutoBroadcaster.getLogger().info("Updating configuration file to version " + updater.getVersion() + ".");
                updater.upgrade(config);
                changed = true;
            } else {
                break;
            }
        }

        if (changed)
            saveConfig();

    }

    /**
     *
     * @param str Interval expressed in friendly-way. Ex: 10h3m20s
     * @return Interval seconds
     */
    public static int parseInterval(String str) throws IllegalArgumentException {
        // Back compatibility
        Pattern oldInterval = Pattern.compile("^\\d+$");
        Matcher mOld = oldInterval.matcher(str);
        if (mOld.matches())
            return Integer.parseInt(str);

        Pattern intervalPattern = Pattern.compile("^(\\d+h)?(\\d+m)?(\\d+s)?$");
        Matcher m = intervalPattern.matcher(str);

        if (!m.matches())
            throw new IllegalArgumentException("Not a valid interval expression!");

        int hours = 0, mins = 0, secs = 0;

        String hoursStr = m.group(1);
        String minsStr = m.group(2);
        String secsStr = m.group(3);

        if (hoursStr != null)
            hours = Integer.parseInt(hoursStr.substring(0, hoursStr.length() - 1));
        if (minsStr != null)
            mins = Integer.parseInt(minsStr.substring(0, minsStr.length() - 1));
        if (secsStr != null)
            secs = Integer.parseInt(secsStr.substring(0, secsStr.length() - 1));

        return secs + mins * 60 + hours * 3600;
    }

    public static final Function TEXT_LIST_TRANSFORMER = new Function<Object, Text>() {
        @Override
        public Text apply(Object o) {
            return TextUtils.deserializeText((String) o, true);
        }
    };

    public static final Function STRING_LIST_TRANSFORMER = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return (String) o;
        }
    };
}
