package tubes.prak.pbo.models;

import java.util.Arrays;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;

public class Produk extends Item{
    private Satuan satuan_produk;
    private double harga_jual;
    private String imgUrl;
    private String deskripsiProduk;
    private int stok ;
    private int batasKritisProduk;

    public Produk(int id_produk, String nama_produk, String satuan_produk, double harga_rata2_produk, double harga_jual, String url, String deskripsi, int stok, int batasKritisProduk){
        super(id_produk, nama_produk, Satuan.valueOf(satuan_produk), harga_rata2_produk);
        this.satuan_produk = Satuan.valueOf(satuan_produk);
        this.harga_jual = harga_jual;
        this.imgUrl = url;
        this.deskripsiProduk = deskripsi;
        this.stok = stok;
        this.batasKritisProduk = batasKritisProduk;
    }
    public int getStok(){
        return this.stok;
    }

    public int getIdProduk(){
        return super.getId();
    }

    public String getNamaProduk(){
        return super.getNama();
    }


    public String getSatuanProduk(){
        return super.getSatuan().name();
    }


    public double getHargaRata2Produk(){
        return super.getHargaRata2();
    }

    public double getHargaJualProduk(){
        return this.harga_jual;
    }

    public String getImageUrl(){
        return this.imgUrl;
    }

    @Override
    public List<String> getTableHeader() {
        return Arrays.asList("ID Produk","Nama Produk", "Satuan Produk","HPP Produk","Harga Jual","Jumlah Stock","Batas Kritis");
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{getId(), getNama(),getSatuan().name(),getHargaRata2(),getHargaJualProduk(),getStok(),getBatasKritisProduk()};
    }

    public Satuan getSatuan_produk() {
        return satuan_produk;
    }

    public int getBatasKritisProduk() {
        return batasKritisProduk;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }


}
