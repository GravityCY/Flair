package me.gravityio.flair.config;

import me.gravityio.flair.Flair;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class WatchThread extends Thread {
    public static final int CHECK_FREQUENCY = 1000;
    private final Path path;
    private long lastTrigger;
    private long lastSize;
    private String lastHash;

    public static String hashFile(Path path) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ignored) {
            return null;
        }

        try (InputStream fis = Files.newInputStream(path);
             DigestInputStream dis = new DigestInputStream(fis, digest)) {
            while (dis.read() != -1) ;
        }
        byte[] hash = digest.digest();
        return Base64.getEncoder().encodeToString(hash);
    }

    public WatchThread(File path) {
        this.path = path.toPath();
    }

    public boolean isSame(Path path) {
        try {
            long size = Files.size(path);
            String hashFile = hashFile(path);
            if (size == this.lastSize && Objects.equals(this.lastHash, hashFile))
                return true;

            this.lastSize = size;
            this.lastHash = hashFile;

            return false;
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to get file hash", e);
            return false;
        }
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
                    if (System.currentTimeMillis() - this.lastTrigger < CHECK_FREQUENCY) continue;
                    if (!fileName.endsWith(FlairConfig.CONFIG_FILENAME)) continue;
                    if (this.isSame(this.path.resolve(fileName))) continue;

                    Flair.LOGGER.info("Flair soundmap changed, reloading...");
                    Minecraft.getMinecraft().func_152344_a(FlairConfig::load);
                    this.lastTrigger = System.currentTimeMillis();
                }

                if (!key.reset()) break;
            }
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to watch config folder", e);
        }
    }
}
