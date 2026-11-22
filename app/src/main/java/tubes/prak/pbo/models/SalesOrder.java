package tubes.prak.pbo.models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SalesOrder {
    private int idOrder;
    private int idUser;
    private String namaUser;
    private String alamatUser;
    private Date tanggalPesan;
    private String caraBayar;
    private String statusPembayaran;
    private final List<SalesOrderDetail> details;

    public SalesOrder(int idOrder, int idUser, String namaUser, String alamatUser,
                      Date tanggalPesan, String caraBayar, String statusPembayaran) {
        this.idOrder = idOrder;
        this.idUser = idUser;
        this.namaUser = namaUser;
        this.alamatUser = alamatUser;
        this.tanggalPesan = tanggalPesan;
        this.caraBayar = caraBayar;
        this.statusPembayaran = statusPembayaran;
        this.details = new ArrayList<>();
    }

    public void tambahDetail(SalesOrderDetail detail) {
        if (detail != null) {
            this.details.add(detail);
        }
    }

    public int getIdOrder() {
        return idOrder;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public String getAlamatUser() {
        return alamatUser;
    }

    public String getUserDisplay() {
        String nama = (namaUser != null && !namaUser.isBlank()) ? namaUser : "Tanpa Nama";
        return idUser + "#" + nama;
    }

    public Date getTanggalPesan() {
        return tanggalPesan;
    }

    public String getCaraBayar() {
        return caraBayar;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public List<SalesOrderDetail> getDetails() {
        return details;
    }

    public double getTotalTransaksi() {
        double total = 0.0;
        for (SalesOrderDetail detail : details) {
            total += detail.getSubtotal();
        }
        return total;
    }
}