package tubes.prak.pbo.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.models.Bom;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.repositories.BomRepository;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.repositories.RawMaterialRepository;
import tubes.prak.pbo.util.ImageStorage;

public class ItemController {
    ProdukRepository produkRepo = new ProdukRepository();
    RawMaterialRepository rawMaterialRepo = new RawMaterialRepository();
    BomRepository bomRepository = new BomRepository();

    public void registerProduk(String namaProduk, Satuan satuanProduk, double hargaJualProduk,
                               int stokKritis, File gambar) throws Exception {

        if (namaProduk == null || namaProduk.isBlank() || satuanProduk == null
                || hargaJualProduk == 0 || stokKritis == 0) {
            throw new Exception("Field Harus Lengkap!");
        }
        if (hargaJualProduk < 0) throw new Exception("Harga jual tidak valid!");
        if (stokKritis < 0) throw new Exception("Stok tidak boleh di bawah 0!");

        String imageUrl = ImageStorage.save(gambar, "produk");   // null kalau tidak ada gambar

        try {
            Produk newProduk = new Produk(0, namaProduk, satuanProduk.name(), 0,
                    hargaJualProduk, imageUrl, " ", 0, stokKritis);
            produkRepo.registerProduk(newProduk);
        } catch (Exception e) {
            ImageStorage.delete(imageUrl);   // rollback: jangan tinggalkan file yatim
            throw e;
        }
    }

    public void registerRawMaterial(String namaMaterial, Satuan satuanMaterial, double hargaMaterial,
                                    double stokKritis, double stokMaterial, File gambar) throws Exception {
        if (namaMaterial == null || namaMaterial.isBlank() || satuanMaterial == null) {
            throw new Exception("Form harus diisi dengan lengkap!");
        }
        if (hargaMaterial < 0) throw new Exception("Harga Material tidak boleh kurang dari 0 !");
        if (stokKritis < 0) throw new Exception("Stok kritis tidak boleh kurang dari 0 !");
        if (stokMaterial < 0) throw new Exception("Stok material tidak boleh kurang dari 0 !");

        String imageUrl = ImageStorage.save(gambar, "raw_material");

        try {
            RawMaterial newRawMaterial = new RawMaterial(0, namaMaterial, satuanMaterial,
                    hargaMaterial, stokKritis, stokMaterial, imageUrl);
            rawMaterialRepo.registerItem(newRawMaterial);
        } catch (Exception e) {
            ImageStorage.delete(imageUrl);
            throw e;
        }
    }

    public void registerBomProduk(Produk produk, Map<RawMaterial, Double> kebutuhanMaterial) throws Exception {
        if (produk == null) {
            throw new Exception("Produk harus dipilih!");
        }
        if (kebutuhanMaterial == null || kebutuhanMaterial.isEmpty()) {
            throw new Exception("Minimal harus menambahkan 1 bahan baku!");
        }

        for (Map.Entry<RawMaterial, Double> entry : kebutuhanMaterial.entrySet()) {
            if (entry.getKey() == null) {
                throw new Exception("Terdapat baris bahan baku yang belum dipilih!");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new Exception("Jumlah kebutuhan untuk " + entry.getKey().getNama() + " harus lebih dari 0!");
            }
        }

        bomRepository.registerBomForProduk(produk, kebutuhanMaterial);
    }

    public List<RawMaterial> getAllMaterials(){
        return rawMaterialRepo.findAllRawMaterial();
    }

    // 1. Kalkulasi reaktif di memori (tanpa query database berulang)
    public double hitungTotalHpp(Bom bom, double jumlahProduksi) {
        if (bom == null || jumlahProduksi <= 0) {
            return 0.0;
        }

        double totalHpp = 0.0;
        for (Map.Entry<RawMaterial, Double> entry : bom.getKebutuhanMaterial().entrySet()) {
            RawMaterial material = entry.getKey();
            double qtyPerProduk = entry.getValue();

            totalHpp += (material.getHargaRata2() * qtyPerProduk) * jumlahProduksi;
        }
        return totalHpp;
    }

