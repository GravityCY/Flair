# Flair
A clientside (at least it should be) mod that adds sound effects to stuff and things

If nothing works, I'm not surprised...

***

**Minecraft Forge 1.7.10**

**NEEDS GTNHLIB AND UNIMIXINS** 

*(i think only those. Idk man I literally made this for GTNH so just use GTNH :)*

***

# The uhhhhh... "Showcase".... If you can even call it that...
https://www.youtube.com/watch?v=tkQRdXkX-sc

***

Inspired by the Sounds mod :D

***

Right now adds sounds for:
* Interacting with items in your inventory
* Crafting
* Typing in chat or (some) search fields
* Dropping items
* Switching hotbar slots
* Opening and closing GUIs (e.g. chests, machines)

***

- Sounds are fully configurable with conditional rules

***

## GTNH Config
By default, the mod ships only with the Vanilla Config. 
If you want the GTNH Config (that I also used in the "showcase"), for now I've put it on my [discord](https://discord.gg/s79tqpURE7)! 

## What the config looks like 
To properly learn how to configure stuff, please look at the default config generated! As soon as you join a world run /flair config to look at the default config! It explains a lot more there!

The config allows you to control what sounds should play for what items.  
You can define rules like:

```ini
# The default sound to play if an item doesn't have a sound
default item play dig.stone 0.6 1

# If an item has 'wood' in its name, play wood sound
if item $DisplayName contains wood play dig.wood

# Play a fire sound when a furnace is opened
set block furnace play fire.fire
```

***

# Commands
You can also use helpful in-game commands to streamline setup, such as:

* /flair config
  * to open the config with your text editor
* /flair log sounds
  * to log all sounds playing around you to a file next to the config
* /flair find <approximate sound name> 
  * to search for similar named sounds
* /flair play <approximate sound name> 
  * to play the first similar named sound
* /flair hand
  * to tell you the item in your hand and copy it to clipboard
* /flair block
  * to tell you the block your looking at and copy it to clipboard

***

## Thoughts

While editing my GT: New Horizons videos. I often ended up manually adding sound effects during editing just to make the game feel more satisfying, so I made this...