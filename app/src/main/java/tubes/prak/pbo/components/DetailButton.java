package tubes.prak.pbo.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

// Kelas Reusable RoundedButton
public class DetailButton extends JButton {
    private Color normalBg = Color.WHITE;
    private Color hoverBg = new Color(240, 243, 246);
    private Color pressedBg = new Color(220, 225, 230);
    private Color borderColor = new Color(200, 205, 215);
    private Color textColor = new Color(45, 55, 72);

    public DetailButton(String text) {
        super(text);
        setForeground(textColor);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    /*
        Cara Pakai:

        DetailButton btn1 = new DetailButton("Lihat detail");
        btn1.setPreferredSize(new Dimension(160, 45)); sesuaikan ukuran dengan kebutuhan
        
    */
}
