package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.Vendor;

public class VendorView extends JPanel {
    public VendorView(List<Vendor> vendors) {
        setLayout(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID Vendor", "Nama Vendor", "Telepon", "Alamat" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Vendor vendor : vendors) {
            model.addRow(new Object[] {
                    vendor.getId_vendor(),
                    vendor.getNamaVendor(),
                    vendor.getTelpVendor(),
                    vendor.getAlamatVendor()
            });
        }

        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }
}
