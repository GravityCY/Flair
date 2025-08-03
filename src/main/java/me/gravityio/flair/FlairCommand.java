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
        if (args.peek().equals("find")) {

            var chatGui = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            if (!chatGui.getSentMessages().isEmpty()) {
                String message = chatGui.getSentMessages().get(chatGui.getSentMessages().size() - 1);
                chatGui.clearChatMessages();
                chatGui.addToSentMessages(message);
            }

            args.skip(1);
            if (args.isEnd()) return;
            String item = args.eat().toLowerCase();
            Flair.sendMessage("Sounds that contain '%s':", EnumChatFormatting.GREEN, item);
            List<String> results = findSounds(item, 25);
            for (String result : results) {
                Flair.sendMessage("%s", EnumChatFormatting.GRAY, result);
            }
        } else if (args.peek().equals("hand")) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
            int id = Item.getIdFromItem(stack.getItem());
            String name = GameData.getItemRegistry().getNameForObject(stack.getItem());;
            int meta = stack.getHasSubtypes() ? stack.getItemDamage() : 0;
            Flair.sendMessage("You are holding %s:%d (%d:%d)", EnumChatFormatting.GREEN, name, meta, id, meta);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return Lists.newArrayList("find", "hand");
        return null;
    }
}
