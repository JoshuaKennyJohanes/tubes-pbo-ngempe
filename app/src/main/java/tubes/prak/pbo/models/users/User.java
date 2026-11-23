package tubes.prak.pbo.models.users;

import java.sql.SQLException;
import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.Interfaces.UserPanelVisitor;

public abstract class User {
    protected int id;
    protected String nama;
    protected String email;
    protected Role role;
    protected String userName;
    protected String password;

    public User(int id, String nama, String email, Role role, String userName, String password) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.role = role;
        this.userName = userName;
        this.password = password;
    }

    public abstract String getDefaultRoute();
    public abstract void accept(UserPanelVisitor visitor) throws SQLException;

    public int getId() { return this.id; }
    public String getNama() { return this.nama; }
    public String getEmail() { return this.email; }
    public Role getRole() { return this.role; }
    public String getUserName() { return this.userName; }
    public String getPassword() { return this.password; }
}