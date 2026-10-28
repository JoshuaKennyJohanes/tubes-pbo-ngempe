package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.util.Router;

public class DetailProduk extends JPanel {

    // Konstruktor menerima produk, mainContentPanel, dan cardLayout
    public DetailProduk(Produk produk, JPanel mainContentPanel, CardLayout cardLayout) {

        // 1. Atur layout untuk Panel utama kelas ini (DetailProduk extends JPanel)
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // 2. Buat panel lokal khusus untuk halaman detail ini (JANGAN pakai parameter
        // mainPanel luar)
        JPanel panelUtamaDetail = new JPanel(new BorderLayout(20, 20));
        panelUtamaDetail.setBackground(new Color(245, 247, 250));
        panelUtamaDetail.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Judul Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(245, 247, 250));

        JLabel judulProduk = new JLabel("Detail Produk");
        judulProduk.setFont(new Font("SansSerif", Font.BOLD, 22));
        judulProduk.setForeground(new Color(30, 30, 30));

        JLabel lblSubtitle = new JLabel("Tentukan jumlah, lalu tambahkan produk ke keranjang.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(110, 110, 110));

        headerPanel.add(judulProduk);
        headerPanel.add(Box.createVerticalStrut(3));
        headerPanel.add(lblSubtitle);
        panelUtamaDetail.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBackground(new Color(245, 247, 250));

        JPanel imageContainer = new JPanel(new GridBagLayout());
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        ImageIcon rawIcon = new ImageIcon(produk.getImageUrl());
        Image scaledImage = rawIcon.getImage().getScaledInstance(260, 260, Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel(new ImageIcon(scaledImage));
        imageContainer.add(lblImage);
        contentPanel.add(imageContainer);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(245, 247, 250));

        JLabel lblNamaProduk = new JLabel(produk.getNamaProduk());
        lblNamaProduk.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNamaProduk.setForeground(new Color(30, 30, 30));

        JLabel lblDesc = new JLabel(
                "Keripik tempe renyah dengan rasa bawang yang gurih. Cocok untuk camilan sehari-hari.");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(90, 90, 90));
        lblDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));

        JPanel hargaPanel = createRowPanel("Harga:",
                "Rp " + String.format("%,.0f", produk.getHargaJualProduk()) + " / pouch");

        JPanel jumlahPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        jumlahPanel.setBackground(new Color(245, 247, 250));
        JLabel lblJumlahLabel = new JLabel("Jumlah:");
        lblJumlahLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblJumlahLabel.setPreferredSize(new Dimension(80, 25));
        lblJumlahLabel.setForeground(new Color(100, 100, 100));

        JLabel lblJumlahValue = new JLabel("2");
        lblJumlahValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblJumlahValue.setForeground(new Color(40, 40, 40));
        lblJumlahValue.setPreferredSize(new Dimension(50, 25));

        JLabel lblSatuan = new JLabel("pouch");
        lblSatuan.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSatuan.setForeground(new Color(100, 100, 100));

        jumlahPanel.add(lblJumlahLabel);
        jumlahPanel.add(lblJumlahValue);
        jumlahPanel.add(lblSatuan);

        double totalSubtotal = produk.getHargaJualProduk() * 2;
        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subtotalPanel.setBackground(new Color(245, 247, 250));
        JLabel lblSubtotalLabel = new JLabel("Subtotal:");
        lblSubtotalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtotalLabel.setPreferredSize(new Dimension(80, 25));
        lblSubtotalLabel.setForeground(new Color(100, 100, 100));

        JLabel lblSubtotalValue = new JLabel("Rp " + String.format("%,.0f", totalSubtotal));
        lblSubtotalValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSubtotalValue.setForeground(new Color(20, 20, 20));

        subtotalPanel.add(lblSubtotalLabel);
        subtotalPanel.add(lblSubtotalValue);

        JPanel buttonActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonActionPanel.setBackground(new Color(245, 247, 250));

        JButton btnTambah = new JButton("Tambah ke Keranjang");
        btnTambah.setBackground(new Color(230, 240, 255));
        btnTambah.setForeground(new Color(0, 102, 204));
        btnTambah.setFocusPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnLihatKeranjang = new JButton("Lihat Keranjang");
        btnLihatKeranjang.setBackground(Color.WHITE);
        btnLihatKeranjang.setForeground(new Color(80, 80, 80));
        btnLihatKeranjang.setFocusPainted(false);
        btnLihatKeranjang.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonActionPanel.add(btnTambah);
        buttonActionPanel.add(btnLihatKeranjang);

        rightPanel.add(lblNamaProduk);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(lblDesc);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(separator);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(hargaPanel);
        rightPanel.add(Box.createVerticalStrut(12));
        rightPanel.add(jumlahPanel);
        rightPanel.add(Box.createVerticalStrut(12));
        rightPanel.add(subtotalPanel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(buttonActionPanel);

        contentPanel.add(rightPanel);
        panelUtamaDetail.add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 247, 250));

        JLabel lblFooterNote = new JLabel("Gambar ilustrasi • Harga simulasi; harga aktual ditetapkan perusahaan.");
        lblFooterNote.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFooterNote.setForeground(new Color(130, 130, 130));

        JButton btnKembali = new JButton("Kembali ke Etalase");
        btnKembali.setBackground(Color.WHITE);
        btnKembali.setForeground(new Color(60, 60, 60));
        btnKembali.setFocusPainted(false);
        btnKembali.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> {
            DashboardPembeli pembeliUI = new DashboardPembeli(mainContentPanel, cardLayout);
                mainContentPanel.add(pembeliUI, Router.ETALASE_PAGE);
                cardLayout.show(mainContentPanel, Router.ETALASE_PAGE);
            // try {
                
            // } catch (SQLException e) {
            //     e.printStackTrace();
            // }
        });

        JPanel backButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        backButtonWrapper.setBackground(new Color(245, 247, 250));
        backButtonWrapper.add(btnKembali);

        footerPanel.add(lblFooterNote, BorderLayout.NORTH);
        footerPanel.add(backButtonWrapper, BorderLayout.CENTER);

        panelUtamaDetail.add(footerPanel, BorderLayout.SOUTH);

        // 3. Masukkan panelUtamaDetail ke dalam `this`
        add(panelUtamaDetail, BorderLayout.CENTER);
    }

    private JPanel createRowPanel(String labelText, String valueText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(new Color(245, 247, 250));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setPreferredSize(new Dimension(80, 25));
        lbl.setForeground(new Color(100, 100, 100));

        JLabel val = new JLabel(valueText);
        val.setFont(new Font("SansSerif", Font.BOLD, 14));
        val.setForeground(new Color(40, 40, 40));

        panel.add(lbl);
        panel.add(val);
        return panel;
    }
}