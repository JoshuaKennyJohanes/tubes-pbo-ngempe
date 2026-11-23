package tubes.prak.pbo.models.productRelated;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.Interfaces.PurchaseAble;

public class RawMaterial extends Item implements PurchaseAble {
    private double batasKritisMaterial;
    private double jumlahStok;
    private double hargaPembelianTerbaru;

    private String imageUrl;

    public RawMaterial(int id_raw_material, String nama_material, Satuan satuan_material, double harga_rata2_material, double batas_kritis, double jumlahStok, String imageUrl){
        super(id_raw_material, nama_material, satuan_material, harga_rata2_material);
        this.batasKritisMaterial = batas_kritis;
        this.jumlahStok = jumlahStok;
        this.imageUrl = imageUrl;
    }

    public double getBatasKritis(){
        return this.batasKritisMaterial;
    }

    public double getJumlahStok(){
        return this.jumlahStok;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    @Override
    public void setHargaPembelianTerbaru(double hargaTerbaru) {
        this.hargaPembelianTerbaru = hargaTerbaru;
    }

}
