package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tubes.prak.pbo.ENUM.StatusPembayaran;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.components.base.CardListPanel;
import tubes.prak.pbo.components.cards.RiwayatTransaksiCard;
import tubes.prak.pbo.controller.SalesOrderController;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.users.User;

public class RiwayatTransaksiView extends JPanel {

    private static final String FILTER_SEMUA = "Semua Status";

    private final SalesOrderController salesOrderController = new SalesOrderController();
    private final User currentUser;

    private final CardListPanel<SalesOrder> orderList = new CardListPanel<>(
            order -> new RiwayatTransaksiCard(order, this::handleBayarOrder, this::handleBatalkanOrder));

    private JComboBox<String> cbFilterStatus;

    public RiwayatTransaksiView(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        orderList.setEmptyMessage("Belum ada riwayat transaksi yang sesuai filter.");

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(orderList, BorderLayout.CENTER);

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

        cbFilterStatus = new JComboBox<>(new String[]{FILTER_SEMUA, "PENDING", "PAID", "CANCELLED"});
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

    // Nama method dipertahankan agar pemanggil di luar view tidak perlu diubah
    public void refreshKeranjang(User user) {
        if (user == null) {
            orderList.clear();
            return;
        }

        StatusPembayaran wanted = selectedStatusFilter();

        try {
            List<SalesOrder> orders = salesOrderController.getSalesOrdersByUser(user.getId()).stream()
                    .filter(order -> wanted == null
                            || StatusPembayaran.fromString(order.getStatusPembayaran()) == wanted)
                    .toList();

            orderList.setItems(orders);
        } catch (SQLException e) {
            orderList.clear();
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat riwayat transaksi: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** null berarti "Semua Status" (atau combo belum dibuat). */
    private StatusPembayaran selectedStatusFilter() {
        String filter = (cbFilterStatus != null) ? (String) cbFilterStatus.getSelectedItem() : null;
        if (filter == null || FILTER_SEMUA.equals(filter)) {
            return null;
        }
        return StatusPembayaran.valueOf(filter);
    }

    private void handleBayarOrder(SalesOrder order) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Konfirmasi pembayaran QRIS untuk Order #" + order.getIdOrder() + "?\n\n" +
                        "Total Pembayaran: " + CardFormat.rupiah(order.getTotalTransaksi()) + "\n" +
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
}