
public class Admin extends User {

    public Admin(String id, String name, String password, String registeredAt) {
        super(id, name, password, registeredAt, "admin");
    }

    public Admin() {
        super("u_0000000001", "admin", "^^admin$$", "01-01-2025_00:00:00", "admin");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

