package tubes.prak.pbo.components.cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.models.productRelated.Produk;

public class ProductCard extends JPanel {

    public static final int CARD_HEIGHT = 340;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final Produk produk;
    private JButton detailButton;

    public ProductCard(Produk produk) {
        this.produk = produk;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        setPreferredSize(new Dimension(240, CARD_HEIGHT));
        setMinimumSize(new Dimension(210, CARD_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, CARD_HEIGHT));

        // 1. ATAS: Kotak Gambar Produk
        JPanel imageBox = new JPanel(new GridBagLayout());
        imageBox.setBackground(new Color(248, 250, 252));
        imageBox.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        imageBox.setPreferredSize(new Dimension(210, 155));

        ImageIcon productIcon = loadScaledImage(produk.getImageUrl(), 200, 145);
        if (productIcon != null) {
            imageBox.add(new JLabel(productIcon));
        } else {
            JLabel lblNoImg = new JLabel("Tidak Ada Gambar", SwingConstants.CENTER);
            lblNoImg.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblNoImg.setForeground(new Color(148, 163, 184));
            imageBox.add(lblNoImg);
        }

        // 2. TENGAH: Nama Produk, Harga, dan Info Stok
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("<html><body style='width: 185px;'>" + produk.getNamaProduk() + "</body></html>");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPrice = new JLabel("Rp " + CURRENCY_FORMAT.format(produk.getHargaJual()));
        lblPrice.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblPrice.setForeground(new Color(15, 23, 42));
        lblPrice.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean tersedia = produk.getStok() > 0;
        String stokText = tersedia
                ? "Stok: " + produk.getStok() + " " + produk.getSatuanProduk()
                : "Stok Habis";
        JLabel lblStok = new JLabel(stokText);
        lblStok.setFont(new Font("SansSerif", tersedia ? Font.PLAIN : Font.BOLD, 11));
        lblStok.setForeground(tersedia ? new Color(100, 116, 139) : new Color(185, 28, 28));
        lblStok.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(lblTitle);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(lblPrice);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(lblStok);

        // 3. BAWAH: Tombol JButton Biru Standar
        detailButton = new JButton(tersedia ? "Lihat Detail" : "Stok Habis");
        detailButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        detailButton.setBackground(tersedia ? new Color(37, 99, 235) : new Color(148, 163, 184));
        detailButton.setForeground(Color.WHITE);
        detailButton.setFocusPainted(false);
        detailButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailButton.setPreferredSize(new Dimension(0, 34));

        add(imageBox, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(detailButton, BorderLayout.SOUTH);
    }

    private ImageIcon loadScaledImage(String imageUrl, int maxW, int maxH) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        File file = new File(imageUrl);
        if (!file.exists()) {
            return null;
        }
        ImageIcon rawIcon = new ImageIcon(imageUrl);
        if (rawIcon.getIconWidth() <= 0 || rawIcon.getIconHeight() <= 0) {
            return null;
        }
        Image scaled = rawIcon.getImage().getScaledInstance(maxW, maxH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public JButton getDetailButton() {
        return detailButton;
    }
}