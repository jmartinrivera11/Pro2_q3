
package sistema;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;

public class Usuario implements Serializable {
    private String username;
    private String password;
    private File directorioUsuario;
    private boolean esAdmin;
    
    public Usuario(String username, String password, boolean esAdmin) throws IOException {
        this.username = username;
        this.password = password;
        this.esAdmin = esAdmin;
        this.directorioUsuario = new File("C:\\MiniWindows\\" + username);
        
        if (!directorioUsuario.exists()) {
            if (directorioUsuario.mkdirs()) {
                System.out.println("Directorio creado para el usuario: " + username);
                crearDirectoriosBasicos();
            } else {
                throw new IOException("Error al crear el directorio del usuario.");
            }
        } else {
            System.out.println("El directorio del usuario ya existe.");
        }
    }
    
    private void crearDirectoriosBasicos() {
        new File(directorioUsuario, "Mis Documentos").mkdirs();
        new File(directorioUsuario, "Música").mkdirs();
        new File(directorioUsuario, "Mis Imágenes").mkdirs();
        System.out.println("Directorios básicos creados para el usuario: " + username);
    }
    
    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }
    
    public String getUsername() {
        return username;
    }

    public File getDirectorioUsuario() {
        return directorioUsuario;
    }

    public boolean esAdmin() {
        return esAdmin;
    }
}
