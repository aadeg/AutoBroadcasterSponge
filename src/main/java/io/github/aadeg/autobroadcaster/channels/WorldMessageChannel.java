package io.github.aadeg.autobroadcaster.channels;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WorldMessageChannel extends BroadcasterMessageChannel {

    private Set<MessageReceiver> members;
    private Set<World> worlds;

    public WorldMessageChannel(Text announcerName, Set<World> worlds){
        super(announcerName);

        this.worlds = worlds;
        this.members = new HashSet<>();

        for (Player p : Sponge.getGame().getServer().getOnlinePlayers()) {
            if (this.worlds.contains(p.getWorld()))
                this.members.add(p);
        }
    }

    @Override
    public boolean addMember(MessageReceiver messageReceiver) {
        return this.members.add(messageReceiver);
    }

    @Override
    public boolean removeMember(MessageReceiver messageReceiver) {
        return this.members.remove(messageReceiver);
    }

    @Override
    public void clearMembers() {
        this.members.clear();
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.unmodifiableCollection(this.members);
    }

    @Listener
    public void onWorldChange(MoveEntityEvent.Teleport event){
        if (!(event.getTargetEntity() instanceof Player))
            return;

        Player p = (Player) event.getTargetEntity();
        World from = event.getFromTransform().getExtent();
        World to = event.getToTransform().getExtent();

        if (!from.equals(to)){
            if (this.worlds.contains(from))
                removeMember(p);
            if (this.worlds.contains(to))
                addMember(p);
        }
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Login event){
        if (this.worlds.contains(event.getToTransform().getExtent()))
            this.addMember(event.getTargetUser().getPlayer().get());
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event){
        if (this.worlds.contains(event.getTargetEntity().getWorld()))
            this.removeMember(event.getTargetEntity());
    }
}
