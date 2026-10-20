package tubes.prak.pbo.navigation;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.models.User;
import tubes.prak.pbo.view.MainFrame;

public class AppRouter {

    private final AppSession session;
    private final MainFrame mainFrame;

    public AppRouter(AppSession session, MainFrame mainFrame) {
        this.session = session;
        this.mainFrame = mainFrame;
    }

    public void openDashboard(User user) {
        session.start(user);

        if (user.getRole() == Role.MANAGER) {
            mainFrame.showManagerDashboard(user);
        } else if (user.getRole() == Role.PEMBELI) {
            mainFrame.showBuyerDashboard(user);
        } else {
            session.clear();
            mainFrame.showLogin();
        }
    }

    public void logout() {
        session.clear();
        mainFrame.showLogin();
    }
}