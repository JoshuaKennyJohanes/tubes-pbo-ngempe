package tubes.prak.pbo.Interfaces;

import java.sql.SQLException;
import java.util.List;

import tubes.prak.pbo.models.DetailProduksi;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.models.productRelated.Bom;

public interface ProductionDataSource {
    void insert(Produksi produksi, Bom bom) throws SQLException;

    List<DetailProduksi> findDetailsByProduksiId(int idProduksi) throws SQLException;

    List<Produksi> findAll() throws SQLException;

    void mulaiProduksi(int idProduksi, List<DetailProduksi> daftarDetail) throws SQLException;

    void selesaikanProduksi(Produksi produksi, List<DetailProduksi> daftarDetail) throws SQLException;
}
