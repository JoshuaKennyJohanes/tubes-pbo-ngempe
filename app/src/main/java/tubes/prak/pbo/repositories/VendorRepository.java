package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Vendor;

public class VendorRepository {

    public List<Vendor> findAllVendor() throws SQLException {
        String sql = """
            SELECT id_vendor, nama_vendor, no_telp, alamat_vendor
            FROM public.vendor
            ORDER BY nama_vendor ASC
            """;

        List<Vendor> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Vendor(
                        rs.getInt("id_vendor"),
                        rs.getString("nama_vendor"),
                        rs.getString("no_telp"),
                        rs.getString("alamat_vendor")
                ));
            }
        }
        return list;
    }

    public boolean isNamaVendorExists(String namaVendor) throws SQLException {
        String sql = """
            SELECT 1 FROM public.vendor
            WHERE LOWER(TRIM(nama_vendor)) = LOWER(TRIM(?))
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, namaVendor);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insertVendor(Vendor vendor) throws SQLException {
        String sql = """
            INSERT INTO public.vendor (nama_vendor, no_telp, alamat_vendor)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vendor.getNamaVendor());
            stmt.setString(2, vendor.getNoTelp());
            stmt.setString(3, vendor.getAlamatVendor());
            stmt.executeUpdate();
        }
    }
}
