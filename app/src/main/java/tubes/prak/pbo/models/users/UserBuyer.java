package tubes.prak.pbo.models.users;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.Interfaces.UserPanelVisitor;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.util.Router;

public class UserBuyer extends User {
    private final List<BarangKeranjang> keranjangUser = new ArrayList<>();

    public UserBuyer(int id, String nama, String email, String userName, String password) {
        super(id, nama, email, Role.PEMBELI, userName, password);
    }

    @Override
    public String getDefaultRoute() {
        return Router.ETALASE_PAGE;
    }

    @Override
    public void accept(UserPanelVisitor visitor) throws SQLException {
        visitor.buildForBuyer(this);
    }

    public List<BarangKeranjang> setKosongKeranjang() {
        keranjangUser.clear();
        return this.keranjangUser;
    }

    public List<BarangKeranjang> getBarangKeranjang() {
        return this.keranjangUser;
    }

    public void tambahkeKeranjang(BarangKeranjang item) {
        if (item == null) return;
        for (BarangKeranjang existing : keranjangUser) {
            if (existing.getId_produk() == item.getId_produk()) {
                existing.tambahJumlah(item.getJumlah(), item.getHargaTotal());
                return;
            }
        }
        keranjangUser.add(item);
    }

    public int getJumlahDiKeranjang(int idProduk) {
        for (BarangKeranjang item : keranjangUser) {
            if (item.getId_produk() == idProduk) {
                return item.getJumlah();
            }
        }
        return 0;
    }

    public void hapusDariKeranjang(BarangKeranjang item) {
        if (item != null) {
            keranjangUser.remove(item);
        }
    }
}