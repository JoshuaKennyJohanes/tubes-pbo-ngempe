package tubes.prak.pbo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tubes.prak.pbo.ENUM.StatusPo;
import tubes.prak.pbo.models.PurchaseOrder;

public class PurchaseOrderCard extends JPanel {

    public static final int CARD_HEIGHT = 112;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    private final PurchaseOrder po;
    private final Consumer<PurchaseOrder> onArrivedClick;

    public PurchaseOrderCard(PurchaseOrder po, Consumer<PurchaseOrder> onArrivedClick) {
        this.po = po;
        this.onArrivedClick = onArrivedClick;
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
        setMinimumSize(new Dimension(600, CARD_HEIGHT));

        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
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
        panel.setPreferredSize(new Dimension(245, CARD_HEIGHT - 20));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblId = new JLabel("PO #" + po.getIdPo() + "   ");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblId.setForeground(new Color(30, 41, 59));

        JLabel lblBadge = createStatusBadge(po.getStatusPo());

        topRow.add(lblId);
        topRow.add(lblBadge);

        String tglBeliStr = (po.getTanggalPembelian() != null)
                ? DATE_FORMAT.format(po.getTanggalPembelian())
                : "-";
        JLabel lblTglBeli = new JLabel("Tanggal Pembelian: " + tglBeliStr);
        lblTglBeli.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTglBeli.setForeground(new Color(71, 85, 105));
        lblTglBeli.setAlignmentX(Component.LEFT_ALIGNMENT);

        String tglTerimaStr = (po.getTanggalDiterima() != null)
                ? DATE_FORMAT.format(po.getTanggalDiterima())
                : "Belum Diterima";
        JLabel lblTglTerima = new JLabel("Tanggal Diterima: " + tglTerimaStr);
        lblTglTerima.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTglTerima.setForeground(po.getStatusPo() == StatusPo.ARRIVED
                ? new Color(22, 101, 52)
                : new Color(146, 64, 14));
        lblTglTerima.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblTglBeli);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblTglTerima);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 8, 2, 8));

        String satuan = (po.getSatuanMaterial() != null) ? po.getSatuanMaterial() : "";
        JLabel lblMaterial = new JLabel(
                "Raw Material: " + po.getNamaMaterial() + "  (" + QTY_FORMAT.format(po.getJumlahDibeli()) + " " + satuan + ")"
        );
        lblMaterial.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMaterial.setForeground(new Color(15, 23, 42));
        lblMaterial.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVendor = new JLabel("Vendor: " + po.getNamaVendor());
        lblVendor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblVendor.setForeground(new Color(51, 65, 85));
        lblVendor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCreator = new JLabel("Created By: " + po.getCreatorDisplay());
        lblCreator.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCreator.setForeground(new Color(100, 116, 139));
        lblCreator.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblMaterial);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblVendor);
        panel.add(Box.createVerticalGlue());
        panel.add(lblCreator);

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(215, CARD_HEIGHT - 20));

        JLabel lblHargaAktual = new JLabel("Rp " + CURRENCY_FORMAT.format(po.getHargaBeliAktual()), SwingConstants.RIGHT);
        lblHargaAktual.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblHargaAktual.setForeground(new Color(15, 23, 42));
        lblHargaAktual.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String satuan = (po.getSatuanMaterial() != null) ? po.getSatuanMaterial() : "unit";
        JLabel lblHargaSatuan = new JLabel(
                "@ Rp " + CURRENCY_FORMAT.format(po.getHargaPerSatuan()) + " / " + satuan,
                SwingConstants.RIGHT
        );
        lblHargaSatuan.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblHargaSatuan.setForeground(new Color(100, 116, 139));
        lblHargaSatuan.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(lblHargaAktual);
        panel.add(Box.createVerticalStrut(2));
        panel.add(lblHargaSatuan);
        panel.add(Box.createVerticalGlue());

        if (po.getStatusPo() == StatusPo.PAID) {
            JButton btnArrived = new JButton("Tandai Arrived");
            btnArrived.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnArrived.setBackground(new Color(22, 163, 74));
            btnArrived.setForeground(Color.WHITE);
            btnArrived.setFocusPainted(false);
            btnArrived.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnArrived.addActionListener(e -> {
                if (onArrivedClick != null) {
                    onArrivedClick.accept(po);
                }
            });
            panel.add(btnArrived);
        } else {
            JLabel lblDone = new JLabel("Stok & HPP Diperbarui", SwingConstants.RIGHT);
            lblDone.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblDone.setForeground(new Color(22, 101, 52));
            lblDone.setAlignmentX(Component.RIGHT_ALIGNMENT);
            panel.add(lblDone);
        }

        return panel;
    }

    private JLabel createStatusBadge(StatusPo status) {
        JLabel badge = new JLabel(" " + status.name() + " ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setOpaque(true);

        if (status == StatusPo.ARRIVED) {
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
}