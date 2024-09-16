
package ig;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class PerfilFrame {

    private JPanel panel;

    public PerfilFrame(String username) {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        Color encabezadoColor = new Color(236, 240, 241);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.setBackground(encabezadoColor);

        String imagePath = username + "/profile_picture.png";

        ImageIcon profileImageIcon = null;
        File profileImageFile = new File(imagePath);

        if (profileImageFile.exists()) {
            profileImageIcon = new ImageIcon(profileImageFile.getAbsolutePath());
        } else {
            String imagen_defaul = "/Img/Icon_Defaul.png";
            profileImageIcon = new ImageIcon(getClass().getResource(imagen_defaul));
        }

        Image profileImage = profileImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel profilePicLabel = new JLabel(new ImageIcon(profileImage));
        profilePicLabel.setBorder(BorderFactory.createLineBorder(encabezadoColor, 2, true)); 

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel followersLabel = new JLabel(contarFollowers(username) + " Seguidores");
        followersLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        JLabel followingLabel = new JLabel(contarFollowing(username) + " Seguidos");
        followingLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(encabezadoColor);
        infoPanel.add(followersLabel);
        infoPanel.add(new JLabel("   ")); 
        infoPanel.add(followingLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(profilePicLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        topPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        topPanel.add(infoPanel, gbc);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 10, 10)); 
        gridPanel.setBackground(Color.WHITE); 

        File userDir = new File(username);
        if (userDir.exists() && userDir.isDirectory()) {
            File[] imageFiles = userDir.listFiles((dir, name) -> (name.toLowerCase().endsWith(".png")
                    || name.toLowerCase().endsWith(".jpg")
                    || name.toLowerCase().endsWith(".jpeg")) 
                    && !name.equals("profile_picture.png"));  

            if (imageFiles != null) {
                for (int index = 0; index < 9; index++) { 
                    JPanel imagePanel = new JPanel(new BorderLayout());
                    imagePanel.setBackground(Color.WHITE); 
                    if (index < imageFiles.length) {
                        File imageFile = imageFiles[index];
                        ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());

                        JLabel imageLabel = new JLabel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Image img = imageIcon.getImage();
                                g.drawImage(img, 0, 0, getWidth(), getHeight(), this); 
                            }
                        };

                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); 
                        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

                        imagePanel.add(imageLabel, BorderLayout.CENTER);
                    }

                    gridPanel.add(imagePanel);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setPreferredSize(new Dimension(800, 600));

        panel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    private int contarFollowers(String username) {
        File followersFile = new File(username + "/followers.ins");
        if (!followersFile.exists()) {
            return 0;
        }
        int count = 0;
        try (BufferedReader leer = new BufferedReader(new FileReader(followersFile))) {
            while (leer.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    private int contarFollowing(String username) {
        File followingFile = new File(username + "/following.ins");
        if (!followingFile.exists()) {
            return 0;
        }
        int contador = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(followingFile))) {
            while (reader.readLine() != null) {
                contador++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contador;
    }
}
