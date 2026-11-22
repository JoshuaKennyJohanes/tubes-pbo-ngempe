package tubes.prak.pbo.models;

public class SalesOrderDetail {
    private int idOrderDetails;
    private int idOrder;
    private int idProduk;
    private String namaProduk;
    private int jumlahTerjual;
    private double hargaJualAktual;
    private double hppAktualSaatTerjual;

    public SalesOrderDetail(int idOrderDetails, int idOrder, int idProduk,
                            int jumlahTerjual, double hargaJualAktual, double hppAktualSaatTerjual) {
        this.idOrderDetails = idOrderDetails;
        this.idOrder = idOrder;
        this.idProduk = idProduk;
        this.jumlahTerjual = jumlahTerjual;
        this.hargaJualAktual = hargaJualAktual;
        this.hppAktualSaatTerjual = hppAktualSaatTerjual;
    }

    public int getIdOrderDetails() {
        return idOrderDetails;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public int getJumlahTerjual() {
        return jumlahTerjual;
    }

    public double getHargaJualAktual() {
        return hargaJualAktual;
    }

    public double getHppAktualSaatTerjual() {
        return hppAktualSaatTerjual;
    }

    public double getSubtotal() {
        return jumlahTerjual * hargaJualAktual;
    }
}