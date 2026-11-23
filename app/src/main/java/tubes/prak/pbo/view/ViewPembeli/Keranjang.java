package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.components.base.CardListPanel;
import tubes.prak.pbo.components.cards.KeranjangItemCard;
import tubes.prak.pbo.controller.SalesOrderController;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.users.UserBuyer;

public class Keranjang extends JPanel {

    private final SalesOrderController salesOrderController = new SalesOrderController();
    private final UserBuyer currentUser;

    private final CardListPanel<BarangKeranjang> itemList =
            new CardListPanel<>(item -> new KeranjangItemCard(item, this::handleHapusItem));

    // User yang sedang ditampilkan (bisa berbeda dari currentUser jika refresh dipanggil dari luar)
    private UserBuyer displayedUser;

    private JLabel lblJumlahItem;
    private JLabel labeltotalbayar;
    private JButton btnCheckout;

    public Keranjang(UserBuyer user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        itemList.setGap(8);
        itemList.setEmptyMessage("Keranjang Anda masih kosong. Silakan pilih produk di menu Etalase.");

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(itemList, BorderLayout.CENTER);
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

    private void handleHapusItem(BarangKeranjang item) {
        if (displayedUser == null) {
            return;
        }
        displayedUser.hapusDariKeranjang(item);
        refreshKeranjang(displayedUser);
    }

    public void refreshKeranjang(UserBuyer user) {
        this.displayedUser = user;

        Collection<BarangKeranjang> items = (user != null) ? user.getBarangKeranjang() : List.of();

        double totalBayar = 0.0;
        int totalQty = 0;
        for (BarangKeranjang item : items) {
            totalBayar += item.getHargaTotal();
            totalQty += item.getJumlah();
        }

        itemList.setItems(items);
        btnCheckout.setEnabled(!items.isEmpty());
        lblJumlahItem.setText(totalQty + " Item");
        labeltotalbayar.setText("Total Bayar: " + CardFormat.rupiah(totalBayar));
    }
}