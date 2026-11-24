package tubes.prak.pbo.components.cards;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import tubes.prak.pbo.components.base.BadgeStyle;
import tubes.prak.pbo.components.base.BaseCard;
import static tubes.prak.pbo.components.base.CardColors.BTN_PRIMARY;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.STRONG;
import static tubes.prak.pbo.components.base.CardColors.SUBTLE;
import static tubes.prak.pbo.components.base.CardColors.TITLE;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.productRelated.Item;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.productRelated.RawMaterial;

public class WarehouseItemCard extends BaseCard {

    private final Item item;
    private final Consumer<Item> onEdit;

    public WarehouseItemCard(Item item, Consumer<Item> onEdit) {
        super(125, 500);
        this.item = item;
        this.onEdit = onEdit;
        build();
    }

    // KIRI: Identitas Item (ID, Nama, Satuan, Badge Status Stok)
    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(260);

        String tipeLabel = (item instanceof Produk)
                ? "PRODUK #" + item.getId()
                : "RAW MATERIAL #" + item.getId();

        JLabel lblId = label(tipeLabel, Font.BOLD, 11, MUTED);
        JTextArea txtNama = new JTextArea(item.getNama());

        txtNama.setLineWrap(true);
        txtNama.setWrapStyleWord(true);
        txtNama.setEditable(false);
        txtNama.setFocusable(false);
        txtNama.setOpaque(false);
        txtNama.setForeground(TITLE);
        txtNama.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtNama.setBorder(null);

        String satuanStr = (item.getSatuan() != null) ? item.getSatuan().name() : "-";
        boolean kritis = isStokKritis();

        JPanel bottomRow = row();
        bottomRow.add(label("Satuan: " + satuanStr + "   ", Font.PLAIN, 12, SUBTLE));
        bottomRow.add(badge(kritis ? "Stok Kritis" : "Stok Aman",
                kritis ? BadgeStyle.DANGER : BadgeStyle.SUCCESS));

        panel.add(lblId);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtNama);
        panel.add(Box.createVerticalGlue());
        panel.add(bottomRow);
        return panel;
    }

    // TENGAH: Detail numerik (HPP, Harga Jual, Jumlah Stok, Batas Kritis)
    @Override
    protected JComponent buildCenter() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 16, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(4, 12, 4, 12));

        if (item instanceof Produk p) {
            panel.add(metricBox("HPP Produk (Rata-rata)", CardFormat.rupiah(p.getHPP())));
            panel.add(metricBox("Harga Jual", CardFormat.rupiah(p.getHargaJual())));
            panel.add(metricBox("Jumlah Stok", p.getStok() + " " + p.getSatuanProduk()));
            panel.add(metricBox("Batas Kritis", p.getBatasKritisProduk() + " " + p.getSatuanProduk()));
        } else if (item instanceof RawMaterial rm) {
            String satuan = (rm.getSatuan() != null) ? rm.getSatuan().name() : "";
            panel.add(metricBox("Harga Rata-rata / Satuan", CardFormat.rupiah(rm.getHargaRata2())));
            panel.add(metricBox("Kategori", "Bahan Baku"));
            panel.add(metricBox("Jumlah Stok", CardFormat.qty(rm.getJumlahStok()) + " " + satuan));
            panel.add(metricBox("Batas Kritis", CardFormat.qty(rm.getBatasKritis()) + " " + satuan));
        }
        return panel;
    }

    // KANAN: Tombol Edit
    @Override
    protected JComponent buildRight() {
        JButton btnEdit = button("Edit", BTN_PRIMARY, Color.WHITE, invoke(onEdit, item));
        btnEdit.setPreferredSize(new Dimension(92, 32));
        return centered(btnEdit, 100);
    }

    private JPanel metricBox(String title, String value) {
        JPanel box = column();
        box.add(label(title, Font.PLAIN, 11, MUTED));
        box.add(Box.createVerticalStrut(2));
        box.add(label(value, Font.BOLD, 13, STRONG));
        return box;
    }

    private boolean isStokKritis() {
        return switch (item) {
            case Produk p -> p.getStok() <= p.getBatasKritisProduk();
            case RawMaterial rm -> rm.getJumlahStok() <= rm.getBatasKritis();
            default -> false;
        };
    }

    public Item getItem() {
        return item;
    }
}