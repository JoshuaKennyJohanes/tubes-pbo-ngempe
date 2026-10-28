package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JPanel;

import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.view.NavbarPanelBuyer;

public class DashboardPembeli extends JPanel {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardPembeli(JPanel mainPanel, CardLayout cardLayoutInput) {

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        try {
            EtalaseView panelEtalase = new EtalaseView();
            RiwayatTransaksiView panelRiwayat = new RiwayatTransaksiView();

            mainContentPanel.add(panelEtalase, Router.ETALASE_PAGE);
            mainContentPanel.add(panelRiwayat, Router.RIWAYAT_TRANSAKSI);

            NavbarPanelBuyer navbar = new NavbarPanelBuyer(mainContentPanel, cardLayout, mainPanel, cardLayoutInput);

            add(navbar, BorderLayout.WEST);
            add(mainContentPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}