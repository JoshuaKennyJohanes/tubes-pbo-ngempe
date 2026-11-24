package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import tubes.prak.pbo.components.base.CardListPanel;
import tubes.prak.pbo.components.cards.WarehouseItemCard;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.models.productRelated.Item;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.productRelated.RawMaterial;

public class WarehouseView extends JPanel {

    private static final String CARD_MONITOR = "CARD_MONITOR";
    private static final String CARD_REGISTER = "CARD_REGISTER";
    private static final String CARD_EDIT_PRODUK = "CARD_EDIT_PRODUK";
    private static final String CARD_EDIT_RAW_MATERIAL = "CARD_EDIT_RAW_MATERIAL";

    private static final int PREVIEW_SIZE = 85;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.##");

    private final ItemController itemController = new ItemController();
    private final CardLayout cardLayout = new CardLayout();

    // Komponen Halaman 1: Monitor Warehouse (Default)
    private JComboBox<String> cbKategori;
    private final CardListPanel<Item> itemList = new CardListPanel<>(
            item -> new WarehouseItemCard(item, this::handleEditItem));

    // Komponen Halaman 3: Edit Produk
    private Produk currentEditingProduk;
    private JLabel lblEditProdukId;
    private JTextField txtEditProdukNama;
    private JTextField txtEditProdukSatuan;
    private JTextField txtEditProdukHpp;
    private JTextField txtEditProdukHargaJual;
    private JTextField txtEditProdukStok;
    private JTextField txtEditProdukBatasKritis;
    private JTextArea txtEditProdukDeskripsi;
    private JLabel lblEditProdukImagePreview;
    private File selectedNewProdukImage;

    // Komponen Halaman 4: Edit Raw Material (Stock Opname)
    private RawMaterial currentEditingRawMaterial;
    private JLabel lblEditRawId;
    private JTextField txtEditRawNama;
    private JTextField txtEditRawSatuan;
    private JTextField txtEditRawHargaRata2;
    private JTextField txtEditRawBatasKritis;
    private JTextField txtEditRawStokLama;
    private JTextField txtEditRawStokBaru;

    public WarehouseView() {
        setLayout(cardLayout);

        add(buildMonitorPanel(), CARD_MONITOR);
        add(buildRegisterPanel(), CARD_REGISTER);
        add(buildEditProdukPanel(), CARD_EDIT_PRODUK);
        add(buildEditRawMaterialPanel(), CARD_EDIT_RAW_MATERIAL);

        refreshData();
    }

