package tubes.prak.pbo.models.users;

import java.sql.SQLException;
import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.Interfaces.UserPanelVisitor;
import tubes.prak.pbo.util.Router;

public abstract class UserStaff extends User {

    public UserStaff(int id, String nama, String email, Role role, String userName, String password) {
        super(id, nama, email, role, userName, password);
    }

    @Override
    public String getDefaultRoute() {
        return Router.WAREHOUSE_PAGE;
    }

    @Override
    public void accept(UserPanelVisitor visitor) throws SQLException {
        visitor.buildForStaff(this);
    }

    // Hak akses dasar semua staff
    public boolean canAccessWarehouse() { return true; }
    public boolean canAccessProduction() { return true; }
    public boolean canAccessSalesOrder() { return true; }

    // Hak akses spesifik yang wajib ditentukan oleh turunan Staff
    public abstract boolean canAccessBom();
    public abstract boolean canAccessFinance();
    public abstract boolean canAccessPurchasing();
}