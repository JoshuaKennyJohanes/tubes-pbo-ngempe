package tubes.prak.pbo.view.ViewPembeli;


import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.User;

public class Keranjang extends JPanel{
    private DefaultTableModel modeltable;
    private JTable table;

    public Keranjang(User user) {

        setLayout(new BorderLayout());

        String[] kolom = {
            "Nama Produk",
            "Jumlah Produk",
            "Jumlah Total"
        };

        modeltable = new DefaultTableModel(kolom, 0);

        table = new JTable(modeltable);

        table.add(new JLabel("Keranjang"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshKeranjang(user);
    }
    public void refreshKeranjang(User user) {
            
        modeltable.setRowCount(0);
            
        for (BarangKeranjang produk : user.getBarangKeranjang()) {
            System.out.println(produk.getNamaBarang());
            modeltable.addRow(new Object[] {
                    produk.getNamaBarang(),
                    produk.getJumlah(),
                    produk.getHargaTotal()
                });
            }
            System.out.println("Row count: "+modeltable.getRowCount());
    }
}