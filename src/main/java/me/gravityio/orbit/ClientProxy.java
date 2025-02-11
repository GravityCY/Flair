package me.gravityio.orbit;

import me.gravityio.orbit.commands.OrbitCommand;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class ClientProxy extends CommonProxy {
    private static final KeyBinding ACTIVATE_ORBIT_BIND = new KeyBinding("bind", Keyboard.KEY_R, "category");

    public static ClientProxy INSTANCE;

    public final OrbitSettings orbitSettings = new OrbitSettings();

    private DummyEntity cameraEntity;
    public boolean orbit = false;
    private float prevFov;
    private volatile long endTime = 0;

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.


    @Override
    public void init(FMLInitializationEvent event) {
        INSTANCE = this;

        ClientRegistry.registerKeyBinding(ACTIVATE_ORBIT_BIND);
        ClientCommandHandler.instance.registerCommand(new OrbitCommand());
        FMLCommonHandler.instance().bus().register(this);
    }

    public static void sendMessage(String message, Object... args) {
        message = String.format(message, args);
        String[] arr = message.split("\n");
        if (arr.length == 0) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        } else {
            for (String str : arr) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
            }
        }
    }

    public static Vector3f vec3tof(Vec3 v) {
        return new Vector3f((float) v.xCoord, (float) v.yCoord, (float) v.zCoord);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        while (ACTIVATE_ORBIT_BIND.isPressed()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            EntityClientPlayerMP player = minecraft.thePlayer;
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                MovingObjectPosition hit = player.rayTrace(5, 0);
                this.orbitSettings.setOrbitPos(new Vector3f(hit.blockX + 0.5f, hit.blockY + 0.5f, hit.blockZ + 0.5f));
                sendMessage("Orbit Position set to (%.2f, %.2f, %.2f)", this.orbitSettings.orbitPos.x, this.orbitSettings.orbitPos.y, this.orbitSettings.orbitPos.z);
                return;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
                if (this.orbitSettings.orbitPos == null) {
                    sendMessage("Set an orbit position first! (Hold ctrl with this keybind)");
                    return;
                }
                Vector3f pos = new Vector3f((float) player.posX, (float) player.posY, (float) player.posZ);
                this.orbitSettings.setStartAngle(pos);
                this.orbitSettings.setStartDistance(pos);
                this.orbitSettings.setHeightOffset(pos);
                sendMessage(
                    String.format(
                        "Starting angle set to (%.1f), distance set to (%.1f), height offset set to (%.1f)",
                        Math.toDegrees(this.orbitSettings.startAngleRad), this.orbitSettings.orbitDistance, this.orbitSettings.heightOffset
                    )
                );
                return;
            }

            if (this.orbit) {
                this.setOrbit(false);
                return;
            }

            this.setOrbit(true);
        }
    }

    public void setOrbit(boolean orbit) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (this.cameraEntity == null) {
            this.cameraEntity = new DummyEntity(mc, player.worldObj, mc.getSession(), player.sendQueue, player.getStatFileWriter());
        }

        if (orbit) {
            if (this.orbitSettings.orbitPos == null) {
                sendMessage("Set an orbit position! (Hold ctrl while holding '%s') or /orbit pos <x> <y> <z>", Keyboard.getKeyName(ACTIVATE_ORBIT_BIND.getKeyCode()));
                return;
            }
            sendMessage("Orbiting (%.2f, %.2f, %.2f) for %.2f seconds", this.orbitSettings.orbitPos.x, this.orbitSettings.orbitPos.y, this.orbitSettings.orbitPos.z, this.orbitSettings.orbitTimeMs / 1000f);

            mc.renderViewEntity = this.cameraEntity;
            this.cameraEntity.setWorld(player.worldObj);
            this.cameraEntity.setPos((float) player.posX, (float) player.posY, (float) player.posZ);
            this.endTime = System.currentTimeMillis() + this.orbitSettings.orbitTimeMs;
            this.prevFov = mc.gameSettings.fovSetting;
            mc.gameSettings.fovSetting = this.orbitSettings.orbitFov;
        } else {
            sendMessage("Stopped Orbiting!");
            mc.renderViewEntity = player;
            this.endTime = 0;
            mc.gameSettings.fovSetting = this.prevFov;
        }

        this.orbit = orbit;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderEvent(TickEvent.RenderTickEvent event) {
        if (!this.orbit) return;

        this.orbitSettings.orbitStep(this.endTime, this.cameraEntity);
        if (System.currentTimeMillis() >= this.endTime) {
            this.setOrbit(false);
        }
    }


}
