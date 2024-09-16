package gui;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;

public class NavegadorArchivos extends JFrame {

    private JTree arbolArchivos;
    private DefaultMutableTreeNode nodoSeleccionado;
    private File directorioRaiz;
    private File archivoCopiado = null;
    private boolean cortar = false;

    public NavegadorArchivos(File directorioRaiz) {
        this.directorioRaiz = directorioRaiz;
        setTitle("Navegador de Archivos");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setUIFont(new javax.swing.plaf.FontUIResource("Consolas", Font.PLAIN, 14));

        DefaultMutableTreeNode raiz = crearNodoArbol(directorioRaiz);
        arbolArchivos = new JTree(raiz);
        arbolArchivos.setFont(new Font("Consolas", Font.PLAIN, 14));
        arbolArchivos.setRowHeight(24);
        arbolArchivos.setBackground(new Color(60, 63, 65));
        arbolArchivos.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(arbolArchivos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        JPopupMenu menuContextual = crearMenuContextual();
        arbolArchivos.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = arbolArchivos.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1 && SwingUtilities.isRightMouseButton(e)) {
                    TreePath selPath = arbolArchivos.getPathForLocation(e.getX(), e.getY());
                    nodoSeleccionado = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    menuContextual.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        setLocationRelativeTo(null);
    }

    private JPopupMenu crearMenuContextual() {
        JPopupMenu menuContextual = new JPopupMenu();
        String[] opciones = {"Crear Archivo", "Crear Carpeta", "Eliminar", "Renombrar", "Registrar Datos", "Organizar Archivos", "Organizar por Carpetas", "Copiar", "Pegar"};

        for (String opcion : opciones) {
            JMenuItem item = new JMenuItem(opcion);
            item.setFont(new Font("Consolas", Font.PLAIN, 14));
            item.setBackground(new Color(75, 75, 75));
            item.setForeground(Color.WHITE);
            
            item.addActionListener(e -> manejarAccion(opcion));

            menuContextual.add(item);
        }
        return menuContextual;
    }

    private void manejarAccion(String opcion) {
        switch (opcion) {
            case "Crear Archivo":
                crearArchivo();
                break;
            case "Crear Carpeta":
                crearCarpeta();
                break;
            case "Eliminar":
                eliminar();
                break;
            case "Renombrar":
                renombrar();
                break;
            case "Registrar Datos":
                registrarDatos();
                break;
            case "Organizar Archivos":
                organizarArchivos();
                break;
            case "Organizar por Carpetas":
                organizarPorCarpetas();
                break;
            case "Copiar":
                copiar();
                break;
            case "Pegar":
                pegar();
                break;
            default:
                break;
        }
    }

    private void crearArchivo() {
        File directorio = directorioSeleccionado();
        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            JOptionPane.showMessageDialog(this, "La ruta especificada no existe o no es un directorio.");
            return;
        }

        String nombreArchivo = JOptionPane.showInputDialog("Nombre del nuevo archivo:");
        if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
            File archivo = new File(directorio, nombreArchivo);
            try {
                if (archivo.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Archivo creado: " + archivo.getName());
                    recargarArbol();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo crear el archivo. Verifique el nombre o permisos.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al crear el archivo: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "El nombre del archivo no puede estar vacío.");
        }
    }

    private void crearCarpeta() {
        File directorio = directorioSeleccionado();
        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            JOptionPane.showMessageDialog(this, "La ruta especificada no existe o no es un directorio.");
            return;
        }

