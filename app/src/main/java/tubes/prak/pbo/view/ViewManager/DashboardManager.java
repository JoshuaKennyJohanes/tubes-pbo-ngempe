package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tubes.prak.pbo.util.Router;



public class DashboardManager extends JPanel {

   private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardManager(JPanel mainPanel, CardLayout cardLayoutInput) {


        // 1. Inisialisasi Panel Utama dengan BorderLayout
        setLayout(new BorderLayout());
        add(new JLabel("Ini Container Dashbaord Manager"));

        // 2. Buat Panel Konten Kanan menggunakan CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);


        WarehouseView warehousePanel = new WarehouseView();
        ProduksiView produksiPanel = new ProduksiView();
        mainContentPanel.add(warehousePanel,Router.WAREHOUSE_PAGE);
        mainContentPanel.add(produksiPanel, Router.PRODUKSI_PAGE);



        // 3. Buat Navbar 
        NavbarPanelManager navbar = new NavbarPanelManager(mainContentPanel, cardLayout, mainPanel, cardLayoutInput);

        // 4. Tambahkan kedua panel anak ke JFrame
        add(navbar, BorderLayout.WEST);            
        add(mainContentPanel, BorderLayout.CENTER); 
    }
}
