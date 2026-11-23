package tubes.prak.pbo.models.users;

import tubes.prak.pbo.ENUM.Role;

public class UserManager extends UserStaff {

    public UserManager(int id, String nama, String email, String userName, String password) {
        super(id, nama, email, Role.MANAGER, userName, password);
    }

    @Override
    public boolean canAccessBom() { return true; }

    @Override
    public boolean canAccessFinance() { return true; }

    @Override
    public boolean canAccessPurchasing() { return true; }
}