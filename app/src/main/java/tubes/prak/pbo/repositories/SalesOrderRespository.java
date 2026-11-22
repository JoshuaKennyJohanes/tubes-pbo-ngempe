package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.User;
;

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
            try(ResultSet rs = ps.executeQuery();){

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
            if(user.getBarangKeranjang().size() != 0){

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

                // INSERT DETAIL
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

                PreparedStatement psDetail =
                        conn.prepareStatement(sqlDetail);

                for (BarangKeranjang barang : user.getBarangKeranjang()) {

                    psDetail.setInt(1, idOrder);
                    psDetail.setInt(2, barang.getJumlah());
                    psDetail.setInt(3, barang.getId_produk());

                    psDetail.addBatch();
                }

                psDetail.executeBatch();

                conn.commit();
                return true;
                // INSERT SALES ORDER
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
}