package tubes.prak.pbo.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import tubes.prak.pbo.ENUM.JenisPos;
import tubes.prak.pbo.ENUM.StatusPo;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.PurchaseOrder;
import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.models.Vendor;
import tubes.prak.pbo.repositories.FinanceRepository;
import tubes.prak.pbo.repositories.PurchaseOrderRepository;
import tubes.prak.pbo.repositories.RawMaterialRepository;
import tubes.prak.pbo.repositories.VendorRepository;
import tubes.prak.pbo.util.Session;

public class PurchaseOrderController {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final VendorRepository vendorRepository = new VendorRepository();
    private final PurchaseOrderRepository poRepository = new PurchaseOrderRepository();
    private final RawMaterialRepository rawMaterialRepository = new RawMaterialRepository();
    private final FinanceRepository financeRepository = new FinanceRepository();

    // ==================== VENDOR ====================

    public List<Vendor> getAllVendors() throws SQLException {
        return vendorRepository.findAllVendor();
    }

    public void tambahVendor(String namaVendor, String noTelp, String alamatVendor) throws Exception {
        if (namaVendor == null || namaVendor.isBlank()) {
            throw new Exception("Nama vendor wajib diisi!");
        }
        if (noTelp == null || noTelp.isBlank()) {
            throw new Exception("Nomor telepon vendor wajib diisi!");
        }
        if (alamatVendor == null || alamatVendor.isBlank()) {
            throw new Exception("Alamat vendor wajib diisi!");
        }
        if (vendorRepository.isNamaVendorExists(namaVendor)) {
            throw new Exception("Vendor \"" + namaVendor.trim() + "\" sudah terdaftar!");
        }

        Vendor vendor = new Vendor(0, namaVendor.trim(), noTelp.trim(), alamatVendor.trim());
        vendorRepository.insertVendor(vendor);
    }

    // ==================== RAW MATERIAL & FINANCE HELPER ====================

    public List<RawMaterial> getAllRawMaterials() {
        return rawMaterialRepository.findAllRawMaterial();
    }

    public double getAvailableCash() throws SQLException {
        return financeRepository.getTotalCash();
    }

    public List<PurchaseOrder> getAllPurchaseOrders() throws SQLException {
        return poRepository.findAllPurchaseOrders();
    }

    // Method kalkulasi subtotal & harga per satuan untuk form PO
    public double hitungSubtotal(double hargaBeliAktual) {
        return Math.max(0.0, hargaBeliAktual);
    }

    public double hitungHargaPerSatuan(double jumlahDibeli, double hargaBeliAktual) {
        if (jumlahDibeli <= 0 || hargaBeliAktual <= 0) {
            return 0.0;
        }
        return hargaBeliAktual / jumlahDibeli;
    }

    // ==================== TRANSAKSI PURCHASE ORDER ====================

    public void createPurchaseOrder(Vendor vendor, RawMaterial material,
                                    double jumlahDibeli, double hargaBeliAktual) throws Exception {
        // 1. Validasi keberadaan vendor
        List<Vendor> daftarVendor = vendorRepository.findAllVendor();
        if (daftarVendor.isEmpty() || vendor == null) {
            throw new Exception("Data vendor kosong atau belum dipilih! Silakan tambahkan vendor terlebih dahulu.");
        }

        // 2. Validasi raw material, jumlah, dan harga
        if (material == null) {
            throw new Exception("Raw material harus dipilih terlebih dahulu!");
        }
        if (jumlahDibeli <= 0) {
            throw new Exception("Jumlah pembelian harus lebih dari 0 " + material.getSatuan().name() + "!");
        }
        if (hargaBeliAktual <= 0) {
            throw new Exception("Harga beli aktual harus lebih dari Rp 0!");
        }

        double subtotalPo = hitungSubtotal(hargaBeliAktual);
        Integer userId = (Session.getSession() != null) ? Session.getSession().getId() : null;

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 3. Validasi kecukupan saldo cash dari finance_ledger
            double cashTersedia = financeRepository.getTotalCash(conn);
            if (cashTersedia < subtotalPo) {
                throw new Exception(
                        "Saldo kas tidak mencukupi untuk membuat Purchase Order!\n" +
                                "Cash Tersedia: Rp " + CURRENCY_FORMAT.format(cashTersedia) + "\n" +
                                "Total PO: Rp " + CURRENCY_FORMAT.format(subtotalPo)
                );
            }

            // 4. Insert PO dengan status default PAID (stok raw_material belum bertambah)
            PurchaseOrder newPo = new PurchaseOrder(
                    0,
                    material.getId(),
                    vendor.getIdVendor(),
                    userId,
                    null,
                    jumlahDibeli,
                    subtotalPo,
                    StatusPo.PAID,
                    null,
                    vendor.getNamaVendor(),
                    material.getNama(),
                    material.getSatuan().name(),
                    null
            );

            int newPoId = poRepository.insertPurchaseOrder(conn, newPo);

            // 5. Potong kas dengan mencatat transaksi PEMBELIAN di finance_ledger
            String deskripsiLedger = String.format(
                    "PEMBELIAN - PO #%d | %s (%.2f %s) dari %s",
                    newPoId,
                    material.getNama(),
                    jumlahDibeli,
                    material.getSatuan().name(),
                    vendor.getNamaVendor()
            );

            financeRepository.insertLedger(
                    conn,
                    newPoId,
                    null,
                    JenisPos.PEMBELIAN,
                    subtotalPo,
                    deskripsiLedger,
                    userId
            );

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // Konfirmasi barang tiba (ARRIVED): update status PO, tambah stok, & hitung ulang harga rata-rata
    public void confirmPoArrived(int idPo) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            PurchaseOrder po = poRepository.findByIdForUpdate(conn, idPo);
            if (po == null) {
                throw new Exception("Data Purchase Order #" + idPo + " tidak ditemukan!");
            }
            if (po.getStatusPo() == StatusPo.ARRIVED) {
                throw new Exception("Purchase Order #" + idPo + " sudah berstatus ARRIVED!");
            }

            // 1. Ubah status PO menjadi ARRIVED & isi tanggal_diterima
            poRepository.markAsArrived(conn, idPo);

            // 2. Tambah stok raw_material & perbarui harga_rata2_bahan_baku
            rawMaterialRepository.tambahStokDanUpdateHargaRata2(
                    conn,
                    po.getIdRawMaterial(),
                    po.getJumlahDibeli(),
                    po.getHargaBeliAktual()
            );

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}