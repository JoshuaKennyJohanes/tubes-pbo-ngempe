package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.productRelated.Bom;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.productRelated.RawMaterial;

public class BomRepository {
    // Tambahkan method ini di dalam class BomRepository
    public void registerBomForProduk(Produk produk, Map<RawMaterial, Double> kebutuhanMaterial) throws SQLException {
        String sql = "INSERT INTO public.bom (id_produk, id_raw_material, jumlah_kebutuhan) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Map.Entry<RawMaterial, Double> entry : kebutuhanMaterial.entrySet()) {
                    stmt.setInt(1, produk.getIdProduk());
                    stmt.setInt(2, entry.getKey().getId());
                    stmt.setDouble(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }


    public static Bom findByProdukId(int idProduk) throws SQLException {
        // Sesuaikan nama kolom JOIN jika di database Anda berbeda
        String sql = """
            SELECT b.id_bom, b.id_produk, b.jumlah_kebutuhan,
                   r.id_raw_material, r.nama_material, r.harga_rata2_bahan_baku
            FROM public.bom b
            JOIN public.raw_material r ON b.id_raw_material = r.id_raw_material
            WHERE b.id_produk = ?
            """;

        Bom bom = null;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduk);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (bom == null) {
                    bom = new Bom(rs.getInt("id_bom"), rs.getInt("id_produk"));
                }

                // Hanya mengambil id, nama, dan harga rata-rata sesuai kebutuhan kalkulasi
                RawMaterial material = new RawMaterial(
                        rs.getInt("id_raw_material"),
                        rs.getString("nama_material"),
                        null,
                        rs.getDouble("harga_rata2_bahan_baku"),
                        0,
                        0,
                        null
                );

                double jumlahKebutuhan = rs.getDouble("jumlah_kebutuhan");
                bom.tambahMaterial(material, jumlahKebutuhan);
            }
        }
        return bom;
    }
    // Mengambil semua baris BOM dan mengelompokkannya menjadi 1 objek Bom per id_produk
    public List<Bom> findAllGroupedByProduk() throws SQLException {
        String sql = """
            SELECT id_bom, id_produk, id_raw_material, jumlah_kebutuhan
            FROM public.bom
            ORDER BY id_produk ASC, id_bom ASC
            """;

        Map<Integer, Bom> groupedMap = new LinkedHashMap<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idBom = rs.getInt("id_bom");
                int idProduk = rs.getInt("id_produk");
                int idRawMaterial = rs.getInt("id_raw_material");
                double jumlahKebutuhan = rs.getDouble("jumlah_kebutuhan");

                Bom bom = groupedMap.computeIfAbsent(idProduk, k -> new Bom(idBom, idProduk));

                // Placeholder RawMaterial dengan ID (akan dilengkapi detailnya di ItemController)
                RawMaterial materialRef = new RawMaterial(
                        idRawMaterial, "", null, 0, 0, 0, null
                );
                bom.tambahMaterial(materialRef, jumlahKebutuhan);
            }
        }
        return new ArrayList<>(groupedMap.values());
    }

    // Mengambil semua id_produk yang sudah memiliki BOM (untuk filter menu Tambah BOM)
    public Set<Integer> findAllRegisteredProdukIds() throws SQLException {
        String sql = "SELECT DISTINCT id_produk FROM public.bom";
        Set<Integer> registeredIds = new HashSet<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                registeredIds.add(rs.getInt("id_produk"));
            }
        }
        return registeredIds;
    }

    // Menghapus seluruh resep BOM milik suatu produk
    public void deleteByProdukId(int idProduk) throws SQLException {
        String sql = "DELETE FROM public.bom WHERE id_produk = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduk);
            stmt.executeUpdate();
        }
    }

    // Mengganti seluruh komposisi BOM milik suatu produk dalam 1 transaksi atomik
    public void replaceBomForProduk(int idProduk, Map<RawMaterial, Double> kebutuhanMaterial) throws SQLException {
        String deleteSql = "DELETE FROM public.bom WHERE id_produk = ?";
        String insertSql = "INSERT INTO public.bom (id_produk, id_raw_material, jumlah_kebutuhan) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                deleteStmt.setInt(1, idProduk);
                deleteStmt.executeUpdate();

                for (Map.Entry<RawMaterial, Double> entry : kebutuhanMaterial.entrySet()) {
                    insertStmt.setInt(1, idProduk);
                    insertStmt.setInt(2, entry.getKey().getId());
                    insertStmt.setDouble(3, entry.getValue());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }
}
