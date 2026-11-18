package tubes.prak.pbo.view.ViewPembeli;


import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.exception.InvalidCredentialsException;
import tubes.prak.pbo.models.BarangKeranjang;
import tubes.prak.pbo.models.User;
import tubes.prak.pbo.repositories.SalesOrderRespository;

public class Keranjang extends JPanel{
    private DefaultTableModel modeltable;
    private JTable table;

    private JLabel labeltotalbayar;
    private double totalbayar;
    private JButton btnCheckout;
    private SalesOrderRespository repo;
    public Keranjang(User user) {

        setLayout(new BorderLayout());

        String[] kolom = {
            "Nama Produk",
            "Jumlah Produk",
            "Jumlah Total",
            "Hilangkan dari Keranjang"
        };

        modeltable = new DefaultTableModel(kolom, 0);
        labeltotalbayar = new JLabel();
        table = new JTable(modeltable);
        btnCheckout = new JButton();

        add(new JLabel("Keranjang"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel panelBawah = new JPanel(new BorderLayout());

        panelBawah.add(labeltotalbayar, BorderLayout.CENTER);
        
        btnCheckout = new JButton("Checkout");
        panelBawah.add(btnCheckout, BorderLayout.EAST);

        add(panelBawah, BorderLayout.SOUTH);


        btnCheckout.addActionListener(e -> {
            try {

                repo = new SalesOrderRespository();

                boolean cek = repo.checkout(user);
                if(cek == false){
                    throw new InvalidCredentialsException("Keranjang Masih Kosong");
                }
                user.setKosongKeranjang();
                refreshKeranjang(user);
                JOptionPane.showMessageDialog(
                        this,
                        "Checkout berhasil!"
                );

                refreshKeranjang(user);

            } catch (InvalidCredentialsException ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Checkout gagal!"
                );
            }
        });
    }
    public void refreshKeranjang(User user) {
            
        modeltable.setRowCount(0);

        totalbayar = 0;
        for (BarangKeranjang produk : user.getBarangKeranjang()) {
            
            modeltable.addRow(new Object[] {
                produk.getNamaBarang(),
                produk.getJumlah(),
                produk.getHargaTotal(),
                
            });
            totalbayar += produk.getHargaTotal();
        }
            labeltotalbayar.setText("Total Bayar: "+totalbayar);
    }
}