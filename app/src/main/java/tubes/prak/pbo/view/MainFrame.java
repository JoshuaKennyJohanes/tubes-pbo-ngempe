
package tubes.prak.pbo.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tubes.prak.pbo.models.User;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("ERP & E-Commerce");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(cards);
    }

    public void setLoginView(LoginView view) {
        cards.add(view, "LOGIN");
        showLogin();
    }

    public void showLogin() {
        cardLayout.show(cards, "LOGIN");
    }

    public void showBuyerDashboard(User user) {
        JPanel panel = createDashboard(
            "Buyer Dashboard",
            user
        );

        cards.add(panel, "BUYER");
        cardLayout.show(cards, "BUYER");
    }

    public void showManagerDashboard(User user) {
        JPanel panel = createDashboard(
            "Manager Dashboard",
            user
        );

        cards.add(panel, "MANAGER");
        cardLayout.show(cards, "MANAGER");
    }

    private JPanel createDashboard(String title, User user) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel heading = new JLabel(
            title + " — Selamat datang, " + user.getNama(),
            SwingConstants.CENTER
        );

        panel.add(heading, BorderLayout.CENTER);

        return panel;
    }
}