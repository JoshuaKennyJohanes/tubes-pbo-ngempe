package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Produk;


public class ProdukRepository {

    public static List<Produk> findAllProduk() throws SQLException {
        List<Produk> allList = new ArrayList<>();

        String sql = """
            SELECT *
            FROM public.produk
            ORDER BY id_produk ASC
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Produk produk = new Produk(
                        resultSet.getInt("id_produk"),
                        resultSet.getString("nama_produk"),
                        resultSet.getString("satuan_produk"),
                        resultSet.getDouble("harga_rata2_produk"),
                        resultSet.getDouble("harga_jual"),
                        resultSet.getString("image_url"),
                        resultSet.getString("deskripsi_produk"),
                        resultSet.getInt("jumlah_stok"),
                        resultSet.getInt("batas_kritis")
                );
                allList.add(produk);
            }
        }
        return allList;
    }

    public void registerProduk(Produk produk) throws SQLException {
        String sql = """
            INSERT INTO public.produk (
                nama_produk, satuan_produk, harga_rata2_produk, harga_jual,
                jumlah_stok, batas_kritis, image_url, deskripsi_produk
            )
            VALUES (?,?,?,?,?,?,?,?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produk.getNamaProduk());
            stmt.setString(2, produk.getSatuanProduk());
            stmt.setDouble(3, produk.getHargaRata2Produk());
            stmt.setDouble(4, produk.getHargaJualProduk());
            stmt.setInt(5, produk.getStok());
            stmt.setInt(6, produk.getBatasKritisProduk());
            stmt.setString(7, produk.getImageUrl());
            stmt.setString(8, produk.getDeskripsiProduk());
            stmt.executeUpdate();
        }
    }

    // Cek apakah nama produk sudah terdaftar (mengecualikan excludeIdProduk saat proses Edit)
    public boolean isNamaProdukExists(String namaProduk, Integer excludeIdProduk) throws SQLException {
        String sql = (excludeIdProduk == null)
                ? "SELECT 1 FROM public.produk WHERE LOWER(TRIM(nama_produk)) = LOWER(TRIM(?)) LIMIT 1"
                : "SELECT 1 FROM public.produk WHERE LOWER(TRIM(nama_produk)) = LOWER(TRIM(?)) AND id_produk <> ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, namaProduk);
            if (excludeIdProduk != null) {
                stmt.setInt(2, excludeIdProduk);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Update data produk dari menu Warehouse
    public void updateProduk(Produk produk) throws SQLException {
        String sql = """
            UPDATE public.produk
            SET nama_produk = ?,
                harga_jual = ?,
                jumlah_stok = ?,
                batas_kritis = ?,
                image_url = ?,
                deskripsi_produk = ?
            WHERE id_produk = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produk.getNamaProduk());
            stmt.setDouble(2, produk.getHargaJualProduk());
            stmt.setInt(3, produk.getStok());
            stmt.setInt(4, produk.getBatasKritisProduk());
            stmt.setString(5, produk.getImageUrl());
            stmt.setString(6, produk.getDeskripsiProduk());
            stmt.setInt(7, produk.getIdProduk());
            stmt.executeUpdate();
        }
    }

    public static void tambahStokDanUpdateHargaRata2(Connection conn, int idProduk, double jumlahProduksi, double totalHppProduksi) throws SQLException {
        String sqlSelect = """
            SELECT jumlah_stok, harga_rata2_produk
            FROM public.produk
            WHERE id_produk = ?
            FOR UPDATE
            """;

        String sqlUpdate = """
            UPDATE public.produk
            SET jumlah_stok = ?, harga_rata2_produk = ?
            WHERE id_produk = ?
            """;

        int stokLama;
        double hargaRata2Lama;

        try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
            stmtSelect.setInt(1, idProduk);
            try (ResultSet rs = stmtSelect.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Produk dengan ID " + idProduk + " tidak ditemukan.");
                }
                stokLama = rs.getInt("jumlah_stok");
                hargaRata2Lama = rs.getDouble("harga_rata2_produk");
            }
        }

        int stokBaru = stokLama + (int) Math.round(jumlahProduksi);
        double totalNilaiLama = stokLama * hargaRata2Lama;
        double hargaRata2Baru = (stokBaru > 0)
                ? (totalNilaiLama + totalHppProduksi) / stokBaru
                : hargaRata2Lama;

        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setInt(1, stokBaru);
            stmtUpdate.setDouble(2, hargaRata2Baru);
            stmtUpdate.setInt(3, idProduk);
            stmtUpdate.executeUpdate();
        }
    }
}
