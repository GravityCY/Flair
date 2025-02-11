package me.gravityio.orbit.commands;

import me.gravityio.orbit.ClientProxy;
import me.gravityio.orbit.EasingType;
import me.gravityio.orbit.OrbitSettings;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OrbitCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "Orbit";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage:" +
            "\n/orbit start" +
            "\n/orbit stop" +
            "\n/orbit from_player" +
            "\n/orbit fov <fov (decimal)>" +
            "\n/orbit distance <distance (decimal)>" +
            "\n/orbit rotations <rotations (decimal)>" +
            "\n/orbit angle <angle> (degrees)" +
            "\n/orbit height <height> (decimal)" +
            "\n/orbit time <time> (seconds)" +
            "\n/orbit ease set <ease_type> (use /orbit ease list)" +
            "\n/orbit ease list" +
            "\n/orbit pos <x> <y> <z>";
    }

    @Override
    public List<String> getCommandAliases() {
        return null;
    }

    private void lowercase(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].toLowerCase();
        }
    }

    private EasingType getEasing(String s) {
        try {
            return EasingType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ClientProxy mod = ClientProxy.INSTANCE;
        OrbitSettings settings = mod.orbitSettings;

        this.lowercase(args);

        if (args.length < 1) {
            ClientProxy.sendMessage(
                "Usage:" +
                    "\n/orbit start" +
                    "\n/orbit stop" +
                    "\n/orbit from_player" +
                    "\n/orbit fov <fov (decimal)>" +
                    "\n/orbit distance <distance (decimal)>" +
                    "\n/orbit rotations <rotations (decimal)>" +
                    "\n/orbit angle <angle> (degrees)" +
                    "\n/orbit height <height> (decimal)" +
                    "\n/orbit time <time> (seconds)" +
                    "\n/orbit ease set <ease_type> (use /orbit ease list)" +
                    "\n/orbit ease list" +
                    "\n/orbit pos <x> <y> <z>"
            );
            return;
        }

        switch (args[0]) {
            case "start": {
                mod.setOrbit(true);
                break;
            }
            case "stop": {
                mod.setOrbit(false);
                break;
            }
            case "pos": {
                if (args.length < 4) {
                    ClientProxy.sendMessage("Current Orbit Position: " + settings.orbitPos);
                    return;
                }

                Vector3f pos = new Vector3f(
                    Float.parseFloat(args[1]),
                    Float.parseFloat(args[2]),
                    Float.parseFloat(args[3])
                );
                settings.setOrbitPos(pos);
                ClientProxy.sendMessage(
                    String.format(
                        "Orbit Position set to (%s, %s, %s)",
                        settings.orbitPos.x, settings.orbitPos.y, settings.orbitPos.z
                    )
                );
                break;
            }
            case "rotations": {
                if (args.length < 2) {
                    ClientProxy.sendMessage("Current Rotations: " + settings.rotations);
                    return;
                }

                settings.rotations = Float.parseFloat(args[1]);
                ClientProxy.sendMessage("Set Rotations to: " + settings.rotations);
                break;
            }
            case "fov": {
                if (args.length < 2) {
                    ClientProxy.sendMessage("Current FOV: " + settings.orbitFov);
                    return;
                }

                settings.orbitFov = Float.parseFloat(args[1]);
                ClientProxy.sendMessage("Set Orbit FOV to: " + settings.orbitFov);
                break;
            }
            case "from_player": {
                ChunkCoordinates coords = sender.getPlayerCoordinates();
                Vector3f pos = new Vector3f((float) coords.posX, (float) coords.posY, (float) coords.posZ);
                settings.setStartAngle(pos);
                settings.setStartDistance(pos);
                settings.setHeightOffset(pos);
                ClientProxy.sendMessage(
                    String.format(
                        "Starting angle set to (%.1f), distance set to (%.1f), height offset set to (%.1f)",
                        settings.heightOffset, Math.toDegrees(settings.startAngleRad), settings.orbitDistance
                    )
                );
                break;
            }
            case "distance": {
                if (args.length == 1) {
                    ClientProxy.sendMessage("Current Orbit Distance: " + settings.orbitDistance);
                    return;
                }

                settings.orbitDistance = Float.parseFloat(args[1]);
                ClientProxy.sendMessage("Set Orbit Distance to: " + settings.orbitDistance);
                break;
            }
            case "angle": {
                if (args.length == 1) {
                    ClientProxy.sendMessage("Current Start Angle: " + Math.toDegrees(settings.startAngleRad));
                    return;
                }

                settings.startAngleRad = (float) Math.toRadians(Float.parseFloat(args[1]));
                ClientProxy.sendMessage("Set Start Angle to: " + args[1]);
                break;
            }
            case "height": {
                if (args.length < 2) {
                    ClientProxy.sendMessage("Current Height Offset: " + settings.heightOffset);
                    return;
                }

                settings.heightOffset = Float.parseFloat(args[1]);
                ClientProxy.sendMessage("Set Height Offset to: " + settings.heightOffset);
                break;
            }
            case "time": {
                if (args.length < 2) {
                    ClientProxy.sendMessage("Current Orbit Time: " + settings.orbitTimeMs / 1000f + "s");
                    return;
                }
                settings.orbitTimeMs = (long) (Float.parseFloat(args[1]) * 1000f);
                ClientProxy.sendMessage("Set Orbit Time to: " + settings.orbitTimeMs / 1000f + "s");
                break;
            }
            case "ease": {
                if (args.length == 1) {
                    ClientProxy.sendMessage("Usage: /orbit ease set <ease_type> (use /orbit ease list)");
                    return;
                }
                switch (args[1]) {
                    case "list": {
                        ClientProxy.sendMessage("Available Easing Types:");
                        for (EasingType value : EasingType.values()) {
                            ClientProxy.sendMessage(value.name());
                        }
                        return;
                    }
                    case "set": {
                        if (args.length < 3) {
                            ClientProxy.sendMessage("Current Easing Type: " + settings.easeType);
                            return;
                        }

                        EasingType temp = this.getEasing(args[2]);
                        if (temp == null) {
                            ClientProxy.sendMessage("Invalid Easing Type");
                            return;
                        }
                        settings.easeType = temp;
                        ClientProxy.sendMessage("Set Orbit Ease Type to: " + settings.easeType);
                        return;
                    }
                }
                EasingType temp = this.getEasing(args[1]);
                if (temp == null) {
                    ClientProxy.sendMessage("Invalid Easing Type");
                    return;
                }

                settings.easeType = temp;
                ClientProxy.sendMessage("Set Orbit Ease Type to: " + settings.easeType);
                break;
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        List<String> list = new ArrayList<>(6);
        if (args.length < 1) {
            list.add("fov");
            list.add("angle");
            list.add("distance");
            list.add("height");
            list.add("rotations");
            list.add("time");
            list.add("ease");
            list.add("from_player");
            list.add("pos");
        } else if (args.length < 2) {
            this.lowercase(args);
            if (args[0].equals("ease")) {
                for (EasingType value : EasingType.values()) {
                    list.add(value.name());
                }
            }
        }
        return list;
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
