package me.gravityio.flair;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.condition.*;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class FlairCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "flair";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/flair set";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) return;
        if (args[0].equals("set")) {
            if (args.length == 1) return;

            String item = args[1];
            if (!GameData.getItemRegistry().containsKey(item)) {
                Flair.LOGGER.error("Item not found: {}", item);
                return;
            }
            if (args.length == 2) {
                Flair.LOGGER.error("Expected sound to play..");
                return;
            }
            String sound = args[2];
//            FlairConfig.CONFIG.put(GameData.getItemRegistry().getObject(item), , );
        } else if (args[0].equals("if")) {
            String[] parseArgs = Arrays.copyOfRange(args,1, args.length);
            Parser.parseIf(parseArgs);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Arrays.asList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
