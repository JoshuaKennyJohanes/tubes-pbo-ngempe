package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.config.DatabaseConfig;
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
}
