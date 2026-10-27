package tubes.prak.pbo.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class DetailButton extends JButton {
    private Color normalBg = ColorPalette.whiteMatte;
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
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = height;
        g2.setColor(normalBg);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
