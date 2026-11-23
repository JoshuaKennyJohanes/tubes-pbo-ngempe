package tubes.prak.pbo.models;

import java.sql.Date;
import tubes.prak.pbo.ENUM.JenisPos;

public class FinanceLedger {
    private int idLedger;
    private Integer idPo;
    private Integer idOrder;
    private JenisPos jenisPos;
    private double nominal;
    private String deskripsi;
    private Date tanggalTransaksi;
    private Integer createdBy;
    private String namaCreator;

    public FinanceLedger(int idLedger, Integer idPo, Integer idOrder, JenisPos jenisPos,
                         double nominal, String deskripsi, Date tanggalTransaksi,
                         Integer createdBy, String namaCreator) {
        this.idLedger = idLedger;
        this.idPo = idPo;
        this.idOrder = idOrder;
        this.jenisPos = jenisPos;
        this.nominal = nominal;
        this.deskripsi = deskripsi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.createdBy = createdBy;
        this.namaCreator = namaCreator;
    }

    public int getIdLedger() {
        return idLedger;
    }

    public Integer getIdPo() {
        return idPo;
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public JenisPos getJenisPos() {
        return jenisPos;
    }

    public double getNominal() {
        return nominal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public Date getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public String getNamaCreator() {
        return namaCreator;
    }

    public String getReferensiDisplay() {
        if (idOrder != null) {
            return "Sales Order #" + idOrder;
        }
        if (idPo != null) {
            return "Purchase Order #" + idPo;
        }
        if (jenisPos == JenisPos.OPNAME) {
            return "Stock Opname (Warehouse)";
        }
        if (jenisPos == JenisPos.DEPOSIT) {
            return "Deposit Kas";
        }
        return "-";
    }

    public String getCreatorDisplay() {
        if (createdBy == null) {
            return "-";
        }
        String nama = (namaCreator != null && !namaCreator.isBlank()) ? namaCreator : "User";
        return createdBy + "#" + nama;
    }

    public boolean isArusMasuk() {
        return jenisPos == JenisPos.PENJUALAN || jenisPos == JenisPos.DEPOSIT;
    }
}