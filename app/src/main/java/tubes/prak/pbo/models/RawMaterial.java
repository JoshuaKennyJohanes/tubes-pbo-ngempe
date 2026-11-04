package tubes.prak.pbo.models;

import tubes.prak.pbo.ENUM.Satuan;

public class RawMaterial extends Item{
    private double batasKritisMaterial;
    private double jumlahStok;

    public RawMaterial(int id_raw_material, String nama_material, Satuan satuan_material, double harga_rata2_material, double batas_kritis, double jumlahStok){
        super(id_raw_material, nama_material, satuan_material, harga_rata2_material);
        this.batasKritisMaterial = batas_kritis;
        this.jumlahStok = jumlahStok; 
    }

    public double getBatasKritis(){
        return this.batasKritisMaterial;
    }

    public double getJumlahStok(){
        return this.jumlahStok;
    }
}
