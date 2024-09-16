
package ig;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CrearCuenta extends JFrame {
    
    private File selectedImage;
    private String rutaRaizMiniWindows = "C:\\MiniWindows\\Instagram";
    
    public CrearCuenta() {
        setTitle("Crear Cuenta");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon backgroundImageIcon = cargarFondo("/Img/Fondo_Insta.png");
        JLabel background = new JLabel();
        if (backgroundImageIcon != null) {
            background.setIcon(backgroundImageIcon);
        } else {
            background.setBackground(Color.BLACK);
            background.setOpaque(true);
        }
        background.setLayout(new GridBagLayout());

        JPanel registerPanel = new JPanel();
        registerPanel.setPreferredSize(new Dimension(450, 550));
        registerPanel.setBackground(new Color(0, 0, 0, 170));
        registerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Crear Cuenta");
        titleLabel.setFont(new Font("Serif", Font.ITALIC, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 10, 20, 10); 
        registerPanel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setForeground(Color.WHITE);
        JTextField nameText = new JTextField(30);

        JLabel genderLabel = new JLabel("Genero:");
        genderLabel.setForeground(Color.WHITE);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Masculino", "Femenino"});

        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setForeground(Color.WHITE);
        JTextField userText = new JTextField(30);

        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passText = new JPasswordField(30);

        JLabel edadLabel = new JLabel("Edad:");
        edadLabel.setForeground(Color.WHITE);
        JTextField edadText = new JTextField(3);

        JLabel fotoLabel = new JLabel("Foto de Perfil:");
        fotoLabel.setForeground(Color.WHITE);

        JTextField selectedImageName = new JTextField(30);
        selectedImageName.setEditable(false);

        JButton boton_seleccionar = new JButton("Seleccionar Foto");
        boton_seleccionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedImage = fileChooser.getSelectedFile();
                    selectedImageName.setText(selectedImage.getName());
                    JOptionPane.showMessageDialog(null, "Imagen seleccionada: " + selectedImage.getName());
                    fileChooser.setVisible(false);
                }
            }
        });

        JButton boton_crear = new JButton("Crear Cuenta");
        JButton boton_volver = new JButton("Volver");

        boton_crear.setBackground(new Color(255, 0, 0));
        boton_crear.setForeground(Color.WHITE);
        boton_volver.setBackground(new Color(255, 0, 0));
        boton_volver.setForeground(Color.WHITE);

        boton_crear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = nameText.getText();
                String genero = genderComboBox.getSelectedItem().toString();
                String username = userText.getText();
                String password = new String(passText.getPassword());
                int edad = Integer.parseInt(edadText.getText());

                if (crearCuenta(nombre, genero, username, password, edad)) {
                    if (selectedImage != null) {
                        copiarImagenCarpetUsuario(username);
                    }
                    JOptionPane.showMessageDialog(null, "Cuenta creada exitosamente");
                    new MenuPrincipal(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "El usuario ya existe. Intenta con otro nombre");
                }
            }
        });

        boton_volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Insta();
                dispose();
            }
        });

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(nameText, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(genderLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(genderComboBox, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(userText, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(passText, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(edadLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(edadText, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(fotoLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(boton_seleccionar, gbc);

        gbc.gridy = 7;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(selectedImageName, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 0, 0)); 
        buttonPanel.setBackground(new Color(0, 0, 0)); 
        buttonPanel.add(boton_volver);
        buttonPanel.add(boton_crear);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(buttonPanel, gbc);

        background.add(registerPanel, new GridBagConstraints());

        add(background);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                redimensionarImagen(background);
            }
        });

        setVisible(true);
    }

    private ImageIcon cargarFondo(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                return new ImageIcon(icon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH));
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error: Imagen no encontrada");
            return null;
        }
    }

    private void redimensionarImagen(JLabel background) {
        ImageIcon backgroundImageIcon = cargarFondo("/Img/Fondo_Insta.png");
        if (backgroundImageIcon != null) {
            Image scaledImage = backgroundImageIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            background.setIcon(new ImageIcon(scaledImage));
        }
    }

    private boolean crearCuenta(String nombre, String genero, String username, String password, int edad) {
        File carpetaInstagram = new File(rutaRaizMiniWindows);
        if (!carpetaInstagram.exists()) {
            carpetaInstagram.mkdirs();
        }

        File userFile = new File(carpetaInstagram, "users.ins");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            if (existeUsuario(username)) {
                return false;
            }
            writer.write(nombre + "," + genero + "," + username + "," + password + "," + edad + ",true\n");
            
            File userFolder = new File(carpetaInstagram, username);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            new File(userFolder, "followers.ins").createNewFile();
            new File(userFolder, "following.ins").createNewFile();
            new File(userFolder, "insta.ins").createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean existeUsuario(String username) {
        File userFile = new File("users.ins");
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 3 && userData[2].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void copiarImagenCarpetUsuario(String username) {
        File userFolder = new File(username);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        File destFile = new File(userFolder, "profile_picture.png");
        try (InputStream is = new FileInputStream(selectedImage);
             OutputStream os = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
