package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.ENUM.StatusPembayaran;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;

public class RiwayatTransaksiCard extends JPanel {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    private final SalesOrder order;
    private final StatusPembayaran status;
    private final Consumer<SalesOrder> onPayClick;
    private final Consumer<SalesOrder> onCancelClick;

    public RiwayatTransaksiCard(SalesOrder order,
                                Consumer<SalesOrder> onPayClick,
                                Consumer<SalesOrder> onCancelClick) {
        this.order = order;
        this.status = StatusPembayaran.fromString(order.getStatusPembayaran());
        this.onPayClick = onPayClick;
        this.onCancelClick = onCancelClick;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        int itemCount = Math.max(1, order.getDetails().size());
        int dynamicHeight = Math.max(114, 70 + (itemCount * 20));
        setPreferredSize(new Dimension(0, dynamicHeight));
        setMinimumSize(new Dimension(580, dynamicHeight));

        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(225, 90));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblId = new JLabel("Order #" + order.getIdOrder() + "   ");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblId.setForeground(new Color(30, 41, 59));

        JLabel lblBadge = createStatusBadge(status);

        topRow.add(lblId);
        topRow.add(lblBadge);

        String tglStr = (order.getTanggalPesan() != null)
                ? DATE_FORMAT.format(order.getTanggalPesan())
                : "-";
        JLabel lblTanggal = new JLabel("Tanggal Pesan: " + tglStr);
        lblTanggal.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTanggal.setForeground(new Color(71, 85, 105));
        lblTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);

        String metode = (order.getCaraBayar() != null && !order.getCaraBayar().isBlank())
                ? order.getCaraBayar()
                : "QRIS";
        JLabel lblMetode = new JLabel("Metode Bayar: " + metode);
        lblMetode.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblMetode.setForeground(new Color(51, 65, 85));
        lblMetode.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblTanggal);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblMetode);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 8, 0, 8));

        JLabel lblTitle = new JLabel("Rincian Produk:");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(new Color(51, 65, 85));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(4));

        if (order.getDetails().isEmpty()) {
            JLabel lblEmpty = new JLabel("- Tidak ada rincian item");
            lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblEmpty);
        } else {
            for (SalesOrderDetail detail : order.getDetails()) {
                String line = String.format("• %s  (%d x Rp %s = Rp %s)",
                        detail.getNamaProduk(),
                        detail.getJumlahTerjual(),
                        CURRENCY_FORMAT.format(detail.getHargaJualAktual()),
                        CURRENCY_FORMAT.format(detail.getSubtotal()));

                JLabel lblItem = new JLabel(line);
                lblItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lblItem.setForeground(new Color(30, 41, 59));
                lblItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(lblItem);
                panel.add(Box.createVerticalStrut(2));
            }
        }

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(225, 90));

        JLabel lblTotalCaption = new JLabel("Total Pembayaran:", SwingConstants.RIGHT);
        lblTotalCaption.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTotalCaption.setForeground(new Color(100, 116, 139));
        lblTotalCaption.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblTotal = new JLabel("Rp " + CURRENCY_FORMAT.format(order.getTotalTransaksi()), SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTotal.setForeground(new Color(15, 23, 42));
        lblTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(lblTotalCaption);
        panel.add(lblTotal);
        panel.add(Box.createVerticalGlue());

        if (status == StatusPembayaran.PENDING) {
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            btnRow.setOpaque(false);
            btnRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JButton btnBatal = new JButton("Batalkan");
            btnBatal.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnBatal.setBackground(new Color(254, 226, 226));
            btnBatal.setForeground(new Color(185, 28, 28));
            btnBatal.setFocusPainted(false);
            btnBatal.addActionListener(e -> {
                if (onCancelClick != null) {
                    onCancelClick.accept(order);
                }
            });

            JButton btnBayar = new JButton("Bayar");
            btnBayar.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnBayar.setBackground(new Color(22, 163, 74));
            btnBayar.setForeground(Color.WHITE);
            btnBayar.setFocusPainted(false);
            btnBayar.addActionListener(e -> {
                if (onPayClick != null) {
                    onPayClick.accept(order);
                }
            });

            btnRow.add(btnBatal);
            btnRow.add(btnBayar);
            panel.add(btnRow);
        } else if (status == StatusPembayaran.PAID) {
            JLabel lblPaid = new JLabel("Pembayaran Lunas (QRIS)", SwingConstants.RIGHT);
            lblPaid.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblPaid.setForeground(new Color(22, 101, 52));
            lblPaid.setAlignmentX(Component.RIGHT_ALIGNMENT);
            panel.add(lblPaid);
        } else {
            JLabel lblCancelled = new JLabel("Pesanan Dibatalkan", SwingConstants.RIGHT);
            lblCancelled.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblCancelled.setForeground(new Color(185, 28, 28));
            lblCancelled.setAlignmentX(Component.RIGHT_ALIGNMENT);
            panel.add(lblCancelled);
        }

        return panel;
    }

    private JLabel createStatusBadge(StatusPembayaran status) {
        JLabel badge = new JLabel(" " + status.name() + " ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setOpaque(true);

        switch (status) {
            case PAID -> {
                badge.setBackground(new Color(220, 252, 231));
                badge.setForeground(new Color(22, 101, 52));
                badge.setBorder(new LineBorder(new Color(134, 239, 172), 1, true));
            }
            case PENDING -> {
                badge.setBackground(new Color(254, 243, 199));
                badge.setForeground(new Color(146, 64, 14));
                badge.setBorder(new LineBorder(new Color(252, 211, 77), 1, true));
            }
            case CANCELLED -> {
                badge.setBackground(new Color(254, 226, 226));
                badge.setForeground(new Color(185, 28, 28));
                badge.setBorder(new LineBorder(new Color(252, 165, 165), 1, true));
            }
        }
        return badge;
    }
}