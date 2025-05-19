package Model;
public abstract class User {
    protected String id;
    protected String name;
    protected String password;
    protected String registeredAt;
    protected String role;

    public User(String id, String name, String password, String registeredAt, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.registeredAt = registeredAt;
        this.role = role;
    }

    public User() {
        this("u_0000000000", "unknown", "^^$$", "01-01-2025_00:00:00", "customer");
    }


    @Override
    public String toString() {
        return "{" +
                "\"user_id\":\"" + id + "\"," +
                "\"user_name\":\"" + name + "\"," +
                "\"user_password\":\"" + password + "\"," +
                "\"user_register_time\": \"" + registeredAt + "\"," +
                "\"user_role\":\"" + role + "\"," +
                "}";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name; }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }
    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}

