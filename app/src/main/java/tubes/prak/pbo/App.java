package tubes.prak.pbo;

import javax.swing.SwingUtilities;

import tubes.prak.pbo.controller.AuthController;
import tubes.prak.pbo.navigation.AppRouter;
import tubes.prak.pbo.navigation.AppSession;
import tubes.prak.pbo.repositories.UserRepository;
import tubes.prak.pbo.service.AuthService;
import tubes.prak.pbo.view.LoginView;
import tubes.prak.pbo.view.MainFrame;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            LoginView loginView = new LoginView();

            UserRepository userRepository = new UserRepository();
            AuthService authService = new AuthService(userRepository);

            AppSession session = new AppSession();
            AppRouter router =new AppRouter(session, mainFrame);

            mainFrame.setLoginView(loginView);

            new AuthController(
                loginView,
                authService,
                router
            );

            mainFrame.setVisible(true);
        });
    }
}