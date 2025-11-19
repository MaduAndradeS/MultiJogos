import javafx.animation.PauseTransition;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class JogoDaVelha extends Application {

    private Stage primaryStage;
    private Button[][] botoes = new Button[3][3];
    private String jogadorAtual = "X";
    private boolean jogoAtivo = true;

    // Cores neon
    private final String NEON_X = "rgb(255,60,60)";      // vermelho neon
    private final String NEON_O = "rgb(70,170,255)";     // azul neon
    private final String NEON_ROXO = "rgb(160,60,255)";  // roxo neon

    @Override
    public void start(Stage stage) {
        AudioPlayer.playLoop("/musica_velha.wav");

        this.primaryStage = stage;
        stage.setTitle("Jogo da Velha");

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

                // Cor neutra roxa escura neon
                btn.setStyle("-fx-background-color: rgb(30,0,45);" +   // roxo escuro
                        "-fx-text-fill: #542b70;");                    // roxo apagado

                int l = linha;
                int c = coluna;
                btn.setOnAction(e -> cliqueBotao(btn, l, c));

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
            btn.setStyle("-fx-background-color: rgb(60,0,20);" +       // vermelho neon escuro
                    "-fx-text-fill: " + NEON_X + ";" +
                    "-fx-font-family: Arcade;" +
                    "-fx-font-size: 62px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(255,60,60,1), 25, 0, 0, 0);");
        } else {
            btn.setStyle("-fx-background-color: rgb(0,15,40);" +      // azul neon escuro
                    "-fx-text-fill: " + NEON_O + ";" +
                    "-fx-font-family: Arcade;" +
                    "-fx-font-size: 62px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(70,170,255,1), 25, 0, 0, 0);");
        }

        if (verificarVitoria()) {
            jogoAtivo = false;
            piscarVencedor(coordenadasVitoria());
            mostrarReiniciarOuMenu("Jogador " + jogadorAtual + " venceu!");
        } else if (verificarEmpate()) {
            jogoAtivo = false;
            mostrarReiniciarOuMenu("Deu velha!");
        } else {
            jogadorAtual = jogadorAtual.equals("X") ? "O" : "X";
        }
    }

    private boolean verificarVitoria() {
        for (int i = 0; i < 3; i++) {

            if (!botoes[i][0].getText().isEmpty() &&
                    botoes[i][0].getText().equals(jogadorAtual) &&
                    botoes[i][1].getText().equals(jogadorAtual) &&
                    botoes[i][2].getText().equals(jogadorAtual))
                return true;

            if (!botoes[0][i].getText().isEmpty() &&
                    botoes[0][i].getText().equals(jogadorAtual) &&
                    botoes[1][i].getText().equals(jogadorAtual) &&
                    botoes[2][i].getText().equals(jogadorAtual))
                return true;
        }

        if (!botoes[0][0].getText().isEmpty() &&
                botoes[0][0].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][2].equals(jogadorAtual))
            return true;

        if (!botoes[0][2].getText().isEmpty() &&
                botoes[0][2].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][0].getText().equals(jogadorAtual))
            return true;

        return false;
    }

    private int[][] coordenadasVitoria() {
        for (int i = 0; i < 3; i++) {

            if (!botoes[i][0].getText().isEmpty() &&
                    botoes[i][0].getText().equals(jogadorAtual) &&
                    botoes[i][1].getText().equals(jogadorAtual) &&
                    botoes[i][2].getText().equals(jogadorAtual))
                return new int[][]{{i,0},{i,1},{i,2}};

            if (!botoes[0][i].getText().isEmpty() &&
                    botoes[0][i].getText().equals(jogadorAtual) &&
                    botoes[1][i].getText().equals(jogadorAtual) &&
                    botoes[2][i].getText().equals(jogadorAtual))
                return new int[][]{{0,i},{1,i},{2,i}};
        }

        if (!botoes[0][0].getText().isEmpty() &&
                botoes[0][0].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][2].equals(jogadorAtual))
            return new int[][]{{0,0},{1,1},{2,2}};

        if (!botoes[0][2].getText().isEmpty() &&
                botoes[0][2].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][0].getText().equals(jogadorAtual))
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Resultado");
        alert.setHeaderText(mensagem);
        alert.setContentText("Deseja jogar novamente?");

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnMenu = new ButtonType("Menu");

        alert.getButtonTypes().setAll(btnSim, btnMenu);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == btnSim) {
                reiniciarJogo();
            } else {
                try {
                    new Menu().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                primaryStage.close();
            }
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
    }

    private void piscarVencedor(int[][] coord) {
        if (coord == null) return;

        for (int[] c : coord) {
            Button btn = botoes[c[0]][c[1]];

            for (int i = 0; i < 5; i++) {

                PauseTransition p1 = new PauseTransition(Duration.millis(150 * (i * 2)));
                PauseTransition p2 = new PauseTransition(Duration.millis(150 * (i * 2 + 1)));

                // Efeito neon roxo na vitória
                p1.setOnFinished(e ->
                        btn.setStyle("-fx-background-color: rgb(120,0,180);" +
                                "-fx-text-fill: white;" +
                                "-fx-effect: dropshadow(gaussian, " + NEON_ROXO + ", 50, 0, 0, 0);"));

                p2.setOnFinished(e -> {
                    if (jogadorAtual.equals("X"))
                        btn.setStyle("-fx-background-color: rgb(60,0,20); -fx-text-fill:" + NEON_X + ";");
                    else
                        btn.setStyle("-fx-background-color: rgb(0,15,40); -fx-text-fill:" + NEON_O + ";");
                });

                p1.play();
                p2.play();
            }
        }
    }

    private VBox criarTitulo() {
        Label titulo = new Label("JOGO DA VELHA");
        titulo.setFont(Font.font("Arcade", 68));

        // cor neon clara para aparecer em fundo escuro
        titulo.setStyle(
                "-fx-text-fill: #4200ae;" +  // roxo neon
                        "-fx-font-weight: bold;"
        );

        // Glow NEON forte via DropShadow
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#C900FF")); // cor do brilho
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        glow.setRadius(70);  // intensidade
        glow.setSpread(0.8); // espessura

        titulo.setEffect(glow);

        VBox box = new VBox(titulo);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 100 0 0 0;");
        return box;
    }


    private HBox criarBotoesControle() {

        String estilo =
                "-fx-font-family: Arcade;" +
                        "-fx-font-size: 24px;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: linear-gradient(#6a00ff, #4c00b8);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #d9b3ff;" +
                        "-fx-border-width: 3;" +
                        "-fx-padding: 12 28;" +
                        "-fx-effect: dropshadow(gaussian, rgba(160,60,255,0.9), 25, 0, 0, 0);";

        Button btnReiniciar = new Button("Reiniciar");
        btnReiniciar.setStyle(estilo);

        Button btnMenu = new Button("Menu");
        btnMenu.setStyle(estilo);

        // Hover neon
        btnReiniciar.setOnMouseEntered(e -> btnReiniciar.setStyle(estilo +
                "-fx-background-color: linear-gradient(#8a2bff, #5900cc);" +
                "-fx-effect: dropshadow(gaussian, rgba(200,120,255,1), 35, 0, 0, 0);"));

        btnReiniciar.setOnMouseExited(e -> btnReiniciar.setStyle(estilo));

        btnMenu.setOnMouseEntered(e -> btnMenu.setStyle(estilo +
                "-fx-background-color: linear-gradient(#8a2bff, #5900cc);" +
                "-fx-effect: dropshadow(gaussian, rgba(200,120,255,1), 35, 0, 0, 0);"));

        btnMenu.setOnMouseExited(e -> btnMenu.setStyle(estilo));

        btnReiniciar.setOnAction(e -> reiniciarJogo());

        btnMenu.setOnAction(e -> {
            AudioPlayer.stop();
            try {
                new Menu().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
}
