package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tubes.prak.pbo.ENUM.StatusPo;
import tubes.prak.pbo.components.base.CardListPanel;
import tubes.prak.pbo.components.cards.PurchaseOrderCard;
import tubes.prak.pbo.controller.PurchaseOrderController;
import tubes.prak.pbo.models.PurchaseOrder;
import tubes.prak.pbo.models.productRelated.RawMaterial;
import tubes.prak.pbo.models.Vendor;

public class PurchaseOrderView extends JPanel {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.##");

    private final PurchaseOrderController poController = new PurchaseOrderController();

    // Header Controls
    private JLabel lblCashTersedia;
    private JComboBox<String> cbFilterStatus;

    // Form Controls
    private JComboBox<Vendor> cbVendor;
    private JComboBox<RawMaterial> cbRawMaterial;
    private JTextField txtJumlahDibeli;
    private JLabel lblSatuanDinamis;
    private JTextField txtHargaBeliAktual;
    private JLabel lblSubtotalValue;
    private JLabel lblHargaSatuanValue;
    private JLabel lblVendorWarning;

    // History Container
    private final CardListPanel<PurchaseOrder> historyList =
            new CardListPanel<>(po -> new PurchaseOrderCard(po, this::handleConfirmArrived));

    public PurchaseOrderView() {
        setLayout(new BorderLayout(10, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(buildHeaderPanel(), BorderLayout.NORTH);
        topSection.add(buildFormPoPanel(), BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(buildHistorySectionPanel(), BorderLayout.CENTER);

        refreshPurchaseOrderData();
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Purchase Order & Manajemen Vendor");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        lblCashTersedia = new JLabel("Cash Tersedia: Rp 0");
        lblCashTersedia.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblCashTersedia.setForeground(new Color(22, 101, 52));
        lblCashTersedia.setOpaque(true);
        lblCashTersedia.setBackground(new Color(220, 252, 231));
        lblCashTersedia.setBorder(new CompoundBorder(
                new LineBorder(new Color(134, 239, 172), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton btnTambahVendor = new JButton("+ Tambah Vendor");
        btnTambahVendor.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnTambahVendor.setBackground(new Color(37, 99, 235));
        btnTambahVendor.setForeground(Color.WHITE);
        btnTambahVendor.setFocusPainted(false);
        btnTambahVendor.addActionListener(e -> openTambahVendorDialog());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshPurchaseOrderData());

        rightControls.add(lblCashTersedia);
        rightControls.add(btnTambahVendor);
        rightControls.add(btnRefresh);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(rightControls, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel buildFormPoPanel() {
        JPanel cardPanel = new JPanel(new BorderLayout(16, 8));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblFormTitle = new JLabel("Buat Purchase Order Baru");
        lblFormTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFormTitle.setForeground(new Color(30, 41, 59));

        lblVendorWarning = new JLabel(" ");
        lblVendorWarning.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblVendorWarning.setForeground(new Color(185, 28, 28));

        titleRow.add(lblFormTitle, BorderLayout.WEST);
        titleRow.add(lblVendorWarning, BorderLayout.EAST);

        // Grid Input Form
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Pilih Raw Material
        cbRawMaterial = new JComboBox<>();
        cbRawMaterial.setPreferredSize(new Dimension(250, 30));
        cbRawMaterial.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RawMaterial rm) {
                    setText(rm.getNama() + " (" + rm.getSatuan().name() + ") - Stok: "
                            + QTY_FORMAT.format(rm.getJumlahStok()));
                }
                return this;
            }
        });
        cbRawMaterial.addActionListener(e -> updateSatuanAndSubtotalPreview());

        // 2. Pilih Vendor
        cbVendor = new JComboBox<>();
        cbVendor.setPreferredSize(new Dimension(250, 30));
        cbVendor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vendor v) {
                    setText(v.getNamaVendor() + " (" + v.getNoTelp() + ")");
                }
                return this;
            }
        });

