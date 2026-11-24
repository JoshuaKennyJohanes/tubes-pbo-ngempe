package tubes.prak.pbo.components.cards;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import tubes.prak.pbo.components.base.BadgeStyle;
import tubes.prak.pbo.components.base.BaseCard;
import static tubes.prak.pbo.components.base.CardColors.BODY;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.SUBTLE;
import static tubes.prak.pbo.components.base.CardColors.SUCCESS_TEXT;
import static tubes.prak.pbo.components.base.CardColors.TITLE;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;

public class SalesOrderCard extends BaseCard {

    private static final String[] COLUMNS = {"ID Produk", "Nama Produk", "Qty", "Harga Satuan", "Subtotal"};

    private final SalesOrder salesOrder;

    public SalesOrderCard(SalesOrder salesOrder) {
        super(175, 520);
        this.salesOrder = salesOrder;
        build();
    }

    // KIRI: Informasi Order, Pembeli, Alamat, Status & Total
    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(295);

        JLabel lblOrderHeader = label(
                "Order #" + salesOrder.getIdOrder() + "  •  " + CardFormat.date(salesOrder.getTanggalPesan()),
                Font.BOLD, 13, TITLE);

        JLabel lblPembeli = label("Pembeli: " + salesOrder.getUserDisplay(), Font.BOLD, 12, BODY);

        String alamat = (salesOrder.getAlamatUser() != null && !salesOrder.getAlamatUser().isBlank())
                ? salesOrder.getAlamatUser()
                : "-";
       JTextArea txtAlamat = new JTextArea("Alamat: " + alamat);

        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setEditable(false);
        txtAlamat.setFocusable(false);
        txtAlamat.setOpaque(false);
        txtAlamat.setForeground(MUTED);
        txtAlamat.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtAlamat.setBorder(null);
        String caraBayar = (salesOrder.getCaraBayar() != null) ? salesOrder.getCaraBayar() : "-";
        String statusText = (salesOrder.getStatusPembayaran() != null) ? salesOrder.getStatusPembayaran() : "Pending";

        JPanel paymentRow = row();
        paymentRow.add(label("Bayar: " + caraBayar + "   |   Status: ", Font.PLAIN, 11, SUBTLE));
        paymentRow.add(badge(statusText, statusStyle(statusText)));

        JLabel lblTotal = label("Total Transaksi: " + CardFormat.rupiah(salesOrder.getTotalTransaksi()),
                Font.BOLD, 13, SUCCESS_TEXT);

        panel.add(lblOrderHeader);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblPembeli);
        panel.add(Box.createVerticalStrut(3));
        panel.add(txtAlamat);
        panel.add(Box.createVerticalGlue());
        panel.add(paymentRow);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTotal);
        return panel;
    }

    // TENGAH: Tabel scrollable produk yang dibeli
    @Override
    protected JComponent buildCenter() {
        List<Object[]> rows = new ArrayList<>();
        for (SalesOrderDetail detail : salesOrder.getDetails()) {
            rows.add(new Object[]{
                    detail.getIdProduk(),
                    detail.getNamaProduk(),
                    detail.getJumlahTerjual(),
                    CardFormat.rupiah(detail.getHargaJualAktual()),
                    CardFormat.rupiah(detail.getSubtotal())
            });
        }

        return readOnlyTable(COLUMNS, rows, 23, table -> {
            columnWidth(table, 0, 70, 85);
            columnWidth(table, 1, 210, 0);
            columnWidth(table, 2, 55, 70);
            columnWidth(table, 3, 115, 0);
            columnWidth(table, 4, 125, 0);

            alignColumn(table, 0, SwingConstants.CENTER);
            alignColumn(table, 2, SwingConstants.CENTER);
            alignColumn(table, 3, SwingConstants.RIGHT);
            alignColumn(table, 4, SwingConstants.RIGHT);
        });
    }

    private static BadgeStyle statusStyle(String status) {
        String normalized = status.trim().toLowerCase();
        boolean lunas = normalized.contains("lunas") || normalized.contains("paid")
                || normalized.contains("terbayar") || normalized.contains("selesai");
        return lunas ? BadgeStyle.SUCCESS : BadgeStyle.WARNING;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }
}