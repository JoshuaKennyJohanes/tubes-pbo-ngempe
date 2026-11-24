package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import tubes.prak.pbo.components.base.CardListPanel;
import tubes.prak.pbo.components.cards.BomProductCard;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.models.productRelated.Bom;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.productRelated.RawMaterial;
import tubes.prak.pbo.util.Router;

public class BomView extends JPanel {



    private final ItemController itemController = new ItemController();
    private final CardLayout cardLayout = new CardLayout();

    // Data master
    private List<RawMaterial> masterRawMaterials = new ArrayList<>();

    // Komponen Halaman 1: Daftar BOM (CARD_LIST)
    private final Map<Bom, Double> hppCache = new IdentityHashMap<>();
    private final CardListPanel<Bom> bomList = new CardListPanel<>(bom ->
            new BomProductCard(
                    bom,
                    hppCache.getOrDefault(bom, 0.0),
                    this::openEditBomPanel,
                    this::handleDeleteBom));

    // Komponen Halaman 2: Tambah BOM Baru (CARD_ADD)
    private JComboBox<Produk> cbAddProduk;
    private JPanel addRowsContainer;
    private final List<CompositionRow> addCompositionRows = new ArrayList<>();

    // Komponen Halaman 3: Edit BOM (CARD_EDIT)
    private JLabel lblEditProdukTitle;
    private JPanel editRowsContainer;
    private final List<CompositionRow> editCompositionRows = new ArrayList<>();
    private Bom currentEditingBom;

    public BomView() {
        setLayout(cardLayout);

        add(buildListPanel(), Router.CARD_LIST);
        add(buildAddPanel(), Router.CARD_ADD);
        add(buildEditPanel(), Router.CARD_EDIT);

        loadMasterData();
        cardLayout.show(this, Router.CARD_LIST);
    }

    // Tetap disediakan agar kompatibel jika dipanggil saat perpindahan menu di ViewManager
    public void loadMasterData() {
        masterRawMaterials = itemController.getAllMaterials();
        refreshBomList();
    }

