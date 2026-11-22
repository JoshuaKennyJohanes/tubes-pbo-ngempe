package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.models.Vendor;
import tubes.prak.pbo.repositories.PurchaseOrderDataRepository;
import tubes.prak.pbo.util.Session;

public class PurchaseOrderFormView extends JPanel {
    private final JComboBox<String> vendorList = new JComboBox<>();
    private final JComboBox<String> rawMaterialList = new JComboBox<>();
    private final List<Vendor> vendors;
    private final List<RawMaterial> rawMaterials;
    private final PurchaseOrderDataRepository repository;
    private final Runnable onSaved;
    private final JTextField purchaseDateField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JTextField actualPriceField = new JTextField();
    private final JTextField statusField = new JTextField();
    private final JTextField receivedDateField = new JTextField();
    private final JLabel feedbackLabel = new JLabel(" ", JLabel.CENTER);

    public PurchaseOrderFormView(List<Vendor> vendors, List<RawMaterial> rawMaterials,
            PurchaseOrderDataRepository repository, Runnable onSaved) {
        this.vendors = new ArrayList<>(vendors);
        this.rawMaterials = new ArrayList<>(rawMaterials);
        this.repository = repository;
        this.onSaved = onSaved;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        vendorList.addItem("-- Pilih Vendor --");
        for (Vendor vendor : this.vendors) {
            vendorList.addItem(vendor.getNamaVendor());
        }
        rawMaterialList.addItem("-- Pilih Raw Material --");
        for (RawMaterial rawMaterial : this.rawMaterials) {
            rawMaterialList.addItem(rawMaterial.getNama());
        }

        formPanel.add(new JLabel("Nama Vendor:"));
        formPanel.add(vendorList);
        formPanel.add(new JLabel("Raw Material:"));
        formPanel.add(rawMaterialList);
        formPanel.add(new JLabel("Tanggal Pembelian (YYYY-MM-DD):"));
        formPanel.add(purchaseDateField);
        formPanel.add(new JLabel("Jumlah dibeli:"));
        formPanel.add(quantityField);
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

    private void savePurchaseOrder() {
        if (!Session.isLoggedIn()) {
            showFeedback("Sesi login tidak ditemukan. Silakan login kembali.", false);
            return;
        }

        int vendorIndex = vendorList.getSelectedIndex() - 1;
        int rawMaterialIndex = rawMaterialList.getSelectedIndex() - 1;
        if (vendorIndex < 0 || vendorIndex >= vendors.size()
                || rawMaterialIndex < 0 || rawMaterialIndex >= rawMaterials.size()) {
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
            String receivedDateText = receivedDateField.getText().trim();
            Date receivedDate = receivedDateText.isEmpty() ? null : Date.valueOf(receivedDateText);
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double actualPrice = Double.parseDouble(actualPriceField.getText().trim());
            if (quantity <= 0) {
                throw new IllegalArgumentException("Jumlah dibeli harus lebih dari 0.");
            }
            if (actualPrice < 0) {
                throw new IllegalArgumentException("Harga beli tidak boleh negatif.");
            }

            repository.insert(
                    purchaseDate,
                    actualPrice,
                    status,
                    receivedDate,
                    vendors.get(vendorIndex).getId_vendor(),
                    rawMaterials.get(rawMaterialIndex).getId(),
                    Session.getSession().getId(),
                    quantity);
            showFeedback("Purchase order berhasil disimpan.", true);
            onSaved.run();
        } catch (IllegalArgumentException exception) {
            showFeedback("Periksa format input: " + exception.getMessage(), false);
        } catch (SQLException exception) {
            showFeedback("Purchase order gagal disimpan: " + exception.getMessage(), false);
        }
    }

    private void showFeedback(String message, boolean success) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(success ? new Color(0, 128, 0) : new Color(180, 0, 0));
    }
}
