package tubes.prak.pbo.view.ViewManager;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class WarehouseView extends JTabbedPane {

    public WarehouseView() {
        // ada fitur tambah item (produk / raw material)
        // ada tab untuk bisa monitor ada produk dan raw material apa saja

        

         
        JPanel registerItemPanel = new JPanel();
        registerItemPanel.add(new RegisterItemView());
        
        WarehouseMonitorView warehouseMonitor = new WarehouseMonitorView();
        BomView bomView = new BomView();

        addTab("Register Item", registerItemPanel);
        addTab("Monitor Warehouse", warehouseMonitor);
        addTab("Bill Of Materials Entry", bomView);

        this.addChangeListener(e -> {
            // Index 1 adalah tab "Monitor Warehouse"
            if (getSelectedIndex() == 1) {
                warehouseMonitor.refreshData(); // Panggil method refreshData() yang sudah kamu buat!
            }

            if (getSelectedIndex() ==2 ){
                bomView.loadMasterData();
            }

        });

       
    }
}
