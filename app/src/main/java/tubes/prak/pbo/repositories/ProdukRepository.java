package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Produk;


public class ProdukRepository {


    public static List<Produk> findAllProduk() throws SQLException {

        List<Produk> allList = new ArrayList<>();

        String sql = """
            SELECT * 
            FROM public.produk
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
            INSERT INTO produk (nama_produk, satuan_produk, harga_rata2_produk, harga_jual,
                                jumlah_stok, batas_kritis, image_url)
            VALUES (?,?,?,?,?,?,?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produk.getNamaProduk());
            stmt.setString(2, produk.getSatuanProduk());
            stmt.setDouble(3, produk.getHargaRata2Produk());
            stmt.setDouble(4, produk.getHargaJualProduk());
            stmt.setInt(5, produk.getStok());
            stmt.setInt(6, produk.getBatasKritisProduk());
            stmt.setString(7, produk.getImageUrl());   // sesuaikan dengan nama getter di model Produk
            stmt.executeUpdate();
        }
    }
}
