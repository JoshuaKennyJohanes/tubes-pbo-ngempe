package tubes.prak.pbo.components.cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class FinanceSummaryCard extends JPanel {

    public static final int CARD_HEIGHT = 150;

    private final JLabel lblTitle;
    private final JLabel lblMainValue;
    private final JTextArea lblSubDetail;
    private final JPanel accentBar;

    public FinanceSummaryCard(String title, Color accentColor) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(220, CARD_HEIGHT));
        setMinimumSize(new Dimension(180, CARD_HEIGHT));

        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(0, 5));
        add(accentBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(12, 16, 12, 16));

        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 116, 139));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblMainValue = new JLabel("Rp 0");
        lblMainValue.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblMainValue.setForeground(new Color(15, 23, 42));
        lblMainValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubDetail = new JTextArea("-");
        lblSubDetail.setLineWrap(true);
        lblSubDetail.setWrapStyleWord(true);
        lblSubDetail.setEditable(false);
        lblSubDetail.setFocusable(false);
        lblSubDetail.setOpaque(false);
        lblSubDetail.setForeground(new Color(71, 85, 105));
        lblSubDetail.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubDetail.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubDetail.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(235, 239, 245)),
                new EmptyBorder(6, 0, 0, 0)
        ));
        lblSubDetail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(lblMainValue);
        content.add(Box.createVerticalGlue());
        content.add(lblSubDetail);

        add(content, BorderLayout.CENTER);
    }

    public void updateData(String title, String mainValue, Color valueColor, Color accentColor, String subDetail) {
        lblTitle.setText(title);
        lblMainValue.setText(mainValue);
        lblMainValue.setForeground(valueColor);
        accentBar.setBackground(accentColor);
        lblSubDetail.setText(subDetail);
    }
}