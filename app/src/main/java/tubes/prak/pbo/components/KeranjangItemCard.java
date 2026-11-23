package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
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

import tubes.prak.pbo.models.BarangKeranjang;

public class KeranjangItemCard extends JPanel {

    public static final int CARD_HEIGHT = 86;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final BarangKeranjang item;
    private final Consumer<BarangKeranjang> onRemoveClick;

    public KeranjangItemCard(BarangKeranjang item, Consumer<BarangKeranjang> onRemoveClick) {
        this.item = item;
        this.onRemoveClick = onRemoveClick;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        setPreferredSize(new Dimension(0, CARD_HEIGHT));
        setMinimumSize(new Dimension(500, CARD_HEIGHT));

        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, CARD_HEIGHT);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, CARD_HEIGHT - 24));

        JLabel lblNama = new JLabel(item.getNamaBarang());
        lblNama.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNama.setForeground(new Color(15, 23, 42));
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        double hargaSatuan = (item.getJumlah() > 0) ? (item.getHargaTotal() / item.getJumlah()) : 0.0;
        JLabel lblHargaSatuan = new JLabel("Harga Satuan: Rp " + CURRENCY_FORMAT.format(hargaSatuan));
        lblHargaSatuan.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHargaSatuan.setForeground(new Color(100, 116, 139));
        lblHargaSatuan.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblNama);
        panel.add(Box.createVerticalGlue());
        panel.add(lblHargaSatuan);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 14));
        panel.setOpaque(false);

        JLabel lblQtyBadge = new JLabel("  Jumlah: " + item.getJumlah() + " pcs  ");
        lblQtyBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblQtyBadge.setForeground(new Color(29, 78, 216));
        lblQtyBadge.setOpaque(true);
        lblQtyBadge.setBackground(new Color(219, 234, 254));
        lblQtyBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(147, 197, 253), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));

        panel.add(lblQtyBadge);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(190, CARD_HEIGHT - 24));

        JLabel lblSubtotal = new JLabel("Rp " + CURRENCY_FORMAT.format(item.getHargaTotal()), SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblSubtotal.setForeground(new Color(22, 101, 52));
        lblSubtotal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnHapus.setBackground(new Color(254, 226, 226));
        btnHapus.setForeground(new Color(185, 28, 28));
        btnHapus.setBorder(new CompoundBorder(
                new LineBorder(new Color(252, 165, 165), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));
        btnHapus.setFocusPainted(false);
        btnHapus.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnHapus.addActionListener(e -> {
            if (onRemoveClick != null) {
                onRemoveClick.accept(item);
            }
        });

        panel.add(lblSubtotal);
        panel.add(Box.createVerticalGlue());
        panel.add(btnHapus);

        return panel;
    }
}