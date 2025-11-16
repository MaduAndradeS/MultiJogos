import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;

public class SimonGameFX extends Application {

    private final Button[] colorButtons = new Button[4];
    private final Color[] baseColors = {
            Color.web("#1D7CF2"), // azul
            Color.web("#E6322A"), // vermelho
            Color.web("#F2D01D"), // amarelo
            Color.web("#29B573")  // verde
    };
    private final Color[] lightColors = new Color[4];

    private final ArrayList<Integer> sequence = new ArrayList<>();
    private final ArrayList<Integer> playerMoves = new ArrayList<>();
    private final Random random = new Random();

    private boolean acceptingInput = false;
    private boolean playingSequence = false;

    private final float[] freqs = { 523.25f, 659.25f, 783.99f, 1046.50f };

    private Label roundLabel;
    private Button startButton;

    @Override
    public void start(Stage primaryStage) {
        for (int i = 0; i < 4; i++) lightColors[i] = baseColors[i].brighter();

        // Grid dos botões coloridos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setStyle("-fx-background-color: black; -fx-padding: 20;");

        for (int i = 0; i < 4; i++) {
            Button b = new Button();
            b.setMinSize(160, 160);
            final int idx = i;
            b.setStyle(buttonStyle(baseColors[i]));
            b.setOnAction(e -> handlePlayerPress(idx));
            colorButtons[i] = b;
            grid.add(b, i % 2, i / 2);
        }

        // Label de rodada
        roundLabel = new Label("Rodada: 0");
        roundLabel.setTextFill(Color.WHITE);
        roundLabel.setFont(Font.font("Arial", 20));

        // Botão start
        startButton = new Button("Start");
        startButton.setFont(Font.font("Arial", 16));
        startButton.setOnAction(e -> startNewGame());

        HBox controls = new HBox(20, startButton, roundLabel);
        controls.setStyle("-fx-alignment: center; -fx-padding: 10;");

        VBox root = new VBox(15, grid, controls);
        root.setStyle("-fx-background-color: black; -fx-padding: 20;");

        Scene scene = new Scene(root);
        primaryStage.setTitle("Genius (Simon) - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
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
        PauseTransition delay = new PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> playStep(0));
        delay.play();
    }

    private void playStep(int step) {
        if (step >= sequence.size()) {
            playingSequence = false;
            acceptingInput = true;
            return;
        }
        int idx = sequence.get(step);
        flashButton(idx, 450);
        PauseTransition pause = new PauseTransition(Duration.millis(600));
        pause.setOnFinished(e -> playStep(step + 1));
        pause.play();
    }

    private void flashButton(int idx, int durationMs) {
        colorButtons[idx].setStyle(buttonStyle(lightColors[idx]));
        new Thread(() -> playTone(freqs[idx], durationMs)).start();

        PauseTransition pause = new PauseTransition(Duration.millis(durationMs));
        pause.setOnFinished(e -> colorButtons[idx].setStyle(buttonStyle(baseColors[idx])));
        pause.play();
    }

    private void handlePlayerPress(int idx) {
        if (!acceptingInput || playingSequence) return;

        flashButton(idx, 200);
        playerMoves.add(idx);

        int current = playerMoves.size() - 1;
        if (!playerMoves.get(current).equals(sequence.get(current))) {
            acceptingInput = false;
            showError();
            return;
        }

        if (playerMoves.size() == sequence.size()) {
            acceptingInput = false;
            PauseTransition pause = new PauseTransition(Duration.millis(600));
            pause.setOnFinished(e -> nextRound());
            pause.play();
        }
    }

    private void showError() {
        // Pisca branco
        for (Button b : colorButtons) b.setStyle(buttonStyle(Color.WHITE));
        new Thread(() -> playTone(200, 600)).start();

        PauseTransition pause = new PauseTransition(Duration.millis(600));
        pause.setOnFinished(e -> {
            for (int i = 0; i < 4; i++) colorButtons[i].setStyle(buttonStyle(baseColors[i]));
            startNewGame();
        });
        pause.play();
    }

    private String buttonStyle(Color c) {
        return String.format("-fx-background-color: rgb(%d,%d,%d); -fx-border-radius: 20; -fx-background-radius: 20;",
                (int)(c.getRed()*255),
                (int)(c.getGreen()*255),
                (int)(c.getBlue()*255));
    }

    private void playTone(float freqHz, int millis) {
        final float sampleRate = 44100;
        final int samples = (int)((millis / 1000.0) * sampleRate);
        final byte[] output = new byte[samples * 2];
        double amplitude = 0.5;

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
            System.err.println("Áudio indisponível: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
