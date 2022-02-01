package customhardcore.customhardcore.Generic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Helpers.Logger;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class FileHandler {

    public static String getSaveFileDir(CustomHardcore instance, Player player, String folderName) {
        return getSaveFileDir(instance, player.getUniqueId(), folderName);
    }

    public static String getSaveFileDir(CustomHardcore instance, UUID id, String folderName) {
        return instance.getDataFolder().getAbsolutePath()+"\\"+folderName+"\\"+id+".json";
    }

    public static String getSaveFolderDir(CustomHardcore instance, String folderName) {
        return instance.getDataFolder().getAbsolutePath()+"\\"+folderName;
    }

    public static String getSaveFolder(String saveFileDir) {
        if (saveFileDir == null || !saveFileDir.contains("\\"))
            return null;
        return saveFileDir.substring(0, saveFileDir.lastIndexOf("\\"));
    }

    public static File createFile(String directory) {
        try {
            File file = new File(directory);
            if (!file.exists()) {
                String folder = getSaveFolder(file.getAbsolutePath());
                Logger.info("Attempting to create folder: " + folder);
                Files.createDirectories(Paths.get(folder));

                if (!file.createNewFile()) {
                    Logger.error("Failed to create new file: " + directory);
                    return null;
                }
            }
            return file;
        } catch (IOException e) {
            Logger.error("Failed to create new file: " + directory, e);
            return null;
        }
    }

    public static <T extends Serializable, F extends File> boolean writeToFile(T object, F file) {
        if (!file.exists())
            createFile(file.getAbsolutePath());

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(object, writer);
        } catch (IOException e) {
            Logger.error("Failed to write data to file", e);
            return false;
        }
        return true;
    }

    public static <T extends Serializable> T readFromFile(String fileDir, Class<T> clazz) {
        File file = new File(fileDir);
        if (!file.exists()) {
            Logger.error("Could not read from file - file does not exist");
            return null;
        }

        Gson gson = new Gson();
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            Logger.error("Could not read from file", e);
            return null;
        }
    }

}
