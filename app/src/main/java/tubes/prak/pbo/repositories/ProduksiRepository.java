package tubes.prak.pbo.repositories;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Bom;
import tubes.prak.pbo.models.DetailProduksi;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.models.RawMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProduksiRepository {

    // 1. Insert Header Produksi + Snapshot Detail Produksi secara atomik
    public static void insert(Produksi produksi, Bom bom) throws SQLException {
        String sqlProduksi = """
            INSERT INTO public.produksi
            (id_produk, created_by, tanggal_produksi, status_produksi, jumlah_produksi, total_hpp_produksi)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String sqlDetail = """
            INSERT INTO public.detail_produksi
            (id_produksi, id_raw_material, nama_material, qty_per_unit, total_kebutuhan, harga_rata2_saat_itu, subtotal_hpp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            int generatedIdProduksi;

            // A. Simpan ke tabel produksi & ambil id_produksi yang baru terbuat
            try (PreparedStatement stmtProd = conn.prepareStatement(sqlProduksi, Statement.RETURN_GENERATED_KEYS)) {
                stmtProd.setInt(1, produksi.getIdProduk());
                stmtProd.setInt(2, produksi.getCreatedBy());
                stmtProd.setDate(3, new Date(produksi.getTanggalProduksi().getTime()));
                stmtProd.setString(4, produksi.getStatusProduksi().name());
                stmtProd.setDouble(5, produksi.getJumlahProduksi());
                stmtProd.setDouble(6, produksi.getTotalHppProduksi());
                stmtProd.executeUpdate();

                try (ResultSet rsKeys = stmtProd.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        generatedIdProduksi = rsKeys.getInt(1);
                        produksi.setIdProduksi(generatedIdProduksi);
                    } else {
                        throw new SQLException("Gagal mendapatkan ID produksi baru.");
                    }
                }
            }

            // B. Copy-paste snapshot nilai BOM & RawMaterial ke detail_produksi
            try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail)) {
                double jumlahProduksi = produksi.getJumlahProduksi();

                for (Map.Entry<RawMaterial, Double> entry : bom.getKebutuhanMaterial().entrySet()) {
                    RawMaterial rm = entry.getKey();
                    double qtyPerUnit = entry.getValue();
                    double totalKebutuhan = qtyPerUnit * jumlahProduksi;
                    double hargaRata2 = rm.getHargaRata2();
                    double subtotalHpp = totalKebutuhan * hargaRata2;

                    stmtDetail.setInt(1, generatedIdProduksi);
                    stmtDetail.setInt(2, rm.getId());
                    stmtDetail.setString(3, rm.getNama());
                    stmtDetail.setDouble(4, qtyPerUnit);
                    stmtDetail.setDouble(5, totalKebutuhan);
                    stmtDetail.setDouble(6, hargaRata2);
                    stmtDetail.setDouble(7, subtotalHpp);
                    stmtDetail.addBatch();
                }
                stmtDetail.executeBatch();
            }

            conn.commit(); // Simpan permanen jika keduanya berhasil
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Batalkan semua jika salah satu gagal
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // 2. Ambil data historis dari detail_produksi (bukan dari tabel bom lagi)
    public static List<DetailProduksi> findDetailsByProduksiId(int idProduksi) throws SQLException {
        List<DetailProduksi> details = new ArrayList<>();
        String sql = """
            SELECT id_detail_produksi, id_produksi, id_raw_material, nama_material,
                   qty_per_unit, total_kebutuhan, harga_rata2_saat_itu, subtotal_hpp
            FROM public.detail_produksi
            WHERE id_produksi = ?
            ORDER BY id_detail_produksi ASC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduksi);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(new DetailProduksi(
                            rs.getInt("id_detail_produksi"),
                            rs.getInt("id_produksi"),
                            rs.getInt("id_raw_material"),
                            rs.getString("nama_material"),
                            rs.getDouble("qty_per_unit"),
                            rs.getDouble("total_kebutuhan"),
                            rs.getDouble("harga_rata2_saat_itu"),
                            rs.getDouble("subtotal_hpp")
                    ));
                }
            }
        }
        return details;
    }

    public static List<Produksi> findAll() throws SQLException {
        List<Produksi> list = new ArrayList<>();
        String sql = """
        SELECT id_produksi, id_produk, created_by, tanggal_produksi,
               status_produksi, jumlah_produksi, total_hpp_produksi
        FROM public.produksi
        ORDER BY id_produksi DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Konversi varchar database ke Enum StatusProduksi
                StatusProduksi status = StatusProduksi.valueOf(
                        rs.getString("status_produksi").toUpperCase()
                );

                Produksi produksi = new Produksi(
                        rs.getInt("id_produksi"),
                        rs.getInt("id_produk"),
                        rs.getInt("created_by"),
                        rs.getDate("tanggal_produksi"),
                        status,
                        rs.getDouble("jumlah_produksi"),
                        rs.getDouble("total_hpp_produksi")
                );
                list.add(produksi);
            }
        }
        return list;
    }

    public static void mulaiProduksi(int idProduksi, List<DetailProduksi> daftarDetail) throws SQLException {
        String sqlUpdateStatus = """
        UPDATE public.produksi
        SET status_produksi = ?
        WHERE id_produksi = ?
        """;

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Validasi ulang & potong stok setiap raw material berdasarkan detail_produksi
            for (DetailProduksi detail : daftarDetail) {
                RawMaterialRepository.kurangiStokMaterial(
                        conn,
                        detail.getIdRawMaterial(),
                        detail.getNamaMaterial(),
                        detail.getTotalKebutuhan()
                );
            }

            // 2. Ubah status jadwal produksi menjadi ONGOING
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateStatus)) {
                stmt.setString(1, StatusProduksi.ONGOING.name());
                stmt.setInt(2, idProduksi);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static void selesaikanProduksi(Produksi produksi, List<DetailProduksi> daftarDetail) throws SQLException {
        String sqlUpdateStatus = """
        UPDATE public.produksi
        SET status_produksi = ?
        WHERE id_produksi = ?
        """;

        // Hitung total HPP langsung dari snapshot detail_produksi
        double totalHppDariDetail = 0.0;
        for (DetailProduksi detail : daftarDetail) {
            totalHppDariDetail += detail.getSubtotalHpp();
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Tambah stok produk jadi & perbarui harga rata-rata produk
            ProdukRepository.tambahStokDanUpdateHargaRata2(
                    conn,
                    produksi.getIdProduk(),
                    produksi.getJumlahProduksi(),
                    totalHppDariDetail
            );

            // 2. Ubah status jadwal produksi menjadi FINISHED
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateStatus)) {
                stmt.setString(1, StatusProduksi.FINISHED.name());
                stmt.setInt(2, produksi.getIdProduksi());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}