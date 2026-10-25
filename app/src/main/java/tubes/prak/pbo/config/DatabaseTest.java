package tubes.prak.pbo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        // Ambil host dari URL Transaction Pooler Supabase kamu
        String url = "tubes_prak_pbo";
        
        // Username persis seperti yang ada di URL Supabase
        String user = "postgres"; 
        
        // Password database kamu
        String password = "eka@123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Berhasil terhubung ke database local!");
            }
        } catch (SQLException e) {

            System.err.println("Gagal terhubung: " + e.getMessage());
        }
    }
}