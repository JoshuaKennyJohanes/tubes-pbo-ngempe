package tubes.prak.pbo.controller;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class NavigationController {
    private static JPanel rootPanel;
    private static CardLayout rootCardLayout;

    private static JPanel contentPanel;
    private static CardLayout contentCardLayout;

    public static void init(JPanel root, CardLayout rootLayout){
        rootPanel = root;
        rootCardLayout = rootLayout;
    }

    public static void setContentPanel(JPanel content, CardLayout layout){
        contentPanel = content;
        contentCardLayout = layout;
    }

    public static void showRootPage(String pageName){
        rootCardLayout.show(rootPanel,pageName);
    }

    public static void showSubPage(String pageName){
        contentCardLayout.show(contentPanel,pageName);
    }
}
