package tubes.prak.pbo.components.cards;

import static tubes.prak.pbo.components.base.CardColors.BTN_DANGER;
import static tubes.prak.pbo.components.base.CardColors.BTN_PRIMARY;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.SUBTLE;
import static tubes.prak.pbo.components.base.CardColors.SUCCESS_TEXT;
import static tubes.prak.pbo.components.base.CardColors.TITLE;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tubes.prak.pbo.components.base.BaseCard;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.productRelated.Bom;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.productRelated.RawMaterial;

public class BomProductCard extends BaseCard {

    private static final String[] COLUMNS = {"ID Material", "Nama Raw Material", "Jumlah Kebutuhan", "Satuan"};

    private final Bom bom;
    private final double estimasiHpp;
    private final Consumer<Bom> onEdit;
    private final Consumer<Bom> onDelete;

    public BomProductCard(Bom bom, double estimasiHpp, Consumer<Bom> onEdit, Consumer<Bom> onDelete) {
        super(150, 450, new Insets(10, 16, 10, 16));
        this.bom = bom;
        this.estimasiHpp = estimasiHpp;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        build();
    }

    // KIRI: Informasi Produk
    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(210);

        Produk produk = bom.getProduk();
        String namaProduk = (produk != null) ? produk.toString() : "Produk #" + bom.getIdProduk();

        panel.add(label("ID Produk: #" + bom.getIdProduk(), Font.BOLD, 11, MUTED));
        panel.add(Box.createVerticalStrut(4));
        panel.add(label("<html><body style='width: 195px;'>" + namaProduk + "</body></html>",
                Font.BOLD, 14, TITLE));
        panel.add(Box.createVerticalGlue());
        panel.add(label("Komposisi: " + bom.getKebutuhanMaterial().size() + " bahan baku",
                Font.PLAIN, 12, SUBTLE));
        panel.add(Box.createVerticalStrut(2));
        panel.add(label("Est. HPP/unit: " + CardFormat.rupiah(estimasiHpp), Font.PLAIN, 12, SUCCESS_TEXT));
        return panel;
    }

    // TENGAH: Tabel daftar raw material (read-only)
    @Override
    protected JComponent buildCenter() {
        List<Object[]> rows = new ArrayList<>();
        for (Map.Entry<RawMaterial, Double> entry : bom.getKebutuhanMaterial().entrySet()) {
            RawMaterial rm = entry.getKey();
            String nama = (rm.getNama() != null && !rm.getNama().isBlank()) ? rm.getNama() : "-";
            String satuan = (rm.getSatuan() != null) ? rm.getSatuan().name() : "-";
            rows.add(new Object[]{String.valueOf(rm.getId()), nama, CardFormat.qty(entry.getValue()), satuan});
        }

        return readOnlyTable(COLUMNS, rows, 22, table -> {
            columnWidth(table, 0, 80, 95);
            columnWidth(table, 1, 220, 0);
            columnWidth(table, 2, 120, 0);
            columnWidth(table, 3, 80, 95);

            alignColumn(table, 0, SwingConstants.CENTER);
            alignColumn(table, 2, SwingConstants.RIGHT);
            alignColumn(table, 3, SwingConstants.CENTER);
        });
    }

    // KANAN: Tombol Edit & Hapus
    @Override
    protected JComponent buildRight() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new java.awt.Dimension(105, contentHeight()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(button("Edit", BTN_PRIMARY, Color.WHITE, invoke(onEdit, bom)), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(button("Hapus", BTN_DANGER, Color.WHITE, invoke(onDelete, bom)), gbc);

        return panel;
    }

    public Bom getBom() {
        return bom;
    }
}