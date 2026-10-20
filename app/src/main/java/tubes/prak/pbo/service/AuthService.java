package tubes.prak.pbo.service;

import tubes.prak.pbo.models.User;
import tubes.prak.pbo.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String identifier, String password)
            throws SQLException {

        if (identifier == null || identifier.isBlank()
                || password == null || password.isBlank()) {
            return Optional.empty();
        }

        Optional<String> hash =
                userRepository.findPasswordHash(identifier.trim());

        if (hash.isEmpty()) {
            return Optional.empty();
        }

        boolean passwordValid;

        try {
            passwordValid = BCrypt.checkpw(password, hash.get());
        } catch (IllegalArgumentException ex) {
            // Hash tidak valid atau formatnya tidak didukung.
            return Optional.empty();
        }

        if (!passwordValid) {
            return Optional.empty();
        }

        return userRepository.findByLoginIdentifier(identifier.trim());
    }
}