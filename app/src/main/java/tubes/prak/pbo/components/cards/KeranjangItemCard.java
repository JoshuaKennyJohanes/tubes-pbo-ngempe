package tubes.prak.pbo.components.cards;

import static tubes.prak.pbo.components.base.CardColors.BTN_DANGER_SOFT;
import static tubes.prak.pbo.components.base.CardColors.DANGER_TEXT;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.STRONG;
import static tubes.prak.pbo.components.base.CardColors.SUCCESS_TEXT;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.components.base.BadgeStyle;
import tubes.prak.pbo.components.base.BaseCard;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.BarangKeranjang;

public class KeranjangItemCard extends BaseCard {

    private final BarangKeranjang item;
    private final Consumer<BarangKeranjang> onRemoveClick;

    public KeranjangItemCard(BarangKeranjang item, Consumer<BarangKeranjang> onRemoveClick) {
        super(86, 500);
        this.item = item;
        this.onRemoveClick = onRemoveClick;
        build();
    }

    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(280);

        double hargaSatuan = (item.getJumlah() > 0) ? (item.getHargaTotal() / item.getJumlah()) : 0.0;

        panel.add(label(item.getNamaBarang(), Font.BOLD, 14, STRONG));
        panel.add(Box.createVerticalGlue());
        panel.add(label("Harga Satuan: " + CardFormat.rupiah(hargaSatuan), Font.PLAIN, 12, MUTED));
        return panel;
    }

    @Override
    protected JComponent buildCenter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 14));
        panel.setOpaque(false);
        panel.add(badge("Jumlah: " + item.getJumlah() + " pcs", BadgeStyle.INFO));
        return panel;
    }

    @Override
    protected JComponent buildRight() {
        JPanel panel = column(190);

        JButton btnHapus = button("Hapus", BTN_DANGER_SOFT, DANGER_TEXT, invoke(onRemoveClick, item));
        btnHapus.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnHapus.setBorder(new CompoundBorder(
                new LineBorder(BadgeStyle.DANGER.getBorder(), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));
        btnHapus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(labelRight(CardFormat.rupiah(item.getHargaTotal()), Font.BOLD, 15, SUCCESS_TEXT));
        panel.add(Box.createVerticalGlue());
        panel.add(btnHapus);
        return panel;
    }
}