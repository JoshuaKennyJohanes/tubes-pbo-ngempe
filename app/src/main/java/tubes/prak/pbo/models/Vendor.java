package tubes.prak.pbo.models;

public class Vendor {
    private int id_vendor;
    private String namaVendor;
    private String telpVendor;
    private String alamatVendor;

    public Vendor(int id_vendor, String namaVendor, String telpVendor, String alamatVendor) {
        this.alamatVendor = alamatVendor;
        this.id_vendor = id_vendor;
        this.namaVendor = namaVendor;
        this.telpVendor = telpVendor;
    }

    public int getId_vendor() {
        return id_vendor;
    }

    public String getNamaVendor() {
        return namaVendor;
    }

    public String getTelpVendor() {
        return telpVendor;
    }

    public String getAlamatVendor() {
        return alamatVendor;
    }

    @Override
    public String toString() {
        return namaVendor;
    }
}
