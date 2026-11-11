package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Produk;


public class ProdukRepository {

    public List<Produk> findAllProduk(){

        List<Produk> allList = new ArrayList<>();

        String sql="""
            SELECT * FROM produk
                """;
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()){
                    int id = resultSet.getInt("id_produk");
                    String nama = resultSet.getString("nama_produk");
                    Satuan satuan = Satuan.valueOf(resultSet.getString("satuan_produk"));
                    double hargaRata2Produk = resultSet.getDouble("harga_rata2_produk");
                    double hargaJual = resultSet.getDouble("harga_jual");
                    int batasKritisProduk = resultSet.getInt("batas_kritis");
                    int jumlahStok = resultSet.getInt("jumlah_stok");
                    Produk produk = new Produk(id, nama,satuan, hargaRata2Produk, hargaJual,batasKritisProduk,jumlahStok);
                    allList.add(produk);
            }
                    
        
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    
        return allList;
    }

    public void registerProduk(Produk produk){
        String sql = """
                    INSERT INTO produk (nama_produk, satuan_produk, harga_rata2_produk, harga_jual, jumlah_stok, batas_kritis)    
                    VALUES (?,?,?,?,?,?)
                """;
        
        try(Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1,produk.getNama());
                stmt.setString(2,produk.getSatuan().name());
                stmt.setDouble(3,produk.getHargaRata2());
                stmt.setDouble(4,produk.getHargaJualProduk());
                stmt.setInt(5,produk.getStok());
                stmt.setInt(6,produk.getBatasKritisProduk());
                stmt.executeUpdate();
        } catch (Exception e) {
                e.printStackTrace();
        }

        //otomatis terdaftarkan ke warehouse kalau valid dengan
    }
}
