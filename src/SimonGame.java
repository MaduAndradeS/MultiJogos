import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.text.Font;


public class SimonGame extends Application {

    private final List<Integer> sequence = new ArrayList<>();
    private final List<Integer> playerMoves = new ArrayList<>();
    private boolean acceptingInput = false;
    private boolean playingSequence = false;

    private Stage primaryStage; // para voltar ao menu

    private final Color[] baseColors = {
            Color.web("#1D7CF2"), // azul
            Color.web("#E6322A"), // vermelho
            Color.web("#F2D01D"), // amarelo
            Color.web("#29B573")  // verde
    };

    private final Rectangle[] tiles = new Rectangle[4];
    private final Random random = new Random();
    private final Label roundLabel = new Label("Rodada: 0");

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        stage.setTitle("Genius - JavaFX");

        // 1. --- Título ---
        Label titulo = new Label("Jogo Genius");
        titulo.setFont(Font.font("Arial", 36));
        titulo.setTextFill(Color.WHITE);
        titulo.setStyle("-fx-padding: 20 0 10 0;"); // Espaçamento acima e abaixo

        // --- Tabuleiro (GridPane) ---
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefSize(400, 400);

        for (int i = 0; i < 4; i++) {
            Rectangle r = new Rectangle(160, 160, baseColors[i]);
            r.setArcWidth(30);
            r.setArcHeight(30);

            final int idx = i;
            r.setOnMouseClicked(e -> handlePlayerPress(idx));

            tiles[i] = r;
            grid.add(r, i % 2, i / 2);
        }

        // --- Botões de Controle ---
        Button btnReiniciar = new Button("Reiniciar Jogo");
        btnReiniciar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 15");
        btnReiniciar.setMinSize(180, 50); // Exemplo de aumento
        btnReiniciar.setOnAction(e -> startNewGame());

        Button btnVoltarMenu = new Button("Voltar ao Menu");
        btnVoltarMenu.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 15");
        btnVoltarMenu.setMinSize(180, 50); // Exemplo de aumento
        btnVoltarMenu.setOnAction(e -> voltarAoMenu());

        HBox controles = new HBox(20, btnReiniciar, btnVoltarMenu);
        controles.setAlignment(Pos.CENTER);
        controles.setStyle("-fx-padding: 10 0 20 0;"); // Espaçamento abaixo para afastar da borda

        // Botão Start que será removido e o jogo iniciará no primeiro clique
        // Opcional: Se quiser que o jogo comece imediatamente, você pode chamar startNewGame() aqui

        // --- Layout Principal (VBox) ---
        // Ordem: Título, roundLabel, Tabuleiro, Controles
        VBox layout = new VBox(20, titulo, roundLabel, grid, controles);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:#222;");

        roundLabel.setTextFill(Color.WHITE);
        roundLabel.setStyle("-fx-font-size: 22px;");

        Scene scene = new Scene(layout, 800, 800);
        stage.setScene(scene);
        stage.show();

        // Inicia o jogo imediatamente ao carregar
        startNewGame();
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

    private void handlePlayerPress(int index) {
        if (!acceptingInput || playingSequence) return;

        flashTileClick(index);
        playSound(index);

        playerMoves.add(index);
        int step = playerMoves.size() - 1;

        if (!playerMoves.get(step).equals(sequence.get(step))) {
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

    private void playSequence() {
        playingSequence = true;
        acceptingInput = false;

        SequentialTransition seq = new SequentialTransition();

        for (int index : sequence) {

            FillTransition ft = new FillTransition(Duration.millis(300),
                    tiles[index],
                    baseColors[index],
                    Color.WHITE
            );
            ft.setAutoReverse(true);
            ft.setCycleCount(2);

            ft.setOnFinished(e -> playSound(index));

            PauseTransition wait = new PauseTransition(Duration.millis(150));

            seq.getChildren().addAll(ft, wait);
        }

        seq.setOnFinished(e -> {
            playingSequence = false;
            acceptingInput = true;
        });

        seq.play();
    }

    private void flashTileClick(int index) {
        FillTransition ft = new FillTransition(Duration.millis(200),
                tiles[index],
                baseColors[index],
                Color.WHITE
        );
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

    private void showError() {
        playingSequence = false;
        acceptingInput = false;

        for (Rectangle r : tiles) {
            r.setFill(Color.WHITE);
        }

        playErrorSound();

        PauseTransition pause = new PauseTransition(Duration.millis(700));
        pause.setOnFinished(e -> {

            // volta às cores normais antes de perguntar
            for (int i = 0; i < 4; i++)
                tiles[i].setFill(baseColors[i]);

            perguntarVoltarMenu();  // <-- AQUI
        });
        pause.play();
    }

    private void perguntarVoltarMenu() {

        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Fim de jogo");
            alert.setHeaderText("Você errou!");
            alert.setContentText("O que você deseja ?");

            ButtonType sim = new ButtonType("Menu");
            ButtonType nao = new ButtonType("Jogar Novamente");

            alert.getButtonTypes().setAll(sim, nao);

            alert.showAndWait().ifPresent(res -> {
                if (res == sim) {
                    try {
                        primaryStage.close();
                        new Menu().start(new Stage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    startNewGame();
                }
            });
        });
    }


    private void playSound(int index) {
        switch (index) {
            case 0 -> SoundGenerator.playTone(440, 300); // Azul
            case 1 -> SoundGenerator.playTone(554, 300); // Vermelho
            case 2 -> SoundGenerator.playTone(659, 300); // Amarelo
            case 3 -> SoundGenerator.playTone(784, 300); // Verde
        }
    }

    private void voltarAoMenu() {

        try {

            new Menu().start(new Stage());
            primaryStage.close(); // Fecha a janela do Jogo Genius
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void playErrorSound() {
        SoundGenerator.playTone(120, 500);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
