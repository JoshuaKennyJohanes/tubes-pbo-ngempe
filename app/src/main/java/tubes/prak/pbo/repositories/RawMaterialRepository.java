package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.productRelated.RawMaterial;

public class RawMaterialRepository {
    private static final Logger LOGGER = Logger.getLogger(RawMaterialRepository.class.getName());

    public void registerItem(RawMaterial rawMaterial) throws SQLException {
        String sql = """
            INSERT INTO raw_material (nama_material, satuan_material, harga_rata2_bahan_baku,
                                      jumlah_stok, batas_kritis, image_url)
            VALUES (?,?,?,?,?,?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rawMaterial.getNama());
            stmt.setString(2, rawMaterial.getSatuan().name());
            stmt.setDouble(3, rawMaterial.getHargaRata2());
            stmt.setDouble(4, rawMaterial.getJumlahStok());
            stmt.setDouble(5, rawMaterial.getBatasKritis());
            stmt.setString(6, rawMaterial.getImageUrl());
            stmt.executeUpdate();
        }
    }

    public List<RawMaterial> findAllRawMaterial(){
        List<RawMaterial> listRawMaterial = new ArrayList<>();

        String sql = """
                SELECT * FROM raw_material;
                """;

        try(Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()){
                int id_raw_material = resultSet.getInt("id_raw_material");
                String nama_material = resultSet.getString("nama_material");
                Satuan satuan_material = Satuan.valueOf(resultSet.getString("satuan_material"));
                double harga_rata2_bahan_baku = resultSet.getDouble("harga_rata2_bahan_baku");
                double jumlah_stok = resultSet.getDouble("jumlah_stok");
                double batas_kritis = resultSet.getDouble("batas_kritis");
                String image_url = resultSet.getString("image_url");
                RawMaterial newRawMaterial = new RawMaterial(id_raw_material, nama_material, satuan_material, harga_rata2_bahan_baku, batas_kritis, jumlah_stok, image_url);
                listRawMaterial.add(newRawMaterial);
            }

        } catch (SQLException e){
            LOGGER.log(Level.SEVERE, "Gagal memuat raw material.", e);
        }

        return listRawMaterial;
    }

    public static void kurangiStokMaterial(Connection conn, int idRawMaterial, String namaMaterial, double qtyDipakai) throws SQLException {
        String sqlCek = """
        SELECT jumlah_stok
        FROM public.raw_material
        WHERE id_raw_material = ?
        FOR UPDATE
        """;

        String sqlUpdate = """
        UPDATE public.raw_material
        SET jumlah_stok = jumlah_stok - ?
        WHERE id_raw_material = ?
        """;

        try (PreparedStatement stmtCek = conn.prepareStatement(sqlCek)) {
            stmtCek.setInt(1, idRawMaterial);
            try (ResultSet rs = stmtCek.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Bahan baku '" + namaMaterial + "' (ID: " + idRawMaterial + ") tidak ditemukan.");
                }

                double stokTersedia = rs.getDouble("jumlah_stok");
                if (stokTersedia < qtyDipakai) {
                    throw new SQLException(
                            "Stok tidak mencukupi untuk " + namaMaterial +
                                    " (Butuh: " + qtyDipakai + ", Tersedia: " + stokTersedia + ")"
                    );
                }
            }
        }

        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setDouble(1, qtyDipakai);
            stmtUpdate.setInt(2, idRawMaterial);
            stmtUpdate.executeUpdate();
        }
    }

    public void updateStokMaterial(int idRawMaterial, double stokBaru) throws SQLException {
        String sql = """
        UPDATE public.raw_material
        SET jumlah_stok = ?
        WHERE id_raw_material = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, stokBaru);
            stmt.setInt(2, idRawMaterial);
            stmt.executeUpdate();
        }
    }

    //overload untuk mencatat ke finance ledger
    public void updateStokMaterial(Connection conn, int idRawMaterial, double stokBaru) throws SQLException {
        String sql = """
        UPDATE public.raw_material
        SET jumlah_stok = ?
        WHERE id_raw_material = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, stokBaru);
            stmt.setInt(2, idRawMaterial);
            stmt.executeUpdate();
        }
    }

    public void tambahStokDanUpdateHargaRata2(Connection conn, int idRawMaterial,
                                              double qtyMasuk, double totalHargaPo) throws SQLException {
        String sqlCek = """
            SELECT jumlah_stok, harga_rata2_bahan_baku
            FROM public.raw_material
            WHERE id_raw_material = ?
            FOR UPDATE
            """;

        String sqlUpdate = """
            UPDATE public.raw_material
            SET jumlah_stok = ?, harga_rata2_bahan_baku = ?
            WHERE id_raw_material = ?
            """;

        double stokLama;
        double hargaRata2Lama;

        try (PreparedStatement stmtCek = conn.prepareStatement(sqlCek)) {
            stmtCek.setInt(1, idRawMaterial);
            try (ResultSet rs = stmtCek.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Raw material dengan ID " + idRawMaterial + " tidak ditemukan.");
                }
                stokLama = rs.getDouble("jumlah_stok");
                hargaRata2Lama = rs.getDouble("harga_rata2_bahan_baku");
            }
        }

        double totalValueLama = stokLama * hargaRata2Lama;
        double stokBaru = stokLama + qtyMasuk;
        double hargaRata2Baru = (stokBaru > 0) ? (totalValueLama + totalHargaPo) / stokBaru : 0.0;

        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setDouble(1, stokBaru);
            stmtUpdate.setDouble(2, hargaRata2Baru);
            stmtUpdate.setInt(3, idRawMaterial);
            stmtUpdate.executeUpdate();
        }
    }
    
}
