package me.gravityio.flair;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.util.ArrayPointer;
import me.gravityio.flair.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlairCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "flair";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "flair";
    }

    public List<String> findSounds(String item, int maxResults) {
        Map<String, Integer> scored = new HashMap<>();

        for (Object key : Minecraft.getMinecraft().getSoundHandler().sndRegistry.getKeys()) {
            ResourceLocation resourceLocation = (ResourceLocation) key;
            String path = resourceLocation.toString();
            if (!path.contains(item)) continue;
            int score = StringUtils.levenshtein(item, path.toLowerCase());
            scored.put(path, score);
        }

        return scored.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Lower distance = better match
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argsStr) {
        ArrayPointer<String> args = new ArrayPointer<>(0, argsStr);

        if (args.isEnd()) {
            Flair.sendMessage("/flair find, /flair play, /flair hand, /flair config");
            return;
        }

        if (args.peek().equals("find")) {
            args.skip(1);

            if (args.isEnd()) {
                Flair.sendMessage("Usage: /flair find <name> <name> ...");
                return;
            }

            var chatGui = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            if (!chatGui.getSentMessages().isEmpty()) {
                String[] messages = chatGui.getSentMessages().toArray(new String[0]);
                chatGui.clearChatMessages();
                for (String message : messages) {
                    chatGui.addToSentMessages(message);
                }
            }

            StringBuilder arg = new StringBuilder();
            while (args.hasNext()) {
                arg.append(args.eat());
            }

            String item = arg.toString();
            Flair.sendMessage("Sounds that contain '%s':", EnumChatFormatting.GREEN, item);
            List<String> results = findSounds(item, 25);
            for (String result : results) {
                Flair.sendMessage("%s", EnumChatFormatting.GRAY, result);
            }
        } else if (args.peek().equals("play")) {
            args.skip();
            if (args.isEnd()) {
                Flair.sendMessage("Usage: /flair play <name> <name> ...");
                return;
            }
            StringBuilder arg = new StringBuilder();
            while (args.hasNext()) {
                arg.append(args.eat());
            }
            String item = arg.toString();
            List<String> results = findSounds(item, 25);
            if (results.isEmpty()) return;
            String sound = results.get(results.size() - 1);
            Minecraft.getMinecraft().thePlayer.playSound(sound, 1, 1);
            Flair.sendMessage("Playing '%s'...", EnumChatFormatting.RED, sound);
        } else if (args.peek().equals("hand")) {
            args.skip();
            ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (stack == null) {
                Flair.sendMessage("You are not holding anything!");
                return;
            }
            int id = Item.getIdFromItem(stack.getItem());
            String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
            int meta = stack.getHasSubtypes() ? stack.getItemDamage() : 0;
            Flair.sendMessage("You are holding %s:%d (%d:%d)", EnumChatFormatting.GREEN, name, meta, id, meta);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection("set " + name + "@" + meta + " play random.pop"), null);
        } else if (args.peek().equals("config")) {
            try {
                FlairConfig.loadFirst();
                Desktop.getDesktop().open(FlairConfig.CONFIG_FILE);
            } catch (IOException e) {
                Flair.LOGGER.error("Failed to open flair config", e);
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return Lists.newArrayList("find", "play", "hand", "config");
        return null;
    }
}
