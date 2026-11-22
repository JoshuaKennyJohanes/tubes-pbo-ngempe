package tubes.prak.pbo.components;

import tubes.prak.pbo.ENUM.StatusProduksi;
import tubes.prak.pbo.models.Produksi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class ProduksiCard extends JPanel {

    private final Produksi produksi;

    public ProduksiCard(Produksi produksi, Consumer<Produksi> onDetailClick) {
        this.produksi = produksi;
        initUI(onDetailClick);
    }

    private void initUI(Consumer<Produksi> onDetailClick) {
        setLayout(new BorderLayout(16, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        // --- PANEL KIRI: 4 Teks Informasi ---
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 16, 8));
        infoPanel.setOpaque(false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String tanggalStr = (produksi.getTanggalProduksi() != null)
                ? sdf.format(produksi.getTanggalProduksi())
                : "-";

        JLabel lblIdProduksi = new JLabel("ID Produksi: #" + produksi.getIdProduksi());
        lblIdProduksi.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblIdProduksi.setForeground(new Color(30, 41, 59));

        JLabel lblTanggal = new JLabel("Tanggal: " + tanggalStr);
        lblTanggal.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTanggal.setForeground(new Color(71, 85, 105));

        JLabel lblIdProduk = new JLabel("ID Produk: " + produksi.getIdProduk());
        lblIdProduk.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblIdProduk.setForeground(new Color(71, 85, 105));

        JPanel statusWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusWrapper.setOpaque(false);
        statusWrapper.add(createStatusBadge(produksi.getStatusProduksi()));

        infoPanel.add(lblIdProduksi);
        infoPanel.add(lblTanggal);
        infoPanel.add(lblIdProduk);
        infoPanel.add(statusWrapper);

        // --- PANEL KANAN: Tombol Detail ---
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);

        JButton btnDetail = new JButton("Detail");
        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetail.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDetail.setPreferredSize(new Dimension(95, 34));

        btnDetail.addActionListener(e -> {
            if (onDetailClick != null) {
                onDetailClick.accept(this.produksi);
            }
        });

        actionPanel.add(btnDetail);

        add(infoPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.EAST);
    }

    private JLabel createStatusBadge(StatusProduksi status) {
        JLabel badge = new JLabel(status.name());
        badge.setOpaque(true);
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setForeground(status.getTextColor());
        badge.setBackground(status.getBadgeColor());
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }

    public Produksi getProduksi() {
        return produksi;
    }
}