
package ig;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class Comentar {

    private JPanel panel;
    private JTextArea campoComentario;
    private JTextArea areaComentarios;

    public Comentar(String usuario) {
        panel = new JPanel();
        panel.setLayout(null);

        JLabel etiquetaComentario = new JLabel("Comentar:");
        etiquetaComentario.setFont(new Font("Arial", Font.BOLD, 20));
        etiquetaComentario.setBounds(50, 20, 150, 40);
        panel.add(etiquetaComentario);

        campoComentario = new JTextArea();
        campoComentario.setLineWrap(true);
        campoComentario.setWrapStyleWord(true);
        campoComentario.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane scrollComentario = new JScrollPane(campoComentario);
        scrollComentario.setBounds(50, 70, 1000, 200);
        panel.add(scrollComentario);

        JButton botonPublicar = new JButton("Postear Comentario");
        botonPublicar.setBackground(new Color(255, 51, 51));
        botonPublicar.setForeground(Color.WHITE);
        botonPublicar.setFocusPainted(false);
        botonPublicar.setBorderPainted(false);
        botonPublicar.setFont(new Font("Arial", Font.BOLD, 16));
        botonPublicar.setBounds(50, 275, 1000, 50);
        panel.add(botonPublicar);

        areaComentarios = new JTextArea();
        areaComentarios.setEditable(false);
        areaComentarios.setLineWrap(true);
        areaComentarios.setWrapStyleWord(true);
        areaComentarios.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane scrollAreaComentarios = new JScrollPane(areaComentarios);
        scrollAreaComentarios.setBounds(50, 330, 1000, 500);
        panel.add(scrollAreaComentarios);

        botonPublicar.addActionListener(e -> {
            String comentario = campoComentario.getText().trim();
            if (!comentario.isEmpty()) {
                if (!comentarioDuplicado(usuario, comentario)) {
                    publicarComentario(usuario, comentario);
                    mostrarComentarios(usuario);
                    JOptionPane.showMessageDialog(null, "Comentario enviado");
                    campoComentario.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "El comentario ya existe.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El comentario no puede estar vacio.");
            }
        });

        mostrarComentarios(usuario);
    }

    public JPanel getPanel() {
        return panel;
    }

    private boolean comentarioDuplicado(String usuario, String comentario) {
        String fechaFormateada = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String comentarioCompleto = usuario + " escribio: \"" + comentario + "\" el [" + fechaFormateada + "]\n";
        File archivoInsta = new File(usuario + "/insta.ins");

        return comentarioDuplicadoEnArchivo(archivoInsta, comentarioCompleto);
    }

    private void publicarComentario(String usuario, String comentario) {
        if (!usuarioActivo(usuario)) {
            JOptionPane.showMessageDialog(null, "No puedes comentar con una cuenta desactivada.");
            return;
        }

        String fechaFormateada = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String comentarioCompleto = usuario + " escribio: \"" + comentario + "\" el [" + fechaFormateada + "]\n";

        File archivoInsta = new File(usuario + "/insta.ins");
        if (!comentarioDuplicadoEnArchivo(archivoInsta, comentarioCompleto)) {
            escribirComentario(archivoInsta, comentarioCompleto);
        }

        ArrayList<String> seguidores = obtenerSeguidores(usuario);
        for (String seguidor : seguidores) {
            if (!seguidor.equals(usuario)) {
                File archivoSeguidor = new File(seguidor + "/insta.ins");
                if (!comentarioDuplicadoEnArchivo(archivoSeguidor, comentarioCompleto)) {
                    escribirComentario(archivoSeguidor, comentarioCompleto);
                }
            }
        }
    }

    private void escribirComentario(File archivo, String comentario) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, true))) {
            escritor.write(comentario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean comentarioDuplicadoEnArchivo(File archivo, String comentario) {
        if (archivo.exists()) {
            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    if (linea.trim().equalsIgnoreCase(comentario.trim())) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void mostrarComentarios(String usuario) {
        ArrayList<String> comentarios = new ArrayList<>();
        Set<String> comentariosUnicos = new HashSet<>();

        if (usuarioActivo(usuario)) {
            comentarios.addAll(obtenerComentarios(usuario));
        }

        ArrayList<String> siguiendo = obtenerSiguiendo(usuario);
        for (String seguido : siguiendo) {
            if (usuarioActivo(seguido)) {
                comentarios.addAll(obtenerComentarios(seguido));
            }
        }

        Collections.reverse(comentarios);

        areaComentarios.setText("");
        for (String comentario : comentarios) {
            if (!comentariosUnicos.contains(comentario)) {
                areaComentarios.append(comentario + "\n\n");
                comentariosUnicos.add(comentario);
            }
        }
    }

    private boolean usuarioActivo(String usuario) {
        File archivoUsuarios = new File("users.ins");
        try (BufferedReader lector = new BufferedReader(new FileReader(archivoUsuarios))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datosUsuario = linea.split(",");
                if (datosUsuario.length >= 6 && datosUsuario[2].trim().equalsIgnoreCase(usuario.trim())) {
                    return Boolean.parseBoolean(datosUsuario[5].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<String> obtenerComentarios(String usuario) {
        ArrayList<String> comentarios = new ArrayList<>();
        File archivoInsta = new File(usuario + "/insta.ins");
        if (archivoInsta.exists()) {
            try (BufferedReader lector = new BufferedReader(new FileReader(archivoInsta))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    comentarios.add(linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return comentarios;
    }

    private ArrayList<String> obtenerSiguiendo(String usuario) {
        ArrayList<String> siguiendo = new ArrayList<>();
        File archivoSiguiendo = new File(usuario + "/following.ins");
        if (archivoSiguiendo.exists()) {
            try (BufferedReader lector = new BufferedReader(new FileReader(archivoSiguiendo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    siguiendo.add(linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return siguiendo;
    }

    private ArrayList<String> obtenerSeguidores(String usuario) {
        ArrayList<String> seguidores = new ArrayList<>();
        File archivoSeguidores = new File(usuario + "/followers.ins");
        if (archivoSeguidores.exists()) {
            try (BufferedReader lector = new BufferedReader(new FileReader(archivoSeguidores))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    seguidores.add(linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return seguidores;
    }
}
