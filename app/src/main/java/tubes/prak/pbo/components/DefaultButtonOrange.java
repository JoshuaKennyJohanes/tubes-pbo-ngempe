package tubes.prak.pbo.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class DefaultButtonOrange extends JButton {

    // Warna Utama & Hover (Oranye Modern)
    private Color normalColor = new Color(245, 130, 32);  // Vibrant Orange
    private Color hoverColor  = new Color(225, 110, 15);  // Slightly Darker Orange
    private Color clickColor  = new Color(195, 90, 5);    // Deep Orange
    private Color currentColor = normalColor;

    private int cornerRadius = 15; // Kehalusan sudut lengkung (rounded)

    // Konstruktor menerima parameter teks
    public DefaultButtonOrange(String text) {
        super(text);
        
        // Pengaturan dasar agar Swing tidak menggambar style default OS
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Typography
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Padding internal tombol (Atas, Kiri, Bawah, Kanan)
        setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Event Listener untuk efek hover dan klik
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = clickColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }
        });
    }

    // Method pembantu pendaftaran action listener
    public void setOnClickAction(java.awt.event.ActionListener action) {
        this.addActionListener(action);
    }

    // Menggambar latar belakang tombol kustom dengan Anti-Aliasing
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Mengaktifkan anti-aliasing agar garis lengkung halus (tidak patah-patah)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Gambar background dengan sudut rounded
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.dispose();

        // Menggambar teks button di atas background kustom
        super.paintComponent(g);
    }
}