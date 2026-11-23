package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.components.KeranjangItemCard;
import tubes.prak.pbo.controller.SalesOrderController;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.User;

public class Keranjang extends JPanel {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final SalesOrderController salesOrderController = new SalesOrderController();
    private final User currentUser;

    private JLabel lblJumlahItem;
    private JLabel labeltotalbayar;
    private JButton btnCheckout;
    private ScrollableCardListPanel cardListContainer;

    public Keranjang(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);

        refreshKeranjang(user);
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Keranjang Belanja");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblJumlahItem = new JLabel("0 Item");
        lblJumlahItem.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblJumlahItem.setForeground(new Color(51, 65, 85));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblJumlahItem, BorderLayout.EAST);

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

    private JPanel buildFooterPanel() {
        JPanel footerCard = new JPanel(new BorderLayout(16, 0));
        footerCard.setBackground(Color.WHITE);
        footerCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);

        JLabel lblMetode = new JLabel("Metode Pembayaran Default: QRIS (Status awal: PENDING)");
        lblMetode.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblMetode.setForeground(new Color(100, 116, 139));

        labeltotalbayar = new JLabel("Total Bayar: Rp 0");
        labeltotalbayar.setFont(new Font("SansSerif", Font.BOLD, 16));
        labeltotalbayar.setForeground(new Color(15, 23, 42));

        leftInfo.add(lblMetode);
        leftInfo.add(Box.createVerticalStrut(4));
        leftInfo.add(labeltotalbayar);

        JPanel rightAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        rightAction.setOpaque(false);

        btnCheckout = new JButton("Checkout Sekarang");
        btnCheckout.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCheckout.setBackground(new Color(37, 99, 235));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setPreferredSize(new Dimension(180, 36));
        btnCheckout.addActionListener(e -> handleCheckout());

        rightAction.add(btnCheckout);

        footerCard.add(leftInfo, BorderLayout.WEST);
        footerCard.add(rightAction, BorderLayout.EAST);

        return footerCard;
    }

    private void handleCheckout() {
        try {
            int newOrderId = salesOrderController.checkoutKeranjang(currentUser);
            refreshKeranjang(currentUser);

            JOptionPane.showMessageDialog(
                    this,
                    "Checkout berhasil! (Order #" + newOrderId + " - Status: PENDING)\n" +
                            "Silakan buka menu Riwayat Transaksi dan klik 'Bayar' untuk menyelesaikan pembayaran QRIS.",
                    "Checkout Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Checkout Gagal",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void refreshKeranjang(User user) {
        cardListContainer.removeAll();
        double totalBayar = 0.0;
        int totalQty = 0;

        if (user == null || user.getBarangKeranjang().isEmpty()) {
            JLabel lblEmpty = new JLabel("Keranjang Anda masih kosong. Silakan pilih produk di menu Etalase.");
            lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setBorder(new EmptyBorder(24, 10, 24, 10));
            cardListContainer.add(lblEmpty);
            btnCheckout.setEnabled(false);
        } else {
            btnCheckout.setEnabled(true);
            for (BarangKeranjang produk : user.getBarangKeranjang()) {
                cardListContainer.add(new KeranjangItemCard(produk, itemDihapus -> {
                    user.hapusDariKeranjang(itemDihapus);
                    refreshKeranjang(user);
                }));
                cardListContainer.add(Box.createVerticalStrut(8));

                totalBayar += produk.getHargaTotal();
                totalQty += produk.getJumlah();
            }
        }

        lblJumlahItem.setText(totalQty + " Item");
        labeltotalbayar.setText("Total Bayar: Rp " + CURRENCY_FORMAT.format(totalBayar));

        cardListContainer.revalidate();
        cardListContainer.repaint();
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