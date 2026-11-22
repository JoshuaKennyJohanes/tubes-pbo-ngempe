package tubes.prak.pbo.controller;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.models.Bom;
import tubes.prak.pbo.models.DetailProduksi;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.repositories.ProduksiRepository;

import java.util.List;

public class ProduksiController {

    private final ItemController itemController = new ItemController();

    public void buatJadwalProduksi(Produksi produksi, Bom bom) throws Exception {
        if (produksi.getTanggalProduksi() == null) {
            throw new Exception("Tanggal produksi wajib diisi.");
        }
        if (produksi.getJumlahProduksi() <= 0) {
            throw new Exception("Jumlah produksi harus lebih dari 0.");
        }

        // 1. Cek kecukupan stok (akan melempar Exception jika kurang atau error DB)
        itemController.validasiStokMaterial(bom, produksi.getJumlahProduksi());

        // 2. Simpan ke database jika validasi lolos
        ProduksiRepository.insert(produksi, bom);
    }

    public void ubahStatusProduksi(Produksi produksi, StatusProduksi statusBaru) throws Exception {
        if (produksi == null || statusBaru == null) {
            throw new Exception("Data produksi atau status tujuan tidak valid.");
        }

        StatusProduksi statusSaatIni = produksi.getStatusProduksi();

        // Ambil snapshot kebutuhan bahan baku dari tabel detail_produksi
        List<DetailProduksi> daftarDetail = ProduksiRepository.findDetailsByProduksiId(produksi.getIdProduksi());
        if (daftarDetail.isEmpty()) {
            throw new Exception("Rincian detail bahan baku untuk produksi #" + produksi.getIdProduksi() + " tidak ditemukan.");
        }

        if (statusSaatIni == StatusProduksi.PLANNED && statusBaru == StatusProduksi.ONGOING) {
            ProduksiRepository.mulaiProduksi(produksi.getIdProduksi(), daftarDetail);
        } else if (statusSaatIni == StatusProduksi.ONGOING && statusBaru == StatusProduksi.FINISHED) {
            ProduksiRepository.selesaikanProduksi(produksi, daftarDetail);
        } else {
            throw new Exception("Perubahan status dari " + statusSaatIni + " ke " + statusBaru + " tidak diizinkan.");
        }

        // Perbarui state objek di memori setelah transaksi DB berhasil
        produksi.setStatusProduksi(statusBaru);
    }
}