package tubes.prak.pbo.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.controller.NavigationController;
import tubes.prak.pbo.models.User;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;
import tubes.prak.pbo.view.ViewManager.ProduksiView;
import tubes.prak.pbo.view.ViewManager.WarehouseView;
import tubes.prak.pbo.view.ViewPembeli.EtalaseView;
import tubes.prak.pbo.view.ViewPembeli.Keranjang;
import tubes.prak.pbo.view.ViewPembeli.RiwayatTransaksiView;


public class MainShell extends JPanel {
    private Keranjang panelKeranjang;
    private JPanel mainContentPanel;
    private CardLayout contentCardLayout;

    public MainShell(User user) throws SQLException {
        setLayout(new BorderLayout());

        // 1. Inisialisasi Panel Utama dengan BorderLayout
        add(new JLabel("Ini Container Dashbaord Manager"));

        // 2. Buat Panel Konten Kanan menggunakan CardLayout
        contentCardLayout= new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);

        //buat mainContentPanel sebagai subPanel (menu semuanya)
        NavigationController.setContentPanel(mainContentPanel, contentCardLayout);

        
        panelKeranjang = new Keranjang(user);

        mainContentPanel.add(new EtalaseView(mainContentPanel , contentCardLayout,user,panelKeranjang),Router.ETALASE_PAGE);
        
        mainContentPanel.add(panelKeranjang,Router.KERANJANG_PAGE);
        mainContentPanel.add(new RiwayatTransaksiView(user),Router.RIWAYAT_TRANSAKSI);

        mainContentPanel.add(new WarehouseView(), Router.WAREHOUSE_PAGE);
        mainContentPanel.add(new ProduksiView(), Router.PRODUKSI_PAGE);


        add(mainContentPanel,BorderLayout.CENTER);

    }

    public void buildNavbarAndDefaultPanel(User user){
        NavbarPanel navbar = new NavbarPanel(user,panelKeranjang);

        if(Session.getSession().getRole().equals(Role.MANAGER)){
            //show default manager
            contentCardLayout.show(mainContentPanel, Router.WAREHOUSE_PAGE);
        }

        if(Session.getSession().getRole().equals(Role.PEMBELI)){
            //show default pmebeli
            contentCardLayout.show(mainContentPanel, Router.ETALASE_PAGE);
        }

        add(navbar, BorderLayout.WEST);
        revalidate();
        repaint();
    }
}
