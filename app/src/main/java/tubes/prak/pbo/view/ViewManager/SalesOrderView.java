package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.border.EmptyBorder;

import tubes.prak.pbo.controller.SalesOrderController;
import tubes.prak.pbo.models.SalesOrder;

public class SalesOrderView extends JPanel {

    private static final String[] NAMA_BULAN = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    private final SalesOrderController salesOrderController = new SalesOrderController();

    private JComboBox<String> cbBulan;
    private JComboBox<Object> cbTahun;
    private ScrollableCardListPanel cardListContainer;

    public SalesOrderView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        refreshSalesOrders();
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Daftar Sales Order Masuk");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Panel Filter Bulan & Tahun
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        cbBulan = new JComboBox<>(NAMA_BULAN);
        cbBulan.setPreferredSize(new Dimension(125, 28));
        cbBulan.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        cbTahun = new JComboBox<>();
        cbTahun.setPreferredSize(new Dimension(125, 28));

        JButton btnFilter = new JButton("Filter");
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnFilter.setBackground(new Color(37, 99, 235));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> applyFilter());

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetFilter());

        filterPanel.add(new JLabel("Bulan:"));
        filterPanel.add(cbBulan);
        filterPanel.add(new JLabel("Tahun:"));
        filterPanel.add(cbTahun);
        filterPanel.add(btnFilter);
        filterPanel.add(btnReset);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane buildContentPanel() {
        cardListContainer = new ScrollableCardListPanel();
        cardListContainer.setLayout(new BoxLayout(cardListContainer, BoxLayout.Y_AXIS));
        cardListContainer.setBorder(new EmptyBorder(4, 2, 4, 2));

        JScrollPane scrollPane = new JScrollPane(cardListContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    // Dapat dipanggil saat navbar menu Sales Order diklik untuk memuat data terbaru
    public void refreshSalesOrders() {
        reloadAvailableYears();
        loadOrders(null, null);
    }

    private void reloadAvailableYears() {
        Object previousSelection = cbTahun.getSelectedItem();
        cbTahun.removeAllItems();
        cbTahun.addItem("Semua Tahun");

        try {
            List<Integer> years = salesOrderController.getAvailableYears();
            for (Integer year : years) {
                cbTahun.addItem(year);
            }
            if (previousSelection != null) {
                cbTahun.setSelectedItem(previousSelection);
            }
        } catch (SQLException e) {
            // Tetap sediakan opsi tahun berjalan jika query tahun gagal
            cbTahun.addItem(LocalDate.now().getYear());
        }
    }

    private void applyFilter() {
        int selectedMonth = cbBulan.getSelectedIndex() + 1; // 1 = Januari ... 12 = Desember
        Object selectedYearObj = cbTahun.getSelectedItem();
        Integer selectedYear = (selectedYearObj instanceof Integer) ? (Integer) selectedYearObj : null;

        loadOrders(selectedMonth, selectedYear);
    }

    private void resetFilter() {
        cbBulan.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        if (cbTahun.getItemCount() > 0) {
            cbTahun.setSelectedIndex(0); // "Semua Tahun"
        }
        loadOrders(null, null);
    }

    private void loadOrders(Integer bulan, Integer tahun) {
        cardListContainer.removeAll();

        try {
            List<SalesOrder> orders = salesOrderController.getSalesOrdersByFilter(bulan, tahun);

            if (orders.isEmpty()) {
                String pesan = (bulan != null)
                        ? "Tidak ada data sales order pada bulan " + NAMA_BULAN[bulan - 1]
                        + (tahun != null ? " " + tahun : "") + "."
                        : "Belum ada data sales order yang masuk.";

                JLabel lblEmpty = new JLabel(pesan);
                lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 13));
                lblEmpty.setForeground(Color.GRAY);
                lblEmpty.setBorder(new EmptyBorder(20, 10, 20, 10));
                cardListContainer.add(lblEmpty);
            } else {
                for (SalesOrder order : orders) {
                    SalesOrderCard card = new SalesOrderCard(order);
                    cardListContainer.add(card);
                    cardListContainer.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data Sales Order: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        cardListContainer.revalidate();
        cardListContainer.repaint();
    }

    // Memastikan card selalu melebar mengikuti frame aplikasi
    private static class ScrollableCardListPanel extends JPanel implements Scrollable {
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