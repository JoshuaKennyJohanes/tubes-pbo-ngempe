package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.RawMaterial;

public class RawMaterialRepository {

    public void registerItem(RawMaterial rawMaterial) throws SQLException{
        String sql =""" 
                    INSERT INTO raw_material (nama_material, satuan_material, harga_rata2_bahan_baku, jumlah_stok, batas_kritis)
                    VALUES (?,?,?,?,?)
                """;

        try(Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, rawMaterial.getNama());
            stmt.setString(2, rawMaterial.getSatuan().name());
            stmt.setDouble(3, rawMaterial.getHargaRata2());
            stmt.setDouble(4, rawMaterial.getJumlahStok());
            stmt.setDouble(5, rawMaterial.getBatasKritis());
            stmt.executeUpdate();

        }

    }
    
}
