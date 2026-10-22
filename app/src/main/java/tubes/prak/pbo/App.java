package tubes.prak.pbo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tubes.prak.pbo.view.LoginView;

public class App extends JFrame {
        
    public App(){
        setTitle("Ngempe Commerce & MRP");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.lightGray);

        JPanel mainPanel = new JPanel();
        
        LoginView loginPanel = new LoginView();
        mainPanel.add(loginPanel);

        add(mainPanel);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new App().setVisible(true);
        });
    }
}