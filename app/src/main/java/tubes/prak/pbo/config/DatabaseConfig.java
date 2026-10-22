package tubes.prak.pbo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    // DB_URL=jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:6543/postgres
    // DB_USER=postgres.rbfzvkkiterzowlwsbsg
    // DB_PASSWORD=prakpbodatabase

    private static final String URL = "jdbc:postgresql://localhost:5432/tubes_prak_pbo";

    private static final String USER = "postgres";

    private static final String PASSWORD = "12345";

    private DatabaseConfig() {
        // Mencegah class ini dibuat sebagai objek.
    }

    public static Connection getConnection() throws SQLException {
    if (URL == null || USER == null || PASSWORD == null) {
        System.err.println("DB_URL tersedia: " + (URL != null));
        System.err.println("DB_USER tersedia: " + (USER != null));
        System.err.println("DB_PASSWORD tersedia: " + (PASSWORD != null));

        throw new IllegalStateException(
            "Environment variable database belum diatur."
        );
    }

    return DriverManager.getConnection(URL, USER, PASSWORD);
}
}