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
                    resultSet.getString("imageUrl")
                );

                allList.add(produk);
            }
        }

        return allList;
    }
}
