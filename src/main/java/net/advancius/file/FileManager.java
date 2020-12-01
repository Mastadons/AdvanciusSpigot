package net.advancius.file;

import net.advancius.AdvanciusSpigot;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

@FlagManager.FlaggedClass
public class FileManager {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = -5)
    public static void load() {
        AdvanciusSpigot.getInstance().getDataFolder().mkdirs();
    }

    public static File getSourceFile(String path) {
        try {
            File file = File.createTempFile(path.split("\\.")[0], path.split("\\.")[1]);
            Files.copy(getSourceFileStream(path), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream getSourceFileStream(String path) {
        return AdvanciusSpigot.getInstance().getResource(path);
    }

    public static File getServerFile(String path) {
        return new File(AdvanciusSpigot.getInstance().getDataFolder(), path);
    }

    public static void cloneFile(File origin, File target) {
        try {
            if (!origin.exists()) throw new IllegalArgumentException("Origin file does not exist.");
            target.createNewFile();
            Files.copy(origin.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public static void cloneDirectory(Path source, Path target, CopyOption... options) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static File getServerFile(String serverPath, String sourcePath) {
        File serverFile = getServerFile(serverPath);
        if (!serverFile.exists()) cloneFile(getSourceFile(sourcePath), serverFile);
        return serverFile;
    }
}
