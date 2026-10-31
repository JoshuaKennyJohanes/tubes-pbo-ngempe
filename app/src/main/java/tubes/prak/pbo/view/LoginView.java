package tubes.prak.pbo.view;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.controller.LoginController;
import tubes.prak.pbo.controller.NavigationController;
import tubes.prak.pbo.exception.InvalidCredentialsException;
import tubes.prak.pbo.util.Router;
import tubes.prak.pbo.util.Session;

public class LoginView extends JPanel {
    private final LoginController loginController = new LoginController();


    private final JTextField txtusername = new JTextField(20);
    private final JPasswordField txtpassword = new JPasswordField(20);
    private final JButton loginButton = new DefaultButtonOrange("Login");
    private final JButton signUpButton = new DefaultButtonOrange("Sign Up");
    private final JLabel messageLabel = new JLabel(" ");

    public LoginView(JPanel mainPanel, CardLayout cardLayout) {

        setLayout(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Username atau Email"));
        form.add(txtusername);
        form.add(new JLabel("Password"));
        form.add(txtpassword);
        form.add(loginButton);
        form.add(signUpButton);
        form.add(messageLabel);
        add(form);

        handleLoginButton(loginButton, mainPanel, cardLayout);

        signUpButton.addActionListener(e->{
            cardLayout.show(mainPanel, Router.REGISTER_PAGE);
        });
    }

    public void handleLoginButton(JButton loginButton, JPanel mainPanel, CardLayout cardLayout){
        loginButton.addActionListener(e ->{
            String username = txtusername.getText();
            String password = new String(txtpassword.getPassword());

            
            try {
                loginController.login(username, password);

                txtusername.setText("");
                txtpassword.setText("");

                Role userRole = Session.getSession().getRole();
                System.out.print("Role: "+Session.getSession().getRole());

                //baru buat mainshell panel di action listener login dan tunjukan mainshell.
                MainShell newMainShell = new MainShell();
                mainPanel.add(newMainShell,Router.MAIN_SHELL);
                newMainShell.buildNavbarAndDefaultPanel();
                NavigationController.showRootPage(Router.MAIN_SHELL);
                
            }
            catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(LoginView.this, ex.getMessage(), "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            }
            catch (InvalidCredentialsException invd){
                JOptionPane.showMessageDialog(LoginView.this, invd.getMessage(),"Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception ex){
                JOptionPane.showMessageDialog(LoginView.this, "Terjadi Kesalahan" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

          });
    }

   

    public JButton getLoginButton() {
        return loginButton;
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }


    public void setLoginEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
    }
}