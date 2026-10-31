package tubes.prak.pbo.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.controller.NavigationController;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;

public class NavbarPanel extends JPanel {

    public NavbarPanel() {

        // Atur layout dan warna background navbar (sebelah kiri)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(180, 0));

        // --- MEMBUAT TOMBOL ----//

        // Tombol sisi buyer
        DefaultButtonOrange btnEtalase = new DefaultButtonOrange("Etalase");
        DefaultButtonOrange btnRiwayat = new DefaultButtonOrange("Riwayat Transaksi");
        DefaultButtonOrange btnLogout = new DefaultButtonOrange("Log Out");

        // Tombol sisi manager
        DefaultButtonOrange btnWarehouse = new DefaultButtonOrange("Warehouse");
        DefaultButtonOrange btnProduksi = new DefaultButtonOrange("Produksi");


        //Looping dan list untuk menerapkan design ke semua button
        JButton[] menuButtons = {
                btnEtalase, btnRiwayat, btnLogout, btnWarehouse, btnProduksi, btnLogout
        };

        Dimension buttonSize = new Dimension(170, 38);

        for (JButton btn : menuButtons) {
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        }


        //penambahan action listener di luar blok if
        btnEtalase.addActionListener(e -> {
            NavigationController.showSubPage(Router.ETALASE_PAGE);
        });
        btnRiwayat.addActionListener(e -> {
            NavigationController.showSubPage(Router.RIWAYAT_TRANSAKSI);
        });
        
        btnLogout.addActionListener(e -> {
            Session.logOut();
            NavigationController.showRootPage(Router.LOGIN_PAGE);
        });
        btnWarehouse.addActionListener(e -> {
            NavigationController.showSubPage(Router.WAREHOUSE_PAGE);
        });
        btnProduksi.addActionListener(e -> {
            NavigationController.showSubPage(Router.PRODUKSI_PAGE);
        });



        //penambahan button tergantung dengan session.
        if (Session.getSession().getRole().equals(Role.MANAGER)) {
            add(btnWarehouse);
            add(Box.createRigidArea(new Dimension(0, 8)));

            add(btnProduksi);
            add(Box.createRigidArea(new Dimension(0, 8)));
        }

        if (Session.getSession().getRole().equals(Role.PEMBELI)) {
           add(btnEtalase);
           add(Box.createRigidArea(new Dimension(0, 8)));
           add(btnRiwayat);
           add(Box.createRigidArea(new Dimension(0, 8)));
        }

        add(Box.createVerticalGlue());
        add(btnLogout);
    }

}