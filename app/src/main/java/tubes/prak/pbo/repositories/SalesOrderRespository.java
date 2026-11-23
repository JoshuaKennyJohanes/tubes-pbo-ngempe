package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tubes.prak.pbo.ENUM.StatusPembayaran;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.*;
import tubes.prak.pbo.models.users.UserBuyer;

public class SalesOrderRespository {

    private final ProdukRepository produkRepository = new ProdukRepository();

    // Checkout: simpan sales_order (PENDING), detail produk, dan kurangi stok produk
    public int checkout(UserBuyer user) throws Exception {
        if (user == null || user.getBarangKeranjang() == null || user.getBarangKeranjang().isEmpty()) {
            throw new Exception("Keranjang masih kosong!");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            String sqlOrder = """
                INSERT INTO public.sales_order
                (id_user, tanggal_pesan, cara_bayar, status_pembayaran)
                VALUES (?, CURRENT_DATE, ?, ?)
                RETURNING id_order
                """;

            int idOrder;
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder)) {
                psOrder.setInt(1, user.getId());
                psOrder.setString(2, "QRIS");
                psOrder.setString(3, StatusPembayaran.PENDING.name());

                try (ResultSet rs = psOrder.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Gagal membuat ID Sales Order baru.");
                    }
                    idOrder = rs.getInt("id_order");
                }
            }

            String sqlDetail = """
                INSERT INTO public.sales_order_details
                (
                    id_order,
                    id_produk,
                    jumlah_terjual,
                    harga_jual_aktual,
                    hpp_aktual_saat_terjual
                )
                SELECT
                    ?,
                    id_produk,
                    ?,
                    harga_jual,
                    harga_rata2_produk
                FROM public.produk
                WHERE id_produk = ?
                """;

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for (BarangKeranjang barang : user.getBarangKeranjang()) {
                    // Validasi & kurangi stok produk saat checkout
                    produkRepository.kurangiStokProduk(conn, barang.getId_produk(), barang.getJumlah());

                    psDetail.setInt(1, idOrder);
                    psDetail.setInt(2, barang.getJumlah());
                    psDetail.setInt(3, barang.getId_produk());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();
            return idOrder;

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

    // Mengambil riwayat transaksi milik user tertentu beserta detail item & nama produknya
    public List<SalesOrder> findByUserIdWithDetails(int idUser) throws SQLException {
        String sql = """
            SELECT
                so.id_order,
                so.id_user,
                u.nama AS nama_user,
                u.alamat AS alamat_user,
                so.tanggal_pesan,
                so.cara_bayar,
                so.status_pembayaran,
                sod.id_order_details,
                sod.id_produk,
                p.nama_produk,
                sod.jumlah_terjual,
                sod.harga_jual_aktual,
                sod.hpp_aktual_saat_terjual
            FROM public.sales_order so
            JOIN public.users u ON so.id_user = u.id_user
            LEFT JOIN public.sales_order_details sod ON so.id_order = sod.id_order
            LEFT JOIN public.produk p ON sod.id_produk = p.id_produk
            WHERE so.id_user = ?
            ORDER BY so.tanggal_pesan DESC, so.id_order DESC, sod.id_order_details ASC
            """;

        Map<Integer, SalesOrder> orderMap = new LinkedHashMap<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idOrder = rs.getInt("id_order");

                    SalesOrder order = orderMap.computeIfAbsent(idOrder, id -> {
                        try {
                            return new SalesOrder(
                                    id,
                                    rs.getInt("id_user"),
                                    rs.getString("nama_user"),
                                    rs.getString("alamat_user"),
                                    rs.getDate("tanggal_pesan"),
                                    rs.getString("cara_bayar"),
                                    rs.getString("status_pembayaran")
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    int idOrderDetails = rs.getInt("id_order_details");
                    if (!rs.wasNull()) {
                        SalesOrderDetail detail = new SalesOrderDetail(
                                idOrderDetails,
                                idOrder,
                                rs.getInt("id_produk"),
                                rs.getInt("jumlah_terjual"),
                                rs.getDouble("harga_jual_aktual"),
                                rs.getDouble("hpp_aktual_saat_terjual")
                        );
                        String namaProduk = rs.getString("nama_produk");
                        detail.setNamaProduk(namaProduk != null ? namaProduk : "Produk #" + detail.getIdProduk());
                        order.tambahDetail(detail);
                    }
                }
            }
        }
        return new ArrayList<>(orderMap.values());
    }

    // Mengunci baris sales_order untuk proses Bayar / Batalkan
    public SalesOrder findOrderByIdForUpdate(Connection conn, int idOrder) throws SQLException {
        String sqlLock = """
            SELECT
                so.id_order,
                so.id_user,
                u.nama AS nama_user,
                u.alamat AS alamat_user,
                so.tanggal_pesan,
                so.cara_bayar,
                so.status_pembayaran
            FROM public.sales_order so
            JOIN public.users u ON so.id_user = u.id_user
            WHERE so.id_order = ?
            FOR UPDATE OF so
            """;

        SalesOrder order = null;
        try (PreparedStatement stmt = conn.prepareStatement(sqlLock)) {
            stmt.setInt(1, idOrder);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    order = new SalesOrder(
                            rs.getInt("id_order"),
                            rs.getInt("id_user"),
                            rs.getString("nama_user"),
                            rs.getString("alamat_user"),
                            rs.getDate("tanggal_pesan"),
                            rs.getString("cara_bayar"),
                            rs.getString("status_pembayaran")
                    );
                }
            }
        }

        if (order == null) {
            return null;
        }

        String sqlDetails = """
            SELECT
                sod.id_order_details,
                sod.id_order,
                sod.id_produk,
                p.nama_produk,
                sod.jumlah_terjual,
                sod.harga_jual_aktual,
                sod.hpp_aktual_saat_terjual
            FROM public.sales_order_details sod
            LEFT JOIN public.produk p ON sod.id_produk = p.id_produk
            WHERE sod.id_order = ?
            ORDER BY sod.id_order_details ASC
            """;

        try (PreparedStatement stmtDet = conn.prepareStatement(sqlDetails)) {
            stmtDet.setInt(1, idOrder);
            try (ResultSet rsDet = stmtDet.executeQuery()) {
                while (rsDet.next()) {
                    SalesOrderDetail detail = new SalesOrderDetail(
                            rsDet.getInt("id_order_details"),
                            rsDet.getInt("id_order"),
                            rsDet.getInt("id_produk"),
                            rsDet.getInt("jumlah_terjual"),
                            rsDet.getDouble("harga_jual_aktual"),
                            rsDet.getDouble("hpp_aktual_saat_terjual")
                    );
                    String namaProduk = rsDet.getString("nama_produk");
                    detail.setNamaProduk(namaProduk != null ? namaProduk : "Produk #" + detail.getIdProduk());
                    order.tambahDetail(detail);
                }
            }
        }

        return order;
    }

    public void updateStatusPembayaran(Connection conn, int idOrder, StatusPembayaran status) throws SQLException {
        String sql = """
            UPDATE public.sales_order
            SET status_pembayaran = ?
            WHERE id_order = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, idOrder);
            stmt.executeUpdate();
        }
    }

    // Mengambil semua Sales Order + Details + Info Pembeli (untuk sisi Manager)
    public List<SalesOrder> findAllWithDetails(Integer bulan, Integer tahun) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT
                so.id_order,
                so.id_user,
                u.nama AS nama_user,
                u.alamat AS alamat_user,
                so.tanggal_pesan,
                so.cara_bayar,
                so.status_pembayaran,
                sod.id_order_details,
                sod.id_produk,
                sod.jumlah_terjual,
                sod.harga_jual_aktual,
                sod.hpp_aktual_saat_terjual
            FROM public.sales_order so
            JOIN public.users u ON so.id_user = u.id_user
            LEFT JOIN public.sales_order_details sod ON so.id_order = sod.id_order
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();
        if (bulan != null) {
            sql.append(" AND EXTRACT(MONTH FROM so.tanggal_pesan) = ? ");
            params.add(bulan);
        }
        if (tahun != null) {
            sql.append(" AND EXTRACT(YEAR FROM so.tanggal_pesan) = ? ");
            params.add(tahun);
        }

        sql.append(" ORDER BY so.tanggal_pesan DESC, so.id_order DESC, sod.id_order_details ASC ");

        Map<Integer, SalesOrder> orderMap = new LinkedHashMap<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idOrder = rs.getInt("id_order");

                    SalesOrder order = orderMap.computeIfAbsent(idOrder, id -> {
                        try {
                            return new SalesOrder(
                                    id,
                                    rs.getInt("id_user"),
                                    rs.getString("nama_user"),
                                    rs.getString("alamat_user"),
                                    rs.getDate("tanggal_pesan"),
                                    rs.getString("cara_bayar"),
                                    rs.getString("status_pembayaran")
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    int idOrderDetails = rs.getInt("id_order_details");
                    if (!rs.wasNull()) {
                        SalesOrderDetail detail = new SalesOrderDetail(
                                idOrderDetails,
                                idOrder,
                                rs.getInt("id_produk"),
                                rs.getInt("jumlah_terjual"),
                                rs.getDouble("harga_jual_aktual"),
                                rs.getDouble("hpp_aktual_saat_terjual")
                        );
                        order.tambahDetail(detail);
                    }
                }
            }
        }
        return new ArrayList<>(orderMap.values());
    }

    public List<Integer> findAvailableYears() throws SQLException {
        String sql = """
            SELECT DISTINCT EXTRACT(YEAR FROM tanggal_pesan)::int AS tahun
            FROM public.sales_order
            WHERE tanggal_pesan IS NOT NULL
            ORDER BY tahun DESC
            """;

        List<Integer> years = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("tahun"));
            }
        }
        return years;
    }
}