import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.border.EmptyBorder;

/**
 * SimonGame.java
 * Jogo Genius (Simon) em Java Swing com sons gerados dinamicamente (sine waves).
 *
 * Compile: javac SimonGame.java
 * Execute:   java SimonGame
 *
 * Feito para ser simples e autossuficiente (sem arquivos de áudio).
 */
public class SimonGame extends JFrame {
    private final JButton[] colorButtons = new JButton[4];
    private final Color[] baseColors = {
            new Color(0x1D7CF2), // azul
            new Color(0xE6322A), // vermelho
            new Color(0xF2D01D), // amarelo
            new Color(0x29B573)  // verde
    };
    private final Color[] lightColors = {
            baseColors[0].brighter(),
            baseColors[1].brighter(),
            baseColors[2].brighter(),
            baseColors[3].brighter()
    };

    private final JButton startButton = new JButton("Start");
    private final JLabel roundLabel = new JLabel("Rodada: 0", SwingConstants.CENTER);

    private final ArrayList<Integer> sequence = new ArrayList<>();
    private final ArrayList<Integer> playerMoves = new ArrayList<>();
    private final Random random = new Random();

    private boolean acceptingInput = false;
    private boolean playingSequence = false;

    // Frequências para cada botão (Hz)
    private final float[] freqs = { 523.25f, 659.25f, 783.99f, 1046.50f };

    public SimonGame() {
        setTitle("Genius (Simon) - Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(new EmptyBorder(12,12,12,12));
        getContentPane().add(main);

        JPanel grid = new JPanel(new GridLayout(2,2,10,10));
        for (int i = 0; i < 4; i++) {
            JButton b = new JButton();
            b.setBackground(baseColors[i]);
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(160,160));
            final int idx = i;
            b.addActionListener(e -> handlePlayerPress(idx));
            colorButtons[i] = b;
            grid.add(b);
        }
        main.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10,10));
        startButton.addActionListener(e -> startNewGame());
        bottom.add(startButton, BorderLayout.WEST);

        roundLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottom.add(roundLabel, BorderLayout.CENTER);

        JLabel info = new JLabel("Clique Start para começar", SwingConstants.RIGHT);
        bottom.add(info, BorderLayout.EAST);

        main.add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void startNewGame() {
        sequence.clear();
        playerMoves.clear();
        nextRound();
    }

    private void nextRound() {
        playerMoves.clear();
        sequence.add(random.nextInt(4));
        roundLabel.setText("Rodada: " + sequence.size());
        playSequence();
    }

    private void playSequence() {
        playingSequence = true;
        acceptingInput = false;

        // Use Swing Timer para agendar flashes
        final int[] idx = {0};
        Timer timer = new Timer(700, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (idx[0] >= sequence.size()) {
                    ((Timer)e.getSource()).stop();
                    playingSequence = false;
                    acceptingInput = true;
                    return;
                }
                int buttonIndex = sequence.get(idx[0]);
                flashAndPlay(buttonIndex, 450);
                idx[0]++;
            }
        });
        // Start with a small delay
        timer.setInitialDelay(500);
        timer.start();
    }

    private void flashAndPlay(int index, int durationMs) {
        // Flash UI on EDT
        SwingUtilities.invokeLater(() -> {
            colorButtons[index].setBackground(lightColors[index]);
            colorButtons[index].repaint();
        });

        // play sound on separate thread so UI doesn't freeze
        new Thread(() -> {
            playTone(freqs[index], durationMs);
        }).start();

        // schedule revert of color
        new Timer(durationMs, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();
                SwingUtilities.invokeLater(() -> {
                    colorButtons[index].setBackground(baseColors[index]);
                    colorButtons[index].repaint();
                });
            }
        }).start();
    }

    private void handlePlayerPress(int index) {
        if (!acceptingInput || playingSequence) return;

        // immediate feedback
        flashAndPlay(index, 200);
        playerMoves.add(index);

        int currentMoveIndex = playerMoves.size() - 1;
        if (playerMoves.get(currentMoveIndex).intValue() != sequence.get(currentMoveIndex).intValue()) {
            // erro
            acceptingInput = false;
            showError();
            return;
        }

        // se completou a sequência corretamente
        if (playerMoves.size() == sequence.size()) {
            acceptingInput = false;
            // breve pausa antes da próxima rodada
            Timer pause = new Timer(600, e -> {
                ((Timer)e.getSource()).stop();
                nextRound();
            });
            pause.setRepeats(false);
            pause.start();
        }
    }

    private void showError() {
        // toca um som de erro
        new Thread(() -> playTone(200f, 600)).start();

        // pisca todas as cores em vermelho ou algo assim
        SwingUtilities.invokeLater(() -> {
            Color old0 = colorButtons[0].getBackground();
            for (JButton b : colorButtons) {
                b.setBackground(Color.WHITE);
            }
        });

        Timer t = new Timer(600, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();
                // restaura cores
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < 4; i++) colorButtons[i].setBackground(baseColors[i]);
                });
                // reiniciar jogo (ou repetir a mesma rodada). Aqui reiniciamos o jogo:
                sequence.clear();
                playerMoves.clear();
                roundLabel.setText("Rodada: 0");
            }
        });
        t.setRepeats(false);
        t.start();
    }

    /**
     * Gera e toca um tom senoidal (PCM 16-bit) para a frequência e duração especificadas.
     * Isso evita depender de arquivos de áudio externos.
     */
    private void playTone(float freqHz, int millis) {
        final float sampleRate = 44100;
        final int samples = (int)((millis / 1000.0) * sampleRate);
        final byte[] output = new byte[samples * 2]; // 16-bit = 2 bytes/sample

        double amplitude = 0.5; // 0.0..1.0
        for (int i = 0; i < samples; i++) {
            double time = i / sampleRate;
            double angle = 2.0 * Math.PI * freqHz * time;
            short val = (short)(Math.sin(angle) * Short.MAX_VALUE * amplitude);
            output[2*i] = (byte)(val & 0xFF);
            output[2*i+1] = (byte)((val >> 8) & 0xFF);
        }

        AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
        try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
            sdl.open(af);
            sdl.start();
            sdl.write(output, 0, output.length);
            sdl.drain();
            sdl.stop();
        } catch (LineUnavailableException ex) {
            // se falhar, silenciosamente ignora (ou log)
            System.err.println("Áudio indisponível: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Look and feel nativo (opcional)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            SimonGame g = new SimonGame();
            g.setVisible(true);
        });
    }
}
