package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.users.User;
import tubes.prak.pbo.models.users.UserBuyer;
import tubes.prak.pbo.models.users.UserManager;
import tubes.prak.pbo.models.users.UserWorker;

public class UserRepository {

    public User findUser(String username, String password) throws SQLException {

        // 1. Ambil data user berdasarkan username saja (sertakan kolom password_hash)
        String sql = """
                SELECT id_user, nama, email, role, username, password_hash
                FROM public.users
                WHERE username = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                
                // Jika username tidak ditemukan
                if (!rs.next()) {
                    return null;
                }

                // 2. Ambil hash password yang tersimpan di DB
                String storedHash = rs.getString("password_hash");

                // 3. Verifikasi password mentah dari input dengan hash di DB
                if (BCrypt.checkpw(password, storedHash)) {
                    int id = rs.getInt("id_user");
                    String nama = rs.getString("nama");
                    String email = rs.getString("email");
                    Role role = Role.valueOf(rs.getString("role"));
                    String uname = rs.getString("username");

                    return switch (role) {
                        case PEMBELI -> new UserBuyer(id, nama, email, uname, storedHash);
                        case MANAGER -> new UserManager(id, nama, email, uname, storedHash);
                        case WORKER  -> new UserWorker(id, nama, email, uname, storedHash);
                    };
                } else {
                    return null;
                }
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

    public void registerUser(String namaLengkap, String email, String alamat, String username, String password, Role role) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();

        String sql = """
                    INSERT INTO users (nama, alamat, email, username, password_hash, role)
                    VALUES ( ? , ? , ? , ? , ? , ? );
                """;
                
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, namaLengkap);
                    stmt.setString(2, alamat);
                    stmt.setString(3, email);
                    stmt.setString(4, username);
                    stmt.setString(5, password);
                     
                    stmt.setObject(6, role, java.sql.Types.OTHER);
                    stmt.executeUpdate();                
    }
}