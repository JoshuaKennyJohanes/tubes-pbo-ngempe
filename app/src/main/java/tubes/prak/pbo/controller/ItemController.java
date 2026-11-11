package tubes.prak.pbo.controller;

import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.repositories.RawMaterialRepository;

public class ItemController {
    ProdukRepository produkRepo = new ProdukRepository();
    RawMaterialRepository rawMaterialRepo = new RawMaterialRepository();
    
    public void registerProduk(String namaProduk, Satuan satuanProduk, double hargaJualProduk, int stokKritis) throws Exception{
        
        //cek inputan
        if(namaProduk==null || satuanProduk ==null || hargaJualProduk == 0 || stokKritis == 0){
            throw new Exception("Field Harus Lengkap!");
        }
        if(hargaJualProduk<0){
            throw new Exception("Harga jual tidak valid!");
        }

        if(stokKritis<0){
            throw new Exception("Stok tidak boleh di bawah 0!");
        }


        Produk newProduk = new Produk(0, namaProduk, satuanProduk, 0, hargaJualProduk, stokKritis, 0);
        //kalau valid register produk baru
        produkRepo.registerProduk(newProduk);

    }

    public void registerRawMaterial(String namaMaterial, Satuan satuanMaterial, double hargaMaterial, double stokKritis, double stokMaterial) throws Exception{
        if(namaMaterial == null || satuanMaterial == null){
            throw new Exception("Form harus diisi dengan lengkap!");
        }

        if(hargaMaterial < 0 ){
            throw new Exception("Harga Material tidak boleh kurang dari 0 !");
        }

        if(stokKritis < 0){
            throw new Exception("Stok kritis tidak boleh kurang dari 0 !");
        }

        if(stokMaterial < 0){
            throw new Exception("Stok material tidak boleh kurang dari 0 !");
        }

        RawMaterial newRawMaterial = new RawMaterial(0, namaMaterial, satuanMaterial, hargaMaterial, stokKritis, stokMaterial);

        rawMaterialRepo.registerItem(newRawMaterial);

    }   

    public List<Produk> getAllProduk(){
        return produkRepo.findAllProduk();
    }

    public List<RawMaterial> getAllMaterials(){
        return rawMaterialRepo.findAllRawMaterial();
    }

}
