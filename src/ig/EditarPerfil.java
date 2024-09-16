
package ig;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class EditarPerfil {

    private JPanel panel;
    private JTextField text_buscar;

    public EditarPerfil(String username) {
        panel = new JPanel();
        panel.setLayout(null);  

        JLabel searchLabel = new JLabel("Buscar Persona:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setBounds(50, 20, 150, 40);
        panel.add(searchLabel);

        text_buscar = new JTextField();
        text_buscar.setFont(new Font("Arial", Font.PLAIN, 20));
        text_buscar.setBounds(50, 70, 1000, 200);
        text_buscar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(text_buscar);

        JButton boton_buscar = confiBoton("Buscar");
        boton_buscar.setBounds(50, 280, 1000, 50);
        JButton boton_entrarPerfil = confiBoton("Entrar a una cuenta");
        boton_entrarPerfil.setBounds(50, 340, 1000, 50);
        JButton boton_seguir = confiBoton("Seguir");
        boton_seguir.setBounds(50, 400, 1000, 50);
        JButton boton_dejarSeguir = confiBoton("Dejar de Seguir");
        boton_dejarSeguir.setBounds(50, 460, 1000, 50);
        JButton boton_acti_descti = confiBoton("Desactivar - Activar cuenta");
        boton_acti_descti.setBounds(50, 520, 1000, 50);

        panel.add(boton_buscar);
        panel.add(boton_entrarPerfil);
        panel.add(boton_seguir);
        panel.add(boton_dejarSeguir);
        panel.add(boton_acti_descti);

        boton_buscar.addActionListener(e -> {
            String searchText = text_buscar.getText().trim();
            if (!searchText.isEmpty()) {
                buscarPersona(searchText, username);
                text_buscar.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Ingresa un nombre de usuario");
            }
        });

        boton_entrarPerfil.addActionListener(e -> {
            String searchText = text_buscar.getText().trim();
            if (!searchText.isEmpty()) {
                entrarCuenta(searchText, username);
                text_buscar.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Ingresa un nombre de usuario");
            }
        });

        boton_seguir.addActionListener(e -> {
            String searchText = text_buscar.getText().trim();
            if (!searchText.isEmpty()) {
                if (usuarioExiste(searchText)) {
                    seguirUsuario(searchText, username);
                } else {
                    JOptionPane.showMessageDialog(null, "El usuario '" + searchText + "' no existe");
                }
                text_buscar.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Ingresa un nombre de usuario");
            }
        });

        boton_dejarSeguir.addActionListener(e -> {
            String searchText = text_buscar.getText().trim();
            if (!searchText.isEmpty()) {
                if (usuarioExiste(searchText)) {
                    dejarDeSeguirUsuario(searchText, username);
                } else {
                    JOptionPane.showMessageDialog(null, "El usuario '" + searchText + "' no existe.");
                }
                text_buscar.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Ingresa un nombre de usuario");
            }
        });

        boton_acti_descti.addActionListener(e -> {
            desactivarCuenta(username);
            text_buscar.setText("");
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    private void buscarPersona(String searchText, String currentUser) {
        File userFile = new File("users.ins");
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 6 && userData[2].trim().equalsIgnoreCase(searchText.trim())) {
                    boolean isActive = Boolean.parseBoolean(userData[5].trim());
                    if (!isActive) {
                        continue;
                    }
                    found = true;
                    boolean follows = comprobarseguir(currentUser, userData[2]);
                    result.append(userData[2]).append(" - ").append(follows ? "LO SIGUES" : "NO LO SIGUES").append("\n");
                }
            }
            if (found) {
                JOptionPane.showMessageDialog(null, result.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No se encontro el usuario");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean usuarioExiste(String searchText) {
        File userFile = new File("users.ins");
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 6 && userData[2].trim().equalsIgnoreCase(searchText.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void entrarCuenta(String searchText, String currentUser) {
        if (!usuarioExiste(searchText)) {
            JOptionPane.showMessageDialog(null, "El usuario '" + searchText + "' no existe.");
            return;
        }

        File userFile = new File("users.ins");
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 6 && userData[2].trim().equalsIgnoreCase(searchText.trim())) {
                    boolean isActive = Boolean.parseBoolean(userData[5].trim());
                    if (!isActive) {
                        JOptionPane.showMessageDialog(null, "Esta cuenta esta desactivada");
                        return;
                    }

                    found = true;
                    String nombre = userData[0];
                    String genero = userData[1];
                    String username = userData[2];
                    String edad = userData[4];
                    String fechaIngreso = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    int followers = contarFollowers(username);
                    int following = contarFollowing(username);
                    boolean follows = comprobarseguir(currentUser, username);

                    String followStatus = follows ? "DEJAR DE SEGUIR" : "SEGUIR";
                    int followOption = JOptionPane.showConfirmDialog(null,
                            "Nombre: " + nombre + "\nGenero: " + genero + "\nUsername: " + username + "\nEdad: " + edad +
                                    "\nFecha de Ingreso: " + fechaIngreso + "\nFollowers: " + followers + "\nFollowing: " + following + "\n¿" + followStatus + "?",
                            "Perfil de " + username,
                            JOptionPane.YES_NO_OPTION);

                    if (followOption == JOptionPane.YES_OPTION) {
                        if (follows) {
                            dejarDeSeguirUsuario(username, currentUser);
                        } else {
                            seguirUsuario(username, currentUser);
                        }
                    }

                    int verPerfilOption = JOptionPane.showConfirmDialog(null, "¿Deseas ver el perfil de " + username + "?", "Ver Perfil", JOptionPane.YES_NO_OPTION);
                    if (verPerfilOption == JOptionPane.YES_OPTION) {
                        verPerfil(username);
                    }

                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verPerfil(String username) {
        JFrame perfilFrame = new JFrame("Imagenes de " + username);
        perfilFrame.setSize(800, 600);
        perfilFrame.setLocationRelativeTo(null);

        JPanel imagesPanel = new JPanel();
        imagesPanel.setLayout(new GridLayout(0, 3, 10, 10)); 

        File userFolder = new File(username);
        File[] imageFiles = userFolder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg"));

        if (imageFiles != null && imageFiles.length > 0) {
            for (File imageFile : imageFiles) {
                ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
                Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                imagesPanel.add(imageLabel);
            }
        } else {
            JLabel noImagesLabel = new JLabel("No hay imágenes.");
            imagesPanel.add(noImagesLabel);
        }

        JScrollPane scrollPane = new JScrollPane(imagesPanel);
        perfilFrame.add(scrollPane);
        perfilFrame.setVisible(true);
    }

    private void seguirUsuario(String searchUser, String currentUser) {
        if (comprobarseguir(currentUser, searchUser)) {
            JOptionPane.showMessageDialog(null, "Ya sigues a " + searchUser);
            return;
        }

        File followingFile = new File(currentUser + "/following.ins");
        createFileIfNotExists(followingFile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(followingFile, true))) {
            writer.write(searchUser + "\n");
            JOptionPane.showMessageDialog(null, "Ahora sigues a " + searchUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File followersFile = new File(searchUser + "/followers.ins");
        createFileIfNotExists(followersFile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(followersFile, true))) {
            writer.write(currentUser + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        actualizarContadorSeguidos(currentUser);
    }

    private void dejarDeSeguirUsuario(String searchUser, String currentUser) {
        if (!comprobarseguir(currentUser, searchUser)) {
            JOptionPane.showMessageDialog(null, "No sigues a " + searchUser);
            return;
        }

        File followingFile = new File(currentUser + "/following.ins");
        File tempFile = new File(currentUser + "/temp_following.ins");
        createFileIfNotExists(followingFile);

        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(followingFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equalsIgnoreCase(searchUser.trim())) {
                    writer.write(line + "\n");
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            followingFile.delete();
            tempFile.renameTo(followingFile);

            File followersFile = new File(searchUser + "/followers.ins");
            File tempFollowersFile = new File(searchUser + "/temp_followers.ins");
            createFileIfNotExists(followersFile);

            try (BufferedReader followerReader = new BufferedReader(new FileReader(followersFile));
                 BufferedWriter followerWriter = new BufferedWriter(new FileWriter(tempFollowersFile))) {
                String followerLine;
                while ((followerLine = followerReader.readLine()) != null) {
                    if (!followerLine.trim().equalsIgnoreCase(currentUser.trim())) {
                        followerWriter.write(followerLine + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            followersFile.delete();
            tempFollowersFile.renameTo(followersFile);

            JOptionPane.showMessageDialog(null, "Has dejado de seguir a " + searchUser);
            actualizarContadorSeguidos(currentUser);
        } else {
            JOptionPane.showMessageDialog(null, "No sigues a " + searchUser);
        }
    }

    private void desactivarCuenta(String username) {
        File userFile = new File("users.ins");
        File tempFile = new File("temp_users.ins");

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");

                if (userData.length == 6 && !userData[2].trim().isEmpty()) {
                    String userProfile = userData[2].trim();

                    if (userProfile.equalsIgnoreCase(username.trim())) {
                        found = true;
                        boolean isActive = Boolean.parseBoolean(userData[5].trim());

                        if (isActive) {
                            int response = JOptionPane.showConfirmDialog(null,
                                    "¿Estas seguro de que deseas desactivar tu cuenta?",
                                    "Confirmar Desactivacion",
                                    JOptionPane.YES_NO_OPTION);

                            if (response == JOptionPane.YES_OPTION) {
                                userData[5] = "false";
                                JOptionPane.showMessageDialog(null, "Cuenta desactivada.");
                            }
                        } else {
                            userData[5] = "true";
                            JOptionPane.showMessageDialog(null, "Cuenta activada con exito.");
                        }

                        line = String.join(",", userData);
                    }
                }

                writer.write(line + "\n");
            }

            if (!found) {
                JOptionPane.showMessageDialog(null, "No se encontro el usuario  en el archivo");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (userFile.delete()) {
            tempFile.renameTo(userFile);
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo actualizar el archivo de usuarios.");
        }
    }

    private boolean comprobarseguir(String currentUser, String username) {
        File followingFile = new File(currentUser + "/following.ins");
        createFileIfNotExists(followingFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(followingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void actualizarContadorSeguidos(String currentUser) {
        int numSeguidos = contarFollowing(currentUser);
        JOptionPane.showMessageDialog(null, "Seguidos actualizados: " + numSeguidos);
    }

    private int contarFollowers(String username) {
        File followersFile = new File(username + "/followers.ins");
        createFileIfNotExists(followersFile);
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(followersFile))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    private int contarFollowing(String username) {
        File followingFile = new File(username + "/following.ins");
        createFileIfNotExists(followingFile);
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(followingFile))) {
            while ((reader.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    private void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JButton confiBoton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(255, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
}
