package tubes.prak.pbo.models;

import tubes.prak.pbo.ENUM.StatusProduksi;

import java.util.List;
import java.util.ArrayList;

import java.util.Date;


//ini bisa dibentuk dalam mediator pattern
public class Produksi {
    private int idProduksi;
    private int idProduk;
    private int createdBy;
    private Date tanggalProduksi;
    private StatusProduksi statusProduksi;
    private double jumlahProduksi;
    private double totalHppProduksi;

    public Produksi(int idProduksi, int idProduk, int createdBy, Date tanggalProduksi, StatusProduksi statusProduksi, double jumlahProduksi, double totalHppProduksi) {
        this.idProduksi = idProduksi;
        this.idProduk = idProduk;
        this.createdBy = createdBy;
        this.tanggalProduksi = tanggalProduksi;
        this.statusProduksi = statusProduksi;
        this.jumlahProduksi = jumlahProduksi;
        this.totalHppProduksi = totalHppProduksi;
    }

    public int getIdProduksi() { return idProduksi; }
    public void setIdProduksi(int idProduksi) { this.idProduksi = idProduksi; }

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int idProduk) { this.idProduk = idProduk; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Date getTanggalProduksi() { return tanggalProduksi; }
    public void setTanggalProduksi(Date tanggalProduksi) { this.tanggalProduksi = tanggalProduksi; }

    public StatusProduksi getStatusProduksi() { return statusProduksi; }
    public void setStatusProduksi(StatusProduksi statusProduksi) { this.statusProduksi = statusProduksi; }

    public double getJumlahProduksi() { return jumlahProduksi; }
    public void setJumlahProduksi(double jumlahProduksi) { this.jumlahProduksi = jumlahProduksi; }

    public double getTotalHppProduksi() { return totalHppProduksi; }
    public void setTotalHppProduksi(double totalHppProduksi) { this.totalHppProduksi = totalHppProduksi; }
}