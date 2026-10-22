package tubes.prak.pbo.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class NavbarPanelBuyer extends JPanel {


    // Konfigurasi Warna Palette
    private static final Color NAV_BG_COLOR = new Color(33, 37, 41);      // Hitam keabu-abuan (Dark)
    private static final Color BTN_BG_COLOR = new Color(52, 58, 64);      // Abu-abu gelap untuk tombol
    private static final Color BTN_HOVER_COLOR = new Color(73, 80, 87);   // Warna saat mouse mengarah ke tombol
    private static final Color TEXT_COLOR = new Color(248, 249, 250);     // Putih bersih
    private static final Color DANGER_COLOR = new Color(220, 53, 69);     // Merah untuk tombol Logout

    public NavbarPanelBuyer() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0)); // Lebar navbar 200px
        setBackground(NAV_BG_COLOR); // 1. Mengubah background Navbar
        
        // Memberi padding (jarak dalam) untuk navbar: atas 20, kiri 15, bawah 20, kanan 15
        setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // --- Brand / Title Logo ---
        JLabel brandLabel = new JLabel("APP", SwingConstants.CENTER);
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        brandLabel.setForeground(TEXT_COLOR);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Buat Tombol-Tombol Navigasi ---
        JButton btnEtalase = createStyledButton("🛒 Etalase");
        JButton btnRiwayat = createStyledButton("📜 Riwayat Pembelian");
        JButton btnLogout = createStyledButton("🚪 Logout");

        // Khusus tombol logout diberi sentuhan warna merah
        btnLogout.setBackground(DANGER_COLOR);

        // --- Event Listener ---
        // btnEtalase.addActionListener(e -> mainFrame.showEtalase());
        // btnRiwayat.addActionListener(e -> mainFrame.showRiwayat());
        // btnLogout.addActionListener(e -> mainFrame.showLogin());

        // --- Menyusun Komponen ke Dalam Panel ---
        add(brandLabel);
        add(Box.createVerticalStrut(30)); // Jarak bawah logo
        add(btnEtalase);
        add(Box.createVerticalStrut(10)); // Jarak antar tombol
        add(btnRiwayat);
        add(Box.createVerticalGlue());    // Mendorong Logout ke posisi paling bawah
        add(btnLogout);
    }

    /**
     * Helper Method untuk Membuat Tombol Kustom dengan Tampilan Modern
     */
    private JButton createStyledButton(String text) {
        // Menggunakan Custom Class RoundedButton agar sudut tombol melengkung
        JButton button = new RoundedButton(text, 12); 
        
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(BTN_BG_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Mengubah kursor jadi tangan saat di-hover
        
        // Ukuran tombol
        button.setMaximumSize(new Dimension(170, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Effect Hover (Saat Mouse Masuk / Keluar)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!text.contains("Logout")) {
                    button.setBackground(BTN_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!text.contains("Logout")) {
                    button.setBackground(BTN_BG_COLOR);
                }
            }
        });

        return button;
    }

    /**
     * Custom JButton Class untuk Menggambar Sudut Tombol Melengkung (Rounded Corner)
     */
    private static class RoundedButton extends JButton {
        private final int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Menghaluskan pinggiran grafik (Anti-aliasing)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Gambar Background Tombol
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}