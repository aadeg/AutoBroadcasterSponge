package io.github.aadeg.autobroadcaster.channels;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AllPlayerMessageChannel extends BroadcasterMessageChannel {

    private Set<MessageReceiver> members;

    public AllPlayerMessageChannel(Text announcerName){
        super(announcerName);
        this.members = new HashSet<>();
        this.members.addAll(Sponge.getGame().getServer().getOnlinePlayers());
    }

    @Override
    public boolean addMember(MessageReceiver messageReceiver) {
        return members.add(messageReceiver);
    }

    @Override
    public boolean removeMember(MessageReceiver messageReceiver) {
        return members.remove(messageReceiver);
    }

    @Override
    public void clearMembers() {
        members.clear();
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.unmodifiableCollection(this.members);
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Login event){
        this.addMember(event.getTargetUser().getPlayer().get());
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event){
        this.removeMember(event.getTargetEntity());
    }
}
