
package sistema;

import gui.NavegadorArchivos;

import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;

public class SistemaOperativo {
    private List<Usuario> usuarios;
    private Usuario usuarioActual;

    public SistemaOperativo() {
        verificarDirectorioBase();
        usuarios = new ArrayList<>();
        cargarUsuarios();
    }
    
    public void crearUsuario(String username, String password, boolean esAdmin) throws IOException {
        Usuario nuevoUsuario = new Usuario(username, password, esAdmin);
        usuarios.add(nuevoUsuario);
        guardarUsuarios();
    }
    
    private void verificarDirectorioBase() {
        File directorioBase = new File("C:\\MiniWindows");
        if (!directorioBase.exists()) {
            if (directorioBase.mkdirs()) {
                System.out.println("Directorio base creado: C:\\MiniWindows");
            } else {
                System.out.println("Error al crear el directorio base.");
            }
        }
    }
    
    public boolean login(String username, String password) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username) && usuario.verificarPassword(password)) {
                usuarioActual = usuario;
                return true;
            }
        }
        return false;
    }
    
    private void abrirNavegadorArchivos() {
        if (usuarioActual != null) {
            File directorioUsuario = usuarioActual.getDirectorioUsuario();
            SwingUtilities.invokeLater(() -> {
                NavegadorArchivos navegador = new NavegadorArchivos(directorioUsuario);
                navegador.setVisible(true);
            });
        }
    }
    
    private void cargarUsuarios() {
        File archivoUsuarios = new File("C:\\MiniWindows\\users.sop");

        if (archivoUsuarios.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoUsuarios))) {
                usuarios = (List<Usuario>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error al cargar usuarios: " + e.getMessage());
            }
        }
    }
    
    private void guardarUsuarios() throws IOException {
        File archivoUsuarios = new File("C:\\MiniWindows\\users.sop");
        
        if (!archivoUsuarios.getParentFile().exists()) {
            archivoUsuarios.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoUsuarios))) {
            oos.writeObject(usuarios);
        }
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean usuarioExiste(String username) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public File obtenerCarpetaUsuario(String username) {
        if (usuarioActual != null && usuarioActual.esAdmin()) {
            for (Usuario usuario : usuarios) {
                if (usuario.getUsername().equals(username)) {
                    return usuario.getDirectorioUsuario();
                }
            }
        }
        return null;
    }
}
