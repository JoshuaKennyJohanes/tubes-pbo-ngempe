package tubes.prak.pbo.view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.models.User;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;
import tubes.prak.pbo.view.ViewPembeli.Keranjang;

public class NavbarPanelBuyer extends JPanel {

    public NavbarPanelBuyer(JPanel mainContentPanel,CardLayout cardLayoutMainContent, JPanel mainPanel, CardLayout cardLayout,User user) {
        
        // Atur layout dan warna background navbar (sebelah kiri)
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS)); 
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(180, 0)); 


        // Membuat tombol-tombol
        DefaultButtonOrange btnEtalase = new DefaultButtonOrange("Etalase");
        DefaultButtonOrange btnRiwayat = new DefaultButtonOrange("Riwayat Transaksi");
        DefaultButtonOrange btnKeranjang = new DefaultButtonOrange("Keranjang");
        DefaultButtonOrange btnLogout = new DefaultButtonOrange("Log Out");

        JButton[] menuButtons = {
            btnEtalase, btnRiwayat, btnLogout , btnKeranjang
        };

        Dimension buttonSize = new Dimension(170,38);

        for(JButton btn : menuButtons){
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
        }


        // Menambahkan listener/fungsi ke tombol
        btnEtalase.addActionListener(e->{
            cardLayoutMainContent.show(mainContentPanel,Router.ETALASE_PAGE);
        });
        btnRiwayat.addActionListener(e->{

            cardLayoutMainContent.show(mainContentPanel,Router.RIWAYAT_TRANSAKSI);
        });
        btnKeranjang.addActionListener(e ->{
            Keranjang panelKeranjang = new Keranjang(user);
            
            mainContentPanel.add(
                panelKeranjang,
                Router.KERANJANG_PAGE
            );
            cardLayoutMainContent.show(mainContentPanel, Router.KERANJANG_PAGE);
        });

        btnLogout.addActionListener(e->{
            Session.logOut();
            cardLayout.show(mainPanel,Router.LOGIN_PAGE);

        });

        // Menambahkan tombol ke panel navbar
        add(btnEtalase);
        add(Box.createRigidArea(new Dimension(0,8)));
        add(btnRiwayat);
        add(Box.createRigidArea(new Dimension(0,8)));
        add(btnKeranjang);
        add(Box.createRigidArea(new Dimension(0,8)));


        add(Box.createVerticalGlue());
        add(btnLogout);
    }
}