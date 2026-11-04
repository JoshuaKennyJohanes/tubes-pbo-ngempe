package tubes.prak.pbo.view.ViewManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class WarehouseView extends JTabbedPane {

    public WarehouseView() {
        // ada fitur tambah item (produk / raw material)
        // ada tab untuk bisa monitor ada produk dan raw material apa saja

        

        JPanel registerItemPanel = new JPanel();
        registerItemPanel.add(new RegisterItemView());
        
        JPanel warehouseMonitor = new JPanel();
        warehouseMonitor.add(new JLabel("Ini tab warhouse monitor"));

        addTab("Register Item", registerItemPanel);
        addTab("Monitor Warehouse", warehouseMonitor);

       
    }
}
