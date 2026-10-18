package tubes.prak.pbo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        // Ambil host dari URL Transaction Pooler Supabase kamu
        String url = "jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:6543/postgres";
        
        // Username persis seperti yang ada di URL Supabase
        String user = "postgres.rbfzvkkiterzowlwsbsg"; 
        
        // Password database kamu
        String password = "prakpbodatabase";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Berhasil terhubung ke database Supabase!");
            }
        } catch (SQLException e) {
            System.err.println("Gagal terhubung: " + e.getMessage());
        }
    }
}