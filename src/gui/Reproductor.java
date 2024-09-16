
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class Reproductor extends JFrame implements ActionListener {

    private AudioPlayer player = new AudioPlayer();
    private Thread playbackThread;
    private Timer timer;

    private boolean isPlaying = false;
    private boolean isPause = false;

    private String audioFilePath;
    private String lastOpenPath;

    private JLabel labelFileName = new JLabel("Archivo actual:");
    private JLabel labelTimeCounter = new JLabel("00:00:00");
    private JLabel labelDuration = new JLabel("00:00:00");

    private JButton buttonOpen = new JButton("Open");
    private JButton buttonPlay = new JButton("Play");
    private JButton buttonPause = new JButton("Pause");

    private JSlider sliderTime = new JSlider();
    
    private ImageIcon iconOpen = new ImageIcon(getClass().getResource("images\\Open.png"));
    private ImageIcon iconPlay = new ImageIcon(getClass().getResource("images\\Play.gif"));
    private ImageIcon iconStop = new ImageIcon(getClass().getResource("images\\Stop.gif"));
    private ImageIcon iconPause = new ImageIcon(getClass().getResource("images\\Pause.png"));

    private File directorioMusica;
    
    public Reproductor(File directorioMusica) {
        super("Reproductor Musical");
        this.directorioMusica = directorioMusica;
        
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        
        buttonOpen = crearBotonPersonalizado("Abrir", iconOpen);
        buttonPlay = crearBotonPersonalizado("Play", iconPlay);
        buttonPause = crearBotonPersonalizado("Pause", iconPause);

        buttonPlay.setEnabled(false);
        buttonPause.setEnabled(false);

        
        labelFileName.setFont(new Font("Consolas", Font.BOLD, 16));
        labelTimeCounter.setFont(new Font("Consolas", Font.PLAIN, 14));
        labelDuration.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        sliderTime.setPreferredSize(new Dimension(400, 10));
        sliderTime.setEnabled(false);
        sliderTime.setValue(0);
        sliderTime.setBackground(new Color(220, 220, 220));
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(labelFileName, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        add(labelTimeCounter, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.8;
        constraints.gridwidth = 1;
        add(sliderTime, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0.1;
        add(labelDuration, constraints);
        
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelButtons.setOpaque(false);
        panelButtons.add(buttonOpen);
        panelButtons.add(buttonPlay);
        panelButtons.add(buttonPause);

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(panelButtons, constraints);

        buttonOpen.addActionListener(this);
        buttonPlay.addActionListener(this);
        buttonPause.addActionListener(this);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopPlaying();
            }
        });
        
        getContentPane().setBackground(new Color(60, 63, 65));
        labelFileName.setForeground(Color.WHITE);
        labelTimeCounter.setForeground(Color.WHITE);
        labelDuration.setForeground(Color.WHITE);
        
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        
        setPreferredSize(new Dimension(800, 300));
        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JButton crearBotonPersonalizado(String texto, ImageIcon icono) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Consolas", Font.BOLD, 14));
        boton.setPreferredSize(new Dimension(150, 50));
        boton.setIcon(icono);
        boton.setBackground(new Color(45, 137, 239));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2, true));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            if (button == buttonOpen) {
                openFile();
            } else if (button == buttonPlay) {
                if (!isPlaying) {
                    playBack();
                } else {
                    stopPlaying();
                }
            } else if (button == buttonPause) {
                if (!isPause) {
                    pausePlaying();
                } else {
                    resumePlaying();
                }
            }
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser(directorioMusica);

        FileFilter wavFilter = new FileFilter() {
            @Override
            public String getDescription() {
                return "Sound file (*.WAV)";
            }

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(".wav");
                }
            }
        };

        fileChooser.setFileFilter(wavFilter);
        fileChooser.setDialogTitle("Open Audio File");
        fileChooser.setAcceptAllFileFilterUsed(false);

        int userChoice = fileChooser.showOpenDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            lastOpenPath = fileChooser.getSelectedFile().getParent();
            if (isPlaying || isPause) {
                stopPlaying();
                while (player.getAudioClip().isRunning()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            playBack();
        }
    }

    private void playBack() {
        timer = new Timer(labelTimeCounter, sliderTime);
        timer.start();
        isPlaying = true;
        playbackThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    buttonPlay.setText("Stop");
                    buttonPlay.setIcon(iconStop);
                    buttonPlay.setEnabled(true);

                    buttonPause.setText("Pause");
                    buttonPause.setEnabled(true);

                    player.load(audioFilePath);
                    timer.setAudioClip(player.getAudioClip());
                    labelFileName.setText("Playing File: " + audioFilePath);
                    sliderTime.setMaximum((int) player.getClipSecondLength());

                    labelDuration.setText(player.getClipLengthString());
                    player.play();

                    resetControls();

                } catch (UnsupportedAudioFileException ex) {
                    JOptionPane.showMessageDialog(Reproductor.this,
                            "The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
                    resetControls();
                    ex.printStackTrace();
                } catch (LineUnavailableException ex) {
                    JOptionPane.showMessageDialog(Reproductor.this,
                            "Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
                    resetControls();
                    ex.printStackTrace();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Reproductor.this,
                            "I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
                    resetControls();
                    ex.printStackTrace();
                }
            }
        });
        playbackThread.start();
    }

    private void stopPlaying() {
        isPause = false;
        buttonPause.setText("Pause");
        buttonPause.setEnabled(false);
        
        if (timer != null) {
            timer.reset();
            timer.interrupt();
        }
        
        if (player != null) {
            player.stop();
        }
        
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
        }

        isPlaying = false;
    }

    private void pausePlaying() {
        buttonPause.setText("Resume");
        isPause = true;
        player.pause();
        timer.pauseTimer();
        playbackThread.interrupt();
    }

    private void resumePlaying() {
        buttonPause.setText("Pause");
        isPause = false;
        player.resume();
        timer.resumeTimer();
        playbackThread.interrupt();
    }

    private void resetControls() {
        timer.reset();
        timer.interrupt();

        buttonPlay.setText("Play");
        buttonPlay.setIcon(iconPlay);

        buttonPause.setEnabled(false);

        isPlaying = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            File directorioMusica = new File("C:\\MiniWindows\\usuario\\Mis Musica");
            new Reproductor(directorioMusica).setVisible(true);
        });
    }
}
