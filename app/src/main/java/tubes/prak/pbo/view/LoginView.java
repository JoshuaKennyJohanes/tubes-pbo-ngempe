package tubes.prak.pbo.view;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tubes.prak.pbo.components.DefaultButtonOrange;

public class LoginView extends JPanel {

    private final JTextField identifierField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new DefaultButtonOrange("Login");
    private final JLabel messageLabel = new JLabel(" ");

    public LoginView() {
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));

        form.add(new JLabel("Username atau Email"));
        form.add(identifierField);
        form.add(new JLabel("Password"));
        form.add(passwordField);
        form.add(loginButton);
        form.add(messageLabel);

        add(form);
    }

    public String getIdentifier() {
        return identifierField.getText().trim();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    public void clearPassword() {
        passwordField.setText("");
    }

    public void setLoginEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
    }
}