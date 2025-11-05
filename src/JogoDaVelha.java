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

public class JogoDaVelha extends Application {

    private Button[][] botoes = new Button[3][3];
    private String jogadorAtual = "X";
    private boolean jogoAtivo = true;

    // 🎨 Cores
    private final Color azulEscuro = Color.rgb(72, 61, 139);
    private final Color azulNeon = Color.rgb(0, 191, 255);
    private final Color magenta = Color.rgb(153, 50, 204);

    @Override
    public void start(Stage stage) {
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
                btn.setFont(Font.font("Arial", 48));
                btn.setMinSize(120, 120);
                btn.setStyle("-fx-background-color: rgb(72,61,139); -fx-text-fill: white;");

                int l = linha;
                int c = coluna;
                btn.setOnAction(e -> cliqueBotao(btn, l, c));

                botoes[linha][coluna] = btn;
                grid.add(btn, coluna, linha);
            }
        }
    }

    private void cliqueBotao(Button btn, int linha, int coluna) {
        if (!jogoAtivo || !btn.getText().isEmpty()) return;

        btn.setText(jogadorAtual);
        btn.setTextFill(azulEscuro);

        if (jogadorAtual.equals("X")) {
            btn.setStyle("-fx-background-color: rgb(0,191,255); -fx-text-fill: rgb(72,61,139);");
        } else {
            btn.setStyle("-fx-background-color: rgb(153,50,204); -fx-text-fill: rgb(72,61,139);");
        }

        if (verificarVitoria()) {
            jogoAtivo = false;
            mostrarAlerta("Jogador " + jogadorAtual + " venceu!");
            perguntarReiniciar();
        } else if (verificarEmpate()) {
            jogoAtivo = false;
            mostrarAlerta("Deu velha!");
            perguntarReiniciar();
        } else {
            jogadorAtual = jogadorAtual.equals("X") ? "O" : "X";
        }
    }

    private boolean verificarVitoria() {
        for (int i = 0; i < 3; i++) {
            if (botoes[i][0].getText().equals(jogadorAtual) &&
                    botoes[i][1].getText().equals(jogadorAtual) &&
                    botoes[i][2].getText().equals(jogadorAtual))
                return true;

            if (botoes[0][i].getText().equals(jogadorAtual) &&
                    botoes[1][i].getText().equals(jogadorAtual) &&
                    botoes[2][i].getText().equals(jogadorAtual))
                return true;
        }

        return (botoes[0][0].getText().equals(jogadorAtual) &&
                botoes[1][1].getText().equals(jogadorAtual) &&
                botoes[2][2].getText().equals(jogadorAtual))
                ||
                (botoes[0][2].getText().equals(jogadorAtual) &&
                        botoes[1][1].getText().equals(jogadorAtual) &&
                        botoes[2][0].getText().equals(jogadorAtual));
    }

    private boolean verificarEmpate() {
        for (Button[] linha : botoes) {
            for (Button b : linha) {
                if (b.getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void perguntarReiniciar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja jogar novamente?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Reiniciar Jogo");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                reiniciarJogo();
            } else {
                System.exit(0);
            }
        });
    }

    private void reiniciarJogo() {
        jogadorAtual = "X";
        jogoAtivo = true;
        for (Button[] linha : botoes) {
            for (Button b : linha) {
                b.setText("");
                b.setStyle("-fx-background-color: rgb(72,61,139); -fx-text-fill: white;");
            }
        }
    }
    
}
