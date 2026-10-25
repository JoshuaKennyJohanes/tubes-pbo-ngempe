package tubes.prak.pbo.view;

import javax.swing.JPanel;

import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NavbarPanelBuyer extends JPanel {

    // Konstruktor menerima ActionListener untuk masing-masing tombol
    public NavbarPanelBuyer(JPanel mainContentPanel,CardLayout cardLayoutMainContent, JPanel mainPanel, CardLayout cardLayout) {
        // Atur layout dan warna background navbar (sebelah kiri)
        setLayout(new GridLayout(5, 1, 5, 5)); // Contoh susunan tombol ke bawah
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(180, 0)); // Lebar 180px, tinggi mengikuti parent

        // Membuat tombol-tombol
        DefaultButtonOrange btnEtalase = new DefaultButtonOrange("Etalase");
        DefaultButtonOrange btnRiwayat = new DefaultButtonOrange("Riwayat Transaksi");
        DefaultButtonOrange btnLogout = new DefaultButtonOrange("Log Out");

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
        add(btnRiwayat);
        add(btnLogout);
    }
}