    // =========================================================================
    // 1. PANEL DAFTAR BOM (HEADER + LIST CARD PRODUK)
    // =========================================================================
    private JPanel buildListPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header bagian atas
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Daftar Bill of Materials (BOM)");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton btnOpenAddBom = new JButton("+ Tambah BOM Baru");
        btnOpenAddBom.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnOpenAddBom.setBackground(new Color(22, 163, 74));
        btnOpenAddBom.setForeground(Color.WHITE);
        btnOpenAddBom.setFocusPainted(false);
        btnOpenAddBom.addActionListener(e -> openAddBomPanel());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnOpenAddBom, BorderLayout.EAST);

        bomList.setEmptyMessage("Belum ada produk yang terdaftar di tabel BOM.");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(bomList, BorderLayout.CENTER);

        return mainPanel;
    }

    public void refreshBomList() {
        hppCache.clear();

        try {
            List<Bom> boms = itemController.getAllBomGroupedByProduk();

            // HPP dihitung di sini karena hitungTotalHpp() bisa melempar SQLException,
            // sedangkan factory card di CardListPanel tidak boleh melempar checked exception.
            for (Bom bom : boms) {
                hppCache.put(bom, itemController.hitungTotalHpp(bom, 1));
            }

            bomList.setItems(boms);
        } catch (SQLException e) {
            bomList.clear();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat daftar BOM: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 2. PANEL TAMBAH BOM BARU (CARD_ADD)
    // =========================================================================
    private JPanel buildAddPanel() {
        JPanel addPanel = new JPanel(new BorderLayout(10, 10));
        addPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Bagian 1: Header Pemilihan Produk (Hanya produk yang belum punya BOM)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        topPanel.setBorder(BorderFactory.createTitledBorder("1. Pilih Produk (Belum Terdaftar di BOM)"));

        cbAddProduk = new JComboBox<>();
        cbAddProduk.setPreferredSize(new Dimension(280, 28));

        topPanel.add(new JLabel("Produk Jadi:"));
        topPanel.add(cbAddProduk);

        // Bagian 2: Container Komposisi Raw Material
        addRowsContainer = new JPanel();
        addRowsContainer.setLayout(new BoxLayout(addRowsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(addRowsContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("2. Tentukan Komposisi Bahan Baku"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Tombol Aksi Bawah
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelAdd = new JButton("Batal / Kembali");
        JButton btnAddMaterialRow = new JButton("+ Tambah Bahan Baku");
        JButton btnSaveNewBom = new JButton("Simpan BOM");

        btnCancelAdd.addActionListener(e -> cardLayout.show(this, Router.CARD_LIST));
        btnAddMaterialRow.addActionListener(e -> addCompositionRow(addRowsContainer, addCompositionRows));
        btnSaveNewBom.addActionListener(e -> saveNewBom());

        bottomPanel.add(btnCancelAdd);
        bottomPanel.add(btnAddMaterialRow);
        bottomPanel.add(btnSaveNewBom);

        addPanel.add(topPanel, BorderLayout.NORTH);
        addPanel.add(scrollPane, BorderLayout.CENTER);
        addPanel.add(bottomPanel, BorderLayout.SOUTH);

        return addPanel;
    }

    private void openAddBomPanel() {
        try {
            List<Produk> produkTanpaBom = itemController.getProdukTanpaBom();
            if (produkTanpaBom.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Semua produk sudah terdaftar di BOM.\nGunakan tombol 'Edit' pada card produk jika ingin mengubah komposisi.",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            masterRawMaterials = itemController.getAllMaterials();
            if (masterRawMaterials.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Belum ada data bahan baku (Raw Material). Silakan daftarkan bahan baku terlebih dahulu.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Isi ulang dropdown hanya dengan produk yang belum punya BOM
            cbAddProduk.removeAllItems();
            for (Produk p : produkTanpaBom) {
                cbAddProduk.addItem(p);
            }

            // Reset baris komposisi dan langsung siapkan 1 baris awal
            addCompositionRows.clear();
            addRowsContainer.removeAll();
            addCompositionRow(addRowsContainer, addCompositionRows);

            cardLayout.show(this, Router.CARD_ADD);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data produk: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveNewBom() {
        Produk selectedProduk = (Produk) cbAddProduk.getSelectedItem();
        if (selectedProduk == null) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih produk terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (addCompositionRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Minimal harus menambahkan 1 bahan baku.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Map<RawMaterial, Double> materialMap = parseRowsToMap(addCompositionRows);
            itemController.registerBomProduk(selectedProduk, materialMap);

            JOptionPane.showMessageDialog(this,
                    "BOM untuk produk \"" + selectedProduk + "\" berhasil ditambahkan!");

            refreshBomList();
            cardLayout.show(this, Router.CARD_LIST);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Gagal Menyimpan BOM", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 3. PANEL EDIT BOM (CARD_EDIT)
    // =========================================================================
    private JPanel buildEditPanel() {
        JPanel editPanel = new JPanel(new BorderLayout(10, 10));
        editPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header Edit
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Edit Komposisi BOM Produk"));
        lblEditProdukTitle = new JLabel("Produk: -");
        lblEditProdukTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEditProdukTitle.setBorder(new EmptyBorder(6, 8, 6, 8));
        topPanel.add(lblEditProdukTitle, BorderLayout.WEST);

        // Container baris komposisi dinamis
        editRowsContainer = new JPanel();
        editRowsContainer.setLayout(new BoxLayout(editRowsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(editRowsContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Bahan Baku (Dapat Ditambah / Dihapus)"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Tombol aksi bawah
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Batal / Kembali");
        JButton btnAddRow = new JButton("+ Tambah Bahan Baku");
        JButton btnSaveEdit = new JButton("Simpan Perubahan");

        btnBack.addActionListener(e -> cardLayout.show(this, Router.CARD_LIST));
        btnAddRow.addActionListener(e -> addCompositionRow(editRowsContainer, editCompositionRows));
        btnSaveEdit.addActionListener(e -> saveEditedBom());

        bottomPanel.add(btnBack);
        bottomPanel.add(btnAddRow);
        bottomPanel.add(btnSaveEdit);

        editPanel.add(topPanel, BorderLayout.NORTH);
        editPanel.add(scrollPane, BorderLayout.CENTER);
        editPanel.add(bottomPanel, BorderLayout.SOUTH);

        return editPanel;
    }

    private void openEditBomPanel(Bom bom) {
        this.currentEditingBom = bom;
        this.masterRawMaterials = itemController.getAllMaterials();
        this.editCompositionRows.clear();
        this.editRowsContainer.removeAll();

        Produk produk = bom.getProduk();
        String namaProduk = (produk != null) ? produk.toString() : "Produk #" + bom.getIdProduk();
        lblEditProdukTitle.setText("Produk: " + namaProduk + " (ID: #" + bom.getIdProduk() + ")");

        for (Map.Entry<RawMaterial, Double> entry : bom.getKebutuhanMaterial().entrySet()) {
            RawMaterial existingMat = findMasterMaterialById(entry.getKey().getId());
            if (existingMat == null) {
                existingMat = entry.getKey();
            }

            CompositionRow row = createRow(
                    editRowsContainer,
                    editCompositionRows,
                    masterRawMaterials,
                    existingMat,
                    String.valueOf(entry.getValue())
            );
            editCompositionRows.add(row);
            editRowsContainer.add(row.getPanel());
        }

        refreshDropdowns(editCompositionRows);
        editRowsContainer.revalidate();
        editRowsContainer.repaint();

        cardLayout.show(this, Router.CARD_EDIT);
    }

    private void saveEditedBom() {
        if (currentEditingBom == null) return;

        if (editCompositionRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Minimal harus ada 1 bahan baku. Jika ingin menghapus resep produk ini, gunakan tombol Hapus pada card.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Map<RawMaterial, Double> updatedMap = parseRowsToMap(editCompositionRows);
            itemController.updateBomProduk(currentEditingBom.getIdProduk(), updatedMap);

            JOptionPane.showMessageDialog(this, "Perubahan BOM berhasil disimpan!");
            refreshBomList();
            cardLayout.show(this, Router.CARD_LIST);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 4. AKSI HAPUS BOM PER PRODUK
    // =========================================================================
    private void handleDeleteBom(Bom bom) {
        String namaProduk = (bom.getProduk() != null)
                ? bom.getProduk().toString()
                : "ID Produk #" + bom.getIdProduk();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus seluruh resep BOM untuk produk \"" + namaProduk + "\" (ID: " + bom.getIdProduk() + ")?",
                "Konfirmasi Hapus BOM",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                itemController.deleteBomByProduk(bom.getIdProduk());
                JOptionPane.showMessageDialog(this, "BOM produk berhasil dihapus.");
                refreshBomList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus BOM: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 5. HELPER BERSAMA UNTUK BARIS FORM DINAMIS (ADD & EDIT)
    // =========================================================================
    private void addCompositionRow(JPanel container, List<CompositionRow> rowList) {
        List<RawMaterial> available = getAvailableMaterials(rowList, null);
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua bahan baku master sudah dipilih!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CompositionRow row = createRow(container, rowList, available, available.get(0), "");
        rowList.add(row);
        container.add(row.getPanel());

        refreshDropdowns(rowList);
        container.revalidate();
        container.repaint();
    }

    private CompositionRow createRow(JPanel container, List<CompositionRow> rowList,
                                     List<RawMaterial> items, RawMaterial preselected, String initialQty) {
        return new CompositionRow(
                items,
                preselected,
                initialQty,
                rowToRemove -> {
                    container.remove(rowToRemove.getPanel());
                    rowList.remove(rowToRemove);
                    refreshDropdowns(rowList);
                    container.revalidate();
                    container.repaint();
                },
                () -> refreshDropdowns(rowList)
        );
    }

    private Map<RawMaterial, Double> parseRowsToMap(List<CompositionRow> rowList) throws Exception {
        Map<RawMaterial, Double> result = new LinkedHashMap<>();
        for (CompositionRow row : rowList) {
            RawMaterial rm = row.getSelectedMaterial();
            String qtyText = row.getQuantityText().trim();

            if (rm == null) {
                throw new Exception("Terdapat baris bahan baku yang belum dipilih.");
            }
            if (qtyText.isEmpty()) {
                throw new Exception("Jumlah kebutuhan untuk " + rm.getNama() + " belum diisi.");
            }

            double qty;
            try {
                qty = Double.parseDouble(qtyText);
            } catch (NumberFormatException nfe) {
                throw new Exception("Format angka tidak valid pada bahan baku " + rm.getNama() + ".");
            }

            if (qty <= 0) {
                throw new Exception("Jumlah kebutuhan untuk " + rm.getNama() + " harus lebih dari 0.");
            }

            result.put(rm, qty);
        }
        return result;
    }

    private RawMaterial findMasterMaterialById(int idMaterial) {
        if (masterRawMaterials == null) return null;
        for (RawMaterial rm : masterRawMaterials) {
            if (rm.getId() == idMaterial) {
                return rm;
            }
        }
        return null;
    }

    private List<RawMaterial> getAvailableMaterials(List<CompositionRow> rowList, RawMaterial currentSelected) {
        List<RawMaterial> available = new ArrayList<>(masterRawMaterials);
        for (CompositionRow row : rowList) {
            RawMaterial selected = row.getSelectedMaterial();
            if (selected != null && !selected.equals(currentSelected)) {
                available.remove(selected);
            }
        }
        return available;
    }

    private void refreshDropdowns(List<CompositionRow> rowList) {
        for (CompositionRow row : rowList) {
            RawMaterial currentSelected = row.getSelectedMaterial();
            List<RawMaterial> available = getAvailableMaterials(rowList, currentSelected);
            row.updateComboBoxItems(available, currentSelected);
        }
    }

    // =========================================================================
    // INNER CLASS: Baris Form Dinamis Bahan Baku
    // =========================================================================
    private static class CompositionRow {
        private final JPanel panel;
        private final JComboBox<RawMaterial> cbMaterial;
        private final JTextField txtQuantity;
        private final JLabel lblUnit;
        private final ActionListener materialChangeListener;

        public CompositionRow(List<RawMaterial> initialItems,
                              RawMaterial preselected,
                              String initialQty,
                              Consumer<CompositionRow> onRemove,
                              Runnable onSelectionChanged) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            cbMaterial = new JComboBox<>();
            txtQuantity = new JTextField(initialQty, 8);
            lblUnit = new JLabel("-");
            JButton btnDelete = new JButton("X");
            btnDelete.setForeground(Color.RED);

            materialChangeListener = e -> {
                RawMaterial selected = (RawMaterial) cbMaterial.getSelectedItem();
                if (selected != null && selected.getSatuan() != null) {
                    lblUnit.setText(selected.getSatuan().toString());
                }
                if (onSelectionChanged != null) {
                    onSelectionChanged.run();
                }
            };

            for (RawMaterial rm : initialItems) {
                cbMaterial.addItem(rm);
            }

            if (preselected != null) {
                cbMaterial.setSelectedItem(preselected);
            }

            RawMaterial current = (RawMaterial) cbMaterial.getSelectedItem();
            if (current != null && current.getSatuan() != null) {
                lblUnit.setText(current.getSatuan().toString());
            }

            cbMaterial.addActionListener(materialChangeListener);

            btnDelete.addActionListener(e -> {
                if (onRemove != null) {
                    onRemove.accept(this);
                }
            });

            panel.add(new JLabel("Bahan Baku:"));
            panel.add(cbMaterial);
            panel.add(new JLabel("Jumlah:"));
            panel.add(txtQuantity);
            panel.add(lblUnit);
            panel.add(btnDelete);
        }

        public JPanel getPanel() {
            return panel;
        }

        public RawMaterial getSelectedMaterial() {
            return (RawMaterial) cbMaterial.getSelectedItem();
        }

        public String getQuantityText() {
            return txtQuantity.getText();
        }

        public void updateComboBoxItems(List<RawMaterial> availableItems, RawMaterial currentSelected) {
            cbMaterial.removeActionListener(materialChangeListener);
            cbMaterial.removeAllItems();
            for (RawMaterial rm : availableItems) {
                cbMaterial.addItem(rm);
            }
            cbMaterial.setSelectedItem(currentSelected);
            cbMaterial.addActionListener(materialChangeListener);
        }
    }
}