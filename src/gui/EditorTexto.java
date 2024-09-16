
package gui;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.*;

public class EditorTexto extends JFrame {
    
    private JTextPane textPane;
    private JFileChooser fileChooser;
    private RTFEditorKit rtfEditorKit;

    public EditorTexto(File directorioMisDocumentos) {
        setTitle("Editor de Texto");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        int taskbarHeight = screenInsets.bottom;
        
        setSize(screenSize.width, screenSize.height - taskbarHeight);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        textPane = new JTextPane();
        textPane.setFont(new Font("Consolas", Font.PLAIN, 18));
        textPane.setMargin(new Insets(10, 10, 10, 10));

        fileChooser = new JFileChooser(directorioMisDocumentos);
        rtfEditorKit = new RTFEditorKit();
        
        JToolBar toolBar = crearBarraHerramientas();
        
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(textPane), BorderLayout.CENTER);
    }
    
    private JToolBar crearBarraHerramientas() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(60, 63, 65));
        
        JButton btnGuardar = crearBotonEstetico("Guardar");
        JButton btnAbrir = crearBotonEstetico("Abrir");
        JButton btnColor = crearBotonEstetico("Color");
        JComboBox<String> fuenteBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        JComboBox<Integer> tamanoBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 24, 32, 48});

        fuenteBox.setSelectedItem("Consolas");
        tamanoBox.setSelectedItem(18);
        
        toolBar.add(btnAbrir);
        toolBar.addSeparator(new Dimension(15, 0));
        toolBar.add(btnGuardar);
        toolBar.addSeparator(new Dimension(15, 0));
        toolBar.add(btnColor);
        toolBar.addSeparator(new Dimension(25, 0));
        toolBar.add(new JLabel("Fuente:"));
        toolBar.add(fuenteBox);
        toolBar.addSeparator(new Dimension(15, 0));
        toolBar.add(new JLabel("Tamaño:"));
        toolBar.add(tamanoBox);
        
        btnAbrir.addActionListener(e -> abrirArchivo());
        btnGuardar.addActionListener(e -> guardarArchivo());
        btnColor.addActionListener(e -> cambiarColorTexto());
        fuenteBox.addActionListener(e -> cambiarFuente((String) fuenteBox.getSelectedItem()));
        tamanoBox.addActionListener(e -> cambiarTamano((Integer) tamanoBox.getSelectedItem()));

        return toolBar;
    }
    
    private JButton crearBotonEstetico(String texto) {
        JButton boton = new JButton(texto);
        
        boton.setFont(new Font("Consolas", Font.BOLD, 16));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(45, 137, 239));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2, true));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(0, 122, 204));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(45, 137, 239));
            }
        });

        return boton;
    }
    
    private void cambiarColorTexto() {
        Color color = JColorChooser.showDialog(null, "Seleccionar Color", Color.BLACK);
        if (color != null) {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet atributos = new SimpleAttributeSet();
            StyleConstants.setForeground(atributos, color);
            doc.setCharacterAttributes(textPane.getSelectionStart(), textPane.getSelectionEnd() 
                    - textPane.getSelectionStart(), atributos, false);
        }
    }
    
    private void cambiarFuente(String fuente) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontFamily(atributos, fuente);
        doc.setCharacterAttributes(textPane.getSelectionStart(), textPane.getSelectionEnd() 
                - textPane.getSelectionStart(), atributos, false);
    }
    
    private void cambiarTamano(int tamano) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontSize(atributos, tamano);
        doc.setCharacterAttributes(textPane.getSelectionStart(), textPane.getSelectionEnd() 
                - textPane.getSelectionStart(), atributos, false);
    }
    
    private void abrirArchivo() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(archivo)) {
                textPane.setDocument(new DefaultStyledDocument());
                rtfEditorKit.read(fis, textPane.getDocument(), 0);
            } catch (IOException | BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void guardarArchivo() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                rtfEditorKit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
            } catch (IOException | BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EditorTexto editor = new EditorTexto(new File("C:\\MiniWindows\\usuario\\Mis Documentos"));
            editor.setVisible(true);
        });
    }
}
