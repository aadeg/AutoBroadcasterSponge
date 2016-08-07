package io.github.aadeg.autobroadcaster.channels;

import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class BroadcasterMessageChannel implements MutableMessageChannel {

    protected Text announcerName;

    public BroadcasterMessageChannel(Text announcerName){
        this.announcerName = announcerName;
    }

    @Override
    final public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        Text out = Text.builder().append(announcerName, Text.of(" "), original).build();
        return Optional.of(out);
    }
}