        // 3. Jumlah Dibeli + Label Satuan Dinamis
        JPanel qtyWrapper = new JPanel(new BorderLayout(6, 0));
        qtyWrapper.setOpaque(false);
        txtJumlahDibeli = new JTextField();
        txtJumlahDibeli.setPreferredSize(new Dimension(140, 30));

        lblSatuanDinamis = new JLabel("-");
        lblSatuanDinamis.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSatuanDinamis.setForeground(new Color(37, 99, 235));
        lblSatuanDinamis.setPreferredSize(new Dimension(65, 30));

        qtyWrapper.add(txtJumlahDibeli, BorderLayout.CENTER);
        qtyWrapper.add(lblSatuanDinamis, BorderLayout.EAST);

        // 4. Harga Beli Aktual (Total/Borongan)
        txtHargaBeliAktual = new JTextField();
        txtHargaBeliAktual.setPreferredSize(new Dimension(180, 30));

        // Listener kalkulasi real-time subtotal & harga satuan
        DocumentListener calcListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSatuanAndSubtotalPreview(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSatuanAndSubtotalPreview(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSatuanAndSubtotalPreview(); }
        };
        txtJumlahDibeli.getDocument().addDocumentListener(calcListener);
        txtHargaBeliAktual.getDocument().addDocumentListener(calcListener);

        // Baris 1: Raw Material & Vendor
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Raw Material:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        fieldsPanel.add(cbRawMaterial, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Vendor:"), gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.5;
        fieldsPanel.add(cbVendor, gbc);

        // Baris 2: Jumlah Dibeli (dengan label satuan) & Harga Beli Aktual
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Jumlah Dibeli:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        fieldsPanel.add(qtyWrapper, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Harga Aktual / Total (Rp):"), gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.5;
        fieldsPanel.add(txtHargaBeliAktual, gbc);

        // Panel Kanan: Subtotal & Tombol Submit
        JPanel summaryActionPanel = new JPanel();
        summaryActionPanel.setLayout(new BoxLayout(summaryActionPanel, BoxLayout.Y_AXIS));
        summaryActionPanel.setBackground(new Color(248, 250, 252));
        summaryActionPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        summaryActionPanel.setPreferredSize(new Dimension(250, 95));

        JLabel lblSubTitle = new JLabel("Subtotal Purchase Order:");
        lblSubTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubTitle.setForeground(new Color(100, 116, 139));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubtotalValue = new JLabel("Rp 0");
        lblSubtotalValue.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSubtotalValue.setForeground(new Color(15, 23, 42));
        lblSubtotalValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHargaSatuanValue = new JLabel("Harga / satuan: Rp 0");
        lblHargaSatuanValue.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblHargaSatuanValue.setForeground(new Color(71, 85, 105));
        lblHargaSatuanValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSubmitPo = new JButton("Submit PO (Bayar)");
        btnSubmitPo.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubmitPo.setBackground(new Color(22, 163, 74));
        btnSubmitPo.setForeground(Color.WHITE);
        btnSubmitPo.setFocusPainted(false);
        btnSubmitPo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubmitPo.addActionListener(e -> handleSubmitPurchaseOrder());

        summaryActionPanel.add(lblSubTitle);
        summaryActionPanel.add(lblSubtotalValue);
        summaryActionPanel.add(lblHargaSatuanValue);
        summaryActionPanel.add(Box.createVerticalStrut(8));
        summaryActionPanel.add(btnSubmitPo);

        cardPanel.add(titleRow, BorderLayout.NORTH);
        cardPanel.add(fieldsPanel, BorderLayout.CENTER);
        cardPanel.add(summaryActionPanel, BorderLayout.EAST);

        return cardPanel;
    }

    private JPanel buildHistorySectionPanel() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(4, 2, 2, 2));

