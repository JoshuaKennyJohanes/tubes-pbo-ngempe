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

    public static Produk getProdukById(int id){
        String sql = "SELECT * FROM public.produk WHERE id_produk = ?";
         try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Produk(
                    resultSet.getInt("id_produk"),
                    resultSet.getString("nama_produk"),
                    resultSet.getString("satuan_produk"),
                    resultSet.getDouble("harga_rata2_produk"),
                    resultSet.getDouble("harga_jual"),
                    resultSet.getString("imageUrl"),
                    resultSet.getString("deskripsi_produk")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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
                    resultSet.getString("imageUrl"),
                    resultSet.getString("deskripsi_produk")
                );

                allList.add(produk);
            }
        }

        return allList;
    }

}
