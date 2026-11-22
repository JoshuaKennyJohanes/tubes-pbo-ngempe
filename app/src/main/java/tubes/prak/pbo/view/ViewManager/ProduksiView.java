package tubes.prak.pbo.view.ViewManager;

import com.sun.source.util.JavacTask;
import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.components.DetailProduksiPanel;
import tubes.prak.pbo.components.FormProduksiPanel;
import tubes.prak.pbo.components.ProduksiScrollPane;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.repositories.ProduksiRepository;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProduksiView extends JPanel {

    private static final String CARD_LIST = "LIST_VIEW";
    private static final String CARD_FORM = "FORM_VIEW";
    private static final String CARD_DETAIL = "DETAIL_VIEW";

    private final CardLayout cardLayout = new CardLayout();
    private final ProduksiScrollPane scrollPane = new ProduksiScrollPane();
    private DetailProduksiPanel detailPanel;

    public ProduksiView() {
        setLayout(cardLayout);

        add(createListViewPanel(), CARD_LIST);
        add(createFormViewPanel(), CARD_FORM);
        add(createDetailViewPanel(), CARD_DETAIL);

        // Muat data awal dari database
        loadDataProduksi();
        cardLayout.show(this, CARD_LIST);
    }

    // --- HALAMAN 1: Daftar Card Produksi ---
    private JPanel createListViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        // Header atas + tombol buka form
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Jadwal Produksi");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnTambah = new JButton("+ Jadwal Produksi");
        btnTambah.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnTambah.setFocusPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.addActionListener(e -> cardLayout.show(this, CARD_FORM));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnTambah, BorderLayout.EAST);

        // Listener saat tombol "Detail" pada salah satu card ditekan
        scrollPane.setOnDetailClickListener(produksi -> {
            detailPanel.setProduksi(produksi);
            cardLayout.show(this, CARD_DETAIL);
        });

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- HALAMAN 2: Form Penjadwalan Produksi ---
    private JPanel createFormViewPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        // Bar atas untuk tombol kembali/batal dari form
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        topBar.setBackground(Color.WHITE);

        JButton btnKembali = new JButton("← Kembali ke Daftar");
        btnKembali.setFocusPainted(false);
        btnKembali.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> cardLayout.show(this, CARD_LIST));
        topBar.add(btnKembali);

        // Saat form berhasil simpan: reload daftar card & kembali ke LIST_VIEW
        FormProduksiPanel formPanel = new FormProduksiPanel(produksiBaru -> {
            loadDataProduksi();
            cardLayout.show(this, CARD_LIST);
        });

        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // --- HALAMAN 3: Detail Produksi ---
    private JPanel createDetailViewPanel() {
        detailPanel = new DetailProduksiPanel(
                () -> cardLayout.show(this, CARD_LIST),
                this::loadDataProduksi
        );
        return detailPanel;
    }

    // --- Helper Reload Data ke Card ---
    public void loadDataProduksi() {
        try {
            List<Produksi> daftarProduksi = ProduksiRepository.findAll();
            scrollPane.setData(daftarProduksi);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data produksi: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}