package tubes.prak.pbo.models;

import java.util.Arrays;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.Interfaces.ManufactureAble;
import tubes.prak.pbo.Interfaces.SellAble;

public class Produk extends Item implements SellAble, ManufactureAble {
    private double harga_jual;
    private int jumlahStok;
    private int batasKritisProduk;

    public Produk(int id_produk, String nama_produk, Satuan satuan_produk, double harga_rata2_produk, double harga_jual, int batas_kritis, int jumlahStok){
        super(id_produk, nama_produk, satuan_produk, harga_rata2_produk);
        this.batasKritisProduk = batas_kritis;
        this.harga_jual = harga_jual;
        this.jumlahStok = jumlahStok;
    }


    // //method overloading untuk insert
    // public Produk(String nama_produk, Satuan satuan_produk, double harga_jual, int batas_kritis){
    //     setNama(nama_produk);
    //     this.harga_jual = harga_jual;
    //     this.batasKritisProduk = batas_kritis;
    // }

    public double getHargaJualProduk(){
        return this.harga_jual;
    }

    public int getStok(){
        return this.jumlahStok;
    }

    public int getBatasKritisProduk(){
        return this.batasKritisProduk;
    }

    @Override
    public List<String> getTableHeader() {
        return Arrays.asList("ID Produk","Nama Produk", "Satuan Produk","HPP Produk","Harga Jual","Jumlah Stock","Batas Kritis");
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{getId(), getNama(),getSatuan().name(),getHargaRata2(),getHargaJualProduk(),getStok(),getBatasKritisProduk()};
    }

    

    


}
