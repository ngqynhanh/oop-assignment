package Operation;

import Model.Customer;
import Model.CustomerListResult;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerOperation extends UserOperation {
    private static final String FILE_PATH = "data/users.json";
    private static CustomerOperation instance;

    private CustomerOperation() {
        super();
    }

    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }


    // viết theo dạng username@domain.extension
    public boolean validateEmail(String userEmail) {        //[\w.-] ( chữ cái, số, dấu  _, ., -)
        return userEmail != null && userEmail.matches("^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$");
    }                                                       //[a-zA-Z\d.-]chữ cái, số, dấu ., -
    //[a-zA-Z]{2,6} chữ cái từ 2 đến 6 chũ

    // sdt 10 số bắt đầu từ 04 or 03
    public boolean validateMobile(String userMobile) {
        return userMobile != null && userMobile.matches("^(04|03)\\d{8}$");
    }


    // đăng kí customer mới
    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile) {
        UserOperation userOp = UserOperation.getInstance();

        if (!userOp.validateUsername(userName) ||
                !userOp.validatePassword(userPassword) ||
                !validateEmail(userEmail) ||
                !validateMobile(userMobile)) {
            return false;
        }

        if (userOp.checkUsernameExist(userName)) {
            return false;
        }

        String userId = userOp.generateUserId();
        String encryptedPassword = userOp.encryptPassword(userPassword);
        String registerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

        String jsonRecord = String.format(
                "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", " +
                        "\"user_register_time\":\"%s\", \"user_role\":\"customer\", " +
                        "\"user_email\":\"%s\", \"user_mobile\":\"%s\"}",
                userId, userName, encryptedPassword, registerTime, userEmail, userMobile
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(jsonRecord);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    //dùng để cập nhật thông tin ví dụ như khi muốn đổi mật phẩu, email, sdt
    public boolean updateProfile(String attributeName, String value, Customer customer) {
        if (attributeName == null || value == null || customer == null) return false;

        switch (attributeName.toLowerCase()) {//toLowerCase() giúp so sánh ko phân biệt chữ hoa/thường
            case "user_name":
                if (!UserOperation.getInstance().validateUsername(value)) {
                    return false;
                }
                customer.setName(value);// hợp lệ thì gọi customer.setName(value) để gán lại tên mới
                break;

            case "user_password":
                if (!UserOperation.getInstance().validatePassword(value)) {
                    return false;
                }
                customer.setPassword(UserOperation.getInstance().encryptPassword(value));
                break;

            case "user_email":
                if (!validateEmail(value)) {
                    return false;
                }
                customer.setEmail(value);
                break;

            case "user_mobile":
                if (!validateMobile(value)) {
                    return false;
                }
                customer.setPhone(value);
                break;
            default:
                return false;
        }
        return true;
    }


    // XOA CUSTOMER DUA TREN ID
    //    1.đọc từng dòng
//        2.ghi lại tất cả dòng ko bị xóa vào file tạm
//        3.Ghi xong thì xóa file cũ → đổi tên file tạm thành file gốc
    public boolean deleteCustomer(String customerId) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp_users.json");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile)); // đọc file gốc
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));// tự tạo file temp
            String line;
            boolean deleted = false; // biến delete dùng để ghi nhớ có xóa ai ko

            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_id\":\"" + customerId + "\"") && line.contains("\"user_role\":\"customer\"")) {// kiểm tra xem có đúng là customer cần xóa ko
                    deleted = true;// đúng là ng cần xóa gán delete = true
                    continue; // chỉ bỏ qua dòng nếu user_id khớp với tham số truyền vào
                }
                writer.write(line);//nếu dòng ko bị xóa ghi nó vào temp_users.json
                writer.newLine();//
            }
            writer.close();
            reader.close();

            if (inputFile.delete() && tempFile.renameTo(inputFile)) {//đổi tên file tạm thành users.json (ghi đè)
                return deleted;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // trả về danh sách khách hàng theo từng trang
    // mỗi trang chứa tối đa 10 khách hàng từ file user.json

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();//danh sách lưu các đối tượng customer sẽ trả về cho trang hiện tại
        int totalCustomers = 0; //tổng số customer đếm được trong file
        int pageSize = 10; //	Mỗi trang chứa tối đa 10 khách hàng
        int start = (pageNumber - 1) * pageSize;// customer bắt đầu từ bao nhiều  ví dụ có 25 khách và gọi từ trang 2 start = (2 - 1) * 10 = 10
        int end = start + pageSize;// đến bao nhiêu vd end = 10 + 10 = 20

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_role\":\"customer\"")) {// chỉ xử lí các dòng có "user_role":"customer"
                    totalCustomers++;
                    if (totalCustomers > start && totalCustomers <= end) {
                        // totalCustomer > 10 && totalCustomer <= 20 ( lấy khách hàng từ 11 đến 20)

                        String[] tokens = line.replace("{", "").replace("}", "").split(",");
                        String id = "", name = "", password = "", registerTime = "", email = "", mobile = "";
                        for (String token : tokens) {
                            token = token.trim();
                            if (token.contains("\"user_id\"")) id = token.split(":")[1].replace("\"", "").trim();
                            else if (token.contains("\"user_name\""))
                                name = token.split(":")[1].replace("\"", "").trim();
                            else if (token.contains("\"user_password\""))
                                password = token.split(":")[1].replace("\"", "").trim();
                            else if (token.contains("\"user_register_time\""))
                                registerTime = token.split(":")[1].replace("\"", "").trim();
                            else if (token.contains("\"user_email\""))
                                email = token.split(":")[1].replace("\"", "").trim();
                            else if (token.contains("\"user_mobile\""))
                                mobile = token.split(":")[1].replace("\"", "").trim();
                        }
                        customers.add(new Customer(id, name, password, registerTime, email, mobile));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int totalPages = (int) Math.ceil(totalCustomers / 10.0); //tính tổng số trang cần có để hiển thị hết toàn bộ customer, Math.ceil làm tròn lên lỡ có dư thì thêm 1 trang
        return new CustomerListResult(customers, pageNumber, totalPages);
        // trả về 1. danh sách các customer của trang yêu cầu
        //        2. trang hiện tại mà bạn đang xem
        //        3. tổng số trang dựa trên customer trong file
    }



    // XÓA TOAN BO CUSTOMER
    public void deleteAllCustomers() {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp_users.json");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains("\"user_role\":\"customer\"")) { //giữ lại tất cả dòng không phải customer
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
            reader.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateCustomerPhone() {
        Random random = new Random();
        StringBuilder phone = new StringBuilder("0");
        for (int i = 0; i < 9; i++) {
            phone.append(random.nextInt(10));
        }
        return phone.toString();
    }

}