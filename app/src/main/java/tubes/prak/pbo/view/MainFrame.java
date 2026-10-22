package tubes.prak.pbo.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tubes.prak.pbo.models.User;

public class MainFrame extends JFrame {

    // CardLayout Utama (Memisahkan layar LOGIN dan APP utama)
    private final CardLayout mainCardLayout = new CardLayout();
    private final JPanel mainCards = new JPanel(mainCardLayout);

    // CardLayout Khusus Konten Aplikasi (Di dalam Navbar)
    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(contentCardLayout);

    private User currentUser;

    public MainFrame() {
        setTitle("ERP & E-Commerce");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Buat Panel Utama untuk Area yang Memakai Navbar
        JPanel authenticatedAppPanel = new JPanel(new BorderLayout());
        authenticatedAppPanel.add(new NavbarPanelBuyer(this), BorderLayout.WEST);
        authenticatedAppPanel.add(contentCards, BorderLayout.CENTER);

        // 2. Registrasikan ke CardLayout Utama
        mainCards.add(authenticatedAppPanel, "APP");

        // Masukkan ke Window Utama
        add(mainCards);
    }

    public void setLoginView(LoginView view) {
        mainCards.add(view, "LOGIN");
        showLogin();
    }

    public void showLogin() {
        this.currentUser = null;
        mainCardLayout.show(mainCards, "LOGIN");
    }

    public void showBuyerDashboard(User user) {
        this.currentUser = user;

        // Tambahkan halaman-halaman khusus buyer ke contentCards
        contentCards.add(createDashboard("Buyer Dashboard", user), "BUYER_DASHBOARD");
        contentCards.add(createDummyPage("Halaman Etalase Produk"), "ETALASE");
        contentCards.add(createDummyPage("Halaman Riwayat Transaksi"), "RIWAYAT");

        // Pindah layar utama ke area berpapan "APP" (yang ada Navbarnya)
        mainCardLayout.show(mainCards, "APP");
        contentCardLayout.show(contentCards, "BUYER_DASHBOARD");
    }

    public void showManagerDashboard(User user) {
        this.currentUser = user;

        contentCards.add(createDashboard("Manager Dashboard", user), "MANAGER_DASHBOARD");
        
        mainCardLayout.show(mainCards, "APP");
        contentCardLayout.show(contentCards, "MANAGER_DASHBOARD");
    }

    // --- Method yang Dipanggil dari NavbarPanel ---

    public void showEtalase() {
        contentCardLayout.show(contentCards, "ETALASE");
    }

    public void showRiwayat() {
        contentCardLayout.show(contentCards, "RIWAYAT");
    }

    // --- Helper Methods ---

    private JPanel createDashboard(String title, User user) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel(
            title + " — Selamat datang, " + user.getNama(),
            SwingConstants.CENTER
        );
        panel.add(heading, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDummyPage(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}