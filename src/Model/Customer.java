package Model;
public class Customer extends User {
    private String email;
    private String phone;


    public Customer(String id, String name, String password, String registeredAt, String email, String phone) {
        super(id, name, password, registeredAt, "customer");
        this.email = email;
        this.phone = phone;
    }

    /**
     * Constructor with default values
     */
    public Customer() {
        super();
        this.email = "example@gmail.com";
        this.phone = "0123456789";
    }

    @Override
    public String toString() {
        return "{" +
                " user_id\": \"" + id + "\"," +
                " ,user_name\": \"" + name + "\"," +
                " ,user_password\": \"" + password + "\"," +
                " ,user_register_time\": \"" + registeredAt + "\"," +
                " ,user_role\": \"" + role + "\"," +
                " ,user_email\": \"" + email + "\"," +
                " ,user_mobile\": \"" + phone + "\"" +
                "}";
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
