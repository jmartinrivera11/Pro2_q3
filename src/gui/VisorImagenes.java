
package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisorImagenes extends JFrame {
    private JLabel etiquetaImagen;
    private List<File> imagenes;
    private int indiceActual = 0;
    private File directorioImagenes;
    private double escala = 1.0;

    public VisorImagenes(File directorioImagenes) {
        this.directorioImagenes = directorioImagenes;
        setTitle("Visor de Imágenes");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        int taskbarHeight = screenInsets.bottom;
        
        setSize(screenSize.width, screenSize.height - taskbarHeight);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        imagenes = cargarImagenes(directorioImagenes);
        etiquetaImagen = new JLabel();
        etiquetaImagen.setHorizontalAlignment(JLabel.CENTER);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                actualizarImagen();
            }
        });
        
        JButton btnAnterior = crearBotonConIcono("images\\prev.png", "Anterior");
        JButton btnSiguiente = crearBotonConIcono("images\\next.png", "Siguiente");
        JButton btnZoomIn = crearBotonConIcono("images\\zoom_in.png", "Zoom In");
        JButton btnZoomOut = crearBotonConIcono("images\\zoom_out.png", "Zoom Out");
        JButton btnSubirImagen = crearBotonConIcono("images\\up.png", "Subir Imagen");

        btnAnterior.addActionListener(e -> mostrarImagenAnterior());
        btnSiguiente.addActionListener(e -> mostrarImagenSiguiente());
        btnZoomIn.addActionListener(e -> hacerZoom(1.25));
        btnZoomOut.addActionListener(e -> hacerZoom(0.8));
        btnSubirImagen.addActionListener(e -> subirImagen());

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(new Color(60, 63, 65));
        panelBotones.add(btnAnterior);
        panelBotones.add(btnSiguiente);
        panelBotones.add(btnZoomIn);
        panelBotones.add(btnZoomOut);
        panelBotones.add(btnSubirImagen);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(etiquetaImagen), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JButton crearBotonConIcono(String rutaIcono, String toolTipText) {
        JButton boton = new JButton();
        
        ImageIcon icono = null;
        try {
            icono = new ImageIcon(getClass().getResource(rutaIcono));
            Image img = icono.getImage();
            Image newImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            icono = new ImageIcon(newImg);
        } catch (NullPointerException e) {
            System.out.println("No se pudo cargar el ícono: " + rutaIcono);
        }
        
        if (icono != null) {
            boton.setIcon(icono);
        }
        
        boton.setToolTipText(toolTipText);
        boton.setBackground(new Color(45, 137, 239));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2, true));

        return boton;
    }
    
    private List<File> cargarImagenes(File directorio) {
        File[] archivos = directorio.listFiles((dir, nombre) -> nombre.endsWith(".jpg") || nombre.endsWith(".png"));
        if (archivos != null) {
            return new ArrayList<>(Arrays.asList(archivos));
        } else {
            return new ArrayList<>();
        }
    }
    
    private void actualizarImagen() {
        if (imagenes.isEmpty()) {
            etiquetaImagen.setText("No hay imágenes. Suba una imagen para comenzar.");
            etiquetaImagen.setIcon(null);
            etiquetaImagen.setFont(new Font("Consolas", Font.BOLD, 22));
            etiquetaImagen.setForeground(new Color(105, 105, 105));
            return;
        }

        int ancho = etiquetaImagen.getWidth();
        int alto = etiquetaImagen.getHeight();

        if (ancho == 0 || alto == 0) {
            return;
        }

        ImageIcon icono = new ImageIcon(imagenes.get(indiceActual).getPath());
        Image imagenEscalada = icono.getImage().getScaledInstance((int) (ancho * escala), (int) (alto * escala), Image.SCALE_SMOOTH);
        etiquetaImagen.setIcon(new ImageIcon(imagenEscalada));
        etiquetaImagen.setText(null);
    }
    
    private void mostrarImagenAnterior() {
        if (indiceActual > 0) {
            indiceActual--;
            actualizarImagen();
        }
    }
    
    private void mostrarImagenSiguiente() {
        if (indiceActual < imagenes.size() - 1) {
            indiceActual++;
            actualizarImagen();
        }
    }
    
    private void hacerZoom(double factor) {
        escala *= factor;
        actualizarImagen();
    }
    
    private void subirImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            if (archivoSeleccionado != null && (archivoSeleccionado.getName().endsWith(".jpg") 
                    || archivoSeleccionado.getName().endsWith(".png"))) {
                try {
                    File destino = new File(directorioImagenes, archivoSeleccionado.getName());
                    Files.copy(archivoSeleccionado.toPath(), destino.toPath());
                    
                    imagenes = cargarImagenes(directorioImagenes);
                    actualizarImagen();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al subir la imagen: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un archivo de imagen válido (.jpg o .png).");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            File directorioImagenes = new File("C:\\MiniWindows\\usuario\\Mis Imágenes");
            VisorImagenes visor = new VisorImagenes(directorioImagenes);
            visor.setVisible(true);
        });
    }
}
