# AutoBroadcaster
This plugin allows you to broadcast messages automatically in the chat of the server.

You can download this plugin [here](https://ore-staging.spongepowered.org/aadeg/AutoBroadcaster).

## Features
+ Supports text color
+ Supports clickable link in messages
+ Supports user-friendly specification of intervals (ex: 1h2m10s)
+ Customizable prefix to all the messages sent
+ Customizable interval between messages
+ Worlds limitation: you can broadcast a message in all the worlds or only in some
+ Broadcaster can be temporarily disabled 
+ Supports broadcasting in console
+ Supports having multiples broadcaster with independent configuration

## Configuration
The configuration file allows a full customization of the plugin. The default config file look like this

```
autobroadcaster {
    # Here you can define all the broadcaster that you need.
    broadcasters {
        # This is a default broadcaster. You can rename it and modified as you want.
        default {

            # This is the name that will be display in the chat.
            announcerName="&c[&6AutoBroadcaster&c]"
            
            # Set it to false to temporarily disable the broadcaster.
            enabled=true

            # Set it to true if you what to broadcast messages in console.
            broadcastToConsole=false

            # Interval in seconds between two announcement.
            interval=60

            # Messages to broadcast.
            messages=[
                "&6Test Message"
            ]

            # List of worlds name where will be broadcast the messages. Leave it blank to broadcast to all the worlds.
            worlds=[]
        }
    }
    version=2
}
```

If you need more broadcaster with different configuration and different messages, you can add a new one under ```broadcasters``` just like this

```
autobroadcaster {
    # Here you can define all the broadcaster that you need.
    broadcasters {
        # This is a default broadcaster. You can rename it and modified as you want.
        default {
           #...
           # Configuration parameters as before
        }

        second_broadcaster {
           #...
           # Same parameters as the default broadcaster
        }
    }
}
```


## Commands
```/autobroadcaster list```: List the broadcasters. If a broadcaster is disabled, it will be shown in gray.
```/autobroadcaster enable <broadcaster>```: Enable the broadcaster.
```/autobroadcaster disable <broadcaster>```: Disable the broadcaster.
```/autobroadcaster <broadcaster> list```: List the messages of the specified broadcaster.
```/autobroadcaster <broadcaster> add <message>```: Add a new message to the specified broadcaster.
```/autobroadcaster <broadcaster> remove <messageID>```: Remove a new message to the specified broadcaster. The ID of a message can be found using ```/autobroadcaster <broadcaster> list``` command.
```/autobroadcaster reload```: Reload the configuration file. This command allowed to change the configuration file and reload it without restart the server.

## Permissions
```autobroadcaster.list```: Permission for command ```/autobroadcaster list```.
```autobroadcaster.reload```: Permission for command ```/autobroadcaster reload```.

For the next permissions you need to replace ```<broadcaster>``` placeholder with the name of desired brodacaster. 
```autobroadcaster.broadcaster.<broadcaster>.list```: Permission for command ```/autobroadcaster <broadcaster> list```.
```autobroadcaster.broadcaster.<broadcaster>.enable```: Permission for command ```/autobroadcaster <broadcaster> enable```.
```autobroadcaster.broadcaster.<broadcaster>.disable```: Permission for command ```/autobroadcaster <broadcaster> disable```.
```autobroadcaster.broadcaster.<broadcaster>.add```: Permission for command ```/autobroadcaster <broadcaster> add <message>```.
```autobroadcaster.broadcaster.<broadcaster>.remove```: Permission for command ```/autobroadcaster <broadcaster> remove <messageID>```.


## Known Issues
+ Text format of a message resets after a link.