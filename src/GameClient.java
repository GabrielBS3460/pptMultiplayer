import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GameClient {
    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        // Cria a interface gráfica
        JFrame frame = new JFrame("Pedra, Papel e Tesoura");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JLabel resultLabel = new JLabel("");
        frame.add(resultLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2));

        ImageIcon rockIcon = new ImageIcon("pedra.png");
        Image scaledRockImage = rockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        rockIcon = new ImageIcon(scaledRockImage);
        JButton rockButton = new JButton("Pedra", rockIcon);
        rockButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonPanel.add(rockButton);

        ImageIcon paperIcon = new ImageIcon("papel.png");
        Image scaledPaperImage = paperIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        paperIcon = new ImageIcon(scaledPaperImage);
        JButton paperButton = new JButton("Papel", paperIcon);
        paperButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonPanel.add(paperButton);

        ImageIcon scissorsIcon = new ImageIcon("tesoura.png");
        Image scaledScissorsImage = scissorsIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        scissorsIcon = new ImageIcon(scaledScissorsImage);
        JButton scissorsButton = new JButton("Tesoura", scissorsIcon);
        scissorsButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonPanel.add(scissorsButton);

        frame.add(buttonPanel, BorderLayout.CENTER);


        Clip backgroundMusic = AudioSystem.getClip();
        AudioInputStream backgroundMusicStream = AudioSystem.getAudioInputStream(new File("musicaDeFundo.wav"));
        backgroundMusic.open(backgroundMusicStream);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

        // Conecta ao servidor
        Socket socket = new Socket("localhost", 12345);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        // Adiciona listeners aos botões
        rockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Envia a jogada ao servidor
                    output.writeUTF("Pedra");
                    // Recebe o resultado do servidor
                    String result = input.readUTF();
                    try {
                        // Cria um clip para o efeito sonoro
                        Clip soundEffect = AudioSystem.getClip();
                        AudioInputStream soundEffectStream = AudioSystem.getAudioInputStream(new File("efeitoSonoro.wav"));
                        soundEffect.open(soundEffectStream);

                        // Toca o efeito sonoro quando o jogador vencer
                        if (result.equals("Jogador 1 venceu!") || result.equals("Jogador 2 venceu!")) {
                            soundEffect.setFramePosition(0);
                            soundEffect.start();
                        }
                    } catch (LineUnavailableException t) {
                        // Trata a exceção aqui
                        System.err.println("Não foi possível reproduzir o áudio: " + t.getMessage());
                    } catch (UnsupportedAudioFileException ex) {
                        throw new RuntimeException(ex);
                    }

                    resultLabel.setText(result);
                    // Pergunta ao usuário se ele quer continuar
                    int option = JOptionPane.showConfirmDialog(frame, "Deseja continuar?", "Continuar", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        // Envia ao servidor que o jogador quer continuar
                        output.writeBoolean(true);
                    } else {
                        // Envia ao servidor que o jogador quer sair
                        output.writeBoolean(false);
                        // Recebe o relatório final do servidor
                        String report = input.readUTF();
                        // Exibe o relatório final na interface gráfica
                        JTextArea textArea = new JTextArea(report);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        scrollPane.setPreferredSize(new Dimension(500, 500));
                        JOptionPane.showMessageDialog(frame, scrollPane, "Relatório Final", JOptionPane.INFORMATION_MESSAGE);
                        // Verifica se o outro jogador saiu
                        if (input.available() > 0) {
                            String message = input.readUTF();
                            JOptionPane.showMessageDialog(frame, message, "Fim da Partida", JOptionPane.INFORMATION_MESSAGE);
                        }
                        // Fecha a conexão com o servidor
                        input.close();
                        output.close();
                        socket.close();
                        // Fecha a janela
                        frame.dispose();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        paperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Envia a jogada ao servidor
                    output.writeUTF("Papel");
                    // Recebe o resultado do servidor
                    String result = input.readUTF();

                    try {
                        // Cria um clip para o efeito sonoro
                        Clip soundEffect = AudioSystem.getClip();
                        AudioInputStream soundEffectStream = AudioSystem.getAudioInputStream(new File("efeitoSonoro.wav"));
                        soundEffect.open(soundEffectStream);

                        // Toca o efeito sonoro quando o jogador vencer
                        if (result.equals("Jogador 1 venceu!") || result.equals("Jogador 2 venceu!")) {
                            soundEffect.setFramePosition(0);
                            soundEffect.start();
                        }
                    } catch (LineUnavailableException t) {
                        // Trata a exceção aqui
                        System.err.println("Não foi possivel reproduzir o audio: " + t.getMessage());
                    } catch (UnsupportedAudioFileException ex) {
                        throw new RuntimeException(ex);
                    }

                    resultLabel.setText(result);
                    // Pergunta ao usuário se ele quer continuar
                    int option = JOptionPane.showConfirmDialog(frame, "Deseja continuar?", "Continuar", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        // Envia ao servidor que o jogador quer continuar
                        output.writeBoolean(true);
                    } else {
                        // Envia ao servidor que o jogador quer sair
                        output.writeBoolean(false);
                        // Recebe o relatório final do servidor
                        String report = input.readUTF();
                        // Exibe o relatório final na interface gráfica
                        JTextArea textArea = new JTextArea(report);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        scrollPane.setPreferredSize(new Dimension(500, 500));
                        JOptionPane.showMessageDialog(frame, scrollPane, "Relatorio Final", JOptionPane.INFORMATION_MESSAGE);
                        // Verifica se o outro jogador saiu
                        if (input.available() > 0) {
                            String message = input.readUTF();
                            JOptionPane.showMessageDialog(frame, message, "Fim da Partida", JOptionPane.INFORMATION_MESSAGE);
                        }
                        // Fecha a conexão com o servidor
                        input.close();
                        output.close();
                        socket.close();
                        // Fecha a janela
                        frame.dispose();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        scissorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Envia a jogada ao servidor
                    output.writeUTF("Tesoura");
                    // Recebe o resultado do servidor
                    String result = input.readUTF();

                    try {
                        // Cria um clip para o efeito sonoro
                        Clip soundEffect = AudioSystem.getClip();
                        AudioInputStream soundEffectStream = AudioSystem.getAudioInputStream(new File("efeitoSonoro.wav"));
                        soundEffect.open(soundEffectStream);

                        // Toca o efeito sonoro quando o jogador vencer
                        if (result.equals("Jogador 1 venceu!") || result.equals("Jogador 2 venceu!")) {
                            soundEffect.setFramePosition(0);
                            soundEffect.start();
                        }
                    } catch (LineUnavailableException t) {
                        // Trata a exceção aqui
                        System.err.println("Não foi possivel reproduzir o audio: " + t.getMessage());
                    } catch (UnsupportedAudioFileException ex) {
                        throw new RuntimeException(ex);
                    }

                    resultLabel.setText(result);
                    // Pergunta ao usuário se ele quer continuar
                    int option = JOptionPane.showConfirmDialog(frame, "Deseja continuar?", "Continuar", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        // Envia ao servidor que o jogador quer continuar
                        output.writeBoolean(true);
                    } else {
                        // Envia ao servidor que o jogador quer sair
                        output.writeBoolean(false);
                        // Recebe o relatório final do servidor
                        String report = input.readUTF();
                        // Exibe o relatório final na interface gráfica
                        JTextArea textArea = new JTextArea(report);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        scrollPane.setPreferredSize(new Dimension(500, 500));
                        JOptionPane.showMessageDialog(frame, scrollPane, "Relatorio Final", JOptionPane.INFORMATION_MESSAGE);
                        // Verifica se o outro jogador saiu
                        if (input.available() > 0) {
                            String message = input.readUTF();
                            JOptionPane.showMessageDialog(frame, message, "Fim da Partida", JOptionPane.INFORMATION_MESSAGE);
                        }
                        // Fecha a conexão com o servidor
                        input.close();
                        output.close();
                        socket.close();
                        // Fecha a janela
                        frame.dispose();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Exibe o frame
        frame.setVisible(true);
    }
}