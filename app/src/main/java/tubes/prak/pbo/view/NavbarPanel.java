package tubes.prak.pbo.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.controller.NavigationController;
import tubes.prak.pbo.models.users.UserBuyer;
import tubes.prak.pbo.models.users.UserStaff;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;
import tubes.prak.pbo.view.ViewPembeli.Keranjang;

public class NavbarPanel extends JPanel {

    private static final Dimension BUTTON_SIZE = new Dimension(170, 38);

    private NavbarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(180, 0));
    }

    public static NavbarPanel forBuyer(UserBuyer buyer, Keranjang panelKeranjang) {
        NavbarPanel navbar = new NavbarPanel();

        navbar.addMenuButton("Etalase", () -> NavigationController.showSubPage(Router.ETALASE_PAGE));
        navbar.addMenuButton("Riwayat Transaksi", () -> NavigationController.showSubPage(Router.RIWAYAT_TRANSAKSI));
        navbar.addMenuButton("Keranjang", () -> {
            panelKeranjang.refreshKeranjang(buyer);
            NavigationController.showSubPage(Router.KERANJANG_PAGE);
        });

        navbar.addLogoutButton();
        return navbar;
    }

    public static NavbarPanel forStaff(UserStaff staff) {
        NavbarPanel navbar = new NavbarPanel();

        if (staff.canAccessWarehouse()) {
            navbar.addMenuButton("Warehouse", () -> NavigationController.showSubPage(Router.WAREHOUSE_PAGE));
        }
        if (staff.canAccessProduction()) {
            navbar.addMenuButton("Produksi", () -> NavigationController.showSubPage(Router.PRODUKSI_PAGE));
        }
        if (staff.canAccessBom()) {
            navbar.addMenuButton("BOM Management", () -> NavigationController.showSubPage(Router.BOM_PAGE));
        }
        if (staff.canAccessSalesOrder()) {
            navbar.addMenuButton("Sales Order", () -> NavigationController.showSubPage(Router.SALES_ODER));
        }
        if (staff.canAccessFinance()) {
            navbar.addMenuButton("Finance", () -> NavigationController.showSubPage(Router.FINANCE_PAGE));
        }
        if (staff.canAccessPurchasing()) {
            navbar.addMenuButton("Purchasing", () -> NavigationController.showSubPage(Router.PURCHASE_ORDER));
        }

        navbar.addLogoutButton();
        return navbar;
    }

    private void addMenuButton(String label, Runnable action) {
        DefaultButtonOrange btn = new DefaultButtonOrange(label);
        styleButton(btn);
        btn.addActionListener(e -> action.run());
        add(btn);
        add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void addLogoutButton() {
        DefaultButtonOrange btnLogout = new DefaultButtonOrange("Log Out");
        styleButton(btnLogout);
        btnLogout.addActionListener(e -> {
            Session.logOut();
            NavigationController.showRootPage(Router.LOGIN_PAGE);
        });
        add(Box.createVerticalGlue());
        add(btnLogout);
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(BUTTON_SIZE);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}