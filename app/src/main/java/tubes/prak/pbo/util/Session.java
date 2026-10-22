package tubes.prak.pbo.util;

import tubes.prak.pbo.models.User;

public class Session {
    private static  User userLoggedIn;

    public static void setSession(User user){
        userLoggedIn = user;
    }

    public static User getSession(){
        return userLoggedIn;
    }

    public static void logOut(){
        userLoggedIn = null;
    }

    public static boolean isLoggedIn(){
        return userLoggedIn != null;
    }
}
