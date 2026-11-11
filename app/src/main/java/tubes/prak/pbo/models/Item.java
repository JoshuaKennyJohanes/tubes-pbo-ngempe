package tubes.prak.pbo.models;

import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;

public abstract class Item {
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

    @Override 
    public String toString(){
        return id_item + " - " + nama_item;
    }
    public abstract List<String> getTableHeader();
    public abstract  Object[] toTableRow();
    

}
