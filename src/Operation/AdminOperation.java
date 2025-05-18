package Operation;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminOperation extends UserOperation {
    private static final String FILE_PATH ="data/users.json";
    private static AdminOperation instance;


    private AdminOperation() {}

    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }


    public boolean registerAdmin(String adminName, String adminPassword) {
        // ktra tên admin ko dc trống và phải dài ít nhất 5 kí tự
        if (adminName == null || adminName.length() < 5) {
            return false;
        }

        //ktra password có đúng ko
        if (!UserOperation.getInstance().validatePassword(adminPassword)) {
            return false;
        }

        //ktra username đã tồn tại chua nếu chưa -> tạo tk
        if (UserOperation.getInstance().checkUsernameExist(adminName)) {
            return false;
        }


        String userId = UserOperation.getInstance().generateUserId();// tạo userID mới
        String encryptedPassword = UserOperation.getInstance().encryptPassword(adminPassword);// mã hóa pass
        String registerTime = LocalDateTime.now()// lưu tgian dki là tgian hiện tại
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

        String jsonRecord = String.format(
                "{\"user_id\":\"%s\", " +
                        "\"user_name\":\"%s\", " +
                        "\"user_password\":\"%s\", " +
                        "\"user_register_time\":\"%s\", " +
                        "\"user_role\":\"admin\"}",
                userId, adminName, encryptedPassword, registerTime
        );//tạo thông tin thành chuỗi json lưu vào file

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(jsonRecord);// ghi dòng json xuống file
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
