import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JogoDaVelha extends Application {

    private Stage primaryStage; // guardamos a stage pra poder fechá-la ao voltar ao menu
    private Button[][] botoes = new Button[3][3];
    private String jogadorAtual = "X";
    private boolean jogoAtivo = true;

    private final Color azulEscuro = Color.rgb(0, 0, 0);

    @Override
    public void start(Stage stage) {
        AudioPlayer.playLoop("/musica_velha.wav"); // música do jogo da velha

        this.primaryStage = stage; // salvar referência
        stage.setTitle("Jogo da Velha - JavaFX");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        inicializarTabuleiro(grid);

        Scene scene = new Scene(grid, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void inicializarTabuleiro(GridPane grid) {
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 3; coluna++) {
                Button btn = new Button("");
                btn.setFont(Font.font("Times New Roman", 48));
                btn.setMinSize(120, 120);
                btn.setStyle("-fx-background-color: rgb(76,74,74); -fx-text-fill: #55483b;");

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
        btn.setTextFill(azulEscuro);

        if (jogadorAtual.equals("X")) {
            btn.setStyle("-fx-background-color: rgb(0,0,0); -fx-text-fill: rgb(255,0,0);");
        } else {
            btn.setStyle("-fx-background-color: rgb(255,255,255); -fx-text-fill: rgb(0,53,255);");
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
        // Linhas e colunas
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

        // Diagonais
        if (!botoes[0][0].getText().isEmpty() &&
                botoes[0][0].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][2].getText().equals(jogadorAtual))
            return true;

        if (!botoes[0][2].getText().isEmpty() &&
                botoes[0][2].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][0].getText().equals(jogadorAtual))
            return true;

        return false;
    }

    private int[][] coordenadasVitoria() {
        // Retorna as coordenadas dos botões que formam a vitória
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
                botoes[2][2].getText().equals(jogadorAtual))
            return new int[][]{{0,0},{1,1},{2,2}};

        if (!botoes[0][2].getText().isEmpty() &&
                botoes[0][2].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][0].getText().equals(jogadorAtual))
            return new int[][]{{0,2},{1,1},{2,0}};

        return null;
    }

    private boolean verificarEmpate() {
        for (Button[] linha : botoes) {
            for (Button b : linha) {
                if (b.getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void mostrarReiniciarOuMenu(String mensagem) {
        // Usamos CONFIRMATION com dois botões: Sim e Menu
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Resultado");
        alert.setHeaderText(mensagem);
        alert.setContentText("Deseja jogar novamente?");

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnMenu = new ButtonType("Menu");
        alert.getButtonTypes().setAll(btnSim, btnMenu);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == btnSim) {
                reiniciarJogo(); // apenas reinicia o tabuleiro (mesma janela)
            } else if (resposta == btnMenu) {
                // Abre o Menu em NOVA janela e fecha a janela atual do jogo
                try {
                    new Menu().start(new Stage()); // abre novo Stage com o menu
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    primaryStage.close(); // fecha a janela do jogo (sempre)
                }
            }
        });
    }

    private void reiniciarJogo() {
        jogadorAtual = "X";
        jogoAtivo = true;
        for (Button[] linha : botoes) {
            for (Button b : linha) {
                b.setText("");
                b.setStyle("-fx-background-color: rgb(76,74,74); -fx-text-fill: #55483b;");
            }
        }
    }

    private void piscarVencedor(int[][] coordenadas) {
        if (coordenadas == null) return;

        int ciclos = 4;
        Duration dur = Duration.millis(200);

        for (int[] c : coordenadas) {
            Button b = botoes[c[0]][c[1]];

            for (int i = 0; i < ciclos; i++) {
                PauseTransition p1 = new PauseTransition(dur.multiply(i * 2));
                PauseTransition p2 = new PauseTransition(dur.multiply(i * 2 + 1));

                p1.setOnFinished(e -> b.setStyle("-fx-background-color: yellow; -fx-text-fill:" +
                        (jogadorAtual.equals("X") ? "rgb(255,0,0)" : "rgb(0,53,255)") + ";"));

                p2.setOnFinished(e -> b.setStyle("-fx-background-color:" +
                        (jogadorAtual.equals("X") ? "rgb(0,0,0)" : "rgb(255,255,255)") + "; -fx-text-fill:" +
                        (jogadorAtual.equals("X") ? "rgb(255,0,0)" : "rgb(0,53,255)") + ";"));

                p1.play();
                p2.play();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
