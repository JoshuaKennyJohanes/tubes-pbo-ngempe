package tubes.prak.pbo.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashingPassword {
    public static String getHashedPass(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }


}
