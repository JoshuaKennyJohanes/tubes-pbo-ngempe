package tubes.prak.pbo;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.view.LoginView;
import tubes.prak.pbo.view.RegisterView;
import tubes.prak.pbo.view.ViewManager.DashboardManager;
import tubes.prak.pbo.view.ViewPembeli.DashboardPembeli;

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
        RegisterView registerPanel = new RegisterView(mainPanel, cardLayout);
        try {
            
        } catch (Exception e) {
        }
        DashboardPembeli panelUtamaPembeli = new DashboardPembeli(mainPanel, cardLayout);
        DashboardManager panelUtamaManager = new DashboardManager(mainPanel, cardLayout);

        

        //masukin semua page utama ke dalam cardyoutnya
        mainPanel.add(loginPanel, Router.LOGIN_PAGE);
        mainPanel.add(registerPanel, Router.REGISTER_PAGE);
        mainPanel.add(panelUtamaPembeli, Router.BUYER_DASHBOARD);
        mainPanel.add(panelUtamaManager,Router.MANAGER_DASHBOARD);

        add(mainPanel);
        cardLayout.show(mainPanel, Router.LOGIN_PAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new App().setVisible(true);
        });
    }
}
