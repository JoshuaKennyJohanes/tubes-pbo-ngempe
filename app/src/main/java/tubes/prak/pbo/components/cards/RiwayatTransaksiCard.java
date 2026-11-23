package tubes.prak.pbo.components.cards;

import static tubes.prak.pbo.components.base.CardColors.BODY;
import static tubes.prak.pbo.components.base.CardColors.BTN_DANGER_SOFT;
import static tubes.prak.pbo.components.base.CardColors.BTN_SUCCESS;
import static tubes.prak.pbo.components.base.CardColors.DANGER_TEXT;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.STRONG;
import static tubes.prak.pbo.components.base.CardColors.SUBTLE;
import static tubes.prak.pbo.components.base.CardColors.SUCCESS_TEXT;
import static tubes.prak.pbo.components.base.CardColors.TITLE;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tubes.prak.pbo.ENUM.StatusPembayaran;
import tubes.prak.pbo.components.base.BadgeStyle;
import tubes.prak.pbo.components.base.BaseCard;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;

public class RiwayatTransaksiCard extends BaseCard {

    private final SalesOrder order;
    private final StatusPembayaran status;
    private final Consumer<SalesOrder> onPayClick;
    private final Consumer<SalesOrder> onCancelClick;

    public RiwayatTransaksiCard(SalesOrder order,
                                Consumer<SalesOrder> onPayClick,
                                Consumer<SalesOrder> onCancelClick) {
        super(0, 580); // tinggi dinamis, lihat cardHeight()
        this.order = order;
        this.status = StatusPembayaran.fromString(order.getStatusPembayaran());
        this.onPayClick = onPayClick;
        this.onCancelClick = onCancelClick;
        build();
    }

    // Tinggi mengikuti jumlah item pada pesanan
    @Override
    protected int cardHeight() {
        int itemCount = Math.max(1, order.getDetails().size());
        return Math.max(114, 70 + (itemCount * 20));
    }

    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(225);

        JPanel topRow = row();
        topRow.add(label("Order #" + order.getIdOrder() + "   ", Font.BOLD, 13, TITLE));
        topRow.add(badge(status.name(), statusStyle(status)));

        JLabel lblTanggal = label("Tanggal Pesan: " + CardFormat.date(order.getTanggalPesan()),
                Font.PLAIN, 11, SUBTLE);

        String metode = (order.getCaraBayar() != null && !order.getCaraBayar().isBlank())
                ? order.getCaraBayar()
                : "QRIS";
        JLabel lblMetode = label("Metode Bayar: " + metode, Font.BOLD, 11, BODY);

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblTanggal);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblMetode);
        return panel;
    }

    @Override
    protected JComponent buildCenter() {
        JPanel panel = column();
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));

        panel.add(label("Rincian Produk:", Font.BOLD, 12, BODY));
        panel.add(Box.createVerticalStrut(4));

        if (order.getDetails().isEmpty()) {
            panel.add(label("- Tidak ada rincian item", Font.ITALIC, 11, Color.GRAY));
        } else {
            for (SalesOrderDetail detail : order.getDetails()) {
                String line = String.format("• %s  (%d x %s = %s)",
                        detail.getNamaProduk(),
                        detail.getJumlahTerjual(),
                        CardFormat.rupiah(detail.getHargaJualAktual()),
                        CardFormat.rupiah(detail.getSubtotal()));
                panel.add(label(line, Font.PLAIN, 12, TITLE));
                panel.add(Box.createVerticalStrut(2));
            }
        }
        return panel;
    }

    @Override
    protected JComponent buildRight() {
        JPanel panel = column(225);

        panel.add(labelRight("Total Pembayaran:", Font.PLAIN, 11, MUTED));
        panel.add(labelRight(CardFormat.rupiah(order.getTotalTransaksi()), Font.BOLD, 15, STRONG));
        panel.add(Box.createVerticalGlue());

        switch (status) {
            case PENDING -> {
                JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
                btnRow.setOpaque(false);
                btnRow.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnRow.add(button("Batalkan", BTN_DANGER_SOFT, DANGER_TEXT, invoke(onCancelClick, order)));
                btnRow.add(button("Bayar", BTN_SUCCESS, Color.WHITE, invoke(onPayClick, order)));
                panel.add(btnRow);
            }
            case PAID -> panel.add(labelRight("Pembayaran Lunas (QRIS)", Font.ITALIC, 11, SUCCESS_TEXT));
            default -> panel.add(labelRight("Pesanan Dibatalkan", Font.ITALIC, 11, DANGER_TEXT));
        }
        return panel;
    }

    private static BadgeStyle statusStyle(StatusPembayaran status) {
        return switch (status) {
            case PAID -> BadgeStyle.SUCCESS;
            case PENDING -> BadgeStyle.WARNING;
            case CANCELLED -> BadgeStyle.DANGER;
            default -> BadgeStyle.NEUTRAL;
        };
    }
}