package tubes.prak.pbo.models;

public class BarangKeranjang {
    private int id_produk;
    private String namabarang;
    private int Jumlah;
    private double hargatotal;

    public BarangKeranjang(int id, String nama, int jmh, double hargatotal) {
        this.id_produk = id;
        this.namabarang = nama;
        this.Jumlah = jmh;
        this.hargatotal = hargatotal;
    }

    public void tambahJumlah(int qtyTambahan, double subtotalTambahan) {
        this.Jumlah += qtyTambahan;
        this.hargatotal += subtotalTambahan;
    }

    public String getNamaBarang() {
        return this.namabarang;
    }

    public int getJumlah() {
        return this.Jumlah;
    }

    public double getHargaTotal() {
        return this.hargatotal;
    }

    public int getId_produk() {
        return id_produk;
    }
}
