package tubes.prak.pbo;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.view.LoginView;
import tubes.prak.pbo.view.ViewPembeli.EtalaseView;

public class App extends JFrame {
        
    public App(){
        
        setTitle("Ngempe Commerce & MRP");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.lightGray);

        CardLayout cardLayout = new CardLayout();
        setLayout(cardLayout);

        JPanel mainPanel = new JPanel(cardLayout);
        
        LoginView loginPanel = new LoginView(mainPanel,cardLayout);
        EtalaseView etalasePanel = new EtalaseView(mainPanel, cardLayout);



        //masukin semua page ke dalam cardyoutnya
        mainPanel.add(loginPanel, Router.LOGIN_PAGE);
        mainPanel.add(etalasePanel, Router.ETALASE_PAGE);

        add(mainPanel);

        cardLayout.show(mainPanel, Router.LOGIN_PAGE);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new App().setVisible(true);
        });
    }
}