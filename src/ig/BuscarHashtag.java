
package ig;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class BuscarHashtag {

    private JPanel panel;
    private JTextArea hashtagResultados;
    private JTextField text_hashtag;

    public BuscarHashtag(String username) {
        panel = new JPanel();
        panel.setLayout(null);

        JLabel hashtagLabel = new JLabel("Buscar Hashtag:");
        hashtagLabel.setFont(new Font("Arial", Font.BOLD, 20));
        hashtagLabel.setBounds(50, 20, 300, 40);
        panel.add(hashtagLabel);

        text_hashtag = new JTextField();
        text_hashtag.setFont(new Font("Arial", Font.PLAIN, 16));
        text_hashtag.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        text_hashtag.setBounds(50, 70, 1000, 200);
        panel.add(text_hashtag);

        JButton buscarHashtag = new JButton("Buscar Hashtag");
        buscarHashtag.setBackground(new Color(255, 51, 51));
        buscarHashtag.setForeground(Color.WHITE);
        buscarHashtag.setFocusPainted(false);
        buscarHashtag.setBorderPainted(false);
        buscarHashtag.setFont(new Font("Arial", Font.BOLD, 16));
        buscarHashtag.setBounds(50, 275, 1000, 50);
        panel.add(buscarHashtag);

        hashtagResultados = new JTextArea();
        hashtagResultados.setEditable(false);
        hashtagResultados.setLineWrap(true);
        hashtagResultados.setWrapStyleWord(true);
        hashtagResultados.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane displayScrollPane = new JScrollPane(hashtagResultados);
        displayScrollPane.setBounds(50, 330, 1000, 500);
        panel.add(displayScrollPane);
        
        buscarHashtag.addActionListener(e -> {
            String hashtag = text_hashtag.getText().trim();
            if (!hashtag.isEmpty()) {
                if (!hashtag.startsWith("#")) {
                    hashtag = "#" + hashtag;
                }
                mostrarHashtagResults(hashtag);  
                text_hashtag.setText("");  
            } else {
                JOptionPane.showMessageDialog(null, "El campo de hashtag no puede estar vacío.");
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    private void mostrarHashtagResults(String hashtag) {
        File usersFile = new File("users.ins");
        StringBuilder result = new StringBuilder();
        hashtagResultados.setText("");

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 3) {
                    String username = userData[2].trim();
                    buscarEnComentariosUsuario(hashtag, username, result);
                }
            }

            if (result.length() > 0) {
                hashtagResultados.setText(result.toString());
            } else {
                hashtagResultados.setText("No se encontraron comentarios con ese hashtag");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buscarEnComentariosUsuario(String hashtag, String username, StringBuilder result) {
        File instaFile = new File(username + "/insta.ins");
        if (instaFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(instaFile))) {
                String line;
                boolean foundHashtag = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(hashtag)) {
                        if (!result.toString().contains(line)) { 
                            result.append(username).append(" escribio: ").append(line).append("\n\n");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
