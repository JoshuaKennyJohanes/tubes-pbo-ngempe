package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.components.panels.QuantityInput;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.users.UserBuyer;
import tubes.prak.pbo.util.Router;

public class DetailProduk extends JPanel {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    public DetailProduk(Produk produk, JPanel mainContentPanel, CardLayout cardLayout,
                        UserBuyer user, Keranjang panelKeranjang) {
        this(produk, mainContentPanel, cardLayout, user, panelKeranjang, null);
    }

    public DetailProduk(Produk produk, JPanel mainContentPanel, CardLayout cardLayout,
                        UserBuyer user, Keranjang panelKeranjang, Runnable onBackToEtalase) {

        setLayout(new BorderLayout(10, 12));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel judulProduk = new JLabel("Detail Produk");
        judulProduk.setFont(new Font("SansSerif", Font.BOLD, 16));
        judulProduk.setForeground(new Color(15, 23, 42));

        JButton btnKembali = new JButton("← Kembali ke Etalase");
        btnKembali.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnKembali.setBackground(Color.WHITE);
        btnKembali.setForeground(new Color(51, 65, 85));
        btnKembali.setFocusPainted(false);
        btnKembali.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> {
            if (onBackToEtalase != null) {
                onBackToEtalase.run();
            }
            cardLayout.show(mainContentPanel, Router.ETALASE_PAGE);
        });

