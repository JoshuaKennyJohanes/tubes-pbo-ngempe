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

    private JLabel labeltotalbayar;
    private double totalbayar;

    public Keranjang(User user) {

        setLayout(new BorderLayout());

        String[] kolom = {
            "Nama Produk",
            "Jumlah Produk",
            "Jumlah Total"
        };

        modeltable = new DefaultTableModel(kolom, 0);
        labeltotalbayar = new JLabel();
        table = new JTable(modeltable);

        add(new JLabel("Keranjang"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(labeltotalbayar,BorderLayout.SOUTH);

        refreshKeranjang(user);
    }
    public void refreshKeranjang(User user) {
            
        modeltable.setRowCount(0);

        totalbayar = 0;
        for (BarangKeranjang produk : user.getBarangKeranjang()) {
            
            modeltable.addRow(new Object[] {
                produk.getNamaBarang(),
                produk.getJumlah(),
                produk.getHargaTotal()
            });
            totalbayar += produk.getHargaTotal();
        }
            labeltotalbayar.setText("Total Bayar: "+totalbayar);
    }
}