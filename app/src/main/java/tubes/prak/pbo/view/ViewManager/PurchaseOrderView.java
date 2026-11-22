package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Vendor;
import tubes.prak.pbo.repositories.VendorRepository;
import tubes.prak.pbo.util.Session;

public class PurchaseOrderView extends JPanel {
    private final JComboBox<String> vendorList = new JComboBox<>();
    private final JComboBox<String> rawMaterialList = new JComboBox<>();
    private final ArrayList<Vendor> vendors = new ArrayList<>();
    private final ArrayList<Integer> rawMaterialIds = new ArrayList<>();
    private final JTextField purchaseDateField = new JTextField();
    private final JTextField actualPriceField = new JTextField();
    private final JTextField statusField = new JTextField();
    private final JTextField jumlahDibeliField = new JTextField();
    private final JTextField receivedDateField = new JTextField();
    private final JLabel feedbackLabel = new JLabel(" ", JLabel.CENTER);

    public PurchaseOrderView() throws SQLException {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        loadVendors();
        loadRawMaterials();

        formPanel.add(new JLabel("Nama Vendor:"));
        formPanel.add(vendorList);
        formPanel.add(new JLabel("Raw Material:"));
        formPanel.add(rawMaterialList);
        formPanel.add(new JLabel("Tanggal Pembelian (YYYY-MM-DD):"));
        formPanel.add(purchaseDateField);
        formPanel.add(new JLabel("Jumlah dibeli: "));
        formPanel.add(jumlahDibeliField);
        formPanel.add(new JLabel("Harga Beli Aktual:"));
        formPanel.add(actualPriceField);
        formPanel.add(new JLabel("Status PO:"));
        formPanel.add(statusField);
        formPanel.add(new JLabel("Tanggal Diterima (YYYY-MM-DD, opsional):"));
        formPanel.add(receivedDateField);
        add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Simpan");
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(saveButton);
        buttonPanel.add(feedbackLabel);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(event -> savePurchaseOrder());
    }

    private void loadVendors() {
        vendorList.addItem("-- Pilih Vendor --");
        vendors.addAll(VendorRepository.getAllVendor());
        for (Vendor vendor : vendors) {
            vendorList.addItem(vendor.getNamaVendor());
        }
    }

    private void loadRawMaterials() throws SQLException {
        rawMaterialList.addItem("-- Pilih Raw Material --");
        String sql = "SELECT id_raw_material, nama_material FROM raw_material ORDER BY nama_material";

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rawMaterialIds.add(resultSet.getInt("id_raw_material"));
                rawMaterialList.addItem(resultSet.getString("nama_material"));
            }
        }
    }

    private void savePurchaseOrder() {
        if (!Session.isLoggedIn()) {
            showFeedback("Sesi login tidak ditemukan. Silakan login kembali.", false);
            return;
        }

        int vendorIndex = vendorList.getSelectedIndex() - 1;
        int rawMaterialIndex = rawMaterialList.getSelectedIndex() - 1;
        if (vendorIndex < 0 || vendorIndex >= vendors.size()
                || rawMaterialIndex < 0 || rawMaterialIndex >= rawMaterialIds.size()) {
            showFeedback("Pilih vendor dan raw material terlebih dahulu.", false);
            return;
        }

        String status = statusField.getText().trim();
        if (status.isEmpty()) {
            showFeedback("Status PO harus diisi.", false);
            return;
        }
        try {
            Date purchaseDate = Date.valueOf(purchaseDateField.getText().trim());
            Date receivedDate = receivedDateField.getText().trim().isEmpty()
                    ? null
                    : Date.valueOf(receivedDateField.getText().trim());
            int jumlahDibeli = Integer.parseInt(jumlahDibeliField.getText().trim());
            if (jumlahDibeli <= 0) {
                throw new IllegalArgumentException("Jumlah dibeli harus lebih dari 0.");
            }
            double actualPrice = Double.parseDouble(actualPriceField.getText().trim());
            if (actualPrice < 0) {
                throw new IllegalArgumentException("Harga beli tidak boleh negatif.");
            }
            int createdBy = Session.getSession().getId();

            String sql = "INSERT INTO purchase_order "
                    + "(tanggal_pembelian, harga_beli_aktual, status_po, tanggal_diterima, id_vendor, id_raw_material, created_by, jumlah_dibeli) "
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
                statement.setInt(5, vendors.get(vendorIndex).getId_vendor());
                statement.setInt(6, rawMaterialIds.get(rawMaterialIndex));
                statement.setInt(7, createdBy);
                statement.setInt(8, jumlahDibeli);
                statement.executeUpdate();
            }

            showFeedback("Purchase order berhasil disimpan.", true);
        } catch (IllegalArgumentException exception) {
            showFeedback("Periksa format tanggal (YYYY-MM-DD), jumlah dibeli, dan harga beli. "
                    + exception.getMessage(), false);
        } catch (SQLException exception) {
            showFeedback("Purchase order gagal disimpan: " + exception.getMessage(), false);
        }
    }

    private void showFeedback(String message, boolean success) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(success ? new Color(0, 128, 0) : new Color(180, 0, 0));
    }
}