        JLabel lblSectionTitle = new JLabel("History Purchase Order");
        lblSectionTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSectionTitle.setForeground(new Color(30, 41, 59));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPanel.setOpaque(false);
        cbFilterStatus = new JComboBox<>(new String[]{"Semua Status", "PAID", "ARRIVED"});
        cbFilterStatus.setPreferredSize(new Dimension(130, 26));
        cbFilterStatus.addActionListener(e -> loadHistoryCards());

        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(cbFilterStatus);

        topBar.add(lblSectionTitle, BorderLayout.WEST);
        topBar.add(filterPanel, BorderLayout.EAST);

        historyList.setGap(8);
        historyList.setEmptyMessage("Belum ada riwayat Purchase Order yang sesuai filter.");

        section.add(topBar, BorderLayout.NORTH);
        section.add(historyList, BorderLayout.CENTER);

        return section;
    }

    // Dapat dipanggil dari MainShell saat tab/menu Purchase Order dibuka
    public void refreshPurchaseOrderData() {
        loadCashInfo();
        loadRawMaterialCombo();
        loadVendorCombo();
        updateSatuanAndSubtotalPreview();
        loadHistoryCards();
    }

    private void loadCashInfo() {
        try {
            double cash = poController.getAvailableCash();
            lblCashTersedia.setText("Cash Tersedia: Rp " + CURRENCY_FORMAT.format(cash));
        } catch (SQLException e) {
            lblCashTersedia.setText("Cash Tersedia: Error");
        }
    }

    private void loadRawMaterialCombo() {
        RawMaterial selected = (RawMaterial) cbRawMaterial.getSelectedItem();
        cbRawMaterial.removeAllItems();

        List<RawMaterial> materials = poController.getAllRawMaterials();
        for (RawMaterial rm : materials) {
            cbRawMaterial.addItem(rm);
        }

        if (selected != null) {
            for (int i = 0; i < cbRawMaterial.getItemCount(); i++) {
                if (cbRawMaterial.getItemAt(i).getId() == selected.getId()) {
                    cbRawMaterial.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void loadVendorCombo() {
        Vendor selected = (Vendor) cbVendor.getSelectedItem();
        cbVendor.removeAllItems();

        try {
            List<Vendor> vendors = poController.getAllVendors();
            for (Vendor v : vendors) {
                cbVendor.addItem(v);
            }

            if (vendors.isEmpty()) {
                lblVendorWarning.setText("* Belum ada vendor terdaftar. Klik '+ Tambah Vendor' terlebih dahulu!");
            } else {
                lblVendorWarning.setText(" ");
            }

            if (selected != null) {
                for (int i = 0; i < cbVendor.getItemCount(); i++) {
                    if (cbVendor.getItemAt(i).getIdVendor() == selected.getIdVendor()) {
                        cbVendor.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat daftar vendor: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSatuanAndSubtotalPreview() {
        RawMaterial selectedRm = (RawMaterial) cbRawMaterial.getSelectedItem();
        String satuan = (selectedRm != null) ? selectedRm.getSatuan().name() : "-";
        lblSatuanDinamis.setText(satuan);

        double qty = parseDoubleSafe(txtJumlahDibeli.getText());
        double hargaAktual = parseDoubleSafe(txtHargaBeliAktual.getText());

        double subtotal = poController.hitungSubtotal(hargaAktual);
        double hargaPerSatuan = poController.hitungHargaPerSatuan(qty, hargaAktual);

        lblSubtotalValue.setText("Rp " + CURRENCY_FORMAT.format(subtotal));
        lblHargaSatuanValue.setText("Harga / " + satuan + ": Rp " + CURRENCY_FORMAT.format(hargaPerSatuan));
    }

    private double parseDoubleSafe(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void handleSubmitPurchaseOrder() {
        try {
            // Validasi awal jika vendor belum ada
            if (cbVendor.getItemCount() == 0 || cbVendor.getSelectedItem() == null) {
                throw new Exception("Vendor masih kosong! Silakan isi form Tambah Vendor terlebih dahulu.");
            }

            Vendor vendor = (Vendor) cbVendor.getSelectedItem();
            RawMaterial material = (RawMaterial) cbRawMaterial.getSelectedItem();

            double jumlahDibeli;
            double hargaBeliAktual;
            try {
                jumlahDibeli = Double.parseDouble(txtJumlahDibeli.getText().trim());
                hargaBeliAktual = Double.parseDouble(txtHargaBeliAktual.getText().trim());
            } catch (NumberFormatException ex) {
                throw new Exception("Jumlah dibeli dan harga aktual harus berupa angka yang valid!");
            }

            poController.createPurchaseOrder(vendor, material, jumlahDibeli, hargaBeliAktual);

            JOptionPane.showMessageDialog(this,
                    "Purchase Order berhasil dibuat (Status: PAID)!\n" +
                            "Kas terpotong sebesar Rp " + CURRENCY_FORMAT.format(hargaBeliAktual) + ".\n" +
                            "Klik 'Tandai Arrived' pada kartu PO saat barang tiba di gudang.",
                    "Purchase Order Berhasil", JOptionPane.INFORMATION_MESSAGE);

            txtJumlahDibeli.setText("");
            txtHargaBeliAktual.setText("");
            refreshPurchaseOrderData();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Gagal Membuat Purchase Order", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleConfirmArrived(PurchaseOrder po) {
        String satuan = (po.getSatuanMaterial() != null) ? po.getSatuanMaterial() : "";
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Konfirmasi bahwa barang untuk PO #" + po.getIdPo() + " telah diterima?\n\n" +
                        "Material: " + po.getNamaMaterial() + "\n" +
                        "Jumlah Masuk: +" + QTY_FORMAT.format(po.getJumlahDibeli()) + " " + satuan + "\n" +
                        "Total Nilai PO: Rp " + CURRENCY_FORMAT.format(po.getHargaBeliAktual()) + "\n\n" +
                        "Stok bahan baku akan bertambah dan harga rata-rata bahan baku akan diperbarui.",
                "Konfirmasi Barang Tiba (ARRIVED)",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                poController.confirmPoArrived(po.getIdPo());
                JOptionPane.showMessageDialog(this,
                        "PO #" + po.getIdPo() + " berstatus ARRIVED!\nStok dan harga rata-rata bahan baku telah diperbarui.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshPurchaseOrderData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Gagal Memperbarui Status PO", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadHistoryCards() {
        String filter = (String) cbFilterStatus.getSelectedItem();

        try {
            List<PurchaseOrder> filtered = poController.getAllPurchaseOrders().stream()
                    .filter(po -> matchesFilter(po, filter))
                    .toList();
            historyList.setItems(filtered);
        } catch (SQLException e) {
            historyList.clear();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat riwayat Purchase Order: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean matchesFilter(PurchaseOrder po, String filter) {
        if (filter == null) {
            return true;
        }
        return switch (filter) {
            case "PAID" -> po.getStatusPo() == StatusPo.PAID;
            case "ARRIVED" -> po.getStatusPo() == StatusPo.ARRIVED;
            default -> true;
        };
    }

    private void openTambahVendorDialog() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        JTextField txtNamaVendor = new JTextField();
        JTextField txtNoTelp = new JTextField();
        JTextArea txtAlamat = new JTextArea(3, 22);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        formPanel.add(new JLabel("Nama Vendor:"));
        formPanel.add(txtNamaVendor);
        formPanel.add(new JLabel("No. Telepon:"));
        formPanel.add(txtNoTelp);
        formPanel.add(new JLabel("Alamat Vendor:"));
        formPanel.add(new JScrollPane(txtAlamat));

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Tambah Vendor Baru",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                poController.tambahVendor(
                        txtNamaVendor.getText(),
                        txtNoTelp.getText(),
                        txtAlamat.getText()
                );
                JOptionPane.showMessageDialog(this,
                        "Vendor \"" + txtNamaVendor.getText().trim() + "\" berhasil ditambahkan!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadVendorCombo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Gagal Menambah Vendor", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}