package tubes.prak.pbo.models;

import tubes.prak.pbo.ENUM.Role;

public class User {
    private int id;
    private String nama;
    private String email;
    private Role role;
    private String userName;
    private String password;



    public User(int id, String nama, String email, Role role, String userName, String password){
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.role = role;
        this.userName = userName;
        this.password = password;
        
    }

    public int getId(){
        return this.id;
    }

    public String getNama(){
        return this.nama;
    }

    public String getEmail(){
        return this.email;
    }

    public Role getRole(){
        return this.role;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getPasswword(){
        return this.password;
    }

}
