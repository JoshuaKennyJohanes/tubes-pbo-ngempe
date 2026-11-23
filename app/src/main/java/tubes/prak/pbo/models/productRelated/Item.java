package tubes.prak.pbo.models.productRelated;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.Interfaces.StoreAble;

public abstract class Item implements StoreAble {
    private int id_item;
    private String nama_item;
    private Satuan satuan_item;
    private double harga_rata2_item;

    public Item(int id_item, String nama_item, Satuan satuan_item, double harga_rata2_item){
        this.id_item = id_item;
        this.nama_item = nama_item;
        this.satuan_item = satuan_item;
        this.harga_rata2_item = harga_rata2_item;
    }

    public Item(){

    }

    @Override
    public String getInformation() {
        return getId() + "#" + getNama() + " (" + getSatuan().toString() + ") " ;
    }

    public int getId(){
        return this.id_item;
    }

    public String getNama(){
        return this.nama_item;
    }

    public Satuan getSatuan(){
        return this.satuan_item;
    }

    public double getHargaRata2(){
        return this.harga_rata2_item;
    }

    public void setId(int id){
        this.id_item = id;
    }

    public void setNama(String nama){
        this.nama_item = nama;
    }

    public void setSatuan(Satuan satuan){
        this.satuan_item = satuan;
    }

    public void setHPP(double hpp){
        this.harga_rata2_item = hpp;
    }




    @Override 
    public String toString(){
        return id_item + " - " + nama_item;
    }


}
