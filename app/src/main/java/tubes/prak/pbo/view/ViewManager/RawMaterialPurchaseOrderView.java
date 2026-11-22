package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.RawMaterial;

public class RawMaterialPurchaseOrderView extends JPanel {
    public RawMaterialPurchaseOrderView(List<RawMaterial> rawMaterials) {
        setLayout(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Nama Raw Material", "Satuan", "Harga Rata-rata", "Stok", "Batas Kritis" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (RawMaterial rawMaterial : rawMaterials) {
            model.addRow(new Object[] {
                    rawMaterial.getId(),
                    rawMaterial.getNama(),
                    rawMaterial.getSatuan(),
                    rawMaterial.getHargaRata2(),
                    rawMaterial.getJumlahStok(),
                    rawMaterial.getBatasKritis()
            });
        }

        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }
}
