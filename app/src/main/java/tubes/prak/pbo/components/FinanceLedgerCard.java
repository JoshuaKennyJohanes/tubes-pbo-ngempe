package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.ENUM.JenisPos;
import tubes.prak.pbo.models.FinanceLedger;

public class FinanceLedgerCard extends JPanel {

    public static final int CARD_HEIGHT = 96;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    private final FinanceLedger ledger;

    public FinanceLedgerCard(FinanceLedger ledger) {
        this.ledger = ledger;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 222), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        setPreferredSize(new Dimension(0, CARD_HEIGHT));
        setMinimumSize(new Dimension(520, CARD_HEIGHT));

        // 1. KIRI: ID Ledger, Badge Jenis Pos, Referensi Transaksi & Tanggal
        add(buildLeftPanel(), BorderLayout.WEST);

        // 2. TENGAH: Deskripsi & Created By
        add(buildCenterPanel(), BorderLayout.CENTER);

        // 3. KANAN: Nominal Transaksi
        add(buildRightPanel(), BorderLayout.EAST);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, CARD_HEIGHT);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(250, CARD_HEIGHT - 20));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblId = new JLabel("Ledger #" + ledger.getIdLedger() + "   ");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblId.setForeground(new Color(30, 41, 59));

        JLabel lblBadge = createJenisPosBadge(ledger.getJenisPos());

        topRow.add(lblId);
        topRow.add(lblBadge);

        JLabel lblRef = new JLabel(ledger.getReferensiDisplay());
        lblRef.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRef.setForeground(new Color(51, 65, 85));
        lblRef.setAlignmentX(Component.LEFT_ALIGNMENT);

        String tanggalStr = (ledger.getTanggalTransaksi() != null)
                ? DATE_FORMAT.format(ledger.getTanggalTransaksi())
                : "-";
        JLabel lblTanggal = new JLabel("Tanggal: " + tanggalStr);
        lblTanggal.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTanggal.setForeground(new Color(100, 116, 139));
        lblTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblRef);
        panel.add(Box.createVerticalGlue());
        panel.add(lblTanggal);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 8, 2, 8));

        String deskripsiText = (ledger.getDeskripsi() != null && !ledger.getDeskripsi().isBlank())
                ? ledger.getDeskripsi()
                : ledger.getJenisPos().name();

        JLabel lblDeskripsi = new JLabel("<html><body style='width: 340px;'>" + deskripsiText + "</body></html>");
        lblDeskripsi.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDeskripsi.setForeground(new Color(30, 41, 59));
        lblDeskripsi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCreator = new JLabel("Created By: " + ledger.getCreatorDisplay());
        lblCreator.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCreator.setForeground(new Color(100, 116, 139));
        lblCreator.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblDeskripsi);
        panel.add(Box.createVerticalGlue());
        panel.add(lblCreator);

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(195, CARD_HEIGHT - 20));

        boolean isMasuk = ledger.isArusMasuk();
        String prefix = isMasuk ? "+ Rp " : "- Rp ";

        JLabel lblNominal = new JLabel(prefix + CURRENCY_FORMAT.format(ledger.getNominal()), SwingConstants.RIGHT);
        lblNominal.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblNominal.setForeground(isMasuk ? new Color(22, 101, 52) : new Color(185, 28, 28));

        panel.add(lblNominal, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createJenisPosBadge(JenisPos jenisPos) {
        JLabel badge = new JLabel(" " + jenisPos.name() + " ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setOpaque(true);

        switch (jenisPos) {
            case PENJUALAN -> {
                badge.setBackground(new Color(220, 252, 231));
                badge.setForeground(new Color(22, 101, 52));
                badge.setBorder(new LineBorder(new Color(134, 239, 172), 1, true));
            }
            case DEPOSIT -> {
                badge.setBackground(new Color(219, 234, 254));
                badge.setForeground(new Color(29, 78, 216));
                badge.setBorder(new LineBorder(new Color(147, 197, 253), 1, true));
            }
            case PEMBELIAN -> {
                badge.setBackground(new Color(254, 243, 199));
                badge.setForeground(new Color(146, 64, 14));
                badge.setBorder(new LineBorder(new Color(252, 211, 77), 1, true));
            }
            case OPNAME -> {
                badge.setBackground(new Color(254, 226, 226));
                badge.setForeground(new Color(185, 28, 28));
                badge.setBorder(new LineBorder(new Color(252, 165, 165), 1, true));
            }
        }
        return badge;
    }

    public FinanceLedger getLedger() {
        return ledger;
    }
}