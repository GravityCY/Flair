package me.gravityio.flair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return false;
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
