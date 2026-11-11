package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tubes.prak.pbo.ENUM.Satuan;
import tubes.prak.pbo.controller.ItemController;

public class RegisterItemView extends JPanel {

    

    ItemController itemController = new ItemController();
    private JComboBox<Satuan> cbItemUnit;
    private JComboBox<String> cbItemType;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Komponen Form Umum (Raw Material & Product)

    private JTextField txtProductName, txtProductPrice, txtProductCriticalLimit;
    private JTextField txtRawName, txtRawCost, txtRawCriticalLimit, txtRawStock;

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

        // 1. Panel Default (Kosong/Instruksi)
        JPanel emptyPanel = new JPanel();
        emptyPanel.add(new JLabel("Silakan pilih jenis item terlebih dahulu."));

        // 2. Panel Form Product
        JPanel productPanel = createProductForm();

        // 3. Panel Form Raw Material
        JPanel rawMaterialPanel = createRawMaterialForm();

        // Menambahkan panel-panel ke CardLayout
        cardPanel.add(emptyPanel, "EMPTY");
        cardPanel.add(productPanel, "PRODUCT");
        cardPanel.add(rawMaterialPanel, "RAW_MATERIAL");

        add(cardPanel, BorderLayout.CENTER);

        // --- PANEL BAWAH: Tombol Action ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton btnSubmit = new JButton("Simpan");
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- EVENT HANDLERS ---
        cbItemType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cbItemType.getSelectedItem();
                if ("Product".equals(selected)) {
                    cardLayout.show(cardPanel, "PRODUCT");
                } else if ("Raw Material".equals(selected)) {
                    cardLayout.show(cardPanel, "RAW_MATERIAL");
                } else {
                    cardLayout.show(cardPanel, "EMPTY");
                }
            }
        });

        btnSubmit.addActionListener(e -> handleSubmit());
    }

    // Form khusus Produk
    private JPanel createProductForm() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        Satuan[] listSatuan = { Satuan.GR, Satuan.KG, Satuan.L, Satuan.ML, Satuan.PACK, Satuan.PCS, Satuan.TABUNG };
        cbItemUnit = new JComboBox<>(listSatuan);


       

        txtProductName = new JTextField();
        txtProductPrice = new JTextField();
        txtProductCriticalLimit = new JTextField();

        panel.add(new JLabel("Nama Produk:"));
        panel.add(txtProductName);
        panel.add(new JLabel("Satuan (e.g. Pcs, Box):"));
        panel.add(cbItemUnit);
        panel.add(new JLabel("Harga Jual:"));
        panel.add(txtProductPrice);
        panel.add(new JLabel("Batas Kritis Warehouse:"));
        panel.add(txtProductCriticalLimit);

        return panel;
    }

    // Form khusus Raw Material
    private JPanel createRawMaterialForm() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        Satuan[] listSatuan = { Satuan.GR, Satuan.KG, Satuan.L, Satuan.ML, Satuan.PACK, Satuan.PCS, Satuan.TABUNG };
        cbItemUnit = new JComboBox<>(listSatuan);

        txtRawName = new JTextField();
        txtRawCost = new JTextField();
        txtRawCriticalLimit = new JTextField();
        txtRawStock = new JTextField();

        panel.add(new JLabel("Nama Bahan Baku:"));
        panel.add(txtRawName);
        panel.add(new JLabel("Satuan (e.g. gr, ml, kg):"));
        panel.add(cbItemUnit);
        panel.add(new JLabel("Harga per Satuan:"));
        panel.add(txtRawCost);
        panel.add(new JLabel("Batas Kritis:"));
        panel.add(txtRawCriticalLimit);
        panel.add(new JLabel("Jumlah Stok:"));
        panel.add(txtRawStock);

        return panel;
    }

    // Logic saat tombol Simpan diklik
    private void handleSubmit() {
        String selectedType = (String) cbItemType.getSelectedItem();

        if ("Product".equals(selectedType)) {
            String name = txtProductName.getText();
            Satuan unit = Satuan.valueOf(cbItemUnit.getSelectedItem().toString());
            double price = Double.parseDouble(txtProductPrice.getText());
            int limit = Integer.parseInt(txtProductCriticalLimit.getText());


            try {
                itemController.registerProduk(name, unit, price, limit);
                //munculkan option pane kalau suksse
                //bisa diganti sama generic panel sukses
                JOptionPane.showMessageDialog(this,
                    "Product Berhasil Disimpan:\n" +
                            "Nama: " + name + "\n" +
                            "Satuan: " + unit + "\n" +
                            "Harga Jual: " + price + "\n" +
                            "Batas Kritis: " + limit,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
              
            }
            
            
            txtProductName.setText("");
            txtProductPrice.setText("");
            txtProductCriticalLimit.setText("");

        }
        else if ("Raw Material".equals(selectedType)) {
            String rawName = txtRawName.getText();
            Satuan rawUnit = Satuan.valueOf(cbItemUnit.getSelectedItem().toString());
            double rawStock = Double.parseDouble(txtRawStock.getText());
            double rawPrice = Double.parseDouble(txtRawCost.getText());
            double rawLimit = Double.parseDouble(txtRawCriticalLimit.getText());


            try {
                itemController.registerRawMaterial(rawName, rawUnit, rawPrice, rawLimit, rawStock);
                JOptionPane.showMessageDialog(this,
                    "Product Berhasil Disimpan:\n" +
                            "Nama: " + rawName + "\n" +
                            "Satuan: " + rawUnit + "\n" +
                            "Harga Bahan Baku: " + rawPrice + "\n" +
                            "Batas Kritis: " + rawLimit,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

                txtRawName.setText("");
                txtRawCost.setText("");
                txtRawStock.setText("");
                txtRawCriticalLimit.setText("");
            } catch (Exception e) {
                System.out.println("Error Naon Anjing ieu goblog");
            }


        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih jenis item terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

}
