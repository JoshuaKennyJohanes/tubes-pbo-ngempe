package tubes.prak.pbo.view.ViewManager;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JTabbedPane;

import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.models.Vendor;
import tubes.prak.pbo.repositories.PurchaseOrderDataRepository;

public class PurchaseOrderView extends JTabbedPane {
    public PurchaseOrderView() throws SQLException {
        PurchaseOrderDataRepository repository = new PurchaseOrderDataRepository();
        List<Vendor> vendors = repository.findVendors();
        List<RawMaterial> rawMaterials = repository.findRawMaterials();
        HistoryPurchaseOrder historyView = new HistoryPurchaseOrder(repository);

        addTab("Form Pembelian",
                new PurchaseOrderFormView(vendors, rawMaterials, repository, historyView::refreshData));
        addTab("Vendor", new VendorView(vendors));
        addTab("Raw Material", new RawMaterialPurchaseOrderView(rawMaterials));
        addTab("History Purchase Order", historyView);

        addChangeListener(event -> {
            if (getSelectedComponent() == historyView) {
                historyView.refreshData();
            }
        });
    }
}
