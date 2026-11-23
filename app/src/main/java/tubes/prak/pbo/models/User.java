package tubes.prak.pbo.models;

import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.Role;

public class User {
    private int id;
    private String nama;
    private String email;
    private Role role;
    private String userName;
    private String password;
    private List<BarangKeranjang> keranjangUser = new ArrayList<>();


    public User(int id, String nama, String email, Role role, String userName, String password){
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.role = role;
        this.userName = userName;
        this.password = password;

    }
    public List<BarangKeranjang> setKosongKeranjang(){
        keranjangUser.clear();
        return this.keranjangUser;
    }
    public List<BarangKeranjang> getBarangKeranjang(){
        System.out.println(keranjangUser.size());
        return this.keranjangUser;
    }

    public String isiKeranjang(){
        System.out.println(keranjangUser.size());
        return "";
    }

    public int getId(){
        return this.id;
    }

    public String getNama(){
        return this.nama;
    }

    public String getEmail(){
        return this.email;
    }

    public Role getRole(){
        return this.role;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getPasswword(){
        return this.password;
    }



    public void tambahkeKeranjang(BarangKeranjang item) {
        if (item == null) {
            return;
        }
        for (BarangKeranjang existing : keranjangUser) {
            if (existing.getId_produk() == item.getId_produk()) {
                existing.tambahJumlah(item.getJumlah(), item.getHargaTotal());
                return;
            }
        }
        keranjangUser.add(item);
    }

    public int getJumlahDiKeranjang(int idProduk) {
        for (BarangKeranjang item : keranjangUser) {
            if (item.getId_produk() == idProduk) {
                return item.getJumlah();
            }
        }
        return 0;
    }

    public void hapusDariKeranjang(BarangKeranjang item) {
        if (item != null) {
            keranjangUser.remove(item);
        }
    }


}