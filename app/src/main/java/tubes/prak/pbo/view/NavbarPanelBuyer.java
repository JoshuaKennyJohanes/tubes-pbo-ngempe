package tubes.prak.pbo.view;

import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NavbarPanelBuyer extends JPanel {

    public NavbarPanelBuyer(JPanel mainContentPanel,CardLayout cardLayoutMainContent, JPanel mainPanel, CardLayout cardLayout) {
        
        // Atur layout dan warna background navbar (sebelah kiri)
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS)); 
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(180, 0)); 


        // Membuat tombol-tombol
        DefaultButtonOrange btnEtalase = new DefaultButtonOrange("Etalase");
        DefaultButtonOrange btnRiwayat = new DefaultButtonOrange("Riwayat Transaksi");
        DefaultButtonOrange btnLogout = new DefaultButtonOrange("Log Out");

        JButton[] menuButtons = {
            btnEtalase, btnRiwayat, btnLogout
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

        btnLogout.addActionListener(e->{
            Session.logOut();
            cardLayout.show(mainPanel,Router.LOGIN_PAGE);

        });

        // Menambahkan tombol ke panel navbar
        add(btnEtalase);
        add(Box.createRigidArea(new Dimension(0,8)));
        add(btnRiwayat);
        add(Box.createRigidArea(new Dimension(0,8)));


        add(Box.createVerticalGlue());
        add(btnLogout);
    }
}