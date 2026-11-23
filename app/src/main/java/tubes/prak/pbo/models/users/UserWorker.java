package tubes.prak.pbo.models.users;

import tubes.prak.pbo.ENUM.Role;

public class UserWorker extends UserStaff {

    public UserWorker(int id, String nama, String email, String userName, String password) {
        super(id, nama, email, Role.WORKER, userName, password);
    }

    @Override
    public boolean canAccessBom() { return true; } // Ubah ke false jika Worker tidak boleh akses BOM

    @Override
    public boolean canAccessFinance() { return false; }

    @Override
    public boolean canAccessPurchasing() { return false; }
}