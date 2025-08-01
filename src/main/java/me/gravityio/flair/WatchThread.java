package me.gravityio.flair;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class WatchThread extends Thread{

    private final Path path;
    private long lastTrigger;

    public WatchThread(File path) {
        this.path = path.toPath();
    }

    @Override
    public void run() {
        try (WatchService watch = FileSystems.getDefault().newWatchService()) {
            this.path.register(watch, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey key;
                try {
                    key = watch.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    if (System.currentTimeMillis() - this.lastTrigger < 500) continue;
                    if (fileName.toString().endsWith(FlairConfig.CONFIG_PATH)) {
                        Flair.LOGGER.info("Flair config changed, reloading...");
                        Minecraft.getMinecraft().func_152344_a(FlairConfig::loadConfig);
                    } else if (fileName.endsWith(FlairConfig.SOUNDMAP_PATH)) {
                        Flair.LOGGER.info("Flair soundmap changed, reloading...");
                        Minecraft.getMinecraft().func_152344_a(FlairConfig::loadSoundMap);
                    } else {
                        continue;
                    }
                    this.lastTrigger = System.currentTimeMillis();
                }

                if (!key.reset()) break;
            }
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to watch config folder", e);
        }
    }
}
