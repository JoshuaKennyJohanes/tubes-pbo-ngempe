package tubes.prak.pbo.models;

import java.sql.Date;
import tubes.prak.pbo.ENUM.StatusPo;

public class PurchaseOrder {
    private int idPo;
    private int idRawMaterial;
    private int idVendor;
    private Integer createdBy;
    private Date tanggalPembelian;
    private double jumlahDibeli;
    private double hargaBeliAktual; // Harga total / borongan PO
    private StatusPo statusPo;
    private Date tanggalDiterima;

    // Field hasil JOIN untuk tampilan History Card
    private String namaVendor;
    private String namaMaterial;
    private String satuanMaterial;
    private String namaCreator;

    public PurchaseOrder(int idPo, int idRawMaterial, int idVendor, Integer createdBy,
                         Date tanggalPembelian, double jumlahDibeli, double hargaBeliAktual,
                         StatusPo statusPo, Date tanggalDiterima,
                         String namaVendor, String namaMaterial, String satuanMaterial, String namaCreator) {
        this.idPo = idPo;
        this.idRawMaterial = idRawMaterial;
        this.idVendor = idVendor;
        this.createdBy = createdBy;
        this.tanggalPembelian = tanggalPembelian;
        this.jumlahDibeli = jumlahDibeli;
        this.hargaBeliAktual = hargaBeliAktual;
        this.statusPo = statusPo;
        this.tanggalDiterima = tanggalDiterima;
        this.namaVendor = namaVendor;
        this.namaMaterial = namaMaterial;
        this.satuanMaterial = satuanMaterial;
        this.namaCreator = namaCreator;
    }

    // Method perhitungan subtotal PO (harga aktual/borongan)
    public double getSubtotal() {
        return hargaBeliAktual;
    }

    // Method perhitungan harga per 1 satuan dari PO ini
    public double getHargaPerSatuan() {
        return (jumlahDibeli > 0) ? (hargaBeliAktual / jumlahDibeli) : 0.0;
    }

    public int getIdPo() {
        return idPo;
    }

    public int getIdRawMaterial() {
        return idRawMaterial;
    }

    public int getIdVendor() {
        return idVendor;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public Date getTanggalPembelian() {
        return tanggalPembelian;
    }

    public double getJumlahDibeli() {
        return jumlahDibeli;
    }

    public double getHargaBeliAktual() {
        return hargaBeliAktual;
    }

    public StatusPo getStatusPo() {
        return statusPo;
    }

    public Date getTanggalDiterima() {
        return tanggalDiterima;
    }

    public String getNamaVendor() {
        return namaVendor;
    }

    public String getNamaMaterial() {
        return namaMaterial;
    }

    public String getSatuanMaterial() {
        return satuanMaterial;
    }

    public String getNamaCreator() {
        return namaCreator;
    }

    public String getCreatorDisplay() {
        if (createdBy == null) {
            return "-";
        }
        String nama = (namaCreator != null && !namaCreator.isBlank()) ? namaCreator : "User";
        return createdBy + "#" + nama;
    }
}