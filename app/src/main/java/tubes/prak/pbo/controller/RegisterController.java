package tubes.prak.pbo.controller;



import java.sql.SQLException;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.exception.InvalidCredentialsException;
import tubes.prak.pbo.models.User;
import tubes.prak.pbo.repositories.UserRepository;
import tubes.prak.pbo.util.HashingPassword;

public class RegisterController{
    UserRepository userRepository = new UserRepository();

    public void register(String nama, String username, String password, String email, String alamat, Role role) throws InvalidCredentialsException, IllegalArgumentException, SQLException{

        System.out.println(username + password);

       
        if(username==null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username tidak boleh kosong!");
        }

        if(password==null || password.trim().isEmpty()){
            throw new InvalidCredentialsException("Password Salah coba lagi");
        }
        

        //validasi user yang sudah ada
        User user = userRepository.findUser(username, password);
        //jika sudah ada throw exception bahwa username sudah ada
        if(user!=null){
            throw new InvalidCredentialsException("Username sudah ada, pilih username lain!");
        } else {

            //hashing password dengan static util

            String hashedPass = HashingPassword.getHashedPass(password);

            userRepository.registerUser(nama, email, alamat, username, hashedPass, Role.PEMBELI);
        }


        
    }
}