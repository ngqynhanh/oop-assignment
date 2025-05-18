package DBUtil;

import Model.User;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDB {
    private static UserDB instance = null;
    private static final String FILE_PATH = "src/data/users.json";
    private final List<User> userList = loadUsers(FILE_PATH);

    private UserDB() {
    }

    public static UserDB getInstance() {
        if (instance == null) {
            instance = new UserDB();
        }
        return instance;
    }

    public List<User> getUsers() {
        return userList;
    }

    public List<User> loadUsers(String FILE_PATH) {
        List<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                JSONObject json = new JSONObject(line);
                String userId = json.optString("user_id", null);
                String userName = json.optString("user_name", null);
                String userPassword = json.optString("user_password", null);
                String userRegisterTime = json.optString("user_register_time", null);
                String userRole = json.optString("user_role", null);

                if (userId != null && userName != null && userPassword != null && userRegisterTime != null && userRole != null) {
                    users.add(new User(userId, userName, userPassword, userRegisterTime, userRole));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void saveUsers(User user) {
        // Implement save logic if needed
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/data/users.json", true))) {
            String jsonString = user.toString();
            bw.write(jsonString);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
