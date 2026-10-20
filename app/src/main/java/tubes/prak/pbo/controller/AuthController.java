package tubes.prak.pbo.controller;

import java.sql.SQLException;
import java.util.Optional;

import javax.swing.SwingWorker;

import tubes.prak.pbo.models.User;
import tubes.prak.pbo.navigation.AppRouter;
import tubes.prak.pbo.service.AuthService;
import tubes.prak.pbo.view.LoginView;

public class AuthController {

    private final LoginView view;
    private final AuthService authService;
    private final AppRouter router;

    public AuthController(
            LoginView view,
            AuthService authService,
            AppRouter router
    ) {
        this.view = view;
        this.authService = authService;
        this.router = router;

        bindEvents();
    }

    private void bindEvents() {
        view.getLoginButton().addActionListener(e -> login());
    }

    private void login() {
        String identifier = view.getIdentifier();
        char[] passwordChars = view.getPassword();

        if (identifier.isBlank() || passwordChars.length == 0) {
            view.showMessage("Username/email dan password wajib diisi.");
            return;
        }

        String password = new String(passwordChars);
        view.setLoginEnabled(false);
        view.showMessage("Memproses login...");

        // Hindari menjalankan query database di Swing Event Dispatch Thread.
        SwingWorker<Optional<User>, Void> worker =
                new SwingWorker<>() {

            @Override
            protected Optional<User> doInBackground()
                    throws SQLException {
                return authService.login(identifier, password);
            }

            @Override
            protected void done() {
                try {
                    Optional<User> result = get();

                    if (result.isPresent()) {
                        router.openDashboard(result.get());
                    } else {
                        view.showMessage(
                            "Login gagal. Periksa username/email dan password."
                        );
                        view.clearPassword();
                    }

                } catch (Exception ex) {

                    ex.printStackTrace();
                    view.showMessage(
                        "Login tidak dapat diproses. Coba lagi nanti."
                    );
                    view.clearPassword();

                    // Catat detail exception melalui logger aplikasi.
                    // Jangan tampilkan kredensial atau detail koneksi ke pengguna.
                } finally {
                    view.setLoginEnabled(true);
                    java.util.Arrays.fill(passwordChars, '\0');
                }
            }
        };

        worker.execute();
    }
}