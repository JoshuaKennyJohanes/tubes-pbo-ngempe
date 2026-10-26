package tubes.prak.pbo.models;

import tubes.prak.pbo.ENUM.Satuan;

public class Produk {
    private int id_produk;
    private String nama_produk;
    private String satuan_produk;
    private double harga_rata2_produk;
    private double harga_jual;

    public Produk(int id_produk, String nama_produk, String satuan_produk, double harga_rata2_produk, double harga_jual){
        this.id_produk = id_produk;
        this.nama_produk = nama_produk;
        
        
        this.satuan_produk = satuan_produk;
        this.harga_rata2_produk = harga_rata2_produk;
        this.harga_jual = harga_jual;
    }


    public int getIdProduk(){
        return this.id_produk;
    }



    public String getNamaProduk(){
        return this.nama_produk;
    }


    public String getSatuanProduk(){
        return this.satuan_produk;
    }


    public double getHargaRata2Produk(){
        return this.harga_rata2_produk;
    }

    public double getHargaJualProduk(){
        return this.harga_jual;
    }
}
