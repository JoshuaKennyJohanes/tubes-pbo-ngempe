package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class QuantityInput extends JPanel {
    private final JTextField textField;
    private int curr = 1;

    public QuantityInput(int jumlah) {
    
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(80, 35));
        

        Border outerBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        Border padding = BorderFactory.createEmptyBorder(2, 8, 2, 8);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, padding));

        textField = new JTextField(String.valueOf(jumlah), 5);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBorder(null);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(textField, BorderLayout.CENTER);
        curr = jumlah;
    }

    public void addChangeListener(IntConsumer listener) {
        textField.addCaretListener(e -> {
            try {
                String text = textField.getText().trim();
                if (!text.isEmpty()) {
                    int val = Integer.parseInt(text);
                    if (val >= 0 && val != curr) {
                        curr = val;
                        listener.accept(curr);
                    }
                }
            } catch (NumberFormatException ignored) {

            }
        });
    }

    public int getValue() {
        try {
            if (!textField.getText().trim().isEmpty()) {
                curr = Integer.parseInt(textField.getText().trim());
            }
        } catch (NumberFormatException ignored) {}
        return curr;
    }

    public void setValue(int value) {
        this.curr = value;
        textField.setText(String.valueOf(value));
    }
}
