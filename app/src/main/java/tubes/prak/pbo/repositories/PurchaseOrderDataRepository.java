package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.models.Vendor;

public class PurchaseOrderDataRepository {
    public List<Vendor> findVendors() throws SQLException {
        List<Vendor> vendors = new ArrayList<>();
        String sql = "SELECT id_vendor, nama_vendor, no_telp, alamat_vendor FROM vendor ORDER BY nama_vendor";

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                vendors.add(new Vendor(
                        resultSet.getInt("id_vendor"),
                        resultSet.getString("nama_vendor"),
                        resultSet.getString("no_telp"),
                        resultSet.getString("alamat_vendor")));
            }
        }
        return vendors;
    }

    public List<RawMaterial> findRawMaterials() throws SQLException {
        List<RawMaterial> rawMaterials = new ArrayList<>();
        String sql = "SELECT id_raw_material, nama_material, satuan_material, "
                + "harga_rata2_bahan_baku, jumlah_stok, batas_kritis, image_url "
                + "FROM raw_material ORDER BY nama_material";

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rawMaterials.add(new RawMaterial(
                        resultSet.getInt("id_raw_material"),
                        resultSet.getString("nama_material"),
                        Satuan.valueOf(resultSet.getString("satuan_material")),
                        resultSet.getDouble("harga_rata2_bahan_baku"),
                        resultSet.getDouble("batas_kritis"),
                        resultSet.getDouble("jumlah_stok"), 
                        resultSet.getString("image_url"))
                    );
            }
        }
        return rawMaterials;
    }

    public void insert(Date purchaseDate, double actualPrice, String status, Date receivedDate,
            int vendorId, int rawMaterialId, int createdBy, int quantity) throws SQLException {
        String sql = "INSERT INTO purchase_order "
                + "(tanggal_pembelian, harga_beli_aktual, status_po, tanggal_diterima, "
                + "id_vendor, id_raw_material, created_by, jumlah_dibeli) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, purchaseDate);
            statement.setDouble(2, actualPrice);
            statement.setString(3, status);
            if (receivedDate == null) {
                statement.setNull(4, java.sql.Types.DATE);
            } else {
                statement.setDate(4, receivedDate);
            }
            statement.setInt(5, vendorId);
            statement.setInt(6, rawMaterialId);
            statement.setInt(7, createdBy);
            statement.setInt(8, quantity);
            statement.executeUpdate();
        }
    }

    public List<HistoryRecord> findHistory() throws SQLException {
        List<HistoryRecord> history = new ArrayList<>();
        String sql = "SELECT po.tanggal_pembelian, v.nama_vendor, rm.nama_material, "
                + "po.jumlah_dibeli, po.harga_beli_aktual, po.status_po, po.tanggal_diterima, po.created_by "
                + "FROM purchase_order po "
                + "JOIN vendor v ON po.id_vendor = v.id_vendor "
                + "JOIN raw_material rm ON po.id_raw_material = rm.id_raw_material "
                + "ORDER BY po.tanggal_pembelian DESC";

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                history.add(new HistoryRecord(
                        resultSet.getDate("tanggal_pembelian"),
                        resultSet.getString("nama_vendor"),
                        resultSet.getString("nama_material"),
                        resultSet.getInt("jumlah_dibeli"),
                        resultSet.getDouble("harga_beli_aktual"),
                        resultSet.getString("status_po"),
                        resultSet.getDate("tanggal_diterima"),
                        resultSet.getInt("created_by")));
            }
        }
        return history;
    }

    public static final class HistoryRecord {
        private final Date purchaseDate;
        private final String vendorName;
        private final String rawMaterialName;
        private final int quantity;
        private final double actualPrice;
        private final String status;
        private final Date receivedDate;
        private final int createdBy;

        public HistoryRecord(Date purchaseDate, String vendorName, String rawMaterialName, int quantity,
                double actualPrice, String status, Date receivedDate, int createdBy) {
            this.purchaseDate = purchaseDate;
            this.vendorName = vendorName;
            this.rawMaterialName = rawMaterialName;
            this.quantity = quantity;
            this.actualPrice = actualPrice;
            this.status = status;
            this.receivedDate = receivedDate;
            this.createdBy = createdBy;
        }

        public Object[] toTableRow() {
            return new Object[] {
                    purchaseDate,
                    vendorName,
                    rawMaterialName,
                    quantity,
                    actualPrice,
                    status,
                    receivedDate,
                    createdBy
            };
        }
    }
}
