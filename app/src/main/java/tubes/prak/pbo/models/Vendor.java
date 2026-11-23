package tubes.prak.pbo.models;

public class Vendor {
    private int idVendor;
    private String namaVendor;
    private String noTelp;
    private String alamatVendor;

    public Vendor(int idVendor, String namaVendor, String noTelp, String alamatVendor) {
        this.idVendor = idVendor;
        this.namaVendor = namaVendor;
        this.noTelp = noTelp;
        this.alamatVendor = alamatVendor;
    }

    public int getIdVendor() {
        return idVendor;
    }

    public String getNamaVendor() {
        return namaVendor;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getAlamatVendor() {
        return alamatVendor;
    }

    @Override
    public String toString() {
        return namaVendor + (noTelp != null && !noTelp.isBlank() ? " (" + noTelp + ")" : "");
    }
}