    // Dapat dipanggil dari MainShell saat tombol menu Warehouse diklik
    public final void refreshData() {
        String kategori = (String) cbKategori.getSelectedItem();

        try {
            if ("Raw Material".equals(kategori)) {
                itemList.setEmptyMessage("Belum ada data bahan baku (Raw Material) di warehouse.");
                List<RawMaterial> materials = itemController.getAllMaterials();
                itemList.setItems(materials);
            } else {
                itemList.setEmptyMessage("Belum ada data produk di warehouse.");
                List<Produk> produks = itemController.getAllProduk();
                itemList.setItems(produks);
            }
        } catch (SQLException e) {
            itemList.clear();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data warehouse: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 1. HALAMAN UTAMA: MONITOR WAREHOUSE (CARD_MONITOR)
    // =========================================================================
    private JPanel buildMonitorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Warehouse Monitor");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        cbKategori = new JComboBox<>(new String[] { "Produk", "Raw Material" });
        cbKategori.setPreferredSize(new Dimension(150, 28));
        cbKategori.addActionListener(e -> refreshData());

        leftHeader.add(lblTitle);
        leftHeader.add(new JLabel("   |   Tampilkan:"));
        leftHeader.add(cbKategori);

        JButton btnOpenRegister = new JButton("+ Register Item");
        btnOpenRegister.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnOpenRegister.setBackground(new Color(22, 163, 74));
        btnOpenRegister.setForeground(Color.WHITE);
        btnOpenRegister.setFocusPainted(false);
        btnOpenRegister.addActionListener(e -> cardLayout.show(this, CARD_REGISTER));

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(btnOpenRegister, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(itemList, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 2. HALAMAN REGISTER ITEM (CARD_REGISTER)
    // =========================================================================
    private JPanel buildRegisterPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)));

        JLabel lblTitle = new JLabel("Register Item Baru");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton btnBack = new JButton("← Kembali ke Warehouse");
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            refreshData();
            cardLayout.show(this, CARD_MONITOR);
        });

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);

        RegisterItemView registerItemView = new RegisterItemView();

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(registerItemView, BorderLayout.CENTER);

        return wrapper;
    }

    // =========================================================================
    // ROUTING TOMBOL EDIT DARI CARD
    // =========================================================================
    private void handleEditItem(Item item) {
        if (item instanceof Produk produk) {
            openEditProdukPanel(produk);
        } else if (item instanceof RawMaterial rawMaterial) {
            openEditRawMaterialPanel(rawMaterial);
        }
    }

    // =========================================================================
    // 3. HALAMAN EDIT PRODUK (CARD_EDIT_PRODUK)
    // =========================================================================
    private JPanel buildEditProdukPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)));

        lblEditProdukId = new JLabel("Edit Data Produk");
        lblEditProdukId.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(lblEditProdukId, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        txtEditProdukNama = new JTextField();
        txtEditProdukSatuan = createReadOnlyField();
        txtEditProdukHpp = createReadOnlyField();
        txtEditProdukHargaJual = new JTextField();
        txtEditProdukStok = new JTextField();
        txtEditProdukBatasKritis = new JTextField();

        txtEditProdukDeskripsi = new JTextArea(3, 25);
        txtEditProdukDeskripsi.setLineWrap(true);
        txtEditProdukDeskripsi.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtEditProdukDeskripsi);

        // Panel Image Picker untuk Edit Produk
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnChangePhoto = new JButton("Ganti Foto...");
        lblEditProdukImagePreview = new JLabel("Tidak ada foto", SwingConstants.CENTER);
        lblEditProdukImagePreview.setPreferredSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE));
        lblEditProdukImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        btnChangePhoto.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img == null) {
                        JOptionPane.showMessageDialog(this, "File bukan gambar yang valid!", "Peringatan",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    selectedNewProdukImage = file;
                    displayScaledImage(lblEditProdukImagePreview, img);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal membaca gambar: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        imagePanel.add(btnChangePhoto);
        imagePanel.add(lblEditProdukImagePreview);

        addFormRow(formPanel, 0, "Nama Produk:", txtEditProdukNama);
        addFormRow(formPanel, 1, "Satuan (Tetap):", txtEditProdukSatuan);
        addFormRow(formPanel, 2, "HPP Produk (Tetap):", txtEditProdukHpp);
        addFormRow(formPanel, 3, "Harga Jual:", txtEditProdukHargaJual);
        addFormRow(formPanel, 4, "Jumlah Stok:", txtEditProdukStok);
        addFormRow(formPanel, 5, "Batas Kritis:", txtEditProdukBatasKritis);
        addFormRow(formPanel, 6, "Deskripsi Produk:", scrollDesc);
        addFormRow(formPanel, 7, "Foto Produk:", imagePanel);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(formPanel, BorderLayout.NORTH);
        JScrollPane formScroll = new JScrollPane(topWrapper);
        formScroll.setBorder(BorderFactory.createEmptyBorder());

        // Bottom Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan Perubahan");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> cardLayout.show(this, CARD_MONITOR));
        btnSave.addActionListener(e -> saveEditedProduk());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(formScroll, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void openEditProdukPanel(Produk produk) {
        this.currentEditingProduk = produk;
        this.selectedNewProdukImage = null;

        lblEditProdukId.setText("Edit Data Produk — ID #" + produk.getIdProduk());
        txtEditProdukNama.setText(produk.getNamaProduk());
        txtEditProdukSatuan.setText(produk.getSatuanProduk());
        txtEditProdukHpp.setText("Rp " + CURRENCY_FORMAT.format(produk.getHPP()));
        txtEditProdukHargaJual.setText(String.valueOf((long) produk.getHargaJual()));
        txtEditProdukStok.setText(String.valueOf(produk.getStok()));
        txtEditProdukBatasKritis.setText(String.valueOf(produk.getBatasKritisProduk()));
        txtEditProdukDeskripsi.setText(produk.getDeskripsiProduk() != null ? produk.getDeskripsiProduk() : "");

        // Tampilkan foto lama jika ada
        lblEditProdukImagePreview.setIcon(null);
        lblEditProdukImagePreview.setText("Tidak ada foto");
        if (produk.getImageUrl() != null && !produk.getImageUrl().isBlank()) {
            File oldFile = new File(produk.getImageUrl());
            if (oldFile.exists()) {
                try {
                    BufferedImage img = ImageIO.read(oldFile);
                    if (img != null) {
                        displayScaledImage(lblEditProdukImagePreview, img);
                    }
                } catch (IOException ignored) {
                }
            }
        }

        cardLayout.show(this, CARD_EDIT_PRODUK);
    }

    private void saveEditedProduk() {
        if (currentEditingProduk == null)
            return;

        try {
            String namaBaru = txtEditProdukNama.getText().trim();
            double hargaJualBaru = Double.parseDouble(txtEditProdukHargaJual.getText().trim());
            int stokBaru = Integer.parseInt(txtEditProdukStok.getText().trim());
            int batasKritisBaru = Integer.parseInt(txtEditProdukBatasKritis.getText().trim());
            String deskripsiBaru = txtEditProdukDeskripsi.getText().trim();

            itemController.updateProduk(
                    currentEditingProduk,
                    namaBaru,
                    hargaJualBaru,
                    stokBaru,
                    batasKritisBaru,
                    deskripsiBaru,
                    selectedNewProdukImage);

            JOptionPane.showMessageDialog(this, "Data produk berhasil diperbarui!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            cardLayout.show(this, CARD_MONITOR);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Harga jual, stok, dan batas kritis harus berupa angka yang valid!",
                    "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 4. HALAMAN EDIT RAW MATERIAL / STOCK OPNAME (CARD_EDIT_RAW_MATERIAL)
    // =========================================================================
    private JPanel buildEditRawMaterialPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)));

        lblEditRawId = new JLabel("Stock Opname Bahan Baku");
        lblEditRawId.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(lblEditRawId, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        txtEditRawNama = createReadOnlyField();
        txtEditRawSatuan = createReadOnlyField();
        txtEditRawHargaRata2 = createReadOnlyField();
        txtEditRawBatasKritis = createReadOnlyField();
        txtEditRawStokLama = createReadOnlyField();
        txtEditRawStokBaru = new JTextField();

        JTextArea txtNote = new JTextArea(
                "Catatan: Hanya penyesuaian pengurangan stok (rusak/hilang/busuk) yang diizinkan. "
                        + "Penambahan stok harus dilakukan melalui menu Purchase Order.");

        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setEditable(false);
        txtNote.setFocusable(false);
        txtNote.setOpaque(false); // transparan seperti JLabel
        txtNote.setForeground(new Color(146, 64, 14));
        txtNote.setFont(new Font("SansSerif", Font.ITALIC, 12));
        txtNote.setBorder(null);

        addFormRow(formPanel, 0, "Nama Bahan Baku:", txtEditRawNama);
        addFormRow(formPanel, 1, "Satuan:", txtEditRawSatuan);
        addFormRow(formPanel, 2, "Harga Rata-rata / Satuan:", txtEditRawHargaRata2);
        addFormRow(formPanel, 3, "Batas Kritis:", txtEditRawBatasKritis);
        addFormRow(formPanel, 4, "Stok Tercatat Saat Ini:", txtEditRawStokLama);
        addFormRow(formPanel, 5, "Stok Aktual Baru:", txtEditRawStokBaru);
        addFormRow(formPanel, 6, "", txtNote);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(formPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan Penyesuaian Stok");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> cardLayout.show(this, CARD_MONITOR));
        btnSave.addActionListener(e -> saveEditedRawMaterial());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(topWrapper, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void openEditRawMaterialPanel(RawMaterial material) {
        this.currentEditingRawMaterial = material;

        String satuan = (material.getSatuan() != null) ? material.getSatuan().name() : "";
        lblEditRawId.setText("Stock Opname Bahan Baku — ID #" + material.getId());
        txtEditRawNama.setText(material.getNama());
        txtEditRawSatuan.setText(satuan);
        txtEditRawHargaRata2.setText("Rp " + CURRENCY_FORMAT.format(material.getHargaRata2()));
        txtEditRawBatasKritis.setText(QTY_FORMAT.format(material.getBatasKritis()) + " " + satuan);
        txtEditRawStokLama.setText(QTY_FORMAT.format(material.getJumlahStok()) + " " + satuan);
        txtEditRawStokBaru.setText(String.valueOf(material.getJumlahStok()));

        cardLayout.show(this, CARD_EDIT_RAW_MATERIAL);
    }

    private void saveEditedRawMaterial() {
        if (currentEditingRawMaterial == null)
            return;

        try {
            double stokBaru = Double.parseDouble(txtEditRawStokBaru.getText().trim());
            itemController.updateStokRawMaterial(currentEditingRawMaterial, stokBaru);

            JOptionPane.showMessageDialog(this, "Stok bahan baku berhasil diperbarui!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            cardLayout.show(this, CARD_MONITOR);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Stok aktual baru harus berupa angka yang valid!",
                    "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menyimpan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================================
    // HELPER UI
    // =========================================================================
    private JTextField createReadOnlyField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(new Color(241, 245, 249));
        tf.setForeground(new Color(71, 85, 105));
        return tf;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private void displayScaledImage(JLabel label, BufferedImage img) {
        double scale = Math.min((double) PREVIEW_SIZE / img.getWidth(), (double) PREVIEW_SIZE / img.getHeight());
        int w = Math.max(1, (int) (img.getWidth() * scale));
        int h = Math.max(1, (int) (img.getHeight() * scale));
        label.setText("");
        label.setIcon(new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
    }
}