package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.StatusPo;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.PurchaseOrder;

public class PurchaseOrderRepository {

    public List<PurchaseOrder> findAllPurchaseOrders() throws SQLException {
        String sql = """
            SELECT
                po.id_po,
                po.id_raw_material,
                po.id_vendor,
                po.created_by,
                po.tanggal_pembelian,
                po.jumlah_dibeli,
                po.harga_beli_aktual,
                po.status_po,
                po.tanggal_diterima,
                v.nama_vendor,
                rm.nama_material,
                rm.satuan_material,
                u.nama AS creator_nama
            FROM public.purchase_order po
            JOIN public.vendor v ON po.id_vendor = v.id_vendor
            JOIN public.raw_material rm ON po.id_raw_material = rm.id_raw_material
            LEFT JOIN public.users u ON po.created_by = u.id_user
            ORDER BY po.tanggal_pembelian DESC, po.id_po DESC
            """;

        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insertPurchaseOrder(Connection conn, PurchaseOrder po) throws SQLException {
        String sql = """
            INSERT INTO public.purchase_order (
                id_raw_material, id_vendor, created_by, tanggal_pembelian,
                jumlah_dibeli, harga_beli_aktual, status_po, tanggal_diterima
            )
            VALUES (?, ?, ?, CURRENT_DATE, ?, ?, ?, NULL)
            RETURNING id_po
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, po.getIdRawMaterial());
            stmt.setInt(2, po.getIdVendor());

            if (po.getCreatedBy() != null) {
                stmt.setInt(3, po.getCreatedBy());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setDouble(4, po.getJumlahDibeli());
            stmt.setDouble(5, po.getHargaBeliAktual());
            stmt.setString(6, po.getStatusPo().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_po");
                }
                throw new SQLException("Gagal mendapatkan ID Purchase Order baru.");
            }
        }
    }

    public PurchaseOrder findByIdForUpdate(Connection conn, int idPo) throws SQLException {
        String sql = """
            SELECT
                po.id_po,
                po.id_raw_material,
                po.id_vendor,
                po.created_by,
                po.tanggal_pembelian,
                po.jumlah_dibeli,
                po.harga_beli_aktual,
                po.status_po,
                po.tanggal_diterima,
                v.nama_vendor,
                rm.nama_material,
                rm.satuan_material,
                u.nama AS creator_nama
            FROM public.purchase_order po
            JOIN public.vendor v ON po.id_vendor = v.id_vendor
            JOIN public.raw_material rm ON po.id_raw_material = rm.id_raw_material
            LEFT JOIN public.users u ON po.created_by = u.id_user
            WHERE po.id_po = ?
            FOR UPDATE OF po
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public void markAsArrived(Connection conn, int idPo) throws SQLException {
        String sql = """
            UPDATE public.purchase_order
            SET status_po = ?, tanggal_diterima = CURRENT_DATE
            WHERE id_po = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, StatusPo.ARRIVED.name());
            stmt.setInt(2, idPo);
            stmt.executeUpdate();
        }
    }

    private PurchaseOrder mapRow(ResultSet rs) throws SQLException {
        int creatorVal = rs.getInt("created_by");
        Integer createdBy = rs.wasNull() ? null : creatorVal;

        return new PurchaseOrder(
                rs.getInt("id_po"),
                rs.getInt("id_raw_material"),
                rs.getInt("id_vendor"),
                createdBy,
                rs.getDate("tanggal_pembelian"),
                rs.getDouble("jumlah_dibeli"),
                rs.getDouble("harga_beli_aktual"),
                StatusPo.fromString(rs.getString("status_po")),
                rs.getDate("tanggal_diterima"),
                rs.getString("nama_vendor"),
                rs.getString("nama_material"),
                rs.getString("satuan_material"),
                rs.getString("creator_nama")
        );
    }
}