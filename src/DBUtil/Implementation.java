package DBUtil;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Implementation {
//    public static String readFileToString(String filePath) {
//        StringBuilder content = new StringBuilder();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return content.toString();
//    }

    public static <T> List<T> loadFromFile(String filePath, Class<T> clazz) {
        List<T> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                T obj = parseJsonObject(json, clazz);
                if (obj != null) list.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static <T> T parseJsonObject(JSONObject json, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (var field : clazz.getFields()) {
                if (json.has(field.getName())) {
                    field.set(instance, json.getString(field.getName()));
                }
            }

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
