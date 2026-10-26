package tubes.prak.pbo.view.ViewManager;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;

public class NavbarPanelManager extends JPanel {

     public NavbarPanelManager(JPanel mainContentPanel,CardLayout cardLayoutMainContent, JPanel mainPanel, CardLayout cardLayout) {
        
        
        // Atur layout dan warna background navbar (sebelah kiri)
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS)); 
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(180, 0)); 

        
        // Membuat tombol-tombol
        DefaultButtonOrange btnWarehouse = new DefaultButtonOrange("Warehouse");
        DefaultButtonOrange btnProduksi = new DefaultButtonOrange("Produksi");
        DefaultButtonOrange btnLogout = new DefaultButtonOrange("Logout");


        JButton [] menuButtons = {
            btnWarehouse, btnProduksi, btnLogout
        };

        Dimension buttonSize = new Dimension (170, 38);

        for(JButton btn : menuButtons){
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        // Menambahkan listener/fungsi ke tombol
        btnWarehouse.addActionListener(e->{
            cardLayoutMainContent.show(mainContentPanel,Router.WAREHOUSE_PAGE);
        });
        btnProduksi.addActionListener(e->{
            cardLayoutMainContent.show(mainContentPanel,Router.PRODUKSI_PAGE);
        });

        btnLogout.addActionListener(e->{
            Session.logOut();
            cardLayout.show(mainPanel,Router.LOGIN_PAGE);

        });

        // Menambahkan tombol ke panel navbar
        add(btnWarehouse);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(btnProduksi);
        add(Box.createRigidArea(new Dimension(0, 8)));

        add(Box.createVerticalGlue());
        add(btnLogout);

    }
    
}
