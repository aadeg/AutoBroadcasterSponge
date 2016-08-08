package io.github.aadeg.autobroadcaster.config.updaters;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public interface ConfigurationUpdater {

    void update(CommentedConfigurationNode config);
    boolean isAlreadyUpdated(CommentedConfigurationNode config);
    int getVersion();

}
