package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.User;

public class UserRepository {

    
public Optional<User> findByLoginIdentifier(String identifier)
        throws SQLException {

    String sql = """
        SELECT id_user, nama, email, role, username, password_hash
        FROM public.users
        WHERE LOWER(username) = LOWER(?)
           OR LOWER(email) = LOWER(?)
        LIMIT 1
        """;

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String loginIdentifier = identifier.trim();

        stmt.setString(1, loginIdentifier);
        stmt.setString(2, loginIdentifier);

        try (ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return Optional.empty();
            }

            User user = new User(
                rs.getInt("id_user"),
                rs.getString("nama"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                rs.getString("username"),
                rs.getString("password_hash")
            );

            return Optional.of(user);
        }
    }
}


    public Optional<String> findPasswordHash(String identifier)
            throws SQLException {

        String sql = """
            SELECT password_hash
            FROM public.users
            WHERE username = ? OR email = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifier);
            stmt.setString(2, identifier);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(rs.getString("password_hash"));
            }
        }
    }
}