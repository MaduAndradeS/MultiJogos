import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class JogoDaVelha extends Application {

    private Stage primaryStage;
    private final Button[][] botoes = new Button[3][3];
    private String jogadorAtual = "X";
    private boolean jogoAtivo = true;
    private boolean contraIA = true; // true = jogar contra IA; false = 2 jogadores

    private static final String NEON_X = "rgb(255,60,60)";
    private static final String NEON_O = "rgb(70,170,255)";
    private static final String NEON_ROXO = "rgb(160,60,255)";

    @Override
    public void start(Stage stage) {
        AudioPlayer.playLoop("/musica_velha.wav");

        this.primaryStage = stage;
        stage.setTitle("Jogo da Velha");

        Alert escolherModo = new Alert(Alert.AlertType.CONFIRMATION);
        escolherModo.setTitle("Modo de Jogo");
        escolherModo.setHeaderText("Escolha o modo de jogo:");
        ButtonType btnIA = new ButtonType("Contra IA");
        ButtonType btnPvP = new ButtonType("2 Jogadores");
        escolherModo.getButtonTypes().setAll(btnIA, btnPvP);
        Optional<ButtonType> modo = escolherModo.showAndWait();

        if (modo.isEmpty()) {
            Platform.exit();
            return;
        }

        contraIA = modo.get() == btnIA;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-image: url('/fundovelha.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: no-repeat;");

        root.setTop(criarTitulo());

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);

        inicializarTabuleiro(grid);
        root.setCenter(grid);

        root.setBottom(criarBotoesControle());

        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    private void inicializarTabuleiro(GridPane grid) {
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 3; coluna++) {
                Button btn = new Button("");
                btn.setFont(Font.font("Arcade", 50));
                btn.setMinSize(150, 150);
                btn.setStyle("-fx-background-color: rgb(30,0,45); -fx-text-fill: #542b70;");

                final int l = linha;
                final int c = coluna;

                btn.setOnAction(_ -> cliqueBotao(btn, l, c));

                botoes[linha][coluna] = btn;
                grid.add(btn, coluna, linha);
            }
        }

        jogadorAtual = "X";
        jogoAtivo = true;
    }

    private void cliqueBotao(Button btn, int linha, int coluna) {
        if (!jogoAtivo || !btn.getText().isEmpty()) return;

        btn.setText(jogadorAtual);
        if (jogadorAtual.equals("X")) {
            btn.setStyle("""
                -fx-background-color: rgb(60,0,20);
                -fx-text-fill: %s;
                -fx-font-family: Arcade;
                -fx-font-size: 62px;
                -fx-effect: dropshadow(gaussian, rgba(255,60,60,1), 25, 0, 0, 0);
                """.formatted(NEON_X));
        } else {
            btn.setStyle("""
                -fx-background-color: rgb(0,15,40);
                -fx-text-fill: %s;
                -fx-font-family: Arcade;
                -fx-font-size: 62px;
                -fx-effect: dropshadow(gaussian, rgba(70,170,255,1), 25, 0, 0, 0);
                """.formatted(NEON_O));
        }

        if (verificarVitoria(jogadorAtual)) {
            jogoAtivo = false;
            piscarVencedor(coordenadasVitoria(jogadorAtual), jogadorAtual,
                    () -> Platform.runLater(() -> mostrarReiniciarOuMenu("Jogador " + jogadorAtual + " venceu!")));
            return;
        }

        if (verificarEmpate()) {
            jogoAtivo = false;
            Platform.runLater(() -> mostrarReiniciarOuMenu("Deu velha!"));
            return;
        }

        if (contraIA) {
            if (jogadorAtual.equals("X")) {
                jogadorAtual = "O";
                PauseTransition esperar = new PauseTransition(Duration.millis(350));
                esperar.setOnFinished(_ -> {
                    jogadaIA();

                    if (verificarVitoria("O")) {
                        jogoAtivo = false;
                        piscarVencedor(coordenadasVitoria("O"), "O",
                                () -> Platform.runLater(() -> mostrarReiniciarOuMenu("IA venceu!")));
                    } else if (verificarEmpate()) {
                        jogoAtivo = false;
                        Platform.runLater(() -> mostrarReiniciarOuMenu("Deu velha!"));
                    }
                });
                esperar.play();
            }
        } else {
            jogadorAtual = jogadorAtual.equals("X") ? "O" : "X";
        }
    }


    private boolean verificarVitoria(String simbolo) {
        for (int i = 0; i < 3; i++) {
            if (!botoes[i][0].getText().isEmpty() &&
                    botoes[i][0].getText().equals(simbolo) &&
                    botoes[i][1].getText().equals(simbolo) &&
                    botoes[i][2].getText().equals(simbolo))
                return true;

            if (!botoes[0][i].getText().isEmpty() &&
                    botoes[0][i].getText().equals(simbolo) &&
                    botoes[1][i].getText().equals(simbolo) &&
                    botoes[2][i].getText().equals(simbolo))
                return true;
        }

        if (!botoes[0][0].getText().isEmpty() &&
                botoes[0][0].getText().equals(simbolo) &&
                botoes[1][1].getText().equals(simbolo) &&
                botoes[2][2].getText().equals(simbolo))
            return true;

        return !botoes[0][2].getText().isEmpty() &&
                botoes[0][2].getText().equals(simbolo) &&
                botoes[1][1].getText().equals(simbolo) &&
                botoes[2][0].getText().equals(simbolo);
    }

    private int[][] coordenadasVitoria(String simbolo) {
        for (int i = 0; i < 3; i++) {
            if (!botoes[i][0].getText().isEmpty() &&
                    botoes[i][0].getText().equals(simbolo) &&
                    botoes[i][1].getText().equals(simbolo) &&
                    botoes[i][2].getText().equals(simbolo))
                return new int[][]{{i,0},{i,1},{i,2}};

            if (!botoes[0][i].getText().isEmpty() &&
                    botoes[0][i].getText().equals(simbolo) &&
                    botoes[1][i].getText().equals(simbolo) &&
                    botoes[2][i].getText().equals(simbolo))
                return new int[][]{{0,i},{1,i},{2,i}};
        }

        if (!botoes[0][0].getText().isEmpty() &&
                botoes[0][0].getText().equals(simbolo) &&
                botoes[1][1].getText().equals(simbolo) &&
                botoes[2][2].getText().equals(simbolo))
            return new int[][]{{0,0},{1,1},{2,2}};

        if (!botoes[0][2].getText().isEmpty() &&
                botoes[0][2].getText().equals(simbolo) &&
                botoes[1][1].getText().equals(simbolo) &&
                botoes[2][0].getText().equals(simbolo))
            return new int[][]{{0,2},{1,1},{2,0}};

        return null;
    }

    private boolean verificarEmpate() {
        for (Button[] linha : botoes)
            for (Button b : linha)
                if (b.getText().isEmpty()) return false;

        return true;
    }

    private void mostrarReiniciarOuMenu(String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Resultado");
            alert.setHeaderText(mensagem);
            alert.setContentText("Deseja jogar novamente?");

            ButtonType btnSim = new ButtonType("Sim");
            ButtonType btnMenu = new ButtonType("Menu");

            alert.getButtonTypes().setAll(btnSim, btnMenu);

            Optional<ButtonType> resposta = alert.showAndWait();
            resposta.ifPresent(r -> {
                if (r == btnSim) {
                    reiniciarJogo();
                } else {
                    try {
                        new Menu().start(new Stage());
                    } catch (Exception ignored) { }
                    primaryStage.close();
                }
            });
        });
    }

    private void reiniciarJogo() {
        jogoAtivo = true;
        jogadorAtual = "X";

        for (Button[] linha : botoes) {
            for (Button b : linha) {
                b.setText("");
                b.setStyle("-fx-background-color: rgb(30,0,45); -fx-text-fill: #542b70;");
            }
        }

        // Ao reiniciar, pergunta novamente o modo
        start(primaryStage);
    }

    private void piscarVencedor(int[][] coord, String simboloVencedor, Runnable onFinished) {
        if (coord == null) {
            if (onFinished != null) onFinished.run();
            return;
        }

        final int flashes = 5;
        final double meio = 150;
        final double totalMillis = flashes * 2 * meio + 60;

        for (int[] c : coord) {
            Button btn = botoes[c[0]][c[1]];

            for (int i = 0; i < flashes; i++) {
                PauseTransition p1 = new PauseTransition(Duration.millis(meio * (i * 2)));
                PauseTransition p2 = new PauseTransition(Duration.millis(meio * (i * 2 + 1)));

                p1.setOnFinished(_ -> btn.setStyle("-fx-background-color: rgb(120,0,180);" +
                        "-fx-text-fill: white;" +
                        "-fx-effect: dropshadow(gaussian, " + NEON_ROXO + ", 50, 0, 0, 0);"));

                p2.setOnFinished(_ -> {
                    if (simboloVencedor.equals("X"))
                        btn.setStyle("-fx-background-color: rgb(60,0,20); -fx-text-fill:" + NEON_X + ";");
                    else
                        btn.setStyle("-fx-background-color: rgb(0,15,40); -fx-text-fill:" + NEON_O + ";");
                });

                p1.play();
                p2.play();
            }
        }

        PauseTransition fim = new PauseTransition(Duration.millis(totalMillis));
        fim.setOnFinished(_ -> {
            if (onFinished != null) onFinished.run();
        });
        fim.play();
    }

    private VBox criarTitulo() {
        Label titulo = new Label("JOGO DA VELHA");
        titulo.setFont(Font.font("Arcade", 68));
        titulo.setStyle("-fx-text-fill: #4200ae; -fx-font-weight: bold;");

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#C900FF"));
        glow.setRadius(70);
        glow.setSpread(0.8);

        titulo.setEffect(glow);

        VBox box = new VBox(titulo);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 100 0 0 0;");
        return box;
    }

    private HBox criarBotoesControle() {
        String estilo = """
                -fx-font-family: Arcade;
                -fx-font-size: 24px;
                -fx-text-fill: white;
                -fx-background-color: linear-gradient(#6a00ff, #4c00b8);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: #d9b3ff;
                -fx-border-width: 3;
                -fx-padding: 12 28;
                -fx-effect: dropshadow(gaussian, rgba(160,60,255,0.9), 25, 0, 0, 0);
                """;

        String estiloHover = estilo +
                "-fx-background-color: linear-gradient(#8a2bff, #5900cc);" +
                "-fx-effect: dropshadow(gaussian, rgba(200,120,255,1), 35, 0, 0, 0);";

        Button btnReiniciar = new Button("Reiniciar");
        btnReiniciar.setStyle(estilo);

        Button btnMenu = new Button("Menu");
        btnMenu.setStyle(estilo);

        btnReiniciar.setOnMouseEntered(_ -> btnReiniciar.setStyle(estiloHover));
        btnReiniciar.setOnMouseExited(_ -> btnReiniciar.setStyle(estilo));

        btnMenu.setOnMouseEntered(_ -> btnMenu.setStyle(estiloHover));
        btnMenu.setOnMouseExited(_ -> btnMenu.setStyle(estilo));

        btnReiniciar.setOnAction(_ -> reiniciarJogo());

        btnMenu.setOnAction(_ -> {
            AudioPlayer.stop();
            try {
                new Menu().start(new Stage());
            } catch (Exception ignored) { }
            primaryStage.close();
        });

        HBox box = new HBox(30, btnReiniciar, btnMenu);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 20 0 45 0;");

        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void jogadaIA() {
        if (!jogoAtivo) return;

        if (tentarJogar("O")) return;
        if (tentarJogar("X")) return;

        if (botoes[1][1].getText().isEmpty()) {
            marcarIA(botoes[1][1]);
            return;
        }

        int[][] cantos = {{0,0},{0,2},{2,0},{2,2}};
        for (int[] c : cantos) {
            if (botoes[c[0]][c[1]].getText().isEmpty()) {
                marcarIA(botoes[c[0]][c[1]]);
                return;
            }
        }

        int[][] lados = {{0,1},{1,0},{1,2},{2,1}};
        for (int[] c : lados) {
            if (botoes[c[0]][c[1]].getText().isEmpty()) {
                marcarIA(botoes[c[0]][c[1]]);
                return;
            }
        }
    }

    private boolean tentarJogar(String simbolo) {
        for (int i = 0; i < 3; i++) {
            int count = 0, vazio = -1;
            for (int j = 0; j < 3; j++) {
                if (botoes[i][j].getText().equals(simbolo)) count++;
                if (botoes[i][j].getText().isEmpty()) vazio = j;
            }
            if (count == 2 && vazio != -1) {
                marcarIA(botoes[i][vazio]);
                return true;
            }
        }

        for (int j = 0; j < 3; j++) {
            int count = 0, vazio = -1;
            for (int i = 0; i < 3; i++) {
                if (botoes[i][j].getText().equals(simbolo)) count++;
                if (botoes[i][j].getText().isEmpty()) vazio = i;
            }
            if (count == 2 && vazio != -1) {
                marcarIA(botoes[vazio][j]);
                return true;
            }
        }

        int count = 0, vazio = -1;
        for (int i = 0; i < 3; i++) {
            if (botoes[i][i].getText().equals(simbolo)) count++;
            if (botoes[i][i].getText().isEmpty()) vazio = i;
        }
        if (count == 2 && vazio != -1) {
            marcarIA(botoes[vazio][vazio]);
            return true;
        }

        count = 0;
        vazio = -1;
        for (int i = 0; i < 3; i++) {
            if (botoes[i][2 - i].getText().equals(simbolo)) count++;
            if (botoes[i][2 - i].getText().isEmpty()) vazio = i;
        }
        if (count == 2 && vazio != -1) {
            marcarIA(botoes[vazio][2 - vazio]);
            return true;
        }

        return false;
    }

    private void marcarIA(Button btn) {
        btn.setText("O");
        btn.setStyle("""
            -fx-background-color: rgb(0,15,40);
            -fx-text-fill: %s;
            -fx-font-family: Arcade;
            -fx-font-size: 62px;
            -fx-effect: dropshadow(gaussian, rgba(70,170,255,1), 25, 0, 0, 0);
            """.formatted(NEON_O));

        jogadorAtual = "X";
    }
}
