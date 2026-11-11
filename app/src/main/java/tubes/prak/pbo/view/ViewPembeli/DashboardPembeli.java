package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import tubes.prak.pbo.models.User;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.view.NavbarPanelBuyer;

public class DashboardPembeli extends JPanel {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardPembeli(JPanel mainPanel, CardLayout cardLayoutInput, User user) {

        setLayout(new BorderLayout());
        Keranjang panelKeranjang;
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        try {
            EtalaseView panelEtalase = new EtalaseView(mainPanel, cardLayoutInput,user);
            RiwayatTransaksiView panelRiwayat = new RiwayatTransaksiView(user);
            panelKeranjang = new Keranjang(user);

            mainContentPanel.add(panelEtalase, Router.ETALASE_PAGE);
            mainContentPanel.add(panelRiwayat, Router.RIWAYAT_TRANSAKSI);
            
            mainContentPanel.add(panelKeranjang,Router.KERANJANG_PAGE);

            NavbarPanelBuyer navbar = new NavbarPanelBuyer(mainContentPanel, cardLayout, mainPanel, cardLayoutInput,user);

            add(navbar, BorderLayout.WEST);
            add(mainContentPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}