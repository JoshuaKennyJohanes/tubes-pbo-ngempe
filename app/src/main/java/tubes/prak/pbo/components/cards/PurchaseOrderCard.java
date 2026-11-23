package tubes.prak.pbo.components.cards;

import static tubes.prak.pbo.components.base.CardColors.BODY;
import static tubes.prak.pbo.components.base.CardColors.BTN_SUCCESS;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.STRONG;
import static tubes.prak.pbo.components.base.CardColors.SUBTLE;
import static tubes.prak.pbo.components.base.CardColors.SUCCESS_TEXT;
import static tubes.prak.pbo.components.base.CardColors.TITLE;
import static tubes.prak.pbo.components.base.CardColors.WARNING_TEXT;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tubes.prak.pbo.ENUM.StatusPo;
import tubes.prak.pbo.components.base.BadgeStyle;
import tubes.prak.pbo.components.base.BaseCard;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.PurchaseOrder;

public class PurchaseOrderCard extends BaseCard {

    private final PurchaseOrder po;
    private final Consumer<PurchaseOrder> onArrivedClick;

    public PurchaseOrderCard(PurchaseOrder po, Consumer<PurchaseOrder> onArrivedClick) {
        super(112, 600, new Insets(10, 16, 10, 16));
        this.po = po;
        this.onArrivedClick = onArrivedClick;
        build();
    }

    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(245);
        boolean arrived = po.getStatusPo() == StatusPo.ARRIVED;

        JPanel topRow = row();
        topRow.add(label("PO #" + po.getIdPo() + "   ", Font.BOLD, 13, TITLE));
        topRow.add(badge(po.getStatusPo().name(), arrived ? BadgeStyle.SUCCESS : BadgeStyle.WARNING));

        JLabel lblTglBeli = label("Tanggal Pembelian: " + CardFormat.date(po.getTanggalPembelian()),
                Font.PLAIN, 11, SUBTLE);

        JLabel lblTglTerima = label(
                "Tanggal Diterima: " + CardFormat.date(po.getTanggalDiterima(), "Belum Diterima"),
                Font.PLAIN, 11, arrived ? SUCCESS_TEXT : WARNING_TEXT);

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblTglBeli);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblTglTerima);
        return panel;
    }

    @Override
    protected JComponent buildCenter() {
        JPanel panel = column();
        panel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        String satuan = (po.getSatuanMaterial() != null) ? po.getSatuanMaterial() : "";
        JLabel lblMaterial = label(
                "Raw Material: " + po.getNamaMaterial() + "  (" + CardFormat.qty(po.getJumlahDibeli()) + " " + satuan + ")",
                Font.BOLD, 13, STRONG);
        JLabel lblVendor = label("Vendor: " + po.getNamaVendor(), Font.PLAIN, 12, BODY);
        JLabel lblCreator = label("Created By: " + po.getCreatorDisplay(), Font.PLAIN, 11, MUTED);

        panel.add(lblMaterial);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblVendor);
        panel.add(Box.createVerticalGlue());
        panel.add(lblCreator);
        return panel;
    }

    @Override
    protected JComponent buildRight() {
        JPanel panel = column(215);

        String satuan = (po.getSatuanMaterial() != null) ? po.getSatuanMaterial() : "unit";

        panel.add(labelRight(CardFormat.rupiah(po.getHargaBeliAktual()), Font.BOLD, 14, STRONG));
        panel.add(Box.createVerticalStrut(2));
        panel.add(labelRight("@ " + CardFormat.rupiah(po.getHargaPerSatuan()) + " / " + satuan,
                Font.PLAIN, 11, MUTED));
        panel.add(Box.createVerticalGlue());

        if (po.getStatusPo() == StatusPo.PAID) {
            JButton btnArrived = button("Tandai Arrived", BTN_SUCCESS, Color.WHITE, invoke(onArrivedClick, po));
            btnArrived.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnArrived.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
            panel.add(btnArrived);
        } else {
            panel.add(labelRight("Stok & HPP Diperbarui", Font.ITALIC, 11, SUCCESS_TEXT));
        }
        return panel;
    }
}