package tubes.prak.pbo;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

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

    }
}
