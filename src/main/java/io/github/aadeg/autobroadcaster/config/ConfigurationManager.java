package io.github.aadeg.autobroadcaster.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;

public class ConfigurationManager {
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

    private void initDefaultConfig(){
        CommentedConfigurationNode broadcaster = config.getNode("autobroadcaster", "broadcasters")
        		.setComment("Here you can define all the broadcaster that you need.");
        
        CommentedConfigurationNode defaultBroadcaster = broadcaster.getNode("default")
        		.setComment("This is a default broadcaster. You can rename it and modified as you want.");
        
        Text announcerName = Text.builder("[").color(TextColors.RED)
        		.append(Text.of(TextColors.GOLD, "AutoBroadcaster"),
        				Text.of(TextColors.RED, "]")).build();
        
        defaultBroadcaster.getNode("announcerName").setValue(serializeText(announcerName))
        	.setComment("This is the name that will be display in the chat.");
        
        defaultBroadcaster.getNode("interval").setValue(60)
        	.setComment("Interval in seconds between two announcement.");
        
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
    
    public static String serializeText(Text text){
    	return TextSerializers.FORMATTING_CODE.serialize(text);
    }
    
    public static Text deserializeText(String str){
    	return TextSerializers.FORMATTING_CODE.deserialize(str);
    }

    public static final Function TEXT_LIST_TRANSFORMER = new Function<Object, Text>() {
        @Override
        public Text apply(Object o) {
            return deserializeText((String) o);
        }
    };

    public static final Function STRING_LIST_TRANSFORMER = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return (String) o;
        }
    };
}
