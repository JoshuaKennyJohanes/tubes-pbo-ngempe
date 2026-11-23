package tubes.prak.pbo.models.productRelated;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.Interfaces.SellAble;

public class Produk extends Item implements SellAble{
    private double harga_jual;
    private String imgUrl;
    private String deskripsiProduk;
    private int stok ;
    private int batasKritisProduk;

    public Produk(int id_produk, String nama_produk, String satuan_produk, double harga_rata2_produk, double harga_jual, String url, String deskripsi, int stok, int batasKritisProduk){
        super(id_produk, nama_produk, Satuan.valueOf(satuan_produk), harga_rata2_produk);
        this.harga_jual = harga_jual;
        this.imgUrl = url;
        this.deskripsiProduk = deskripsi;
        this.stok = stok;
        this.batasKritisProduk = batasKritisProduk;
    }

    public Produk(){

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

    public void setStok(int stok){
        this.stok = stok;
    }

    public void setHargaJual(double harga_jual){
        this.harga_jual = harga_jual;
    }

    public String getImageUrl(){
        return this.imgUrl;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }

    public int getBatasKritisProduk(){
        return this.batasKritisProduk;
    }

    @Override
    public double getHPP(){
        return super.getHargaRata2();
    }

    @Override
    public double getHargaJual(){
        return this.harga_jual;
    }





}