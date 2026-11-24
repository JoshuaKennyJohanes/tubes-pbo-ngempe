package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tubes.prak.pbo.Interfaces.ItemService;
import tubes.prak.pbo.components.cards.ProductCard;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.models.productRelated.Produk;
import tubes.prak.pbo.models.users.UserBuyer;

public class EtalaseView extends JPanel {

    private final ItemService itemService;
    private final JPanel mainContentPanel;
    private final CardLayout cardLayout;
    private final UserBuyer user;
    private final Keranjang panelKeranjang;

    private JTextField txtSearch;
    private JPanel gridPanel;
    private List<Produk> cachedProduk = new ArrayList<>();

    public EtalaseView(JPanel mainContentPanel, CardLayout cardLayout, UserBuyer user, Keranjang panelKeranjang) throws SQLException {
        this(mainContentPanel, cardLayout, user, panelKeranjang, new ItemController());
    }

    public EtalaseView(JPanel mainContentPanel, CardLayout cardLayout, UserBuyer user, Keranjang panelKeranjang,
                       ItemService itemService) throws SQLException {
        this.mainContentPanel = mainContentPanel;
        this.cardLayout = cardLayout;
        this.user = user;
        this.panelKeranjang = panelKeranjang;
        this.itemService = Objects.requireNonNull(itemService);

        setLayout(new BorderLayout(10, 12));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildGridScrollPane(), BorderLayout.CENTER);

        refreshEtalase();
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Etalase Produk");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setPreferredSize(new Dimension(210, 28));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { renderFilteredCards(); }
            @Override
            public void removeUpdate(DocumentEvent e) { renderFilteredCards(); }
            @Override
            public void changedUpdate(DocumentEvent e) { renderFilteredCards(); }
        });

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshEtalase());

        searchPanel.add(new JLabel("Cari Produk:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnRefresh);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane buildGridScrollPane() {
        // Grid 3 kolom per baris dengan jarak horizontal & vertikal 18px
        gridPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        gridPanel.setOpaque(false);

        // Wrapper NORTH mencegah GridLayout meregangkan tinggi card ke bawah saat item sedikit
        ScrollableGridWrapper wrapper = new ScrollableGridWrapper();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 2, 12, 2));
        wrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    public final void refreshEtalase() {
        try {
            cachedProduk = itemService.getAllProduk();
            renderFilteredCards();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat daftar produk: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void renderFilteredCards() {
        gridPanel.removeAll();
        String keyword = (txtSearch != null && txtSearch.getText() != null)
                ? txtSearch.getText().trim().toLowerCase()
                : "";

        for (Produk pk : cachedProduk) {
            if (!keyword.isEmpty() && !pk.getNamaProduk().toLowerCase().contains(keyword)) {
                continue;
            }

            ProductCard card = new ProductCard(pk);
            card.getDetailButton().addActionListener(e -> {
                DetailProduk produkDetail = new DetailProduk(
                        pk,
                        mainContentPanel,
                        cardLayout,
                        user,
                        panelKeranjang,
                        this::refreshEtalase
                );
                mainContentPanel.add(produkDetail, "DETAIL_PRODUK");
                cardLayout.show(mainContentPanel, "DETAIL_PRODUK");
            });

            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private static class ScrollableGridWrapper extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 60;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}