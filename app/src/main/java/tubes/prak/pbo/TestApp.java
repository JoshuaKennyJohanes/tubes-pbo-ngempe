package tubes.prak.pbo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tubes.prak.pbo.components.DetailButton;

public class TestApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Asset Button Dinamis");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 250);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(245, 247, 250));
            panel.setLayout(new GridBagLayout());

            // 1. Tombol dengan teks "Lihat detail" (Sesuai gambar)
            DetailButton btn1 = new DetailButton("Lihat detail");
            btn1.setPreferredSize(new Dimension(160, 45));

            DetailButton btn2 = new DetailButton("Lihat detail");
            btn1.setPreferredSize(new Dimension(160, 45));

            // 2. Contoh teks lain (bisa diubah kapan saja menggunakan .setText())
            // btn1.setText("Simpan Data"); 

            btn1.addActionListener(e -> {
                // Teks pada tombol juga bisa diambil atau diubah secara dinamis saat diklik
                JOptionPane.showMessageDialog(frame, "Tombol dengan teks '" + btn1.getText() + "' diklik!");
            });

            panel.add(btn1);
            panel.add(btn2);
            frame.add(panel);
            frame.setVisible(true);
        });
    }
}
