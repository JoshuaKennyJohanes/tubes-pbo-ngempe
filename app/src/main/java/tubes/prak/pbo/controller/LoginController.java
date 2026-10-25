package tubes.prak.pbo.controller;

import java.sql.SQLException;

import tubes.prak.pbo.exception.InvalidCredentialsException;
import tubes.prak.pbo.models.User;
import tubes.prak.pbo.repositories.UserRepository;
import tubes.prak.pbo.util.Session;



public class  LoginController {
    UserRepository userRepository = new UserRepository();

    public void login(String userName, String password) throws IllegalArgumentException, InvalidCredentialsException, SQLException{
        System.out.println(userName + password);

        if(userName==null || userName.trim().isEmpty()){
            throw new IllegalArgumentException("Username tidak boleh kosong!");
        }

        if(password==null || password.trim().isEmpty()){
            throw new InvalidCredentialsException("Password Salah coba lagi");
        }
        
        User user = userRepository.findUser(userName, password);

        if(user==null){
            throw new InvalidCredentialsException("Username atau password salah");
        }

        Session.setSession(user);
    }
}
