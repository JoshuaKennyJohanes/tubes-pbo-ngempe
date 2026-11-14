package tubes.prak.pbo.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
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


    public void registerBOM(Produk produk, RawMaterial rawMaterial, double qty) throws Exception{
        if(produk == null){
            throw new Exception("Produk Harus dipilih!");
        }
        if(rawMaterial==null){
            throw new Exception("Bahan baku harus dipilih!");
        }
        if(qty <= 0){
            throw new Exception("Masukan nominal bahan baku yang valid");
        }

        try {
            bomRepository.registerBOM(produk, rawMaterial, qty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Produk> getAllProduk() throws SQLException {
        return produkRepo.findAllProduk();
    }

    public List<RawMaterial> getAllMaterials(){
        return rawMaterialRepo.findAllRawMaterial();
    }

}
