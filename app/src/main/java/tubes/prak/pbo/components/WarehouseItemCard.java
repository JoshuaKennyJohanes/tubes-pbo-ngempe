package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.models.Item;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;

public class WarehouseItemCard extends JPanel {

    public static final int CARD_HEIGHT = 125;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.##");

    private final Item item;

    public WarehouseItemCard(Item item, Consumer<Item> onEdit) {
        this.item = item;
        initCardUI(onEdit);
    }

    private void initCardUI(Consumer<Item> onEdit) {
        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        setPreferredSize(new Dimension(0, CARD_HEIGHT));
        setMinimumSize(new Dimension(500, CARD_HEIGHT));

        // 1. KIRI: Identitas Item (ID, Nama, Satuan, Badge Status Stok)
        add(buildIdentityPanel(), BorderLayout.WEST);

        // 2. TENGAH: Detail Numerik (HPP, Harga Jual, Jumlah Stok, Batas Kritis)
        add(buildMetricsPanel(), BorderLayout.CENTER);

        // 3. KANAN: Tombol Edit
        add(buildActionPanel(onEdit), BorderLayout.EAST);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, CARD_HEIGHT);
    }

    private JPanel buildIdentityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(260, CARD_HEIGHT - 24));

        boolean isProduk = item instanceof Produk;
        String tipeLabel = isProduk ? "PRODUK #" + item.getId() : "RAW MATERIAL #" + item.getId();

        JLabel lblId = new JLabel(tipeLabel);
        lblId.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblId.setForeground(new Color(95, 108, 123));
        lblId.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = new JLabel("<html><body style='width: 245px;'>" + item.getNama() + "</body></html>");
        lblNama.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNama.setForeground(new Color(30, 41, 59));
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String satuanStr = (item.getSatuan() != null) ? item.getSatuan().name() : "-";
        JLabel lblSatuan = new JLabel("Satuan: " + satuanStr + "   ");
        lblSatuan.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSatuan.setForeground(new Color(71, 85, 105));

        boolean isKritis = isStokKritis();
        JLabel lblStatus = createStockStatusBadge(isKritis);

        bottomRow.add(lblSatuan);
        bottomRow.add(lblStatus);

        panel.add(lblId);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblNama);
        panel.add(Box.createVerticalGlue());
        panel.add(bottomRow);

        return panel;
    }

    private JPanel buildMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 16, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(4, 12, 4, 12));

        if (item instanceof Produk p) {
            panel.add(createMetricBox("HPP Produk (Rata-rata)", "Rp " + CURRENCY_FORMAT.format(p.getHargaRata2Produk())));
            panel.add(createMetricBox("Harga Jual", "Rp " + CURRENCY_FORMAT.format(p.getHargaJualProduk())));
            panel.add(createMetricBox("Jumlah Stok", p.getStok() + " " + p.getSatuanProduk()));
            panel.add(createMetricBox("Batas Kritis", p.getBatasKritisProduk() + " " + p.getSatuanProduk()));
        } else if (item instanceof RawMaterial rm) {
            String satuan = (rm.getSatuan() != null) ? rm.getSatuan().name() : "";
            panel.add(createMetricBox("Harga Rata-rata / Satuan", "Rp " + CURRENCY_FORMAT.format(rm.getHargaRata2())));
            panel.add(createMetricBox("Kategori", "Bahan Baku"));
            panel.add(createMetricBox("Jumlah Stok", QTY_FORMAT.format(rm.getJumlahStok()) + " " + satuan));
            panel.add(createMetricBox("Batas Kritis", QTY_FORMAT.format(rm.getBatasKritis()) + " " + satuan));
        }

        return panel;
    }

    private JPanel createMetricBox(String title, String value) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(100, 116, 139));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblValue.setForeground(new Color(15, 23, 42));

        box.add(lblTitle);
        box.add(Box.createVerticalStrut(2));
        box.add(lblValue);
        return box;
    }

    private boolean isStokKritis() {
        if (item instanceof Produk p) {
            return p.getStok() <= p.getBatasKritisProduk();
        } else if (item instanceof RawMaterial rm) {
            return rm.getJumlahStok() <= rm.getBatasKritis();
        }
        return false;
    }

    private JLabel createStockStatusBadge(boolean isKritis) {
        JLabel badge = new JLabel(isKritis ? " Stok Kritis " : " Stok Aman ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setOpaque(true);

        if (isKritis) {
            badge.setBackground(new Color(254, 226, 226));
            badge.setForeground(new Color(185, 28, 28));
            badge.setBorder(new LineBorder(new Color(252, 165, 165), 1, true));
        } else {
            badge.setBackground(new Color(220, 252, 231));
            badge.setForeground(new Color(22, 101, 52));
            badge.setBorder(new LineBorder(new Color(134, 239, 172), 1, true));
        }
        return badge;
    }

    private JPanel buildActionPanel(Consumer<Item> onEdit) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, CARD_HEIGHT - 24));

        JButton btnEdit = new JButton("Edit");
        btnEdit.setFocusPainted(false);
        btnEdit.setBackground(new Color(37, 99, 235));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEdit.setPreferredSize(new Dimension(92, 32));
        btnEdit.addActionListener(e -> {
            if (onEdit != null) {
                onEdit.accept(item);
            }
        });

        panel.add(btnEdit, new GridBagConstraints());
        return panel;
    }

    public Item getItem() {
        return item;
    }
}