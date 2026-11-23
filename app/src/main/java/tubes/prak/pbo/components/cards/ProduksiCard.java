package tubes.prak.pbo.components.cards;

import static tubes.prak.pbo.components.base.CardColors.SUBTLE;
import static tubes.prak.pbo.components.base.CardColors.TITLE;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.components.base.BaseCard;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.Produksi;

public class ProduksiCard extends BaseCard {

    private final Produksi produksi;
    private final Consumer<Produksi> onDetailClick;

    public ProduksiCard(Produksi produksi, Consumer<Produksi> onDetailClick) {
        super(84, 400);
        this.produksi = produksi;
        this.onDetailClick = onDetailClick;
        build();
    }

    // TENGAH: 4 teks informasi (2x2)
    @Override
    protected JComponent buildCenter() {
        JPanel info = new JPanel(new GridLayout(2, 2, 16, 8));
        info.setOpaque(false);

        JPanel statusWrapper = row();
        StatusProduksi status = produksi.getStatusProduksi();
        // TODO: petakan StatusProduksi ke BadgeStyle begitu konstanta enum-nya diketahui
        statusWrapper.add(badge(status.name(), status.getTextColor(), status.getBadgeColor()));

        info.add(label("ID Produksi: #" + produksi.getIdProduksi(), Font.BOLD, 14, TITLE));
        info.add(label("Tanggal: " + CardFormat.date(produksi.getTanggalProduksi()), Font.PLAIN, 13, SUBTLE));
        info.add(label("ID Produk: " + produksi.getIdProduk(), Font.PLAIN, 13, SUBTLE));
        info.add(statusWrapper);
        return info;
    }

    // KANAN: Tombol Detail
    @Override
    protected JComponent buildRight() {
        // Tombol default L&F (tanpa warna kustom), sama seperti tampilan sebelumnya
        JButton btnDetail = new JButton("Detail");
        btnDetail.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDetail.setFocusPainted(false);
        btnDetail.addActionListener(e -> invoke(onDetailClick, produksi).run());
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetail.setPreferredSize(new Dimension(95, 34));
        return centered(btnDetail, 105);
    }

    public Produksi getProduksi() {
        return produksi;
    }
}