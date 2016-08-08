package io.github.aadeg.autobroadcaster.config.updaters;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Version2Updater implements ConfigurationUpdater {

    @Override
    public void update(CommentedConfigurationNode config) {
        CommentedConfigurationNode node = config.getNode("autobroadcaster");
        node.getNode("version").setValue(2);
        for (CommentedConfigurationNode broadcasterNode : node.getNode("broadcasters").getChildrenMap().values()) {
            broadcasterNode.getNode("enabled").setValue(true)
                    .setComment("Set it to false to temporarily disable the broadcaster.");

            int intervalSecs = broadcasterNode.getNode("interval").getInt();
            broadcasterNode.getNode("interval").setValue(convertInterval(intervalSecs));
        }

    }

    @Override
    public boolean isAlreadyUpdated(CommentedConfigurationNode config) {
        CommentedConfigurationNode node = config.getNode("autobroadcaster");
        if (!node.getChildrenMap().containsKey("version"))
            return false;
        return node.getNode("version").getInt() >= 2;
    }

    @Override
    public int getVersion(){
        return 2;
    }

    private static String convertInterval(int secs){
        int hours = secs / 3600;
        secs -= hours * 3600;
        int mins = secs / 60;
        secs -= mins * 60;

        StringBuilder out = new StringBuilder();
        if (hours > 0)
            out.append(hours).append('h');
        if (mins > 0)
            out.append(mins).append('m');
        if (secs > 0)
            out.append(secs).append('s');
        return out.toString();
    }
}
