package tubes.prak.pbo.view.ViewManager;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;

public class BomView extends JPanel{
    ItemController itemController = new ItemController();


    // Master Product Selection
    private JComboBox<Produk> cbFinishedProduct;
    private JButton btnAddComposition;
    private JButton btnSaveBom;

    // Panel penampung baris-baris komposisi
    private JPanel compositionContainer;

    // Master data dari database
    private List<RawMaterial> masterRawMaterials;
    
    // Menyimpan baris GUI komposisi yang aktif
    private List<CompositionRow> compositionRows;

    public BomView() {
        
        setSize(650, 500);
        
        // Simulated Master Data dari Database
        loadMasterData();

        compositionRows = new ArrayList<>();

        initUI();
    }

    public void loadMasterData() {
        masterRawMaterials = new ArrayList<>();
        masterRawMaterials = itemController.getAllMaterials();
        cbFinishedProduct = new JComboBox<>();

        for(Produk item : itemController.getAllProduk()){
            cbFinishedProduct.addItem(item);
        }

    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel: Pilih Produk Utama
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("1. Pilih Produk"));
        topPanel.add(new JLabel("Produk Jadi:"));
        topPanel.add(cbFinishedProduct);

        // Center Panel: Container Komposisi (Scrollable)
        compositionContainer = new JPanel();
        compositionContainer.setLayout(new BoxLayout(compositionContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(compositionContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("2. Komposisi Material"));

        // Bottom Panel: Tombol Aksi
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddComposition = new JButton("+ Tambah Komposisi");
        btnSaveBom = new JButton("Simpan BOM");

        bottomPanel.add(btnAddComposition);
        bottomPanel.add(btnSaveBom);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event Listeners
        btnAddComposition.addActionListener(e -> addCompositionRow());
        btnSaveBom.addActionListener(e -> saveBomData());
    }

    // Method untuk menambah baris komposisi baru
    private void addCompositionRow() {
        List<RawMaterial> availableMaterials = getAvailableMaterials(null);

        if (availableMaterials.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Semua bahan baku master sudah dipilih!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CompositionRow row = new CompositionRow(availableMaterials);
        compositionRows.add(row);
        compositionContainer.add(row.getPanel());

        refreshAllDropdowns();
        compositionContainer.revalidate();
        compositionContainer.repaint();
    }

    // Mengambil daftar material yang BELUM dipilih oleh baris lain
    private List<RawMaterial> getAvailableMaterials(RawMaterial currentSelected) {
        List<RawMaterial> available = new ArrayList<>(masterRawMaterials);
        for (CompositionRow row : compositionRows) {
            RawMaterial selected = row.getSelectedMaterial();
            if (selected != null && !selected.equals(currentSelected)) {
                available.remove(selected);
            }
        }
        return available;
    }

    // Memperbarui isi dropdown di setiap baris agar item terpilih di baris lain menghilang
    private void refreshAllDropdowns() {
        for (CompositionRow row : compositionRows) {
            RawMaterial currentSelected = row.getSelectedMaterial();
            List<RawMaterial> available = getAvailableMaterials(currentSelected);
            row.updateComboBoxItems(available, currentSelected);
        }
    }

    // Inner Class untuk Mengelola Baris Komposisi GUI
    // Inner Class untuk Mengelola Baris Komposisi GUI
 private class CompositionRow {
    private JPanel panel;
    private JComboBox<RawMaterial> cbMaterial;
    private JTextField txtQuantity;
    private JLabel lblUnit;
    private JButton btnDelete;
    
    // 1. Simpan referensi listener sebagai field
    private ActionListener materialChangeListener;

    public CompositionRow(List<RawMaterial> initialItems) {
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbMaterial = new JComboBox<>();
        txtQuantity = new JTextField(6);
        lblUnit = new JLabel("-");
        btnDelete = new JButton("X");
        btnDelete.setForeground(Color.RED);

        // 2. Inisialisasi listener 1x saja
        materialChangeListener = e -> {
            RawMaterial selected = (RawMaterial) cbMaterial.getSelectedItem();
            if (selected != null && selected.getSatuan() != null) {
                lblUnit.setText(selected.getSatuan().toString());
                refreshAllDropdowns();
            }
        };

        // Populate initial dropdown items
        for (RawMaterial rm : initialItems) {
            cbMaterial.addItem(rm);
        }

        if (cbMaterial.getItemCount() > 0) {
            RawMaterial selected = (RawMaterial) cbMaterial.getSelectedItem();
            if (selected != null && selected.getSatuan() != null) {
                lblUnit.setText(selected.getSatuan().toString());
            }
        }

        // Pasang listener
        cbMaterial.addActionListener(materialChangeListener);

        // Listener hapus baris
        btnDelete.addActionListener(e -> {
            compositionContainer.remove(panel);
            compositionRows.remove(this);
            refreshAllDropdowns();
            compositionContainer.revalidate();
            compositionContainer.repaint();
        });

        panel.add(new JLabel("Bahan Baku:"));
        panel.add(cbMaterial);
        panel.add(new JLabel("Jumlah:"));
        panel.add(txtQuantity);
        panel.add(lblUnit);
        panel.add(btnDelete);
    }

    public JPanel getPanel() { return panel; }

    public RawMaterial getSelectedMaterial() {
        return (RawMaterial) cbMaterial.getSelectedItem();
    }

    public String getQuantityText() {
        return txtQuantity.getText();
    }

    public void updateComboBoxItems(List<RawMaterial> availableItems, RawMaterial currentSelected) {
        // 3. Lepas listener yang ada secara BENAR (menggunakan removeActionListener)
        cbMaterial.removeActionListener(materialChangeListener); 
        
        cbMaterial.removeAllItems();
        for (RawMaterial rm : availableItems) {
            cbMaterial.addItem(rm);
        }
        cbMaterial.setSelectedItem(currentSelected);

        // 4. Pasang kembali listener setelah manipulasi item selesai
        cbMaterial.addActionListener(materialChangeListener);
    }
}

    // Method untuk simpan data ke Database
    private void saveBomData() {
        

        if (cbFinishedProduct.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih produk jadi terlebih dahulu.");
            return;
        }

        System.out.println("--- MENYIMPAN DATA BOM ---");
        System.out.println("Produk: " + cbFinishedProduct.getSelectedItem());
        
        for (CompositionRow row : compositionRows) {

            try {
            itemController.registerBOM((Produk) cbFinishedProduct.getSelectedItem(), row.getSelectedMaterial(), Double.parseDouble(row.getQuantityText()));
            }    
            catch (Exception e) {
                e.printStackTrace();
            }
            
            //Validasi SOUT
            RawMaterial rm = row.getSelectedMaterial();
            String qty = row.getQuantityText();
            if (rm != null && !qty.isEmpty()) {
                System.out.println("FK Material ID: " + rm.getId() + " | Nama: " + rm.getNama() + " | Qty: " + qty + " " + rm.getSatuan());
            }
        }
        JOptionPane.showMessageDialog(this, "BOM Berhasil Disimpan!");
    }
}