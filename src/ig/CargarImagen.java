
package ig;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class CargarImagen {

    private JPanel panel;
    private JTextField nombreimage; 

    public CargarImagen(String username) {
        panel = new JPanel();
        JLabel fileLabel = new JLabel("Selecciona una imagen:");
        fileLabel.setFont(new Font("Serif", Font.BOLD, 20));

        nombreimage = new JTextField();
        nombreimage.setEditable(false);
        nombreimage.setPreferredSize(new Dimension(300, 30)); 

        JButton selectFileButton = new JButton("Seleccionar");
        JButton uploadButton = new JButton("Subir Imagen");

        JFileChooser fileChooser = new JFileChooser();

        Color customButtonColor = new Color(255, 0, 0);

        selectFileButton.setBackground(customButtonColor);
        selectFileButton.setForeground(Color.WHITE);
        selectFileButton.setFocusPainted(false);
        selectFileButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectFileButton.setPreferredSize(new Dimension(150, 40));

        uploadButton.setBackground(customButtonColor);
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setFont(new Font("Arial", Font.BOLD, 16));
        uploadButton.setPreferredSize(new Dimension(150, 40));

        selectFileButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                nombreimage.setText(selectedFile.getName());
                uploadButton.setEnabled(true); 

                uploadButton.addActionListener(uploadEvent -> {
                    File userDir = new File(username);
                    if (!userDir.exists()) {
                        userDir.mkdirs();
                    }
                    File destFile = new File(userDir, selectedFile.getName());
                    try {
                        copyFile(selectedFile, destFile);
                        JOptionPane.showMessageDialog(null, "Imagen subida correctamente");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error al subir la imagen");
                        ex.printStackTrace();
                    }
                });
            }
        });

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(fileLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nombreimage, gbc); 

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(selectFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(uploadButton, gbc);
        uploadButton.setEnabled(false);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
