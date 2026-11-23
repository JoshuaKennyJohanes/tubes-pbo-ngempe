package tubes.prak.pbo.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tubes.prak.pbo.ENUM.JenisPos;
import tubes.prak.pbo.ENUM.StatusPembayaran;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;
import tubes.prak.pbo.repositories.FinanceRepository;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.repositories.SalesOrderRespository;
import tubes.prak.pbo.models.users.UserBuyer;

public class SalesOrderController {

    private final SalesOrderRespository salesOrderRepository = new SalesOrderRespository();
    private final FinanceRepository financeRepository = new FinanceRepository();
    private final ItemController itemController = new ItemController();

    // ==================== MANAGER METHODS ====================

    public List<SalesOrder> getAllSalesOrders() throws SQLException {
        return getSalesOrdersByFilter(null, null);
    }

    public List<SalesOrder> getSalesOrdersByFilter(Integer bulan, Integer tahun) throws SQLException {
        List<SalesOrder> orders = salesOrderRepository.findAllWithDetails(bulan, tahun);
        List<Produk> allProduk = ProdukRepository.findAllProduk();

        Map<Integer, String> namaProdukMap = new HashMap<>();
        for (Produk produk : allProduk) {
            namaProdukMap.put(produk.getIdProduk(), produk.getNamaProduk());
        }

        for (SalesOrder order : orders) {
            for (SalesOrderDetail detail : order.getDetails()) {
                String namaProduk = namaProdukMap.getOrDefault(
                        detail.getIdProduk(),
                        "Produk #" + detail.getIdProduk()
                );
                detail.setNamaProduk(namaProduk);
            }
        }

        return orders;
    }

    public List<Integer> getAvailableYears() throws SQLException {
        return salesOrderRepository.findAvailableYears();
    }

    // ==================== PEMBELI METHODS ====================

    public List<SalesOrder> getSalesOrdersByUser(int idUser) throws SQLException {
        return salesOrderRepository.findByUserIdWithDetails(idUser);
    }

    public int checkoutKeranjang(UserBuyer user) throws Exception {
        if (user == null) {
            throw new Exception("Sesi user tidak ditemukan!");
        }
        if (user.getBarangKeranjang() == null || user.getBarangKeranjang().isEmpty()) {
            throw new Exception("Keranjang masih kosong! Silakan pilih produk terlebih dahulu.");
        }

        int idOrder = salesOrderRepository.checkout(user);
        user.setKosongKeranjang();
        return idOrder;
    }

    // Ubah status PENDING -> PAID dan catat Revenue ke finance_ledger
    public void bayarOrder(int idOrder) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            SalesOrder order = salesOrderRepository.findOrderByIdForUpdate(conn, idOrder);
            if (order == null) {
                throw new Exception("Pesanan #" + idOrder + " tidak ditemukan!");
            }

            StatusPembayaran currentStatus = StatusPembayaran.fromString(order.getStatusPembayaran());
            if (currentStatus == StatusPembayaran.PAID) {
                throw new Exception("Pesanan #" + idOrder + " sudah dibayar (PAID)!");
            }
            if (currentStatus == StatusPembayaran.CANCELLED) {
                throw new Exception("Pesanan #" + idOrder + " sudah dibatalkan dan tidak dapat dibayar!");
            }

            // 1. Update status menjadi PAID
            salesOrderRepository.updateStatusPembayaran(conn, idOrder, StatusPembayaran.PAID);

            // 2. Hitung total pembayaran dan catat sebagai PENJUALAN (Revenue) di finance_ledger
            double totalRevenue = order.getTotalTransaksi();
            String deskripsi = "PENJUALAN - Sales Order #" + idOrder + " (" + order.getCaraBayar() + ")";

            financeRepository.insertLedger(
                    conn,
                    null,
                    idOrder,
                    JenisPos.PENJUALAN,
                    totalRevenue,
                    deskripsi,
                    order.getIdUser()
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

    // Batalkan pesanan PENDING -> CANCELLED dan kembalikan stok produk
    public void batalkanOrder(int idOrder) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            SalesOrder order = salesOrderRepository.findOrderByIdForUpdate(conn, idOrder);
            if (order == null) {
                throw new Exception("Pesanan #" + idOrder + " tidak ditemukan!");
            }

            StatusPembayaran currentStatus = StatusPembayaran.fromString(order.getStatusPembayaran());
            if (currentStatus != StatusPembayaran.PENDING) {
                throw new Exception("Hanya pesanan berstatus PENDING yang dapat dibatalkan!");
            }

            // 1. Update status menjadi CANCELLED
            salesOrderRepository.updateStatusPembayaran(conn, idOrder, StatusPembayaran.CANCELLED);

            // 2. Kembalikan stok setiap produk di dalam detail pesanan
            for (SalesOrderDetail detail : order.getDetails()) {
                itemController.kembalikanStokProduk(conn, detail.getIdProduk(), detail.getJumlahTerjual());
            }

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