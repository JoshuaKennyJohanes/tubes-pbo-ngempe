package tubes.prak.pbo.view.ViewPembeli;

import java.awt.CardLayout;

import javax.swing.JPanel;

import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.view.NavbarPanelBuyer;


import javax.swing.*;
import java.awt.*;

public class DashboardPembeli extends JPanel {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardPembeli(JPanel mainPanel, CardLayout cardLayoutInput) {


        // 1. Inisialisasi Panel Utama dengan BorderLayout
        setLayout(new BorderLayout());

        // 2. Buat Panel Konten Kanan menggunakan CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Contoh panel Etalase (bisa dipisah jadi class sendiri)
        EtalaseView panelEtalase = new EtalaseView(mainContentPanel, cardLayout);
        panelEtalase.setBackground(Color.LIGHT_GRAY);
        panelEtalase.add(new JLabel("Halaman Etalase Produk"));

        // Contoh panel Riwayat Transaksi (bisa dipisah jadi class sendiri)
        RiwayatTransaksiView panelRiwayat = new RiwayatTransaksiView();
        panelRiwayat.setBackground(Color.WHITE);
        panelRiwayat.add(new JLabel("Halaman Riwayat Transaksi"));

        // Masukkan panel saudara ke mainContentPanel dengan nama kartu (identifier)
        mainContentPanel.add(panelEtalase, Router.ETALASE_PAGE);
        mainContentPanel.add(panelRiwayat, Router.RIWAYAT_TRANSAKSI);

        // 3. Buat Navbar dan lewatkan fungsi aksi (lambda/ActionListener)
        NavbarPanelBuyer navbar = new NavbarPanelBuyer(mainContentPanel, cardLayout, mainPanel, cardLayoutInput);

        // 4. Tambahkan kedua panel anak ke JFrame
        add(navbar, BorderLayout.WEST);            
        add(mainContentPanel, BorderLayout.CENTER); 
    }

}
