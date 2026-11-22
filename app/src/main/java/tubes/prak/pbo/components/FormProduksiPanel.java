package tubes.prak.pbo.components;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.controller.ProduksiController;
import tubes.prak.pbo.models.Bom;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.repositories.BomRepository;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class FormProduksiPanel extends JPanel {

    private final ItemController itemController = new ItemController();
    private final ProduksiController produksiController = new ProduksiController();

    private JComboBox<Produk> cmbProduk;
    private JSpinner spnTanggal;
    private JTextField txtStatus;
    private JTextField txtJumlahProduksi;
    private JLabel lblTotalHpp;
    private JButton btnSimpan;

    private Bom currentBom;
    private double currentTotalHpp = 0.0;
    private final Consumer<Produksi> onSuccessSave;

    public FormProduksiPanel(Consumer<Produksi> onSuccessSave) {
        this.onSuccessSave = onSuccessSave;
        initUI();
        loadDataProduk();
        setupListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("Form Jadwal Produksi Baru");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // --- FORM GRID ---
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Dropdown Produk
        cmbProduk = new JComboBox<>();
        cmbProduk.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Produk p) {
                    setText(p.getNamaProduk() + " (" + p.getSatuanProduk() + ")");
                }
                return this;
            }
        });

        // 2. Tanggal Produksi (Murni Java Swing JSpinner)
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnTanggal = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnTanggal, "dd-MM-yyyy");
        spnTanggal.setEditor(dateEditor);

        // 3. Status Produksi (Default PLANNED & terkunci)
        txtStatus = new JTextField(StatusProduksi.PLANNED.name());
        txtStatus.setEditable(false);
        txtStatus.setBackground(StatusProduksi.PLANNED.getBadgeColor());
        txtStatus.setForeground(StatusProduksi.PLANNED.getTextColor());
        txtStatus.setFont(new Font("SansSerif", Font.BOLD, 12));

        // 4. Jumlah Produksi
        txtJumlahProduksi = new JTextField();

        // 5. Total HPP Produksi (Reaktif)
        lblTotalHpp = new JLabel("Rp 0");
        lblTotalHpp.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotalHpp.setForeground(new Color(15, 23, 42));

        addRow(formGrid, gbc, 0, "Pilih Produk:", cmbProduk);
        addRow(formGrid, gbc, 1, "Tanggal Produksi:", spnTanggal);
        addRow(formGrid, gbc, 2, "Status Produksi:", txtStatus);
        addRow(formGrid, gbc, 3, "Jumlah Produksi:", txtJumlahProduksi);
        addRow(formGrid, gbc, 4, "Estimasi Total HPP:", lblTotalHpp);

        add(formGrid, BorderLayout.CENTER);

        // --- TOMBOL SIMPAN ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        btnSimpan = new JButton("Simpan Jadwal Produksi");
        btnSimpan.setFont(new Font("SansSerif", Font.BOLD, 13));
        bottomPanel.add(btnSimpan);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setPreferredSize(new Dimension(240, 32));
        panel.add(field, gbc);
    }

    private void loadDataProduk() {
        try {
            List<Produk> listProduk = ProdukRepository.findAllProduk();
            for (Produk p : listProduk) {
                cmbProduk.addItem(p);
            }
            if (cmbProduk.getItemCount() > 0) {
                loadBomProdukTerpilih();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat daftar produk: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBomProdukTerpilih() {
        Produk selected = (Produk) cmbProduk.getSelectedItem();
        if (selected == null) return;

        try {
            // Load BOM sekali saat produk dipilih
            currentBom = BomRepository.findByProdukId(selected.getIdProduk());
            updateHppReaktif();
        } catch (SQLException e) {
            currentBom = null;
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat BOM produk: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupListeners() {
        // Trigger saat produk pada dropdown diganti
        cmbProduk.addActionListener(e -> loadBomProdukTerpilih());

        // Trigger reaktif setiap kali user mengetik/menghapus angka jumlah produksi
        txtJumlahProduksi.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateHppReaktif(); }

            @Override
            public void removeUpdate(DocumentEvent e) { updateHppReaktif(); }

            @Override
            public void changedUpdate(DocumentEvent e) { updateHppReaktif(); }
        });

        // Trigger saat tombol simpan diklik
        btnSimpan.addActionListener(e -> prosesSimpan());
    }

    private void updateHppReaktif() {
        double jumlah = parseJumlahProduksi();
        currentTotalHpp = itemController.hitungTotalHpp(currentBom, jumlah);

        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        lblTotalHpp.setText(rupiah.format(currentTotalHpp));
    }

    private double parseJumlahProduksi() {
        try {
            String text = txtJumlahProduksi.getText().trim();
            if (text.isEmpty()) return 0.0;
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void prosesSimpan() {
        Produk selectedProduk = (Produk) cmbProduk.getSelectedItem();
        if (selectedProduk == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih produk terlebih dahulu.");
            return;
        }

        double jumlah = parseJumlahProduksi();
        if (jumlah <= 0) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah produksi yang valid (> 0).");
            return;
        }

        Date tanggalProduksi = (Date) spnTanggal.getValue();
        int createdBy = Session.getSession().getId();

        // idProduksi diisi 0 karena di-generate otomatis oleh serial primary key DB
        Produksi produksiBaru = new Produksi(
                0,
                selectedProduk.getIdProduk(),
                createdBy,
                tanggalProduksi,
                StatusProduksi.PLANNED,
                jumlah,
                currentTotalHpp
        );

        try {
            produksiController.buatJadwalProduksi(produksiBaru, currentBom);
            JOptionPane.showMessageDialog(this, "Jadwal produksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Reset field & beri tahu parent view (ProduksiView) agar me-refresh list card
            txtJumlahProduksi.setText("");
            if (onSuccessSave != null) {
                onSuccessSave.accept(produksiBaru);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }
}
