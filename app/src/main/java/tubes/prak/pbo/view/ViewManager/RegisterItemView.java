package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.controller.ItemController;

public class RegisterItemView extends JPanel {

    private static final int PREVIEW_SIZE = 80;
    private static final String NO_PHOTO_TEXT = "Belum ada foto";

    private final ItemController itemController = new ItemController();

    private JComboBox<String> cbItemType;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Komponen form Product
    private JTextField txtProductName, txtProductPrice, txtProductCriticalLimit;
    private JComboBox<Satuan> cbProductUnit;
    private JLabel lblProductPreview;
    private File productImage;

    // Komponen form Raw Material
    private JTextField txtRawName, txtRawCost, txtRawCriticalLimit, txtRawStock;
    private JComboBox<Satuan> cbRawUnit;
    private JLabel lblRawPreview;
    private File rawImage;

    public RegisterItemView() {
        setLayout(new BorderLayout(10, 10));

        // --- PANEL ATAS: Pilihan Jenis Item ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.add(new JLabel("Pilih Jenis Item:"));

        String[] itemTypes = { "-- Pilih Jenis --", "Product", "Raw Material" };
        cbItemType = new JComboBox<>(itemTypes);
        topPanel.add(cbItemType);
        add(topPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH: Form Dinamis (CardLayout) ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel emptyPanel = new JPanel();
        emptyPanel.add(new JLabel("Silakan pilih jenis item terlebih dahulu."));

        cardPanel.add(emptyPanel, "EMPTY");
        cardPanel.add(wrapTop(createProductForm()), "PRODUCT");
        cardPanel.add(wrapTop(createRawMaterialForm()), "RAW_MATERIAL");

        add(cardPanel, BorderLayout.CENTER);

        // --- PANEL BAWAH: Tombol Action ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton btnSubmit = new JButton("Simpan");
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- EVENT HANDLERS ---
        cbItemType.addActionListener(e -> {
            String selected = (String) cbItemType.getSelectedItem();
            if ("Product".equals(selected)) {
                cardLayout.show(cardPanel, "PRODUCT");
            } else if ("Raw Material".equals(selected)) {
                cardLayout.show(cardPanel, "RAW_MATERIAL");
            } else {
                cardLayout.show(cardPanel, "EMPTY");
            }
        });

        btnSubmit.addActionListener(e -> handleSubmit());
    }

    // ------------------------------------------------------------------
    // Pembuatan form
    // ------------------------------------------------------------------

    /** Membungkus form supaya menempel di atas dan tidak melar memenuhi seluruh tinggi panel. */
    private JPanel wrapTop(JPanel form) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        wrapper.add(form, BorderLayout.NORTH);
        return wrapper;
    }

    private void addRow(JPanel panel, int row, String label, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    // Form khusus Produk
    private JPanel createProductForm() {
        JPanel panel = new JPanel(new GridBagLayout());

        cbProductUnit = new JComboBox<>(satuanOptions());
        txtProductName = new JTextField();
        txtProductPrice = new JTextField();
        txtProductCriticalLimit = new JTextField();

        addRow(panel, 0, "Nama Produk:", txtProductName);
        addRow(panel, 1, "Satuan:", cbProductUnit);
        addRow(panel, 2, "Harga Jual:", txtProductPrice);
        addRow(panel, 3, "Batas Kritis Warehouse:", txtProductCriticalLimit);
        addRow(panel, 4, "Foto Produk:", createImagePicker(true));

        return panel;
    }

    // Form khusus Raw Material
    private JPanel createRawMaterialForm() {
        JPanel panel = new JPanel(new GridBagLayout());

        cbRawUnit = new JComboBox<>(satuanOptions());
        txtRawName = new JTextField();
        txtRawCost = new JTextField();
        txtRawCriticalLimit = new JTextField();
        txtRawStock = new JTextField();

        addRow(panel, 0, "Nama Bahan Baku:", txtRawName);
        addRow(panel, 1, "Satuan:", cbRawUnit);
        addRow(panel, 2, "Harga per Satuan:", txtRawCost);
        addRow(panel, 3, "Batas Kritis:", txtRawCriticalLimit);
        addRow(panel, 4, "Jumlah Stok:", txtRawStock);
        addRow(panel, 5, "Foto Bahan Baku:", createImagePicker(false));

        return panel;
    }

    private Satuan[] satuanOptions() {
        return new Satuan[] { Satuan.GR, Satuan.KG, Satuan.L, Satuan.ML, Satuan.PACK, Satuan.PCS, Satuan.TABUNG };
    }

    // ------------------------------------------------------------------
    // Pemilih gambar
    // ------------------------------------------------------------------

    private JPanel createImagePicker(boolean isProduct) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton btnPick = new JButton("Pilih Foto...");
        JLabel preview = new JLabel(NO_PHOTO_TEXT, SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE));
        preview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        panel.add(btnPick);
        panel.add(preview);

