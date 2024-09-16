
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

public class Consola extends JFrame {
    
    private Funciones cmd = new Funciones("");
    private JTextArea window = new JTextArea();
    private JScrollPane panel = new JScrollPane(window);
    private int lastEditablePosition;
    
    public Consola(File directorioInicial) {
        cmd = new Funciones(directorioInicial.getPath());
        crearFrame();
        window.setText("Microsoft Windows [Version 10.0.22621.521]" 
                + "\n(c) Microsoft Corporation. All rights reserved." + "\n\n" 
                + cmd.getPath() + ">");
        lastEditablePosition = window.getText().length();
    }

    private void crearFrame() {
        window.setBackground(Color.black);
        window.setForeground(Color.white);
        window.setFont(new java.awt.Font("Consolas", 0, 16));
        window.setEditable(true);
        getContentPane().setLayout(new BorderLayout());

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    manejo();
                } else if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) &&
                        window.getCaretPosition() <= lastEditablePosition) {
                    e.consume();
                } else if (window.getCaretPosition() < lastEditablePosition) {
                    window.setCaretPosition(lastEditablePosition); 
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (window.getCaretPosition() < lastEditablePosition) {
                    e.consume(); 
                }
            }
        });

        setTitle("Command Prompt");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void manejo() {
        String text = window.getText();
        int lastIndex = text.lastIndexOf("\n" + cmd.getPath() + ">");
        if (lastIndex != -1) {
            String command = text.substring(lastIndex + cmd.getPath().length() + 2).trim();
            comandos(command);
        }
    }
    
    private void comandos(String command) {
        String[] parts = command.split(" ");
        switch (parts[0].toLowerCase()) {
            case "mkdir":
                if (parts.length == 1) {
                    print("Ingresar el nombre del directorio");
                } else {
                    print(cmd.mkdir(cmd.getPath() + "/" + parts[1]));
                }
                break;

            case "mfile":
                if (parts.length == 1) {
                    print("Ingresar el nombre del archivo");
                } else {
                    print(cmd.mfile(cmd.getPath() + "/" + parts[1]));
                }
                break;

            case "rm":
                if (parts.length == 1) {
                    print("Ingrese el nombre de carpeta/archivo a eliminar");
                } else {
                    File file = new File(cmd.getPath() + "/" + parts[1]);
                    print(cmd.eliminar(file));
                }
                break;

            case "cd":
                if (parts.length == 1) {
                    print("Ingresar el nombre del directorio");
                } else {
                    cmd.cd(parts[1]);
                }
                break;

            case "...":
                cmd.cd("..");
                break;

            case "dir":
                print(cmd.listar(cmd.getPath()));
                break;

            case "date":
                print(LocalDate.now().toString());
                break;

            case "time":
                print(LocalTime.now().toString());
                break;
                
            case "wr":
                if (parts.length < 3) {
                    print("Ingrese el nombre del archivo y contenido");
                } else {
                    StringBuilder content = new StringBuilder();
                    for (int i = 2; i < parts.length; i++) {
                        content.append(parts[i]).append(" ");
                    }
                    print(cmd.escribir(content.toString().trim(), cmd.getPath() + "/" + parts[1]));
                }
                break;
                
            case "rd":
                if (parts.length == 1) {
                    print("Ingrese el nombre del archivo a leer");
                } else {
                    print(cmd.leer(cmd.getPath() + "/" + parts[1]));
                }
                break;

            default:
                print("Comando no reconocido: " + command);
                break;
        }
        window.append("\n" + cmd.getPath() + ">");
        lastEditablePosition = window.getText().length(); 
    }
    
    private void print(String msg) {
        window.append("\n" + msg);
    }
}
