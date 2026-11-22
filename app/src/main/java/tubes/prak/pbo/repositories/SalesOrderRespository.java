package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;
import tubes.prak.pbo.models.User;

public class SalesOrderRespository {

    public void loadData(DefaultTableModel modelPO, int id) {
        modelPO.setRowCount(0);

        String sql = """
            SELECT
                so.id_order,
                so.tanggal_pesan,
                so.cara_bayar,
                so.status_pembayaran,
                SUM(sod.jumlah_terjual) AS jumlah
            FROM sales_order so
            INNER JOIN sales_order_details sod
                ON so.id_order = sod.id_order
            WHERE so.id_user = ?
            GROUP BY
                so.id_order,
                so.tanggal_pesan,
                so.cara_bayar,
                so.status_pembayaran
            """;

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("id_order"),
                            rs.getDate("tanggal_pesan"),
                            rs.getInt("jumlah"),
                            rs.getString("cara_bayar"),
                            rs.getString("status_pembayaran")
                    };
                    modelPO.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkout(User user) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            if (user.getBarangKeranjang().size() != 0) {
                String sqlOrder = """
                    INSERT INTO sales_order
                    (id_user, tanggal_pesan, cara_bayar, status_pembayaran)
                    VALUES (?, CURRENT_DATE, ?, ?)
                    RETURNING id_order
                    """;

                PreparedStatement psOrder = conn.prepareStatement(sqlOrder);
                psOrder.setInt(1, user.getId());
                psOrder.setString(2, "QRIS");
                psOrder.setString(3, "Pending");

                ResultSet rs = psOrder.executeQuery();
                int idOrder = -1;
                if (rs.next()) {
                    idOrder = rs.getInt("id_order");
                }

                String sqlDetail = """
                    INSERT INTO sales_order_details
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
                    FROM produk
                    WHERE id_produk = ?
                    """;

                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                for (BarangKeranjang barang : user.getBarangKeranjang()) {
                    psDetail.setInt(1, idOrder);
                    psDetail.setInt(2, barang.getJumlah());
                    psDetail.setInt(3, barang.getId_produk());
                    psDetail.addBatch();
                }

                psDetail.executeBatch();
                conn.commit();
                return true;
            }
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Mengambil semua Sales Order + Details + Info Pembeli (dengan filter opsional Bulan & Tahun)
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

    // Mengambil daftar tahun unik dari transaksi yang ada untuk isi dropdown filter
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