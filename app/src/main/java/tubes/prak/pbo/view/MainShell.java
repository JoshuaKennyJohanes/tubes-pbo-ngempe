package tubes.prak.pbo.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.sql.SQLException;
import javax.swing.JPanel;

import tubes.prak.pbo.Interfaces.ItemService;
import tubes.prak.pbo.Interfaces.ProductionService;
import tubes.prak.pbo.Interfaces.UserPanelVisitor;
import tubes.prak.pbo.controller.ItemController;
import tubes.prak.pbo.controller.NavigationController;
import tubes.prak.pbo.controller.ProduksiController;
import tubes.prak.pbo.models.users.User;
import tubes.prak.pbo.models.users.UserBuyer;
import tubes.prak.pbo.models.users.UserStaff;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.view.ViewManager.*;
import tubes.prak.pbo.view.ViewPembeli.EtalaseView;
import tubes.prak.pbo.view.ViewPembeli.Keranjang;
import tubes.prak.pbo.view.ViewPembeli.RiwayatTransaksiView;

public class MainShell extends JPanel implements UserPanelVisitor {
    private final JPanel mainContentPanel;
    private final CardLayout contentCardLayout;
    private NavbarPanel navbarPanel;

    public MainShell(User user) throws SQLException {
        setLayout(new BorderLayout());

        contentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);
        NavigationController.setContentPanel(mainContentPanel, contentCardLayout);

        // Polimorfisme: User menentukan sendiri panel apa yang dibangun
        user.accept(this);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    @Override
    public void buildForBuyer(UserBuyer buyer) throws SQLException {
        Keranjang panelKeranjang = new Keranjang(buyer);
        RiwayatTransaksiView dataRiwayat = new RiwayatTransaksiView(buyer);

        mainContentPanel.add(new EtalaseView(
                mainContentPanel,
                contentCardLayout,
                buyer,
                panelKeranjang,
                new ItemController()
        ), Router.ETALASE_PAGE);
        mainContentPanel.add(panelKeranjang, Router.KERANJANG_PAGE);
        mainContentPanel.add(dataRiwayat, Router.RIWAYAT_TRANSAKSI);

        this.navbarPanel = NavbarPanel.forBuyer(buyer, panelKeranjang);
    }

    @Override
    public void buildForStaff(UserStaff staff) throws SQLException {
        WarehouseView warehouseView = null;
        if (staff.canAccessWarehouse()) {
            warehouseView = new WarehouseView();
            mainContentPanel.add(warehouseView, Router.WAREHOUSE_PAGE);
        }
        if (staff.canAccessProduction()) {
            ItemService itemService = new ItemController();
            ProductionService productionService = new ProduksiController(itemService);
            mainContentPanel.add(
                    new ProduksiView(itemService, productionService),
                    Router.PRODUKSI_PAGE
            );
        }
        if (staff.canAccessBom()) {
            mainContentPanel.add(new BomView(), Router.BOM_PAGE);
        }
        if (staff.canAccessSalesOrder()) {
            mainContentPanel.add(new SalesOrderView(), Router.SALES_ODER);
        }
        if (staff.canAccessFinance()) {
            mainContentPanel.add(new FinanceView(), Router.FINANCE_PAGE);
        }
        if (staff.canAccessPurchasing()) {
            mainContentPanel.add(new PurchaseOrderView(), Router.PURCHASE_ORDER);
        }

        this.navbarPanel = NavbarPanel.forStaff(
                staff,
                warehouseView == null ? null : warehouseView::refreshData
        );
    }

    public void buildNavbarAndDefaultPanel(User user) {
        contentCardLayout.show(mainContentPanel, user.getDefaultRoute());
        add(navbarPanel, BorderLayout.WEST);
        revalidate();
        repaint();
    }
}