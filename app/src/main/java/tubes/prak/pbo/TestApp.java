package tubes.prak.pbo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tubes.prak.pbo.components.ProductCard;

public class TestApp {


    public static void main(String[] args) {
    JFrame frame = new JFrame("Single Product Card - 1000x700");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setLocationRelativeTo(null);

    // Panel utama dengan latar belakang abu-abu terang dan GridBagLayout agar posisi card di tengah
    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(new Color(245, 247, 250));
    mainPanel.setLayout(new GridBagLayout());

    // Memuat dan melakukan scaling pada gambar "sawit_wowo.jpg" agar pas di card
    ImageIcon rawIcon = new ImageIcon("app\\sawit_wowo.jpg");
    Image scaledImage = rawIcon.getImage().getScaledInstance(250, 160, Image.SCALE_SMOOTH);
    ImageIcon img = new ImageIcon(scaledImage);

    // Membuat 1 buah card
    ProductCard card = new ProductCard(img, "Original Bawang Gurih", "Rp20.000");
    card.setPreferredSize(new Dimension(310, 360));
    mainPanel.add(card);
    frame.add(mainPanel);
    frame.setVisible(true);

    
    }
}
