package tubes.prak.pbo.navigation;

import java.util.Optional;

import tubes.prak.pbo.models.User;

public class AppSession {

    private User currentUser;

    public void start(User user) {
        this.currentUser = user;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void clear() {
        currentUser = null;
    }
}