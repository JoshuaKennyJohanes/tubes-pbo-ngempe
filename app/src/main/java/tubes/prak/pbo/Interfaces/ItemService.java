package tubes.prak.pbo.Interfaces;

import java.sql.SQLException;
import java.util.List;

import tubes.prak.pbo.models.productRelated.Bom;
import tubes.prak.pbo.models.productRelated.Produk;

public interface ItemService {
    List<Produk> getAllProduk() throws SQLException;

    Bom getBomByProdukId(int idProduk) throws SQLException;

    double hitungTotalHpp(Bom bom, double jumlahProduksi);

    void validasiStokMaterial(Bom bom, double jumlahProduksi) throws Exception;
}
