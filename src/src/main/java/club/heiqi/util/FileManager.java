package club.heiqi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class FileManager {
    public static ClassLoader classLoader = FileManager.class.getClassLoader();

    public static File getFile(String resPath) {
        return new File(Objects.requireNonNull(classLoader.getResource(resPath)).getFile());
    }

    public static String readFile(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件时发生错误: " + e.getMessage(), e);
        }
        return contentBuilder.toString().trim();
    }
}
