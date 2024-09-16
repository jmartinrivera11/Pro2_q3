
package ig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MenuPrincipal extends JFrame {

    private JPanel panel;

    public MenuPrincipal(String username) {
        setTitle("INSTAGRAM");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.setBackground(Color.WHITE);
        sideMenuPanel.setPreferredSize(new Dimension(250, getHeight()));

        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/Img/Icon_Ig.png")));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10)); 

        JLabel logo_ig = new JLabel("Instagram");
        logo_ig.setFont(new Font("Serif", Font.ITALIC, 36));  
        logo_ig.setForeground(Color.BLACK);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.add(iconLabel);
        logoPanel.add(logo_ig);

        JButton boton_perfil = confiBoton("Perfil", "/Img/Icon_Perfil.png");
        JButton boton_cargarImagen = confiBoton("Cargar Imagen", "/Img/Icon_Image.png");
        JButton boton_comentar = confiBoton("Comentar", "/Img/Icon_Coment.png");
        JButton boton_editarperfil = confiBoton("Editar Perfil", "/Img/Icon_EditPerfil.png");
        JButton boton_buscarhashtag = confiBoton("Buscar Hashtag", "/Img/Icon_Hashtag.png");

        JButton boton_cerrar = new JButton("Cerrar Sesion");
        boton_cerrar.setBackground(Color.RED);
        boton_cerrar.setForeground(Color.WHITE);
        boton_cerrar.setFocusPainted(false);
        boton_cerrar.setBorderPainted(false);
        boton_cerrar.setMaximumSize(new Dimension(250, 50));  
        boton_cerrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        boton_cerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Insta();
                dispose();
            }
        });

        panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setLayout(new BorderLayout());

        boton_perfil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarVista(new PerfilFrame(username).getPanel());
            }
        });

        boton_cargarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarVista(new CargarImagen(username).getPanel());
            }
        });

        boton_comentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarVista(new Comentar(username).getPanel());
            }
        });

        boton_editarperfil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarVista(new EditarPerfil(username).getPanel()); 
            }
        });

        boton_buscarhashtag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarVista(new BuscarHashtag(username).getPanel());
            }
        });

        sideMenuPanel.add(logoPanel);
        sideMenuPanel.add(Box.createVerticalStrut(20));
        sideMenuPanel.add(alinearIzquierda(boton_perfil));
        sideMenuPanel.add(Box.createVerticalStrut(2)); 
        sideMenuPanel.add(alinearIzquierda(boton_cargarImagen));
        sideMenuPanel.add(Box.createVerticalStrut(2)); 
        sideMenuPanel.add(alinearIzquierda(boton_comentar));
        sideMenuPanel.add(Box.createVerticalStrut(2));  
        sideMenuPanel.add(Box.createVerticalStrut(2));  
        sideMenuPanel.add(alinearIzquierda(boton_editarperfil));
        sideMenuPanel.add(Box.createVerticalStrut(2));  
        sideMenuPanel.add(alinearIzquierda(boton_buscarhashtag));
        sideMenuPanel.add(Box.createVerticalGlue());
        sideMenuPanel.add(Box.createVerticalStrut(20));
        sideMenuPanel.add(boton_cerrar);  

        add(sideMenuPanel, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void actualizarVista(JPanel nuevoPanel) {
        panel.removeAll();
        panel.add(nuevoPanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JButton confiBoton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setAlignmentX(Component.LEFT_ALIGNMENT); 
        return button;
    }

    private JPanel alinearIzquierda(JButton button) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.add(button);
        return panel;
    }
}
