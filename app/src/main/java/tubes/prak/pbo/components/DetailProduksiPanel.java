package tubes.prak.pbo.components;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.controller.ProduksiController;
import tubes.prak.pbo.models.DetailProduksi;
import tubes.prak.pbo.models.Produksi;
import tubes.prak.pbo.repositories.ProduksiRepository;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class DetailProduksiPanel extends JPanel {

    private final ProduksiController produksiController = new ProduksiController();
    private final Runnable onBack;
    private final Runnable onStatusUpdated;

    private Produksi currentProduksi;

    private JLabel lblHeaderTitle;
    private JLabel lblStatusBadge;
    private JPanel infoGridPanel;
    private JButton btnUbahStatus;
    private DefaultTableModel bomTableModel;

    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final DecimalFormat qtyFormat = new DecimalFormat("#,##0.##");

    public DetailProduksiPanel(Runnable onBack) {
        this(onBack, null);
    }

    public DetailProduksiPanel(Runnable onBack, Runnable onStatusUpdated) {
        this.onBack = onBack;
        this.onStatusUpdated = onStatusUpdated;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // --- 1. TOP BAR: Tombol Kembali, Judul & Badge Status ---
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JButton btnKembali = new JButton("← Kembali");
        btnKembali.setFocusPainted(false);
        btnKembali.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        lblHeaderTitle = new JLabel("Detail Jadwal Produksi");
        lblHeaderTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        lblStatusBadge = new JLabel();
        lblStatusBadge.setOpaque(true);
        lblStatusBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatusBadge.setBorder(new EmptyBorder(5, 12, 5, 12));

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightTopPanel.setOpaque(false);
        rightTopPanel.add(lblStatusBadge);

        topBar.add(btnKembali, BorderLayout.WEST);
        topBar.add(lblHeaderTitle, BorderLayout.CENTER);
        topBar.add(rightTopPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // --- 2. CENTER: Ringkasan Produksi + Tabel Kebutuhan BOM ---
        JPanel centerContainer = new JPanel(new BorderLayout(0, 18));
        centerContainer.setOpaque(false);

        // Box Ringkasan Metadata & Tombol Aksi Status
        JPanel summaryCard = new JPanel(new BorderLayout(16, 12));
        summaryCard.setBackground(new Color(248, 250, 252));
        summaryCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        infoGridPanel = new JPanel(new GridLayout(3, 4, 12, 10));
        infoGridPanel.setOpaque(false);

        btnUbahStatus = new JButton();
        btnUbahStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnUbahStatus.setFocusPainted(false);
        btnUbahStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahStatus.setPreferredSize(new Dimension(220, 36));
        btnUbahStatus.addActionListener(e -> prosesUbahStatus());

        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionBox.setOpaque(false);
        actionBox.add(btnUbahStatus);

        summaryCard.add(infoGridPanel, BorderLayout.CENTER);
        summaryCard.add(actionBox, BorderLayout.SOUTH);

        // Panel Tabel Kebutuhan Bahan Baku
        JPanel tableSection = new JPanel(new BorderLayout(0, 8));
        tableSection.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Rincian Kebutuhan Bahan Baku (BOM)");
        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTableTitle.setForeground(new Color(30, 41, 59));

        String[] columns = {
                "ID Material",
                "Nama Bahan Baku",
                "Kebutuhan / Unit",
                "Total Kebutuhan",
                "Harga Rata-Rata",
                "Subtotal HPP"
        };

        bomTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tblBom = new JTable(bomTableModel);
        tblBom.setRowHeight(30);
        tblBom.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tblBom.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tblBom.getTableHeader().setBackground(new Color(241, 245, 249));
        tblBom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Rata kanan untuk kolom angka
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int col : new int[]{2, 3, 4, 5}) {
            tblBom.getColumnModel().getColumn(col).setCellRenderer(rightRenderer);
        }

        JScrollPane tableScroll = new JScrollPane(tblBom);
        tableScroll.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        tableSection.add(lblTableTitle, BorderLayout.NORTH);
        tableSection.add(tableScroll, BorderLayout.CENTER);

        centerContainer.add(summaryCard, BorderLayout.NORTH);
        centerContainer.add(tableSection, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
    }

    public void setProduksi(Produksi p) {
        if (p == null) return;
        this.currentProduksi = p;

        lblHeaderTitle.setText("Detail Produksi #" + p.getIdProduksi());
        renderInfoGrid(p);
        updateStatusUI(p.getStatusProduksi());
        loadTabelBom(p);
    }

    private void renderInfoGrid(Produksi p) {
        infoGridPanel.removeAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String tanggalStr = (p.getTanggalProduksi() != null) ? sdf.format(p.getTanggalProduksi()) : "-";

        addInfoPair("ID Produk:", String.valueOf(p.getIdProduk()));
        addInfoPair("Tanggal Produksi:", tanggalStr);
        addInfoPair("Jumlah Produksi:", qtyFormat.format(p.getJumlahProduksi()));
        addInfoPair("Total Estimasi HPP:", rupiahFormat.format(p.getTotalHppProduksi()));
        addInfoPair("Dibuat Oleh (User ID):", String.valueOf(p.getCreatedBy()));
        addInfoPair("ID Jadwal:", "#" + p.getIdProduksi());

        infoGridPanel.revalidate();
        infoGridPanel.repaint();
    }

    private void addInfoPair(String key, String value) {
        JLabel lblKey = new JLabel(key);
        lblKey.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblKey.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblVal.setForeground(new Color(15, 23, 42));

        infoGridPanel.add(lblKey);
        infoGridPanel.add(lblVal);
    }

    private void updateStatusUI(StatusProduksi status) {
        lblStatusBadge.setText(status.name());
        lblStatusBadge.setForeground(status.getTextColor());
        lblStatusBadge.setBackground(status.getBadgeColor());

        switch (status) {
            case PLANNED -> {
                btnUbahStatus.setText("Mulai Produksi (ONGOING)");
                btnUbahStatus.setEnabled(true);
                btnUbahStatus.setVisible(true);
            }
            case ONGOING -> {
                btnUbahStatus.setText("Selesaikan Produksi (FINISHED)");
                btnUbahStatus.setEnabled(true);
                btnUbahStatus.setVisible(true);
            }
            case FINISHED -> {
                btnUbahStatus.setText("Produksi Telah Selesai");
                btnUbahStatus.setEnabled(false);
                btnUbahStatus.setVisible(true);
            }
        }
    }

    private void loadTabelBom(Produksi p) {
        bomTableModel.setRowCount(0);
        try {
            // Membaca snapshot historis dari tabel detail_produksi
            java.util.List<DetailProduksi> daftarDetail =
                    ProduksiRepository.findDetailsByProduksiId(p.getIdProduksi());

            for (DetailProduksi detail : daftarDetail) {
                bomTableModel.addRow(new Object[]{
                        detail.getIdRawMaterial(),
                        detail.getNamaMaterial(),
                        qtyFormat.format(detail.getQtyPerUnit()),
                        qtyFormat.format(detail.getTotalKebutuhan()),
                        rupiahFormat.format(detail.getHargaRata2SaatItu()),
                        rupiahFormat.format(detail.getSubtotalHpp())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat rincian detail produksi: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesUbahStatus() {
        if (currentProduksi == null) return;

        StatusProduksi statusBerikutnya = switch (currentProduksi.getStatusProduksi()) {
            case PLANNED -> StatusProduksi.ONGOING;
            case ONGOING -> StatusProduksi.FINISHED;
            default -> null;
        };

        if (statusBerikutnya == null) return;

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Ubah status produksi #" + currentProduksi.getIdProduksi() + " menjadi " + statusBerikutnya.name() + "?",
                "Konfirmasi Perubahan Status",
                JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        try {
            produksiController.ubahStatusProduksi(currentProduksi, statusBerikutnya);
            updateStatusUI(statusBerikutnya);

            // Beri tahu ProduksiView agar daftar card ikut diperbarui
            if (onStatusUpdated != null) {
                onStatusUpdated.run();
            }

            JOptionPane.showMessageDialog(this,
                    "Status produksi berhasil diperbarui ke " + statusBerikutnya.name() + ".",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Gagal Mengubah Status", JOptionPane.ERROR_MESSAGE);
        }
    }
}