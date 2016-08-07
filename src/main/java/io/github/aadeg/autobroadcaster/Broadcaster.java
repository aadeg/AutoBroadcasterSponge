package io.github.aadeg.autobroadcaster;

import io.github.aadeg.autobroadcaster.channels.AllPlayerMessageChannel;
import io.github.aadeg.autobroadcaster.channels.WorldMessageChannel;
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
    private int interval;
    private boolean broadcastToConsole;
    private Set<World> worlds = new HashSet<World>();
    private List<Text> messages = new LinkedList<Text>();

    private Task task = null;
    private MutableMessageChannel channel;
    private Iterator<Text> msgIterator;

    public Broadcaster(String name, Text announcerName, int interval, boolean broadcastToConsole, List<String> worlds, List<Text> messages){
        AutoBroadcaster.getLogger().debug("Initialization of broadcaster " + name + "...");
        this.name = name;
        this.announcerName = announcerName;
        this.interval = interval;
        this.broadcastToConsole = broadcastToConsole;
        this.messages = messages;
        this.msgIterator = this.messages.iterator();

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
        AutoBroadcaster.getLogger().debug("Starting broadcaster " + name + "...");
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