        String nombreCarpeta = JOptionPane.showInputDialog("Nombre de la nueva carpeta:");
        if (nombreCarpeta != null && !nombreCarpeta.trim().isEmpty()) {
            File carpeta = new File(directorio, nombreCarpeta);
            if (carpeta.mkdirs()) {
                JOptionPane.showMessageDialog(this, "Carpeta creada: " + carpeta.getName());
                recargarArbol();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la carpeta.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "El nombre de la carpeta no puede estar vacío.");
        }
    }

    private void eliminar() {
        File archivo = directorioSeleccionado();
        if (archivo != null && archivo.exists()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas eliminar " + archivo.getName() + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    eliminarRecursivo(archivo);
                    JOptionPane.showMessageDialog(this, "Archivo/Carpeta eliminado.");
                    recargarArbol();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar archivo/carpeta: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "El archivo/carpeta no existe.");
        }
    }

    private void eliminarRecursivo(File archivo) throws IOException {
        if (archivo.isDirectory()) {
            File[] archivos = archivo.listFiles();
            if (archivos != null) {
                for (File subArchivo : archivos) {
                    eliminarRecursivo(subArchivo);
                }
            }
        }
        if (!archivo.delete()) {
            throw new IOException("No se pudo eliminar: " + archivo.getAbsolutePath());
        }
    }

    private void renombrar() {
        File archivo = directorioSeleccionado();
        if (archivo == null || !archivo.exists()) {
            JOptionPane.showMessageDialog(this, "El archivo/carpeta no existe.");
            return;
        }

        String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:");
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            File nuevoArchivo = new File(archivo.getParent(), nuevoNombre);
            if (archivo.renameTo(nuevoArchivo)) {
                JOptionPane.showMessageDialog(this, "Archivo renombrado a: " + nuevoArchivo.getName());
                recargarArbol();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo renombrar el archivo/carpeta.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "El nuevo nombre no puede estar vacío.");
        }
    }

    private void registrarDatos() {
        File archivo = directorioSeleccionado();
        if (archivo != null && archivo.isFile()) {
            String texto = JOptionPane.showInputDialog("Escribe el texto a registrar:");
            if (texto != null && !texto.trim().isEmpty()) {
                try (FileWriter writer = new FileWriter(archivo, true)) {
                    writer.write(texto + "\n");
                    JOptionPane.showMessageDialog(this, "Datos registrados.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al escribir en el archivo: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "El texto no puede estar vacío.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un archivo válido para escribir.");
        }
    }

    private void copiar() {
        File seleccionado = directorioSeleccionado();
        if (seleccionado != null && seleccionado.exists()) {
            archivoCopiado = seleccionado;
            cortar = false;
            JOptionPane.showMessageDialog(this, "Archivo/Carpeta copiado: " + archivoCopiado.getName());
        } else {
            JOptionPane.showMessageDialog(this, "No se seleccionó ningún archivo o carpeta.");
        }
    }

    private void pegar() {
        if (archivoCopiado == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún archivo/carpeta para pegar.");
            return;
        }

        File directorioDestino = directorioSeleccionado();
        if (directorioDestino == null || !directorioDestino.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Selecciona un directorio válido para pegar.");
            return;
        }

        File archivoPegado = new File(directorioDestino, archivoCopiado.getName());
        if (archivoPegado.exists()) {
            archivoPegado = new File(directorioDestino, generarNombreUnico(archivoPegado));
        }

        try {
            if (archivoCopiado.isDirectory()) {
                copiarDirectorio(archivoCopiado.toPath(), archivoPegado.toPath());
            } else {
                Files.copy(archivoCopiado.toPath(), archivoPegado.toPath());
            }
            JOptionPane.showMessageDialog(this, "Archivo/Carpeta pegado: " + archivoPegado.getName());
            recargarArbol();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al pegar el archivo/carpeta: " + ex.getMessage());
        }
    }

    private void recargarArbol() {
        DefaultMutableTreeNode raiz = crearNodoArbol(directorioRaiz);
        arbolArchivos.setModel(new DefaultTreeModel(raiz));
    }

    private File directorioSeleccionado() {
        if (nodoSeleccionado == null) {
            return null;
        }

        StringBuilder ruta = new StringBuilder(directorioRaiz.getAbsolutePath());
        TreeNode[] pathNodes = nodoSeleccionado.getPath();
        for (int i = 1; i < pathNodes.length; i++) {
            ruta.append(File.separator).append(pathNodes[i].toString());
        }

        return new File(ruta.toString());
    }

    private DefaultMutableTreeNode crearNodoArbol(File archivo) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(archivo.getName());
        if (archivo.isDirectory()) {
            File[] archivos = archivo.listFiles();
            if (archivos != null) {
                Arrays.sort(archivos, Comparator.comparing(File::getName));
                for (File subArchivo : archivos) {
                    nodo.add(crearNodoArbol(subArchivo));
                }
            }
        }
        return nodo;
    }

    private String generarNombreUnico(File archivo) {
        String nombre = archivo.getName();
        String extension = "";
        int i = nombre.lastIndexOf('.');
        if (i > 0) {
            extension = nombre.substring(i);
            nombre = nombre.substring(0, i);
        }
        int contador = 1;
        while (archivo.exists()) {
            archivo = new File(archivo.getParent(), nombre + " (" + contador + ")" + extension);
            contador++;
        }
        return archivo.getName();
    }

    private void copiarDirectorio(Path origen, Path destino) throws IOException {
        Files.walk(origen).forEach(source -> {
            try {
                Path destinoRuta = destino.resolve(origen.relativize(source));
                if (Files.isDirectory(source)) {
                    Files.createDirectories(destinoRuta);
                } else {
                    Files.copy(source, destinoRuta, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al copiar directorio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void organizarArchivos() {
        File dir = directorioSeleccionado();
        if (dir != null && dir.isDirectory()) {
            File[] archivos = dir.listFiles();
            if (archivos != null) {
                String[] opciones = {"Nombre", "Fecha", "Tipo", "Tamaño"};
                String criterio = (String) JOptionPane.showInputDialog(
                        this,
                        "Seleccione el criterio de organización:",
                        "Organizar Archivos",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (criterio != null) {
                    Comparator<File> comparator = null;

                    switch (criterio) {
                        case "Nombre":
                            comparator = Comparator.comparing(File::isFile)
                                    .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);
                            break;
                        case "Fecha":
                            comparator = Comparator.comparingLong(File::lastModified).reversed()
                                    .thenComparing(File::isFile);
                            break;
                        case "Tipo":
                            comparator = Comparator.comparing((File f) -> f.isDirectory() ? "" : getFileExtension(f))
                                    .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);
                            break;
                        case "Tamaño":
                            comparator = Comparator.comparingLong(File::length).reversed()
                                    .thenComparing(File::isFile)
                                    .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER);
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "Criterio no válido.");
                            return;
                    }
                    
                    Arrays.sort(archivos, comparator);
                    
                    nodoSeleccionado.removeAllChildren();
                    for (File archivo : archivos) {
                        nodoSeleccionado.add(crearNodoArbol(archivo));
                    }
                    
                    ((DefaultTreeModel) arbolArchivos.getModel()).nodeStructureChanged(nodoSeleccionado);
                    JOptionPane.showMessageDialog(this, "Archivos organizados por " + criterio + ".");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un directorio válido para organizar.");
        }
    }

    private void organizarPorCarpetas() {
        File dir = directorioSeleccionado();
        if (dir != null && dir.isDirectory()) {
            File[] archivos = dir.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isFile()) {
                        String extension = getFileExtension(archivo);
                        File nuevaCarpeta = new File(dir, extension.toUpperCase());
                        if (!nuevaCarpeta.exists()) {
                            nuevaCarpeta.mkdir();
                        }
                        archivo.renameTo(new File(nuevaCarpeta, archivo.getName()));
                    }
                }
                recargarArbol();
            }
        }
    }

    private String getFileExtension(File archivo) {
        String nombre = archivo.getName();
        int lastIndex = nombre.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < nombre.length() - 1) {
            return nombre.substring(lastIndex + 1);
        }
        return "Otros";
    }

    private void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFileChooser selectorDirectorio = new JFileChooser();
            selectorDirectorio.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int seleccion = selectorDirectorio.showOpenDialog(null);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File raiz = selectorDirectorio.getSelectedFile();
                NavegadorArchivos ventana = new NavegadorArchivos(raiz);
                ventana.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No se seleccionó un directorio. El programa se cerrará.");
            }
        });
    }
}