    // 2. Validasi kecukupan stok raw material dengan try-catch
    public void validasiStokMaterial(Bom bom, double jumlahProduksi) throws Exception {
        if (bom == null || bom.getKebutuhanMaterial().isEmpty()) {
            throw new Exception("BOM untuk produk ini belum diatur.");
        }

        List<RawMaterial> stokTersediaList;
        try {
            stokTersediaList = rawMaterialRepo.findAllRawMaterial();
        } catch (Exception e) {
            throw new Exception("Gagal memeriksa stok bahan baku: " + e.getMessage(), e);
        }

        for (Map.Entry<RawMaterial, Double> entry : bom.getKebutuhanMaterial().entrySet()) {
            RawMaterial materialDibutuhkan = entry.getKey();
            double totalKebutuhan = entry.getValue() * jumlahProduksi;

            // Sesuaikan getter ID di bawah (misal getId() / getIdItem()) dengan class Item Anda
            RawMaterial stokAktual = cariMaterialById(stokTersediaList, materialDibutuhkan.getId());

            if (stokAktual == null || stokAktual.getJumlahStok() < totalKebutuhan) {
                double sisa = (stokAktual != null) ? stokAktual.getJumlahStok() : 0;
                throw new Exception(
                        "Stok tidak mencukupi untuk " + materialDibutuhkan.getNama() +
                                " (Butuh: " + totalKebutuhan + ", Tersedia: " + sisa + ")"
                );
            }
        }
    }

    private RawMaterial cariMaterialById(List<RawMaterial> list, int idMaterial) {
        for (RawMaterial rm : list) {
            if (rm.getId() == idMaterial) { // Sesuaikan jika nama getter di Item berbeda
                return rm;
            }
        }
        return null;
    }

    public List<Bom> getAllBomGroupedByProduk() throws SQLException {
        List<Bom> rawBomList = bomRepository.findAllGroupedByProduk();
        List<Produk> allProduk = produkRepo.findAllProduk();
        List<RawMaterial> allMaterials = rawMaterialRepo.findAllRawMaterial();

        Map<Integer, Produk> produkMap = new java.util.HashMap<>();
        for (Produk p : allProduk) {
            produkMap.put(p.getIdProduk(), p);
        }

        Map<Integer, RawMaterial> materialMap = new java.util.HashMap<>();
        for (RawMaterial rm : allMaterials) {
            materialMap.put(rm.getId(), rm);
        }

        List<Bom> enrichedList = new java.util.ArrayList<>();
        for (Bom rawBom : rawBomList) {
            Bom fullBom = new Bom(rawBom.getIdBom(), rawBom.getIdProduk());
            fullBom.setProduk(produkMap.get(rawBom.getIdProduk()));

            for (Map.Entry<RawMaterial, Double> entry : rawBom.getKebutuhanMaterial().entrySet()) {
                int idMat = entry.getKey().getId();
                RawMaterial fullMat = materialMap.getOrDefault(idMat, entry.getKey());
                fullBom.tambahMaterial(fullMat, entry.getValue());
            }
            enrichedList.add(fullBom);
        }
        return enrichedList;
    }

    // Mengambil daftar produk yang BELUM memiliki BOM (siap dipakai di menu Tambah BOM)
    public List<Produk> getProdukTanpaBom() throws SQLException {
        List<Produk> allProduk = produkRepo.findAllProduk();
        java.util.Set<Integer> registeredIds = bomRepository.findAllRegisteredProdukIds();

        List<Produk> availableProduk = new java.util.ArrayList<>();
        for (Produk p : allProduk) {
            if (!registeredIds.contains(p.getIdProduk())) {
                availableProduk.add(p);
            }
        }
        return availableProduk;
    }

    public void updateBomProduk(int idProduk, Map<RawMaterial, Double> kebutuhanMaterial) throws Exception {
        if (idProduk <= 0) {
            throw new Exception("ID Produk tidak valid!");
        }
        if (kebutuhanMaterial == null || kebutuhanMaterial.isEmpty()) {
            throw new Exception("Minimal harus terdapat 1 bahan baku dalam BOM!");
        }
        for (Map.Entry<RawMaterial, Double> entry : kebutuhanMaterial.entrySet()) {
            if (entry.getKey() == null) {
                throw new Exception("Bahan baku tidak boleh kosong!");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new Exception("Jumlah kebutuhan untuk " + entry.getKey().getNama() + " harus lebih dari 0!");
            }
        }
        bomRepository.replaceBomForProduk(idProduk, kebutuhanMaterial);
    }

    public void deleteBomByProduk(int idProduk) throws Exception {
        if (idProduk <= 0) {
            throw new Exception("ID Produk tidak valid!");
        }
        bomRepository.deleteByProdukId(idProduk);
    }
}
