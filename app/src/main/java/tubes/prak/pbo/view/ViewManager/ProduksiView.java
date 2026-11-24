package tubes.prak.pbo.view.ViewManager;

import tubes.prak.pbo.components.base.CardListPanel;
import tubes.prak.pbo.components.cards.ProduksiCard;
import tubes.prak.pbo.components.panels.DetailProduksiPanel;
import tubes.prak.pbo.components.panels.FormProduksiPanel;
import tubes.prak.pbo.Interfaces.ItemService;
import tubes.prak.pbo.Interfaces.ProductionService;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.controller.ProduksiController;
import tubes.prak.pbo.models.Produksi;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import javax.swing.border.EmptyBorder;
import java.sql.SQLException;

public class ProduksiView extends JPanel {

    private static final String CARD_LIST = "LIST_VIEW";
    private static final String CARD_FORM = "FORM_VIEW";
    private static final String CARD_DETAIL = "DETAIL_VIEW";

    private final ItemService itemService;
    private final ProductionService productionService;
    private final CardLayout cardLayout = new CardLayout();
    private final CardListPanel<Produksi> produksiList =
            new CardListPanel<>(produksi -> new ProduksiCard(produksi, this::showDetail));
    private DetailProduksiPanel detailPanel;

    public ProduksiView() {
        this(new ItemController(), new ProduksiController());
    }

    public ProduksiView(ItemService itemService, ProductionService productionService) {
        this.itemService = Objects.requireNonNull(itemService);
        this.productionService = Objects.requireNonNull(productionService);
        setLayout(cardLayout);

        produksiList.setListBackground(new Color(248, 250, 252));
        produksiList.setContentInsets(new Insets(12, 12, 12, 12));
        produksiList.setEmptyMessage("Belum ada jadwal produksi.");

        add(createListViewPanel(), CARD_LIST);
        add(createFormViewPanel(), CARD_FORM);
        add(createDetailViewPanel(), CARD_DETAIL);

        // Muat data awal dari database
        loadDataProduksi();
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

        panel.add(header, BorderLayout.NORTH);
        panel.add(produksiList, BorderLayout.CENTER);
        return panel;
    }

    // Dipanggil saat tombol "Detail" pada salah satu card ditekan
    private void showDetail(Produksi produksi) {
        detailPanel.setProduksi(produksi);
        cardLayout.show(this, CARD_DETAIL);
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
        }, itemService, productionService);

        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // --- HALAMAN 3: Detail Produksi ---
    private JPanel createDetailViewPanel() {
        detailPanel = new DetailProduksiPanel(
                () -> cardLayout.show(this, CARD_LIST),
                this::loadDataProduksi,
                productionService
        );
        return detailPanel;
    }

    // --- Helper Reload Data ke Card ---
    public final void loadDataProduksi() {
        try {
            List<Produksi> daftarProduksi = productionService.getAllProduksi();
            produksiList.setItems(daftarProduksi);
        } catch (SQLException e) {
            produksiList.clear();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data produksi: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}