package tubes.prak.pbo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    private static final String URL =
        System.getenv("DB_URL");

    private static final String USER =
        System.getenv("DB_USER");

    private static final String PASSWORD =
        System.getenv("DB_PASSWORD");

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