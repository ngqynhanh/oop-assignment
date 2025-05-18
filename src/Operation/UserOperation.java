package Operation;

import Model.User;
import java.util.*;
import java.io.*;

 public class UserOperation {
     private static UserOperation instance;
     private final String FILE_PATH = "src/data/users.json";

     UserOperation() {}

     public static UserOperation getInstance() {
         if (instance == null) {
             instance = new UserOperation();
         }
         return instance;
     }

     public String generateUserId(){
         int num = new Random().nextInt(1_000_000_000);
         return "u_" + String.format("%010d", num);
     }

    
     public String encryptPassword(String password) {
         //chuỗi tất cả kí tự được dùng để tạo chuỗi ngẫu nhiên
         String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

         // sinh chuỗi ngẫu nhiên
         StringBuilder randomString = new StringBuilder(); //vd của 1 randomString X1Y2Z3
         Random random = new Random();

         // sinh chuỗi có độ dài gấp 2 lần password nhập vào
         for (int i = 0; i < password.length() * 2; i++) {
             randomString.append(chars.charAt(random.nextInt(chars.length())));
         }

         // TRỘN CHUỖI VÀ PASSWORD
         StringBuilder encrypted = new StringBuilder("^^");
         for (int i = 0; i < password.length(); i++) {  // với mỗi kí tự tại password.CharAt(i)
             encrypted.append(randomString.charAt(i * 2));//nối (append) vị trí vd i(0) thì 0 * 2 = 0 (X)
             encrypted.append(randomString.charAt(i * 2 + 1));//nối với vd 0 * 2 + 1 = 1 (1)
             encrypted.append(password.charAt(i)); // gắn một kí tự thật từ mật khẩu (a) -> X1a
         }
         encrypted.append("$$");
         return encrypted.toString();
     }


     public String decryptPassword(String encryptedPassword) {
         if (!encryptedPassword.startsWith("^^") || !encryptedPassword.endsWith("$$")) return "";// nếu chuỗi ko có "^^" va "$$" -> ko hợp lệ -> rỗng
         String content = encryptedPassword.substring(2, encryptedPassword.length() - 2);//bỏ ^^ và $$
         StringBuilder originalPassword = new StringBuilder();

         for (int i = 0; i < content.length(); i += 3) {// vòng lặp lấy lại giá trị thật
             originalPassword.append(content.charAt(i + 2));//  kí tự thứ 3 là kí tự thật vd X1a
         }
         return originalPassword.toString();
     }


     public boolean checkUsernameExist(String userName) {
         try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
             String line; //
             while ((line = reader.readLine()) != null) { // lặp từng dòng đọc và kiếm user cho đến hết file
                 if (line.contains("\"user_name\":\"" + userName + "\"")) {
                     return true; // user name tồn tại trả về true
                 }
             }
         } catch (IOException e) { //dùng IOException chính xác hơn cho việc đọc file
             e.printStackTrace();
         }
         return false; // không tìm thấy user name nào thì trả về false
     }


     // Kiểm tra username phải có ít nhất 5 ký tự và chỉ chứa chữ cái hoặc dấu gạch dưới
     public boolean validateUsername(String userName) {
         if (userName == null) return false;
         if (userName.length() < 5) return false;
         return userName.matches("[a-zA-Z_]+$");
     }


     // Kiểm tra password phải có ít nhất 5 ký tự, chứa ít nhất 1 chữ cái và 1 số
     public boolean validatePassword(String password) {
         if (password == null) {
             return false;
         }
         if (password.length() < 5) {
             return false;
         }
         boolean hasLetter = password.matches(".*[a-zA-Z].*"); // kiểm tra xem có ít nhất 1 chữ cái không chữ thường hay hoa đều dc
         boolean hasDigit = password.matches(".*\\d.*");// có ít nhất 1 số ko ( 0 đến 9)
         return hasLetter && hasDigit;
     }


     public User login(String username, String password) {
         try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 if (line.contains("\"user_name\":\"" + username + "\"")) { // kểm tra tên username trong login có trong file ko
                     String[] tokens = line.replace("{", "").replace("}", "").split(",");// loại bỏ dấu ngoặc nhọn để chỉ lấy nội dung
                     String id = "", encrypted = "", registerTime = "", role = "";// được gán bằng dòng lặp bên dưới

                     for (String token : tokens) {
                         token = token.trim();// dùng trim() để đảm bảo ko có khoảng trắng -> ko bị lỗi khi đọc
                         if (token.contains("\"user_id\"")) id = token.split(":")[1].replace("\"", "").trim(); // loại bỏ các dấu và lấy id gán vào id
                         else if (token.contains("\"user_password\"")) encrypted = token.split(":")[1].replace("\"", "").trim();
                         else if (token.contains("\"user_register_time\"")) registerTime = token.split(":")[1].replace("\"", "").trim();
                         else if (token.contains("\"user_role\"")) role = token.split(":")[1].replace("\"", "").trim();
                     }

                     if (decryptPassword(encrypted).equals(password)) {
                         return new User(id, username, encrypted, registerTime, role);
                     }
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
         return null;
     }

     public String generateUserName() {
            String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder randomString = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 5 + random.nextInt(6); i++) {
                randomString.append(chars.charAt(random.nextInt(chars.length())));
            }
            if (UserOperation.getInstance().validateUsername(randomString.toString()) &&
                    !UserOperation.getInstance().checkUsernameExist(randomString.toString()))
                return randomString.toString();
            else
                return generateUserName();
     }

 }


