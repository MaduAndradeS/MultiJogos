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
import java.util.Objects;
import java.util.Random;
import javafx.scene.text.Font;

public class SimonGame extends Application {

    private final List<Integer> sequence = new ArrayList<>();
    private final List<Integer> playerMoves = new ArrayList<>();
    private boolean acceptingInput = false;
    private boolean playingSequence = false;

    private Stage primaryStage;

    private final Color[] baseColors = {
            Color.web("#00ff6a"),
            Color.web("#ff2e2e"),
            Color.web("#ffe600"),
            Color.web("#1e90ff")
    };

    private final Rectangle[] tiles = new Rectangle[4];
    private final Random random = new Random();
    private final Label roundLabel = new Label("Rodada: 0");

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        stage.setTitle("Genius ");

        Font arcade = Font.loadFont(
                Objects.requireNonNull(getClass().getResourceAsStream("/fonts/ARCADE.ttf")),
                28
        );

        Label titulo = new Label("JOGO GENIUS");
        titulo.setFont(Font.font("Arcade", 70));
        titulo.setTextFill(Color.web("#ffb8ff"));
        titulo.setStyle("-fx-effect: dropshadow(gaussian, #ff4df2, 30, 0.6, 0, 0);");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(12);
        grid.setVgap(12);

        for (int i = 0; i < 4; i++) {
            Rectangle r = new Rectangle(180, 180, baseColors[i]);
            r.setArcWidth(40);
            r.setArcHeight(40);

            r.setStroke(Color.web("#ff4df2"));
            r.setStrokeWidth(4);

            final int idx = i;
            r.setOnMouseClicked(e -> handlePlayerPress(idx));

            tiles[i] = r;
            grid.add(r, i % 2, i / 2);
        }

        Button btnReiniciar = new Button("REINICIAR");
        estiloBotaoArcade(btnReiniciar);

        Button btnVoltarMenu = new Button("MENU");
        estiloBotaoArcade(btnVoltarMenu);

        btnReiniciar.setOnAction(e -> startNewGame());
        btnVoltarMenu.setOnAction(e -> voltarAoMenu());

        HBox controles = new HBox(25, btnReiniciar, btnVoltarMenu);
        controles.setAlignment(Pos.CENTER);
        controles.setStyle("-fx-padding: 10 0 20 0;");

        roundLabel.setFont(Font.font("Arcade", 32));
        roundLabel.setTextFill(Color.web("#ffbfff"));
        roundLabel.setStyle("-fx-effect: dropshadow(gaussian, #ff4df2, 20, 0.4, 0, 0);");

        VBox layout = new VBox(25, titulo, roundLabel, grid, controles);
        layout.setAlignment(Pos.CENTER);

        layout.setStyle(
                "-fx-background-image: url(fundogenius.png); -fx-background-size: cover; " +
                        "-fx-background-repeat: no-repeat; -fx-background-position: center center;"
        );

        Scene scene = new Scene(layout, 800, 800);
        stage.setScene(scene);
        stage.show();

        startNewGame();
    }



    private void estiloBotaoArcade(Button b) {
        b.setMinSize(230, 65);
        b.setFont(Font.font("Arcade", 26));
        b.setTextFill(Color.WHITE);

        b.setStyle(
                "-fx-background-color: rgba(255, 77, 242, 0.25);" +
                        "-fx-border-color: #ff4df2;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 20px;" +
                        "-fx-background-radius: 20px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,77,242,0.6), 18, 0.3, 0, 0);"
        );

        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: rgba(255, 77, 242, 0.45);" +
                        "-fx-border-color: #ff99f7;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 20px;" +
                        "-fx-background-radius: 20px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,77,242,0.9), 25, 0.4, 0, 0);"
        ));

        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: rgba(255, 77, 242, 0.25);" +
                        "-fx-border-color: #ff4df2;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 20px;" +
                        "-fx-background-radius: 20px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,77,242,0.6), 18, 0.3, 0, 0);"
        ));
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

        for (Rectangle r : tiles)
            r.setFill(Color.WHITE);

        playErrorSound();

        PauseTransition pause = new PauseTransition(Duration.millis(700));
        pause.setOnFinished(e -> {
            for (int i = 0; i < 4; i++)
                tiles[i].setFill(baseColors[i]);

            perguntarVoltarMenu();
        });
        pause.play();
    }

    private void perguntarVoltarMenu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fim de jogo");
        alert.setHeaderText("Você errou!");
        alert.setContentText("O que deseja fazer?");

        ButtonType menu = new ButtonType("Menu");
        ButtonType retry = new ButtonType("Jogar Novamente");

        alert.getButtonTypes().setAll(menu, retry);

        alert.showAndWait().ifPresent(res -> {
            if (res == menu) {
                try {
                    primaryStage.close();
                    new Menu().start(new Stage());
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                startNewGame();
            }
        });
    }

    private void playSound(int index) {
        switch (index) {
            case 0 -> SoundGenerator.playTone(440, 300);
            case 1 -> SoundGenerator.playTone(554, 300);
            case 2 -> SoundGenerator.playTone(659, 300);
            case 3 -> SoundGenerator.playTone(784, 300);
        }
    }

    private void voltarAoMenu() {
        try {
            new Menu().start(new Stage());
            primaryStage.close();
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
