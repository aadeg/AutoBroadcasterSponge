package io.github.aadeg.autobroadcaster.config.upgraders;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public interface ConfigurationUpgrader {

    void upgrade(CommentedConfigurationNode config);
    boolean isAlreadyUpgrader(CommentedConfigurationNode config);
    int getVersion();

}
