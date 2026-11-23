package tubes.prak.pbo.models.productRelated;

import java.util.LinkedHashMap;
import java.util.Map;

public class Bom extends Produk {
    private int idBom;
    private int idProduk;
    private Produk produk;
    private final Map<RawMaterial, Double> kebutuhanMaterial;

    public Bom(int idBom, int idProduk) {
        this.idBom = idBom;
        this.idProduk = idProduk;
        this.kebutuhanMaterial = new LinkedHashMap<>();
    }

    public void tambahMaterial(RawMaterial material, double jumlahKebutuhan) {
        this.kebutuhanMaterial.put(material, jumlahKebutuhan);
    }

    public int getIdBom() {
        return idBom;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setProduk(Produk produk){
        this.produk = produk;
    }

    public Produk getProduk(){
        return this.produk;
    }
    public Map<RawMaterial, Double> getKebutuhanMaterial() {
        return kebutuhanMaterial;
    }
}