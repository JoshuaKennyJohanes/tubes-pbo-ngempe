package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;

public class BomRepository {
    public void registerBOM(Produk produk, RawMaterial rawMaterial, double qty) throws SQLException{
        String sql = """
            INSERT INTO BOM (id_produk, id_raw_material, jumlah_kebutuhan)
            VALUES (?,?,?);
            """;
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, produk.getId());
            stmt.setInt(2, rawMaterial.getId());
            stmt.setDouble(3, qty);
            stmt.executeUpdate();
        } 
    }
}
