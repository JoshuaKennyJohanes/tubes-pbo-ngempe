package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.Bom;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;

public class BomProductCard extends JPanel {

    public static final int CARD_HEIGHT = 150;
    private static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.##");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final Bom bom;

    public BomProductCard(Bom bom, double estimasiHpp, Consumer<Bom> onEdit, Consumer<Bom> onDelete) {
        this.bom = bom;
        initCardUI(estimasiHpp, onEdit, onDelete);
    }

    private void initCardUI(double estimasiHpp, Consumer<Bom> onEdit, Consumer<Bom> onDelete) {
        setLayout(new BorderLayout(14, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        // Tinggi tetap, lebar mengikuti parent container
        setPreferredSize(new Dimension(0, CARD_HEIGHT));
        setMinimumSize(new Dimension(450, CARD_HEIGHT));

        // 1. BAGIAN KIRI: Informasi Produk
        JPanel leftPanel = buildProductInfoPanel(estimasiHpp);
        add(leftPanel, BorderLayout.WEST);

        // 2. BAGIAN TENGAH: Tabel Scrollable Daftar Raw Material
        JScrollPane tableScrollPane = buildMaterialTableScrollPane();
        add(tableScrollPane, BorderLayout.CENTER);

        // 3. BAGIAN KANAN: Tombol Edit & Hapus
        JPanel rightPanel = buildActionPanel(onEdit, onDelete);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    public Dimension getMaximumSize() {
        // Memastikan tinggi tetap konstan saat diletakkan di BoxLayout vertikal,
        // namun lebar fleksibel mengikuti frame aplikasi.
        return new Dimension(Integer.MAX_VALUE, CARD_HEIGHT);
    }

    private JPanel buildProductInfoPanel(double estimasiHpp) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(210, CARD_HEIGHT - 20));

        Produk produk = bom.getProduk();
        String namaProduk = (produk != null) ? produk.toString() : "Produk #" + bom.getIdProduk();

        JLabel lblIdProduk = new JLabel("ID Produk: #" + bom.getIdProduk());
        lblIdProduk.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblIdProduk.setForeground(new Color(95, 108, 123));
        lblIdProduk.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNamaProduk = new JLabel("<html><body style='width: 195px;'>" + namaProduk + "</body></html>");
        lblNamaProduk.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNamaProduk.setForeground(new Color(30, 41, 59));
        lblNamaProduk.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblJumlahBahan = new JLabel("Komposisi: " + bom.getKebutuhanMaterial().size() + " bahan baku");
        lblJumlahBahan.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblJumlahBahan.setForeground(new Color(71, 85, 105));
        lblJumlahBahan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHpp = new JLabel("Est. HPP/unit: Rp " + CURRENCY_FORMAT.format(estimasiHpp));
        lblHpp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHpp.setForeground(new Color(22, 101, 52));
        lblHpp.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblIdProduk);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblNamaProduk);
        panel.add(Box.createVerticalGlue());
        panel.add(lblJumlahBahan);
        panel.add(Box.createVerticalStrut(2));
        panel.add(lblHpp);

        return panel;
    }

    private JScrollPane buildMaterialTableScrollPane() {
        String[] columns = {"ID Material", "Nama Raw Material", "Jumlah Kebutuhan", "Satuan"};

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only di dalam card
            }
        };

        for (Map.Entry<RawMaterial, Double> entry : bom.getKebutuhanMaterial().entrySet()) {
            RawMaterial rm = entry.getKey();
            double qty = entry.getValue();

            String idMat = String.valueOf(rm.getId());
            String namaMat = (rm.getNama() != null && !rm.getNama().isBlank()) ? rm.getNama() : "-";
            String qtyFormatted = QTY_FORMAT.format(qty);
            String satuan = (rm.getSatuan() != null) ? rm.getSatuan().name() : "-";

            tableModel.addRow(new Object[]{idMat, namaMat, qtyFormatted, satuan});
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 234, 240));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Proporsi lebar kolom
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(95);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(95);

        // Alignment kolom ID dan Jumlah Kebutuhan
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        return scrollPane;
    }

    private JPanel buildActionPanel(Consumer<Bom> onEdit, Consumer<Bom> onDelete) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(105, CARD_HEIGHT - 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JButton btnEdit = new JButton("Edit");
        btnEdit.setFocusPainted(false);
        btnEdit.setBackground(new Color(37, 99, 235));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEdit.addActionListener(e -> {
            if (onEdit != null) {
                onEdit.accept(bom);
            }
        });

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setFocusPainted(false);
        btnDelete.setBackground(new Color(220, 38, 38));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDelete.addActionListener(e -> {
            if (onDelete != null) {
                onDelete.accept(bom);
            }
        });

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(btnEdit, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(btnDelete, gbc);

        return panel;
    }

    public Bom getBom() {
        return bom;
    }
}