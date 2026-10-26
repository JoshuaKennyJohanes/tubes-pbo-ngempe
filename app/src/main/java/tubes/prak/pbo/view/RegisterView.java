package tubes.prak.pbo.view;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tubes.prak.pbo.ENUM.Role;
import tubes.prak.pbo.components.DefaultButtonOrange;
import tubes.prak.pbo.controller.RegisterController;
import tubes.prak.pbo.exception.InvalidCredentialsException;
import tubes.prak.pbo.util.Router;


public class RegisterView extends JPanel {
    private final RegisterController registerController = new RegisterController();

    private final JTextField txtNamaLengkap = new JTextField(20);
    private final JTextField txtUserName = new JTextField(20);
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtAlamat = new JTextField();  
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton registerButton = new DefaultButtonOrange("Register");
    private final JLabel   messagelabel = new JLabel(" ");
    private final JComboBox<Role> roleDropdown = new JComboBox<>(Role.values());
    private final Role managerRole = Role.MANAGER;

    public RegisterView(JPanel mainPanel, CardLayout cardLayout){
        
        setLayout(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Nama Lengkap"));
        form.add(txtNamaLengkap);
        form.add(new JLabel("Email"));
        form.add(txtEmail);
        form.add(new JLabel("Alamat"));
        form.add(txtAlamat);
        form.add(new JLabel("Username"));
        form.add(txtUserName);
        form.add(new JLabel("Password"));


        // //fitur dropdown hanya ketika session aktif dengan user ber role manager
        // if(Session.getSession().getRole().equals(managerRole)){
        //     form.add(roleDropdown);
        // }

        form.add(txtPassword);
        form.add(registerButton);
        form.add(messagelabel);
        
        add(form);
        
        handleRegisterButton(registerButton, mainPanel, cardLayout);



    }

    public void handleRegisterButton(JButton registerButton, JPanel mainPanel, CardLayout cardLayout){

        registerButton.addActionListener(e->{
            String namaLengkap = txtNamaLengkap.getText();
            String username = txtUserName.getText();
            String password = new String(txtPassword.getPassword());
            String email = txtEmail.getText();
            String alamat = txtAlamat.getText();

            try {
                

                registerController.register(namaLengkap, username, password, email, alamat, Role.PEMBELI);

                txtUserName.setText("");
                txtPassword.setText("");

                //ALIHKAN KE LOGIN PAGE LAGI, TAPI KALAU BERHASIL TUNJUKAN JOPTION PANE

                JOptionPane.showMessageDialog(RegisterView.this, "Anda sudah berhasil sign up","Registrasi Sukses",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, Router.LOGIN_PAGE);
            }
            catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(RegisterView.this, ex.getMessage(), "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            }
            catch (InvalidCredentialsException invd){
                JOptionPane.showMessageDialog(RegisterView.this, invd.getMessage(),"Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception ex){
                JOptionPane.showMessageDialog(RegisterView.this, "Terjadi Kesalahan" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

          });

            
        }




    }


