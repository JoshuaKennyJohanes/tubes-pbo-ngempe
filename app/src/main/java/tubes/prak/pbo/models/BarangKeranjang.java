package tubes.prak.pbo.models;

public class BarangKeranjang {
    private String namabarang;
    private int Jumlah;
    private double hargatotal;

    public BarangKeranjang(String nama , int jmh , double hargatotal){
        this.namabarang = nama;
        this.Jumlah = jmh;
        this.hargatotal = hargatotal;
    }

    public String getNamaBarang(){
        return this.namabarang;
    }

    public int getJumlah(){
        return this.Jumlah;
    }

    public double getHargaTotal(){
        return this.hargatotal;
    }
}
