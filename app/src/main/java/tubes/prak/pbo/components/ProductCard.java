package tubes.prak.pbo.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProductCard extends JPanel {
    private int cornerRadius = 20;
    private JLabel labelImage;
    private JLabel labelTitle;
    private JLabel labelPrice;
    private DetailButton detailButton;

    public ProductCard(ImageIcon icon, String title, String price) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 

        labelImage = new JLabel(icon);
        labelImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title Produk
        labelTitle = new JLabel(title);
        labelTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitle.setForeground(new Color(30, 35, 45));
        labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Price Produk
        labelPrice = new JLabel(price);
        labelPrice.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelPrice.setForeground(new Color(30, 35, 45));
        labelPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Detail Produk Button
        detailButton = new DetailButton("Lihat detail");
        detailButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        detailButton.setPreferredSize(new Dimension(220, 45));

        // Add Layout
        add(labelImage);
        add(Box.createVerticalStrut(15));
        add(labelTitle);
        add(Box.createVerticalStrut(10));
        add(labelPrice);
        add(Box.createVerticalStrut(15));
        add(detailButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.setColor(new Color(215, 220, 230));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.dispose();
    }

     public DetailButton getDetailButton(){
        return detailButton;
    }
}
