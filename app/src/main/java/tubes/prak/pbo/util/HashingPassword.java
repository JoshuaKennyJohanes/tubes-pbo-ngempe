package tubes.prak.pbo.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashingPassword {
    public static void main(String[]args){
        String password = "12345";
        
        // 1. Generate salt dengan tingkat kerumitan (work factor) = 12
        String salt = BCrypt.gensalt(12);
        
        // 2. Hash password menggunakan salt tersebut
        String hashedPassword = BCrypt.hashpw(password, salt);
        
        // Cetak hasil
        System.out.println("Original Password : " + password);
        System.out.println("Generated Salt    : " + salt);
        System.out.println("Hashed Password   : " + hashedPassword);
        
        // 3. Cara melakukan verifikasi (pencocokan) saat user login
        boolean isMatch = BCrypt.checkpw("12345", hashedPassword);
        System.out.println("Password Valid    : " + isMatch);
    }
}
