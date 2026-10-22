package tubes.prak.pbo.controller;

import tubes.prak.pbo.repositories.UserRepository;
import tubes.prak.pbo.view.LoginView;

public class LoginController {
    

    public LoginController(LoginView loginView, UserRepository userRepository){
        loginView.getPassword();

    }
}
