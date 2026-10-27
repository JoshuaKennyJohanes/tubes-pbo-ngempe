package tubes.prak.pbo.view.ViewPembeli;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tubes.prak.pbo.components.ProductCard;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.repositories.ProdukRepository;

public class EtalaseView extends JPanel {

    // public EtalaseView(JPanel mainPanel, CardLayout cardLayout) {
    //     add(new JLabel("Ini panel etalase"));

    // }

    public static void main(String[] args) throws SQLException {
        JFrame frame = new JFrame("Single Product Card - 1000x700");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setLayout(new GridLayout(0, 3, 20, 20));

        List<Produk> allProduk = ProdukRepository.findAllProduk();

        System.out.println("\n Daftar Produk:");
        for (Produk pk : allProduk) {
            ImageIcon rawIcon = new ImageIcon(pk.getImageUrl());
            Image scaledImage = rawIcon.getImage().getScaledInstance(250, 160, Image.SCALE_SMOOTH);
            ImageIcon img = new ImageIcon(scaledImage);

            ProductCard card = new ProductCard(img, pk.getNamaProduk(), String.valueOf(pk.getHargaJualProduk()));
            card.setPreferredSize(new Dimension(310, 360));
            mainPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(scrollPane);
        frame.setVisible(true);

    }
}
