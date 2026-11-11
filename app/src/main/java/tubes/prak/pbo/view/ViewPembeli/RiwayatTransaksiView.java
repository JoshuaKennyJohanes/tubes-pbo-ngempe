package tubes.prak.pbo.view.ViewPembeli;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.User;
import tubes.prak.pbo.repositories.SalesOrderRespository;
;

public class RiwayatTransaksiView extends JPanel {
    private DefaultTableModel modelPO;
    
    private JTable tablePO;
    private JTable tableDetail;
    
    public RiwayatTransaksiView(User user) {
        //TRANSAKSI SEMUA
        setLayout(new BorderLayout());
        
        String[] kolom = {
            "ID",
            "Tanggal Pesan",
            "Jumlah",
            "Cara_Bayar",
            "Status Pembayaran"
        };
        modelPO = new DefaultTableModel(kolom, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
             }
        };
        tablePO = new JTable(modelPO);

        tablePO.setModel(modelPO);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(new JLabel("Riwayat Transaksi"));

        add(content, BorderLayout.CENTER);
        SalesOrderRespository sor = new SalesOrderRespository();
        sor.loadData(modelPO,user.getId());
        

        //TRANSAKSI RINCIAN
        content.add(new JLabel("Klik transaksi untuk melihat rincian"));
        
        content.add(new JScrollPane(tablePO));

        content.add(new JLabel("Rincian Transaksi"));

        tableDetail = new JTable();
        content.add(new JScrollPane(tableDetail));

        add(content, BorderLayout.CENTER);

        
    }
}

