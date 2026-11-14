package tubes.prak.pbo.models;

import java.util.List;
import java.util.ArrayList;

public class Produksi {
    List<RawMaterial> rawMaterialList = new ArrayList<>();
    String tanggal;
    Produk produk;
    int kuantitas;

    public Produksi(Produk produk, int kuantitas, List<RawMaterial> rawMaterialList, String tanggal){
        this.tanggal = tanggal;
        this.produk = produk;
        this.rawMaterialList = rawMaterialList;
        this.kuantitas = kuantitas;
    }

    public String getTanggal() {
        return tanggal;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }
}