        if (isProduct) {
            lblProductPreview = preview;
        } else {
            lblRawPreview = preview;
        }

        btnPick.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG)", "jpg", "jpeg", "png"));

            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File selected = chooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selected);
                if (img == null) {
                    JOptionPane.showMessageDialog(this, "File yang dipilih bukan gambar yang valid!",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (isProduct) {
                    productImage = selected;
                } else {
                    rawImage = selected;
                }
                showPreview(preview, img);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal membaca gambar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /** Menampilkan gambar pada label dengan mempertahankan rasio aspek. */
    private void showPreview(JLabel label, BufferedImage img) {
        double scale = Math.min((double) PREVIEW_SIZE / img.getWidth(), (double) PREVIEW_SIZE / img.getHeight());
        int w = Math.max(1, (int) (img.getWidth() * scale));
        int h = Math.max(1, (int) (img.getHeight() * scale));

        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        label.setText("");
        label.setIcon(new ImageIcon(scaled));
    }

    private void resetPreview(JLabel label) {
        label.setIcon(null);
        label.setText(NO_PHOTO_TEXT);
    }

    // ------------------------------------------------------------------
    // Logic saat tombol Simpan diklik
    // ------------------------------------------------------------------

    private void handleSubmit() {
        String selectedType = (String) cbItemType.getSelectedItem();

        try {
            if ("Product".equals(selectedType)) {
                submitProduct();
            } else if ("Raw Material".equals(selectedType)) {
                submitRawMaterial();
            } else {
                JOptionPane.showMessageDialog(this, "Silakan pilih jenis item terlebih dahulu!", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga, stok, dan batas kritis harus berupa angka!",
                    "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitProduct() throws Exception {
        String name = txtProductName.getText().trim();
        Satuan unit = (Satuan) cbProductUnit.getSelectedItem();
        double price = Double.parseDouble(txtProductPrice.getText().trim());
        int limit = Integer.parseInt(txtProductCriticalLimit.getText().trim());

        itemController.registerProduk(name, unit, price, limit, productImage);

        JOptionPane.showMessageDialog(this,
                "Product Berhasil Disimpan:\n" +
                        "Nama: " + name + "\n" +
                        "Satuan: " + unit + "\n" +
                        "Harga Jual: " + price + "\n" +
                        "Batas Kritis: " + limit,
                "Sukses", JOptionPane.INFORMATION_MESSAGE);

        txtProductName.setText("");
        txtProductPrice.setText("");
        txtProductCriticalLimit.setText("");
        cbProductUnit.setSelectedIndex(0);
        productImage = null;
        resetPreview(lblProductPreview);
    }

    private void submitRawMaterial() throws Exception {
        String rawName = txtRawName.getText().trim();
        Satuan rawUnit = (Satuan) cbRawUnit.getSelectedItem();
        double rawStock = Double.parseDouble(txtRawStock.getText().trim());
        double rawPrice = Double.parseDouble(txtRawCost.getText().trim());
        double rawLimit = Double.parseDouble(txtRawCriticalLimit.getText().trim());

        itemController.registerRawMaterial(rawName, rawUnit, rawPrice, rawLimit, rawStock, rawImage);

        JOptionPane.showMessageDialog(this,
                "Bahan Baku Berhasil Disimpan:\n" +
                        "Nama: " + rawName + "\n" +
                        "Satuan: " + rawUnit + "\n" +
                        "Harga Bahan Baku: " + rawPrice + "\n" +
                        "Batas Kritis: " + rawLimit,
                "Sukses", JOptionPane.INFORMATION_MESSAGE);

        txtRawName.setText("");
        txtRawCost.setText("");
        txtRawStock.setText("");
        txtRawCriticalLimit.setText("");
        cbRawUnit.setSelectedIndex(0);
        rawImage = null;
        resetPreview(lblRawPreview);
    }
}