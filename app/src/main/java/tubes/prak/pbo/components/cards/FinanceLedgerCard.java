package tubes.prak.pbo.components.cards;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import tubes.prak.pbo.ENUM.JenisPos;
import tubes.prak.pbo.components.base.BadgeStyle;
import tubes.prak.pbo.components.base.BaseCard;
import static tubes.prak.pbo.components.base.CardColors.BODY;
import static tubes.prak.pbo.components.base.CardColors.DANGER_TEXT;
import static tubes.prak.pbo.components.base.CardColors.MUTED;
import static tubes.prak.pbo.components.base.CardColors.SUCCESS_TEXT;
import static tubes.prak.pbo.components.base.CardColors.TITLE;
import tubes.prak.pbo.components.base.CardFormat;
import tubes.prak.pbo.models.FinanceLedger;

public class FinanceLedgerCard extends BaseCard {

    private final FinanceLedger ledger;

    public FinanceLedgerCard(FinanceLedger ledger) {
        super(96, 520, new Insets(10, 16, 10, 16));
        this.ledger = ledger;
        build();
    }

    // KIRI: ID Ledger, Badge Jenis Pos, Referensi Transaksi & Tanggal
    @Override
    protected JComponent buildLeft() {
        JPanel panel = column(250);

        JLabel lblId = label("Ledger #" + ledger.getIdLedger(), Font.BOLD, 13, TITLE);
        lblId.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel lblBadge = badge(ledger.getJenisPos().name(), posStyle(ledger.getJenisPos()));

        JPanel topRow = row();
        topRow.setMaximumSize(new Dimension(250, 24));
        topRow.add(lblId);
        topRow.add(Box.createHorizontalStrut(8));
        topRow.add(lblBadge);
        topRow.add(Box.createHorizontalGlue());

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(label(ledger.getReferensiDisplay(), Font.BOLD, 12, BODY));
        panel.add(Box.createVerticalGlue());
        panel.add(label("Tanggal: " + CardFormat.date(ledger.getTanggalTransaksi()), Font.PLAIN, 11, MUTED));
        return panel;
    }

    // TENGAH: Deskripsi & Created By
    @Override
    protected JComponent buildCenter() {
        JPanel panel = column();
        panel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        String deskripsi = (ledger.getDeskripsi() != null && !ledger.getDeskripsi().isBlank())
                ? ledger.getDeskripsi()
                : ledger.getJenisPos().name();

        JTextArea txtDeskripsi = new JTextArea(deskripsi);
        txtDeskripsi.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDeskripsi.setForeground(TITLE);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setEditable(false);
        txtDeskripsi.setFocusable(false);
        txtDeskripsi.setOpaque(false);
        txtDeskripsi.setBorder(null);
        txtDeskripsi.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDeskripsi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        panel.add(txtDeskripsi);
        panel.add(Box.createVerticalGlue());
        panel.add(label("Created By: " + ledger.getCreatorDisplay(), Font.PLAIN, 11, MUTED));
        return panel;
    }

    // KANAN: Nominal Transaksi
    @Override
    protected JComponent buildRight() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        Dimension fixed = new Dimension(185, contentHeight());
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);

        boolean masuk = ledger.isArusMasuk();
        String text = (masuk ? "+ " : "- ") + CardFormat.rupiah(ledger.getNominal());

        JLabel lblNominal = labelRight(text, Font.BOLD, 15, masuk ? SUCCESS_TEXT : DANGER_TEXT);
        panel.add(lblNominal, BorderLayout.CENTER);
        return panel;
    }

    private static BadgeStyle posStyle(JenisPos jenisPos) {
        return switch (jenisPos) {
            case PENJUALAN -> BadgeStyle.SUCCESS;
            case DEPOSIT -> BadgeStyle.INFO;
            case PEMBELIAN -> BadgeStyle.WARNING;
            case OPNAME -> BadgeStyle.DANGER;
            default -> BadgeStyle.NEUTRAL;
        };
    }

    public FinanceLedger getLedger() {
        return ledger;
    }
}