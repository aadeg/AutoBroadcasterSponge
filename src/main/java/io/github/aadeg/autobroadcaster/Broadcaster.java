package io.github.aadeg.autobroadcaster;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import io.github.aadeg.autobroadcaster.channels.AllPlayerMessageChannel;
import io.github.aadeg.autobroadcaster.channels.WorldMessageChannel;
import io.github.aadeg.autobroadcaster.config.ConfigurationManager;
import io.github.aadeg.autobroadcaster.utils.TextUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Broadcaster {
    private String name;
    private Text announcerName;
    private boolean enabled;
    private int interval;
    private boolean broadcastToConsole;
    private Set<World> worlds = new HashSet<World>();
    private LinkedList<Text> messages = new LinkedList<Text>();

    private Task task = null;
    private MutableMessageChannel channel;
    private Iterator<Text> msgIterator = null;

    public Broadcaster(String name, Text announcerName, boolean enabled, int interval, boolean broadcastToConsole, List<String> worlds, List<Text> messages){
        AutoBroadcaster.getLogger().debug("Initialization of broadcaster " + name + "...");
        this.name = name;
        this.announcerName = announcerName;
        this.enabled = enabled;
        this.interval = interval;
        this.broadcastToConsole = broadcastToConsole;
        this.messages.addAll(messages);

        for (String w : worlds){
            Optional<World> opt = Sponge.getGame().getServer().getWorld(w);
            if (opt.isPresent())
                this.worlds.add(opt.get());
            else
                AutoBroadcaster.getLogger().warn("Unable to find world " + w + " in " + name + " broadcaster configuration.");
        }

        initBroadcastChannel();

        AutoBroadcaster.getLogger().debug("Broadcaster " + name + " initialized.");
    }

    private void initBroadcastChannel(){
        if (this.worlds.isEmpty()){
            this.channel = new AllPlayerMessageChannel(announcerName);
            AutoBroadcaster.getLogger().debug("Created all players channel,");
        } else {
            this.channel = new WorldMessageChannel(announcerName, worlds);
            AutoBroadcaster.getLogger().debug("Created specified worlds channel.");
        }

        if (broadcastToConsole) {
            this.channel.addMember(Sponge.getGame().getServer().getConsole());
            AutoBroadcaster.getLogger().debug("Added console to channel.");
        }
    }

    public void start(){
        if (!enabled)
            return;

        AutoBroadcaster.getLogger().debug("Starting broadcaster " + name + "...");
        this.msgIterator = this.messages.iterator();

        Sponge.getEventManager().registerListeners(AutoBroadcaster.getInstance(), this.channel);

        Scheduler scheduler = Sponge.getScheduler();
        Task.Builder taskBuilder = scheduler.createTaskBuilder();

        task = taskBuilder.execute(new AnnouncerRunnable()).async().interval(interval, TimeUnit.SECONDS).submit(AutoBroadcaster.getInstance());
        AutoBroadcaster.getLogger().debug("Broadcaster " + name + " started.");
    }

    public void stop(){
        AutoBroadcaster.getLogger().debug("Stopping broadcaster " + name + "...");
        task.cancel();
        Sponge.getEventManager().unregisterListeners(this.channel);

        AutoBroadcaster.getLogger().debug("Broadcaster " + name + " stopped.");
    }

    public boolean isEnabled(){
        return enabled;
    }

    public boolean enable(){
        if (enabled)
            return false;

        ConfigurationManager.getInstance().getConfig()
                .getNode("autobroadcaster", "broadcasters", name, "enabled").setValue(true);
        ConfigurationManager.getInstance().saveConfig();
        this.enabled = true;
        return true;
    }

    public boolean disable(){
        if (!enabled)
            return false;

        ConfigurationManager.getInstance().getConfig()
                .getNode("autobroadcaster", "broadcasters", name, "enabled").setValue(false);
        ConfigurationManager.getInstance().saveConfig();
        this.enabled = false;
        return true;
    }

    public void addMessage(Text msg){
        stop();

        CommentedConfigurationNode msgsNode = ConfigurationManager.getInstance().getConfig()
                .getNode("autobroadcaster", "broadcasters", this.name, "messages");

        List<String> msgs = new ArrayList<>(msgsNode.getList(ConfigurationManager.STRING_LIST_TRANSFORMER));
        msgs.add(TextUtils.serializeText(msg));

        msgsNode.setValue(msgs);
        ConfigurationManager.getInstance().saveConfig();

        this.messages.addLast(msg);

        start();
    }

    public boolean removeMessage(int index){
        if (index < 0 || index >= messages.size())
            return false;

        stop();

        CommentedConfigurationNode msgsNode = ConfigurationManager.getInstance().getConfig()
                .getNode("autobroadcaster", "broadcasters", this.name, "messages");

        List<String> msgs = new ArrayList<>(msgsNode.getList(ConfigurationManager.STRING_LIST_TRANSFORMER));
        msgs.remove(index);

        msgsNode.setValue(msgs);
        ConfigurationManager.getInstance().saveConfig();

        this.messages.remove(index);
        start();

        return true;
    }

    public ImmutableList<Text> getMessages(){
        return ImmutableList.copyOf(messages);
    }

    @Override
    public String toString(){
        String worlds = "";
        if (this.worlds.isEmpty()) {
            worlds = "ALL";
        } else {
            for (World w : this.worlds)
                worlds += w.getName() + ", ";
        }
        worlds = worlds.substring(0, worlds.length() - 2);

        return this.name + " (Worlds: " + worlds + ")";
    }

    class AnnouncerRunnable implements Runnable {
        @Override
        public void run() {
            if (messages.isEmpty())
                return;

            if (!msgIterator.hasNext())
                msgIterator = messages.iterator();
            Text msg = msgIterator.next();

            channel.send(msg);
        }
    }

}
