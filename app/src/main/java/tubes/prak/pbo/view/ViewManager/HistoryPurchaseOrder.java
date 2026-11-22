package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.repositories.PurchaseOrderDataRepository;
import tubes.prak.pbo.repositories.PurchaseOrderDataRepository.HistoryRecord;

public class HistoryPurchaseOrder extends JPanel {
    private final PurchaseOrderDataRepository repository;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[] { "Tanggal Pembelian", "Vendor", "Raw Material", "Jumlah", "Harga Aktual",
                    "Status", "Tanggal Diterima", "Created By" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel feedbackLabel = new JLabel(" ");

    public HistoryPurchaseOrder(PurchaseOrderDataRepository repository) {
        this.repository = repository;
        setLayout(new BorderLayout());
        add(feedbackLabel, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);

        try {
            List<HistoryRecord> history = repository.findHistory();
            for (HistoryRecord record : history) {
                model.addRow(record.toTableRow());
            }
            feedbackLabel.setText(" ");
        } catch (SQLException exception) {
            feedbackLabel.setText("Gagal memuat history purchase order: " + exception.getMessage());
        }
    }
}
