package tubes.prak.pbo.controller;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.Interfaces.ItemService;
import tubes.prak.pbo.Interfaces.ProductionDataSource;
import tubes.prak.pbo.Interfaces.ProductionService;
import tubes.prak.pbo.models.productRelated.Bom;
import tubes.prak.pbo.models.DetailProduksi;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.repositories.ProduksiRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ProduksiController implements ProductionService {

    private final ProductionDataSource productionDataSource;
    private final ItemService itemService;

    public ProduksiController() {
        this(new ProduksiRepository(), new ItemController());
    }

    public ProduksiController(ItemService itemService) {
        this(new ProduksiRepository(), itemService);
    }

    public ProduksiController(ProductionDataSource productionDataSource, ItemService itemService) {
        this.productionDataSource = Objects.requireNonNull(productionDataSource);
        this.itemService = Objects.requireNonNull(itemService);
    }

    @Override
    public List<Produksi> getAllProduksi() throws SQLException {
        return productionDataSource.findAll();
    }

    @Override
    public List<DetailProduksi> getDetailsProduksi(int idProduksi) throws SQLException {
        return productionDataSource.findDetailsByProduksiId(idProduksi);
    }

    @Override
    public void buatJadwalProduksi(Produksi produksi, Bom bom) throws Exception {
        if (produksi.getTanggalProduksi() == null) {
            throw new Exception("Tanggal produksi wajib diisi.");
        }
        if (produksi.getJumlahProduksi() <= 0) {
            throw new Exception("Jumlah produksi harus lebih dari 0.");
        }

        // 1. Cek kecukupan stok (akan melempar Exception jika kurang atau error DB)
        itemService.validasiStokMaterial(bom, produksi.getJumlahProduksi());

        // 2. Simpan ke database jika validasi lolos
        productionDataSource.insert(produksi, bom);
    }

    @Override
    public void ubahStatusProduksi(Produksi produksi, StatusProduksi statusBaru) throws Exception {
        if (produksi == null || statusBaru == null) {
            throw new Exception("Data produksi atau status tujuan tidak valid.");
        }

        StatusProduksi statusSaatIni = produksi.getStatusProduksi();

        // Ambil snapshot kebutuhan bahan baku dari tabel detail_produksi
        List<DetailProduksi> daftarDetail =
                productionDataSource.findDetailsByProduksiId(produksi.getIdProduksi());
        if (daftarDetail.isEmpty()) {
            throw new Exception("Rincian detail bahan baku untuk produksi #" + produksi.getIdProduksi() + " tidak ditemukan.");
        }

        if (statusSaatIni == StatusProduksi.PLANNED && statusBaru == StatusProduksi.ONGOING) {
            productionDataSource.mulaiProduksi(produksi.getIdProduksi(), daftarDetail);
        } else if (statusSaatIni == StatusProduksi.ONGOING && statusBaru == StatusProduksi.FINISHED) {
            productionDataSource.selesaikanProduksi(produksi, daftarDetail);
        } else {
            throw new Exception("Perubahan status dari " + statusSaatIni + " ke " + statusBaru + " tidak diizinkan.");
        }

        // Perbarui state objek di memori setelah transaksi DB berhasil
        produksi.setStatusProduksi(statusBaru);
    }
}