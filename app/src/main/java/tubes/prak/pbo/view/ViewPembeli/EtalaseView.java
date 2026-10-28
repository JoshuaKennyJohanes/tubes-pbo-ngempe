package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tubes.prak.pbo.components.ProductCard;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.repositories.ProdukRepository;

public class EtalaseView extends JPanel {

    public EtalaseView(JPanel mainContentPanel, CardLayout cardLayout) throws SQLException {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(new Color(245, 247, 250));

        List<Produk> allProduk = ProdukRepository.findAllProduk();

        System.out.println("\n Daftar Produk:");
        for (Produk pk : allProduk) {
            ImageIcon rawIcon = new ImageIcon(pk.getImageUrl());
            Image scaledImage = rawIcon.getImage().getScaledInstance(250, 160, Image.SCALE_SMOOTH);
            ImageIcon img = new ImageIcon(scaledImage);

            ProductCard card = new ProductCard(img, pk.getNamaProduk(), String.valueOf(pk.getHargaJualProduk()));
            card.setPreferredSize(new Dimension(155, 180));
            card.getDetailButton().addActionListener(e -> {
                // DetailProduk detailFrame = new DetailProduk(ProdukRepository.getProdukById(pk.getIdProduk()));
                // detailFrame.setVisible(true);
                DetailProduk produkDetail = new DetailProduk(ProdukRepository.getProdukById(pk.getIdProduk()), mainContentPanel, cardLayout);
                mainContentPanel.add(produkDetail, "DETAIL_PRODUK");
                cardLayout.show(mainContentPanel, "DETAIL_PRODUK");
            });
            gridPanel.add(card);
        }

        // Bungkus gridPanel dengan JScrollPane agar bisa di-scroll ke bawah jika produknya banyak
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Masukkan scrollPane ke dalam EtalaseView ini
        add(scrollPane, BorderLayout.CENTER);

    }
}
