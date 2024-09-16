
package gui;

import ig.Insta;
import sistema.SistemaOperativo;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MiniWindows extends JFrame {

    private SistemaOperativo sistema;
    private JPanel panelPrincipal;
    private JLabel fondo;

    public MiniWindows() {
        sistema = new SistemaOperativo();
        inicializarGUI();
    }

    private void inicializarGUI() {
        setTitle("Mini-Windows");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        int taskbarHeight = screenInsets.bottom;
        
        setSize(screenSize.width, screenSize.height - taskbarHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        ImageIcon imagenFondo = new ImageIcon(getClass().getResource("images\\fondo.png"));
        Image img = imagenFondo.getImage();
        Image newImg = img.getScaledInstance(screenSize.width, screenSize.height - taskbarHeight, Image.SCALE_SMOOTH);
        imagenFondo = new ImageIcon(newImg);

        fondo = new JLabel(imagenFondo);
        fondo.setLayout(new GridBagLayout());
        
        panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        
        JButton loginBtn = crearBotonEstetico("Iniciar Sesión");
        JButton crearCuentaBtn = crearBotonEstetico("Crear Cuenta");

        panelPrincipal.add(loginBtn, gbc);
        panelPrincipal.add(crearCuentaBtn, gbc);
        fondo.add(panelPrincipal);
        setContentPane(fondo);
        
        loginBtn.addActionListener(e -> mostrarPantallaLogin());
        crearCuentaBtn.addActionListener(e -> mostrarPantallaCrearCuenta());
    }

    private JButton crearBotonEstetico(String texto) {
        JButton boton = new JButton(texto);
        
        boton.setPreferredSize(new Dimension(250, 60));
        boton.setFont(new Font("Consolas", Font.PLAIN, 18));
        boton.setBackground(new Color(45, 137, 239));
        boton.setForeground(Color.WHITE);
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
    
    private void mostrarPantallaLogin() {
        panelPrincipal.removeAll();
        panelPrincipal.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Consolas", Font.PLAIN, 16));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Consolas", Font.PLAIN, 16));

        JButton loginButton = crearBotonEstetico("Login");
        JButton cancelarButton = crearBotonEstetico("Cancelar");

        panelPrincipal.add(usernameLabel, gbc);
        panelPrincipal.add(usernameField, gbc);
        panelPrincipal.add(passwordLabel, gbc);
        panelPrincipal.add(passwordField, gbc);
        panelPrincipal.add(loginButton, gbc);
        panelPrincipal.add(cancelarButton, gbc);

        revalidate();
        repaint();
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (sistema.login(username, password)) {
                JOptionPane.showMessageDialog(null, "Login exitoso!");
                mostrarMenuPrincipal();
            } else {
                JOptionPane.showMessageDialog(null, "Login fallido. Verifique sus credenciales.");
            }
        });
        
        cancelarButton.addActionListener(e -> reiniciarPantallaPrincipal());
    }

    private void mostrarPantallaCrearCuenta() {
        panelPrincipal.removeAll();
        panelPrincipal.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JCheckBox adminCheck = new JCheckBox("¿Es administrador?");

        JButton crearButton = crearBotonEstetico("Crear Cuenta");
        JButton cancelarButton = crearBotonEstetico("Cancelar");

        panelPrincipal.add(usernameLabel, gbc);
        panelPrincipal.add(usernameField, gbc);
        panelPrincipal.add(passwordLabel, gbc);
        panelPrincipal.add(passwordField, gbc);
        panelPrincipal.add(adminCheck, gbc);
        panelPrincipal.add(crearButton, gbc);
        panelPrincipal.add(cancelarButton, gbc);

        revalidate();
        repaint();

        crearButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean esAdmin = adminCheck.isSelected();

            if (sistema.usuarioExiste(username)) {
                JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe. Elija otro nombre.");
                return;
            }

            try {
                sistema.crearUsuario(username, password, esAdmin);
                JOptionPane.showMessageDialog(null, "Usuario creado exitosamente!");
                reiniciarPantallaPrincipal();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al crear el usuario: " + ex.getMessage());
            }
        });

        cancelarButton.addActionListener(e -> reiniciarPantallaPrincipal());
    }

    private void reiniciarPantallaPrincipal() {
        panelPrincipal.removeAll();
        panelPrincipal.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton loginBtn = crearBotonEstetico("Iniciar Sesión");
        JButton crearCuentaBtn = crearBotonEstetico("Crear Cuenta");

        panelPrincipal.add(loginBtn, gbc);
        panelPrincipal.add(crearCuentaBtn, gbc);
        
        loginBtn.addActionListener(e -> mostrarPantallaLogin());
        
        crearCuentaBtn.addActionListener(e -> mostrarPantallaCrearCuenta());

        revalidate();
        repaint();
    }
    
    private void mostrarMenuPrincipal() {
        panelPrincipal.removeAll();
        panelPrincipal.setLayout(new BorderLayout());

        JPanel panelEscritorio = new JPanel();
        panelEscritorio.setLayout(new GridLayout(3, 2, 20, 20));
        panelEscritorio.setOpaque(false);

        JButton abrirNavegadorArchivosBtn = crearBotonConIcono("System File", "images\\file.png");
        JButton abrirEditorTextoBtn = crearBotonConIcono("Editor de Texto", "images\\txt.png");
        JButton abrirConsolaBtn = crearBotonConIcono("Consola", "images\\cmd.png");
        JButton abrirVisorImagenesBtn = crearBotonConIcono("Visor de Imágenes", "images\\img.png");
        JButton abrirReproductorMusicalBtn = crearBotonConIcono("Music Player", "images\\music.png");
        JButton cerrarSesionBtn = crearBotonConIcono("Cerrar Sesión", "images\\out.png");
        JButton abrirInstagramBtn = crearBotonConIcono("Instagram", "images\\insta.png");
        
        if (sistema.getUsuarioActual().esAdmin()) {
            JButton crearUsuarioBtn = crearBotonConIcono("Crear Usuario", "images\\create.png");
            crearUsuarioBtn.addActionListener(e -> mostrarFormularioCrearUsuario());
            
            JButton accederCarpetasUsuariosBtn = crearBotonConIcono("Users Folder", "images\\admin.png");
            accederCarpetasUsuariosBtn.addActionListener(e -> {
                String username = JOptionPane.showInputDialog("Ingrese el nombre del usuario para acceder a su carpeta:");
                if (username != null && !username.isEmpty()) {
                    File carpetaUsuario = sistema.obtenerCarpetaUsuario(username);
                    if (carpetaUsuario != null) {
                        NavegadorArchivos navegador = new NavegadorArchivos(carpetaUsuario);
                        navegador.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario no encontrado o no tiene permisos.");
                    }
                }
            });
            
            panelEscritorio.add(crearUsuarioBtn);
            panelEscritorio.add(accederCarpetasUsuariosBtn);
        }

        panelEscritorio.add(abrirNavegadorArchivosBtn);
        panelEscritorio.add(abrirEditorTextoBtn);
        panelEscritorio.add(abrirConsolaBtn);
        panelEscritorio.add(abrirVisorImagenesBtn);
        panelEscritorio.add(abrirReproductorMusicalBtn);
        panelEscritorio.add(cerrarSesionBtn);
        panelEscritorio.add(abrirInstagramBtn);

        panelPrincipal.add(panelEscritorio, BorderLayout.CENTER);

        revalidate();
        repaint();
        
        abrirNavegadorArchivosBtn.addActionListener(e -> {
            File directorioUsuario = sistema.getUsuarioActual().getDirectorioUsuario();
            NavegadorArchivos navegador = new NavegadorArchivos(directorioUsuario);
            navegador.setVisible(true);
        });

        abrirEditorTextoBtn.addActionListener(e -> {
            File misDocumentos = new File(sistema.getUsuarioActual().getDirectorioUsuario(), "Mis Documentos");
            EditorTexto editor = new EditorTexto(misDocumentos);
            editor.setVisible(true);
        });

        abrirConsolaBtn.addActionListener(e -> {
            File directorioUsuario = sistema.getUsuarioActual().getDirectorioUsuario();
            Consola consola = new Consola(directorioUsuario);
            consola.setVisible(true);
        });

        abrirVisorImagenesBtn.addActionListener(e -> {
            File directorioImagenes = new File(sistema.getUsuarioActual().getDirectorioUsuario(), "Mis Imágenes");
            VisorImagenes visor = new VisorImagenes(directorioImagenes);
            visor.setVisible(true);
        });

        abrirReproductorMusicalBtn.addActionListener(e -> {
            File directorioMusica = new File(sistema.getUsuarioActual().getDirectorioUsuario(), "Música");
            Reproductor reproductorMusical = new Reproductor(directorioMusica);
            reproductorMusical.setVisible(true);
        });

        abrirInstagramBtn.addActionListener(e -> {
            new Insta();
        });

        cerrarSesionBtn.addActionListener(e -> reiniciarPantallaPrincipal());
    }

    private void mostrarFormularioCrearUsuario() {
        
        panelPrincipal.removeAll();
        panelPrincipal.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JCheckBox adminCheck = new JCheckBox("¿Es administrador?");
        
        JButton crearButton = crearBotonEstetico("Crear Usuario");
        JButton cancelarButton = crearBotonEstetico("Cancelar");
        
        panelPrincipal.add(usernameLabel, gbc);
        panelPrincipal.add(usernameField, gbc);
        panelPrincipal.add(passwordLabel, gbc);
        panelPrincipal.add(passwordField, gbc);
        panelPrincipal.add(adminCheck, gbc);
        panelPrincipal.add(crearButton, gbc);
        panelPrincipal.add(cancelarButton, gbc);

        revalidate();
        repaint();
        
        crearButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean esAdmin = adminCheck.isSelected();

            if (sistema.usuarioExiste(username)) {
                JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe. Elija otro nombre.");
                return;
            }

            try {
                sistema.crearUsuario(username, password, esAdmin);
                JOptionPane.showMessageDialog(null, "Usuario creado exitosamente!");
                mostrarMenuPrincipal();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al crear el usuario: " + ex.getMessage());
            }
        });
        
        cancelarButton.addActionListener(e -> mostrarMenuPrincipal());
    }
    
    private JButton crearBotonConIcono(String texto, String rutaIcono) {
    JButton boton = new JButton(texto);
    
    ImageIcon icono = null;
    try {
        icono = new ImageIcon(getClass().getResource(rutaIcono));
        Image img = icono.getImage();
        Image newImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        icono = new ImageIcon(newImg);
    } catch (NullPointerException e) {
        System.out.println("No se pudo cargar el ícono: " + rutaIcono);
    }
    
    if (icono != null) {
        boton.setIcon(icono);
    }

    boton.setHorizontalTextPosition(SwingConstants.CENTER);
    boton.setVerticalTextPosition(SwingConstants.BOTTOM);
    
    boton.setPreferredSize(new Dimension(120, 120));
    boton.setFont(new Font("Consolas", Font.PLAIN, 12));
    boton.setBackground(new Color(45, 137, 239));
    boton.setForeground(Color.WHITE);
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MiniWindows ventana = new MiniWindows();
            ventana.setVisible(true);
        });
    }
}
