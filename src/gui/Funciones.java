
package gui;

import java.io.*;

public class Funciones {

    public File dirActual;
    
    public Funciones(String path) {
        dirActual = new File(path);
    }
    
    public String escribir(String mensaje, String path) {
        File file = new File(path);
        String msj = "";
        
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    FileWriter writer = new FileWriter(file);
                    writer.write(mensaje);
                    writer.flush();
                } catch (IOException e) {
                    msj = "Error al crear";
                }
                
                msj = "Texto Agregado";
                return msj;
            } else {
                msj = "Debe de seleccionar un archivo primero";
                return msj;
            }
        } else {
            msj = "Error: Archivo inexistente";
            return msj;
        }
    }
    
    public String mkdir(String path) {
        String msj = "";
        File folder = new File(path);
        
        if (folder.exists()) {
            msj = "Error: Esta carpeta ya existe";
            return msj;
        } else {
            msj = "Carpeta creada existosamente";
            folder.mkdir();
            return msj;
        }
    }

    public String cd(String path) {
        if (path.charAt(0) != '/') {
            File newDir = new File(dirActual.getAbsolutePath() + "/" + path);
            
            if (!newDir.isDirectory()) {
                return "Error: La direccion tiene que ser una carpeta";
            }
            
            dirActual = newDir;
            return "";
        }
        
        dirActual = new File(path);
        return "";
    }

    public void vaciar(File vacioFile) {
        if (vacioFile.isDirectory()) {
            for (File fileVacio : vacioFile.listFiles()) {
                fileVacio.delete();
            }
        }
    }
    
    public String eliminar(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    vaciar(f);
                    f.delete();
                } else {
                    f.delete();
                }
            }
            file.delete();
            return "Carpeta eliminada";
        }
        
        if (file.isFile()) {
            file.delete();
            return "Archivo eliminado";
        }
        return "Error";
    }
    
    public String getPath() {
        try {
            return dirActual.getCanonicalPath();
        } catch (IOException e) {
            return dirActual.getAbsolutePath();
        }
    }
    
    public String mfile(String path) {
        String msj = "";
        File file = new File(path);
        
        if (file.exists()) {
            msj = "Error: Este archivo ya existe";
            return msj;
        } else {
            msj = "Archivo creado existosamente";
            try {
                file.createNewFile();
            } catch (IOException e) {
                msj = "Error: no se pudo crear";
            }
            return msj;
        }
    }
    
    public static String leer(String path) {
        File file = new File(path);
        String msj = "";

        if (file.exists()) {
            if (file.isFile()) {
                try {
                    FileReader reader = new FileReader(file);
                    String contenido = "";
                    for (int i = reader.read(); i != -1; i = reader.read()) {
                        contenido += (char) i;
                    }
                    return contenido;
                } catch (IOException e) {
                    msj = "Error: No se pudo leer";
                    return msj;
                }
            } else {
                msj = "Error: Debe seleccionar un archivo";
                return msj;
            }
        } else {
            msj = "Error: Archivo inexistente";
            return msj;
        }
    }
    
    public static String listar(String path) {
        String list = "";
        File listFile = new File(path);
        
        if (listFile.isDirectory()) {
            for (File file : listFile.listFiles()) {
                list += "\n ->" + file.getName();
            }
            return list;
        } else {
            return "Error: Debe seleccionar un directorio";
        }
    }
}
