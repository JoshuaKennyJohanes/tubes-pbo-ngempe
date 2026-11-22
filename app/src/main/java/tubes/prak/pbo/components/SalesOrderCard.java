package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;

public class SalesOrderCard extends JPanel {

    public static final int CARD_HEIGHT = 175;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    private final SalesOrder salesOrder;

    public SalesOrderCard(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        setPreferredSize(new Dimension(0, CARD_HEIGHT));
        setMinimumSize(new Dimension(520, CARD_HEIGHT));

        // 1. BAGIAN KIRI: Informasi Order, Pembeli, Alamat, Status & Total
        JPanel leftPanel = buildOrderInfoPanel();
        add(leftPanel, BorderLayout.WEST);

        // 2. BAGIAN KANAN/TENGAH: Tabel Scrollable Produk yang Dibeli
        JScrollPane tableScrollPane = buildProductTableScrollPane();
        add(tableScrollPane, BorderLayout.CENTER);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, CARD_HEIGHT);
    }

    private JPanel buildOrderInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(295, CARD_HEIGHT - 24));

        // Baris 1: ID Order & Tanggal Pesan
        String tanggalStr = (salesOrder.getTanggalPesan() != null)
                ? DATE_FORMAT.format(salesOrder.getTanggalPesan())
                : "-";

        JLabel lblOrderHeader = new JLabel("Order #" + salesOrder.getIdOrder() + "  •  " + tanggalStr);
        lblOrderHeader.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblOrderHeader.setForeground(new Color(30, 41, 59));
        lblOrderHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Baris 2: Gabungan ID User & Nama Pembeli (contoh: 12#Budi Santoso)
        JLabel lblPembeli = new JLabel("Pembeli: " + salesOrder.getUserDisplay());
        lblPembeli.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPembeli.setForeground(new Color(51, 65, 85));
        lblPembeli.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Baris 3: Alamat Pengiriman
        String alamat = (salesOrder.getAlamatUser() != null && !salesOrder.getAlamatUser().isBlank())
                ? salesOrder.getAlamatUser()
                : "-";
        JLabel lblAlamat = new JLabel("<html><body style='width: 275px;'>Alamat: " + alamat + "</body></html>");
        lblAlamat.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblAlamat.setForeground(new Color(100, 116, 139));
        lblAlamat.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Baris 4: Cara Bayar & Badge Status Pembayaran
        JPanel paymentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        paymentRow.setOpaque(false);
        paymentRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String caraBayar = (salesOrder.getCaraBayar() != null) ? salesOrder.getCaraBayar() : "-";
        JLabel lblCaraBayar = new JLabel("Bayar: " + caraBayar + "   |   Status: ");
        lblCaraBayar.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCaraBayar.setForeground(new Color(71, 85, 105));

        String statusText = (salesOrder.getStatusPembayaran() != null)
                ? salesOrder.getStatusPembayaran()
                : "Pending";
        JLabel lblStatusBadge = createStatusBadge(statusText);

        paymentRow.add(lblCaraBayar);
        paymentRow.add(lblStatusBadge);

        // Baris 5: Total Transaksi
        JLabel lblTotal = new JLabel("Total Transaksi: Rp " + CURRENCY_FORMAT.format(salesOrder.getTotalTransaksi()));
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotal.setForeground(new Color(22, 101, 52));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblOrderHeader);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblPembeli);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblAlamat);
        panel.add(Box.createVerticalGlue());
        panel.add(paymentRow);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTotal);

        return panel;
    }

    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(" " + status + " ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setOpaque(true);

        String normalized = status.trim().toLowerCase();
        if (normalized.contains("lunas") || normalized.contains("paid") || normalized.contains("terbayar") || normalized.contains("selesai")) {
            badge.setBackground(new Color(220, 252, 231));
            badge.setForeground(new Color(22, 101, 52));
            badge.setBorder(new LineBorder(new Color(134, 239, 172), 1, true));
        } else {
            badge.setBackground(new Color(254, 243, 199));
            badge.setForeground(new Color(146, 64, 14));
            badge.setBorder(new LineBorder(new Color(252, 211, 77), 1, true));
        }
        return badge;
    }

    private JScrollPane buildProductTableScrollPane() {
        String[] columns = {"ID Produk", "Nama Produk", "Qty", "Harga Satuan", "Subtotal"};

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (SalesOrderDetail detail : salesOrder.getDetails()) {
            tableModel.addRow(new Object[]{
                    detail.getIdProduk(),
                    detail.getNamaProduk(),
                    detail.getJumlahTerjual(),
                    "Rp " + CURRENCY_FORMAT.format(detail.getHargaJualAktual()),
                    "Rp " + CURRENCY_FORMAT.format(detail.getSubtotal())
            });
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(23);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 234, 240));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Pengaturan lebar kolom
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(0).setMaxWidth(85);
        table.getColumnModel().getColumn(1).setPreferredWidth(210);
        table.getColumnModel().getColumn(2).setPreferredWidth(55);
        table.getColumnModel().getColumn(2).setMaxWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(115);
        table.getColumnModel().getColumn(4).setPreferredWidth(125);

        // Alignment kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        return scrollPane;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }
}