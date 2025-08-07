package me.gravityio.flair;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.util.ArrayPointer;
import me.gravityio.flair.util.StringUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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


        String command = args.eat();
        switch (command) {
            case "find" -> {
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
            }
            case "play" -> {
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
            }
            case "hand" -> {
                ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
                if (stack == null) {
                    Flair.sendMessage("You are not holding anything!");
                    return;
                }
                int idNum = Item.getIdFromItem(stack.getItem());
                String idStr = GameData.getItemRegistry().getNameForObject(stack.getItem());
                String unlocalizedName = stack.getUnlocalizedName();
                String displayName = stack.getDisplayName();
                int meta = stack.getHasSubtypes() ? stack.getItemDamage() : 0;
                Flair.sendMessage("IdStr: '%s', Meta: %d, IdNum: %d, UnlocalizedName: '%s', DisplayName: '%s'",
                        EnumChatFormatting.GREEN, idStr, meta, idNum, unlocalizedName, displayName);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection("set item " + idStr + "@" + meta + " play random.pop"), null);
            }
            case "block" -> {
                MovingObjectPosition rayhit = Minecraft.getMinecraft().objectMouseOver;
                if (rayhit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                    Flair.sendMessage("You are not looking at a block!");
                    return;
                }
                World world = Minecraft.getMinecraft().theWorld;

                Block block = world.getBlock(rayhit.blockX, rayhit.blockY, rayhit.blockZ);
                int meta = world.getBlockMetadata(rayhit.blockX, rayhit.blockY, rayhit.blockZ);
                if (block == null || block == Blocks.air) {
                    Flair.sendMessage("You are not looking at a block!");
                    return;
                }

                int idNum = Block.getIdFromBlock(block);
                String idStr = GameData.getBlockRegistry().getNameForObject(block);
                String unlocalizedName = block.getUnlocalizedName();
                String localizedName = block.getLocalizedName();
                String itemIconName = block.getItemIconName();
                Flair.sendMessage(
                        "IdStr: '%s', Meta: %d, IdNum: %d, UnlocalizedName: '%s', LocalizedName: '%s', ItemIconName: '%s'",
                        EnumChatFormatting.GREEN, idStr, meta, idNum, unlocalizedName, localizedName, itemIconName
                );
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection("set block " + idStr + "@" + meta + " play random.pop"), null);

            }
            case "config" -> {
                try {
                    FlairConfig.loadFirst();
                    Desktop.getDesktop().open(FlairConfig.CONFIG_FILE);
                } catch (IOException e) {
                    Flair.LOGGER.error("Failed to open flair config", e);
                }
            }
            case "log" -> {
                if (args.isEnd()) {
                    Flair.sendMessage("Expected log type... (screens, sounds)");
                    return;
                }

                String typeStr = args.eat();
                switch (typeStr) {
                    case "screens" -> Flair.INSTANCE.logScreens();
                    case "sounds" -> Flair.INSTANCE.logSounds();
                }
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return Lists.newArrayList("find", "play", "hand", "block", "config", "log");
        return null;
    }
}
