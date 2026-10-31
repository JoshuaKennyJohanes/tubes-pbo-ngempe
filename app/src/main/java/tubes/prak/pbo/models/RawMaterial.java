package tubes.prak.pbo.models;

public class RawMaterial {
    private String id_raw_material;
    private String nama_material;
    private String satuan_material;
    private String harga_rata_rata;

    public RawMaterial(String id_raw_material, String nama_material, String satuan_material, String harga_rata_rata){
        this.id_raw_material = id_raw_material;
        this.nama_material = nama_material;
        this.satuan_material = satuan_material;
        this.harga_rata_rata = harga_rata_rata;
    }
}
