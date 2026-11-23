package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.border.EmptyBorder;

import tubes.prak.pbo.components.cards.FinanceLedgerCard;
import tubes.prak.pbo.components.cards.FinanceSummaryCard;
import tubes.prak.pbo.controller.FinanceController;
import tubes.prak.pbo.models.FinanceLedger;
import tubes.prak.pbo.models.FinanceSummary;

public class FinanceView extends JPanel {

    private static final String[] NAMA_BULAN = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");

    private final FinanceController financeController = new FinanceController();

    private JComboBox<String> cbBulan;
    private JComboBox<Integer> cbTahun;

    // 4 Kartu Monitor Keuangan
    private FinanceSummaryCard cardTotalAssets;
    private FinanceSummaryCard cardTotalExpenses;
    private FinanceSummaryCard cardTotalRevenue;
    private FinanceSummaryCard cardNetProfitLoss;

    // List Kartu Ledger
    private ScrollableCardListPanel ledgerListContainer;

    public FinanceView() {
        setLayout(new BorderLayout(10, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(buildHeaderPanel(), BorderLayout.NORTH);
        topSection.add(buildSummaryCardsPanel(), BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(buildLedgerSectionPanel(), BorderLayout.CENTER);

        refreshFinanceData();
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel lblTitle = new JLabel("Finance Dashboard");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);

        cbBulan = new JComboBox<>(NAMA_BULAN);
        cbBulan.setPreferredSize(new Dimension(120, 28));
        cbBulan.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        cbTahun = new JComboBox<>();
        cbTahun.setPreferredSize(new Dimension(95, 28));
        cbTahun.addItem(LocalDate.now().getYear());

        JButton btnFilter = new JButton("Tampilkan");
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnFilter.setBackground(new Color(37, 99, 235));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> loadDashboardBySelectedPeriod());

        JButton btnDeposit = new JButton("+ Deposit Cash");
        btnDeposit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDeposit.setBackground(new Color(22, 163, 74));
        btnDeposit.setForeground(Color.WHITE);
        btnDeposit.setFocusPainted(false);
        btnDeposit.addActionListener(e -> openDepositCashDialog());

        rightControls.add(new JLabel("Periode:"));
        rightControls.add(cbBulan);
        rightControls.add(cbTahun);
        rightControls.add(btnFilter);
        rightControls.add(Box.createHorizontalStrut(6));
        rightControls.add(btnDeposit);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(rightControls, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel buildSummaryCardsPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        gridPanel.setOpaque(false);

        cardTotalAssets = new FinanceSummaryCard("Total Assets", new Color(37, 99, 235));
        cardTotalExpenses = new FinanceSummaryCard("Total Expenses", new Color(217, 119, 6));
        cardTotalRevenue = new FinanceSummaryCard("Total Revenue", new Color(22, 163, 74));
        cardNetProfitLoss = new FinanceSummaryCard("Net Profit / Loss", new Color(15, 23, 42));

        gridPanel.add(cardTotalAssets);
        gridPanel.add(cardTotalExpenses);
        gridPanel.add(cardTotalRevenue);
        gridPanel.add(cardNetProfitLoss);

        return gridPanel;
    }

    private JPanel buildLedgerSectionPanel() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);

        JLabel lblSectionTitle = new JLabel("Riwayat Transaksi Keuangan (Finance Ledger)");
        lblSectionTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSectionTitle.setForeground(new Color(30, 41, 59));
        lblSectionTitle.setBorder(new EmptyBorder(4, 2, 2, 2));

        ledgerListContainer = new ScrollableCardListPanel();
        ledgerListContainer.setLayout(new BoxLayout(ledgerListContainer, BoxLayout.Y_AXIS));
        ledgerListContainer.setBorder(new EmptyBorder(4, 2, 4, 2));

        JScrollPane scrollPane = new JScrollPane(ledgerListContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        section.add(lblSectionTitle, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    // Dipanggil saat menu Finance dibuka dari navbar MainShell
    public void refreshFinanceData() {
        reloadAvailableYears();
        loadDashboardBySelectedPeriod();
    }

    private void reloadAvailableYears() {
        Integer previous = (Integer) cbTahun.getSelectedItem();
        int currentYear = LocalDate.now().getYear();

        cbTahun.removeAllItems();
        try {
            List<Integer> years = financeController.getAvailableYears();
            if (!years.contains(currentYear)) {
                cbTahun.addItem(currentYear);
            }
            for (Integer y : years) {
                cbTahun.addItem(y);
            }
        } catch (SQLException e) {
            cbTahun.addItem(currentYear);
        }

        if (previous != null) {
            cbTahun.setSelectedItem(previous);
        }
    }

    private void loadDashboardBySelectedPeriod() {
        int bulan = cbBulan.getSelectedIndex() + 1;
        Integer tahunObj = (Integer) cbTahun.getSelectedItem();
        int tahun = (tahunObj != null) ? tahunObj : LocalDate.now().getYear();

        try {
            FinanceSummary summary = financeController.getSummaryByMonthYear(bulan, tahun);
            updateSummaryCards(summary);

            List<FinanceLedger> ledgers = financeController.getLedgerByMonthYear(bulan, tahun);
            renderLedgerCards(ledgers, bulan, tahun);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data Finance: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummaryCards(FinanceSummary s) {
        // 1. Total Assets
        String assetsDetail = "Cash: Rp " + CURRENCY_FORMAT.format(s.getTotalCash())
                + "<br>RM: Rp " + CURRENCY_FORMAT.format(s.getNilaiRawMaterial())
                + " | FG: Rp " + CURRENCY_FORMAT.format(s.getNilaiFinishedGoods());
        cardTotalAssets.updateData(
                "Total Assets",
                "Rp " + CURRENCY_FORMAT.format(s.getTotalAssets()),
                new Color(15, 23, 42),
                new Color(37, 99, 235),
                assetsDetail
        );

        // 2. Total Expenses
        String expensesDetail = "PO Bahan Baku: Rp " + CURRENCY_FORMAT.format(s.getTotalPembelianBulanan())
                + "<br>Kerugian Opname: Rp " + CURRENCY_FORMAT.format(s.getTotalOpnameBulanan());
        cardTotalExpenses.updateData(
                "Total Expenses",
                "Rp " + CURRENCY_FORMAT.format(s.getTotalExpensesBulanan()),
                new Color(180, 83, 9),
                new Color(217, 119, 6),
                expensesDetail
        );

        // 3. Total Revenue
        String revenueDetail = "Total Penjualan Bersih<br>Periode " + NAMA_BULAN[cbBulan.getSelectedIndex()] + " " + cbTahun.getSelectedItem();
        cardTotalRevenue.updateData(
                "Total Revenue",
                "Rp " + CURRENCY_FORMAT.format(s.getTotalRevenueBulanan()),
                new Color(22, 101, 52),
                new Color(22, 163, 74),
                revenueDetail
        );

        // 4. Net Profit / Net Loss
        double net = s.getNetProfitBulanan();
        boolean isProfit = net >= 0;
        String netTitle = isProfit ? "Net Profit" : "Net Loss";
        String netValueStr = (isProfit ? "Rp " : "- Rp ") + CURRENCY_FORMAT.format(Math.abs(net));
        Color netColor = isProfit ? new Color(22, 101, 52) : new Color(185, 28, 28);
        String netDetail = "COGS: Rp " + CURRENCY_FORMAT.format(s.getTotalCogsBulanan())
                + "<br>Opname: Rp " + CURRENCY_FORMAT.format(s.getTotalOpnameBulanan());

        cardNetProfitLoss.updateData(netTitle, netValueStr, netColor, netColor, netDetail);
    }

    private void renderLedgerCards(List<FinanceLedger> ledgers, int bulan, int tahun) {
        ledgerListContainer.removeAll();

        if (ledgers.isEmpty()) {
            JLabel lblEmpty = new JLabel("Belum ada transaksi keuangan pada " + NAMA_BULAN[bulan - 1] + " " + tahun + ".");
            lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setBorder(new EmptyBorder(20, 10, 20, 10));
            ledgerListContainer.add(lblEmpty);
        } else {
            for (FinanceLedger ledger : ledgers) {
                ledgerListContainer.add(new FinanceLedgerCard(ledger));
                ledgerListContainer.add(Box.createVerticalStrut(8));
            }
        }

        ledgerListContainer.revalidate();
        ledgerListContainer.repaint();
    }

    private void openDepositCashDialog() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        JTextField txtNominal = new JTextField();
        JTextField txtKeterangan = new JTextField("Setoran Modal Awal Kas");

        formPanel.add(new JLabel("Nominal Deposit Cash (Rp):"));
        formPanel.add(txtNominal);
        formPanel.add(new JLabel("Keterangan / Deskripsi:"));
        formPanel.add(txtKeterangan);

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Deposit Cash Baru",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                double nominal = Double.parseDouble(txtNominal.getText().trim());
                String keterangan = txtKeterangan.getText().trim();

                financeController.depositCash(nominal, keterangan);
                JOptionPane.showMessageDialog(this,
                        "Deposit cash sebesar Rp " + CURRENCY_FORMAT.format(nominal) + " berhasil dicatat!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshFinanceData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Nominal deposit harus berupa angka yang valid!",
                        "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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