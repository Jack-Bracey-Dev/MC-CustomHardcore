package customhardcore.customhardcore.Helpers.Levelling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import customhardcore.customhardcore.Helpers.Logger;

import java.io.*;

public class FileHandler {

    public static File createFile(String directory) {
        try {
            File file = new File(directory);
            if (!file.exists()) {
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
