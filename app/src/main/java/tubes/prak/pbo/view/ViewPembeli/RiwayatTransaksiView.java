package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.border.EmptyBorder;

import tubes.prak.pbo.ENUM.StatusPembayaran;
import tubes.prak.pbo.components.RiwayatTransaksiCard;
import tubes.prak.pbo.controller.SalesOrderController;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.User;

public class RiwayatTransaksiView extends JPanel {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final SalesOrderController salesOrderController = new SalesOrderController();
    private final User currentUser;

    private JComboBox<String> cbFilterStatus;
    private ScrollableCardListPanel cardListContainer;

    public RiwayatTransaksiView(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        refreshKeranjang(user);
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Riwayat Transaksi Saya");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);

        cbFilterStatus = new JComboBox<>(new String[]{"Semua Status", "PENDING", "PAID", "CANCELLED"});
        cbFilterStatus.setPreferredSize(new Dimension(135, 28));
        cbFilterStatus.addActionListener(e -> refreshKeranjang(currentUser));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshKeranjang(currentUser));

        rightControls.add(new JLabel("Status:"));
        rightControls.add(cbFilterStatus);
        rightControls.add(btnRefresh);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(rightControls, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane buildContentPanel() {
        cardListContainer = new ScrollableCardListPanel();
        cardListContainer.setLayout(new BoxLayout(cardListContainer, BoxLayout.Y_AXIS));
        cardListContainer.setBorder(new EmptyBorder(4, 2, 4, 2));

        JScrollPane scrollPane = new JScrollPane(cardListContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    public void refreshKeranjang(User user) {
        cardListContainer.removeAll();
        if (user == null) {
            return;
        }

        String filter = (cbFilterStatus != null) ? (String) cbFilterStatus.getSelectedItem() : "Semua Status";

        try {
            List<SalesOrder> orders = salesOrderController.getSalesOrdersByUser(user.getId());
            int renderedCount = 0;

            for (SalesOrder order : orders) {
                StatusPembayaran status = StatusPembayaran.fromString(order.getStatusPembayaran());

                if ("PENDING".equals(filter) && status != StatusPembayaran.PENDING) continue;
                if ("PAID".equals(filter) && status != StatusPembayaran.PAID) continue;
                if ("CANCELLED".equals(filter) && status != StatusPembayaran.CANCELLED) continue;

                cardListContainer.add(new RiwayatTransaksiCard(
                        order,
                        this::handleBayarOrder,
                        this::handleBatalkanOrder
                ));
                cardListContainer.add(Box.createVerticalStrut(10));
                renderedCount++;
            }

            if (renderedCount == 0) {
                JLabel lblEmpty = new JLabel("Belum ada riwayat transaksi yang sesuai filter.");
                lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 13));
                lblEmpty.setForeground(Color.GRAY);
                lblEmpty.setBorder(new EmptyBorder(24, 10, 24, 10));
                cardListContainer.add(lblEmpty);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat riwayat transaksi: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        cardListContainer.revalidate();
        cardListContainer.repaint();
    }

    private void handleBayarOrder(SalesOrder order) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Konfirmasi pembayaran QRIS untuk Order #" + order.getIdOrder() + "?\n\n" +
                        "Total Pembayaran: Rp " + CURRENCY_FORMAT.format(order.getTotalTransaksi()) + "\n" +
                        "Status pesanan akan berubah menjadi PAID.",
                "Konfirmasi Pembayaran QRIS",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                salesOrderController.bayarOrder(order.getIdOrder());
                JOptionPane.showMessageDialog(
                        this,
                        "Pembayaran Order #" + order.getIdOrder() + " berhasil! Status sekarang: PAID.",
                        "Pembayaran Berhasil",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refreshKeranjang(currentUser);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Gagal Memproses Pembayaran",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void handleBatalkanOrder(SalesOrder order) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin membatalkan Order #" + order.getIdOrder() + "?\n" +
                        "Stok produk pada pesanan ini akan dikembalikan ke etalase.",
                "Konfirmasi Pembatalan Pesanan",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                salesOrderController.batalkanOrder(order.getIdOrder());
                JOptionPane.showMessageDialog(
                        this,
                        "Order #" + order.getIdOrder() + " berhasil dibatalkan dan stok produk telah dikembalikan.",
                        "Pesanan Dibatalkan",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refreshKeranjang(currentUser);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Gagal Membatalkan Pesanan",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private static class ScrollableCardListPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 60;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