        headerPanel.add(judulProduk, BorderLayout.WEST);
        headerPanel.add(btnKembali, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. KONTEN UTAMA (2 Kolom: Kartu Gambar & Kartu Detail)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Kiri: Kartu Gambar Produk
        JPanel imageContainer = new JPanel(new GridBagLayout());
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        ImageIcon productIcon = loadScaledImage(produk.getImageUrl(), 280, 280);
        if (productIcon != null) {
            imageContainer.add(new JLabel(productIcon));
        } else {
            JLabel lblNoImg = new JLabel("Gambar Produk Tidak Tersedia", SwingConstants.CENTER);
            lblNoImg.setFont(new Font("SansSerif", Font.ITALIC, 13));
            lblNoImg.setForeground(new Color(148, 163, 184));
            imageContainer.add(lblNoImg);
        }
        contentPanel.add(imageContainer);

        // Kanan: Kartu Informasi & Aksi Keranjang
        JPanel rightCard = new JPanel();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.setBackground(Color.WHITE);
        rightCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblNamaProduk = new JLabel(produk.getNamaProduk());
        lblNamaProduk.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNamaProduk.setForeground(new Color(15, 23, 42));
        lblNamaProduk.setAlignmentX(Component.LEFT_ALIGNMENT);

        String deskripsi = (produk.getDeskripsiProduk() != null && !produk.getDeskripsiProduk().isBlank())
                ? produk.getDeskripsiProduk()
                : "Tidak ada deskripsi tambahan untuk produk ini.";
        JLabel lblDesc = new JLabel("<html><body style='width: 340px;'>" + deskripsi + "</body></html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(71, 85, 105));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel hargaPanel = createRowPanel("Harga:",
                "Rp " + CURRENCY_FORMAT.format(produk.getHargaJual()) + " / " + produk.getSatuanProduk());

        boolean tersedia = produk.getStok() > 0;
        JPanel stokPanel = createRowPanel("Stok:",
                tersedia ? (produk.getStok() + " " + produk.getSatuanProduk()) : "Stok Habis");

        // Baris Input Jumlah
        JPanel jumlahPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        jumlahPanel.setOpaque(false);
        jumlahPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jumlahPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel lblJumlahLabel = new JLabel("Jumlah:");
        lblJumlahLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblJumlahLabel.setPreferredSize(new Dimension(85, 28));
        lblJumlahLabel.setForeground(new Color(100, 116, 139));

        QuantityInput qtyInput = new QuantityInput(1);

        JLabel lblSatuan = new JLabel("  " + produk.getSatuanProduk());
        lblSatuan.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSatuan.setForeground(new Color(51, 65, 85));

        jumlahPanel.add(lblJumlahLabel);
        jumlahPanel.add(qtyInput);
        jumlahPanel.add(lblSatuan);

        // Baris Subtotal Dinamis
        double totalSubtotal = produk.getHargaJual() * qtyInput.getValue();
        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subtotalPanel.setOpaque(false);
        subtotalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtotalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lblSubtotalLabel = new JLabel("Subtotal:");
        lblSubtotalLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtotalLabel.setPreferredSize(new Dimension(85, 28));
        lblSubtotalLabel.setForeground(new Color(100, 116, 139));

        JLabel lblSubtotalValue = new JLabel("Rp " + CURRENCY_FORMAT.format(totalSubtotal));
        lblSubtotalValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSubtotalValue.setForeground(new Color(22, 101, 52));

        qtyInput.addChangeListener(qty -> {
            double total = produk.getHargaJual() * qtyInput.getValue();
            lblSubtotalValue.setText("Rp " + CURRENCY_FORMAT.format(total));
        });

        subtotalPanel.add(lblSubtotalLabel);
        subtotalPanel.add(lblSubtotalValue);

        // Baris Tombol Aksi
        JPanel buttonActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonActionPanel.setOpaque(false);
        buttonActionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton btnTambah = new JButton(tersedia ? "+ Tambah ke Keranjang" : "Stok Habis");
        btnTambah.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnTambah.setBackground(tersedia ? new Color(37, 99, 235) : new Color(148, 163, 184));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.setEnabled(tersedia);
        btnTambah.setPreferredSize(new Dimension(185, 36));
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnTambah.addActionListener(e -> {
            int jumlah = qtyInput.getValue();
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah pembelian harus lebih dari 0!",
                        "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int sudahDiKeranjang = user.getJumlahDiKeranjang(produk.getIdProduk());
            if (sudahDiKeranjang + jumlah > produk.getStok()) {
                JOptionPane.showMessageDialog(this,
                        "Stok tidak mencukupi!\n" +
                                "Stok tersedia: " + produk.getStok() + " " + produk.getSatuanProduk() + "\n" +
                                "Sudah ada di keranjang: " + sudahDiKeranjang + " " + produk.getSatuanProduk(),
                        "Stok Terbatas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = jumlah * produk.getHargaJual();
            BarangKeranjang item = new BarangKeranjang(
                    produk.getIdProduk(),
                    produk.getNamaProduk(),
                    jumlah,
                    total
            );
            user.tambahkeKeranjang(item);

            int totalSekarang = user.getJumlahDiKeranjang(produk.getIdProduk());
            JOptionPane.showMessageDialog(this,
                    "Produk \"" + produk.getNamaProduk() + "\" berhasil ditambahkan ke keranjang!\n" +
                            "Total di keranjang saat ini: " + totalSekarang + " " + produk.getSatuanProduk(),
                    "Berhasil Ditambahkan", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnLihatKeranjang = new JButton("Lihat Keranjang");
        btnLihatKeranjang.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLihatKeranjang.setBackground(new Color(22, 163, 74));
        btnLihatKeranjang.setForeground(Color.WHITE);
        btnLihatKeranjang.setFocusPainted(false);
        btnLihatKeranjang.setPreferredSize(new Dimension(150, 36));
        btnLihatKeranjang.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLihatKeranjang.addActionListener(e -> {
            panelKeranjang.refreshKeranjang(user);
            cardLayout.show(mainContentPanel, Router.KERANJANG_PAGE);
        });

        buttonActionPanel.add(btnTambah);
        buttonActionPanel.add(btnLihatKeranjang);

        rightCard.add(lblNamaProduk);
        rightCard.add(Box.createVerticalStrut(10));
        rightCard.add(lblDesc);
        rightCard.add(Box.createVerticalStrut(14));
        rightCard.add(separator);
        rightCard.add(Box.createVerticalStrut(14));
        rightCard.add(hargaPanel);
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(stokPanel);
        rightCard.add(Box.createVerticalStrut(12));
        rightCard.add(jumlahPanel);
        rightCard.add(Box.createVerticalStrut(12));
        rightCard.add(subtotalPanel);
        rightCard.add(Box.createVerticalGlue());
        rightCard.add(buttonActionPanel);

        contentPanel.add(rightCard);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createRowPanel(String labelText, String valueText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(85, 25));
        lbl.setForeground(new Color(100, 116, 139));

        JLabel val = new JLabel(valueText);
        val.setFont(new Font("SansSerif", Font.BOLD, 14));
        val.setForeground(new Color(30, 41, 59));

        panel.add(lbl);
        panel.add(val);
        return panel;
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
}