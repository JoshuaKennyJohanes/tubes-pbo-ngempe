package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
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
                RawMaterial newRawMaterial = new RawMaterial(id_raw_material, nama_material, satuan_material, harga_rata2_bahan_baku, batas_kritis, jumlah_stok);
                listRawMaterial.add(newRawMaterial);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return listRawMaterial;
    }

    
}
