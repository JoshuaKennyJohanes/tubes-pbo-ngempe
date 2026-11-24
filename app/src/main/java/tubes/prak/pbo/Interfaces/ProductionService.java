package tubes.prak.pbo.Interfaces;

import java.sql.SQLException;
import java.util.List;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.models.DetailProduksi;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.models.productRelated.Bom;

public interface ProductionService {
    List<Produksi> getAllProduksi() throws SQLException;

    List<DetailProduksi> getDetailsProduksi(int idProduksi) throws SQLException;

    void buatJadwalProduksi(Produksi produksi, Bom bom) throws Exception;

    void ubahStatusProduksi(Produksi produksi, StatusProduksi statusBaru) throws Exception;
}
