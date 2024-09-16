
package ig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;

public class Insta extends JFrame {

    private JLabel imagen;

    public Insta() {
        setTitle("INSTAGRAM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        imagen = new JLabel(new ImageIcon(getClass().getResource("/Img/Fondo_Insta.png")));
        imagen.setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(500, 650));
        loginPanel.setBackground(new Color(0, 0, 0, 170));
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel instagramLabel = new JLabel("Instagram");
        instagramLabel.setFont(new Font("Serif", Font.ITALIC, 48));
        instagramLabel.setForeground(Color.WHITE);
        instagramLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setForeground(Color.WHITE);
        JTextField userText = new JTextField();
        userText.setPreferredSize(new Dimension(220, 25));

        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passText = new JPasswordField();
        passText.setPreferredSize(new Dimension(220, 25));

        JButton loginButton = new JButton("Log In");
        loginButton.setBackground(new Color(255, 0, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(250, 30));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setBackground(new Color(0, 0, 0, 0));
        JLabel noAccountLabel = new JLabel("¿No tienes cuenta? ");
        noAccountLabel.setForeground(Color.WHITE);

        JButton createAccountButton = new JButton("Crear Cuenta");
        createAccountButton.setBackground(new Color(255, 0, 0));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setPreferredSize(new Dimension(150, 30));
        createAccountButton.setFocusPainted(false);
        createAccountButton.setHorizontalAlignment(SwingConstants.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passText.getPassword());

                if (autenticarUsuario(username, password)) {
                    new MenuPrincipal(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Login incorrecto. Intenta de nuevo");
                }
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CrearCuenta();
                dispose();
            }
        });

        bottomPanel.add(noAccountLabel);
        bottomPanel.add(createAccountButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(instagramLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(passText, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1;
        loginPanel.add(bottomPanel, gbc);

        imagen.add(loginPanel, new GridBagConstraints());

        add(imagen);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                redimensionarImagen();
            }
        });

        setVisible(true);
    }

    private boolean autenticarUsuario(String username, String password) {
        File userFile = new File("users.ins");
        if (!userFile.exists()) {
            JOptionPane.showMessageDialog(null, "No hay usuarios registrados");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 4 && userData[2].equals(username) && userData[3].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void redimensionarImagen() {
        ImageIcon backgroundImageIcon = new ImageIcon(getClass().getResource("/Img/Fondo_Insta.png"));
        Image scaledImage = backgroundImageIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        imagen.setIcon(new ImageIcon(scaledImage));
    }

    public static void main(String[] args) {
        new Insta();
    }
}
