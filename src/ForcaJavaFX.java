import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ForcaJavaFX extends Application {

    private static final List<String> PALAVRAS = Arrays.asList(
            "COMPUTADOR","PROGRAMACAO","JAVA","ALGORITMO","ESTRUTURA",
            "ENGENHARIA","PROFESSOR","FACULDADE","SISTEMA","REDE",
            "BIBLIOTECA","TECLADO","MOUSE","MONITOR","PROCESSADOR", "RESISTOR",
            "VENTOINHA", "CAPACITOR", "LAPTOP", "WINDOWS", "LINUX", "MATRIZ", "VETOR"
    );

    private String palavra;
    private char[] progresso;
    private List<Character> letrasErradas = new ArrayList<>();
    private int erros = 0;
    private static final int MAX_ERROS = 6;

    private Label lblProgresso;
    private Label lblErros;
    private Canvas canvas;

    public ForcaJavaFX() {}


    @Override
    public void start(Stage primaryStage) {
        // Para garantir que nenhuma música anterior continue
        AudioPlayer.stop();

        // Música específica da Forca
        AudioPlayer.playLoop("/ffmusica.wav");

        escolherPalavraAleatoria();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        VBox topBox = new VBox(6);
        topBox.setAlignment(Pos.CENTER);
        Label titulo = new Label("Jogo da Forca");
        titulo.setFont(Font.font(28));

        lblProgresso = new Label(getProgressoFormatado());
        lblProgresso.setFont(Font.font(24));

        lblErros = new Label("Letras erradas: ");
        lblErros.setFont(Font.font(16));

        topBox.getChildren().addAll(titulo, lblProgresso, lblErros);
        root.setTop(topBox);

        // Ajuste 1: Aumentar o Canvas para ocupar melhor o centro (400x400)
        canvas = new Canvas(400, 400);
        desenharForca();

        HBox centerBox = new HBox(canvas);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(12));

        // Ajuste 2: Garantir que o HBox use a altura completa do centro
        centerBox.setFillHeight(true);
        root.setCenter(centerBox);

        VBox bottom = new VBox(8);
        FlowPane teclado = new FlowPane();
        teclado.setHgap(6);
        teclado.setVgap(6);
        teclado.setPadding(new Insets(6));

        // Ajuste 3: Aumentar o wrap length para ~750 para usar a largura de 800
        teclado.setPrefWrapLength(750);

        for (char c = 'A'; c <= 'Z'; c++) {
            Button btn = new Button(String.valueOf(c));
            btn.setPrefWidth(40);
            // Opcional: Aumentar a altura dos botões para preencher melhor o espaço vertical
            btn.setPrefHeight(40);
            btn.setOnAction(e -> {
                btn.setDisable(true);
                processarPalpite(btn.getText().charAt(0), primaryStage);
            });
            teclado.getChildren().add(btn);
        }

        HBox controles = new HBox(8);
        controles.setAlignment(Pos.CENTER);

        Button btnReiniciar = new Button("Reiniciar");
        btnReiniciar.setOnAction(e -> reiniciarJogo(root));

        Button btnDesistir = new Button("Revelar palavra");
        btnDesistir.setTooltip(new Tooltip("Revela a palavra"));
        btnDesistir.setOnAction(e -> {
            mostrarAlerta("Palavra", "A palavra era: " + palavra);
            reiniciarJogo(root);
        });

        Button btnVoltarMenu = new Button("Voltar ao Menu");
        btnVoltarMenu.setOnAction(e -> voltarAoMenu(primaryStage));

        controles.getChildren().addAll(btnReiniciar, btnDesistir, btnVoltarMenu);

        // O ScrollPane é necessário se o teclado for muito grande
        ScrollPane scrollTeclado = new ScrollPane(teclado);
        // Remove as barras de rolagem desnecessárias do ScrollPane (se quiser)
        scrollTeclado.setFitToWidth(true);
        scrollTeclado.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        bottom.getChildren().addAll(scrollTeclado, controles);
        root.setBottom(bottom);

        // Mantenha a Scene com 800x800
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Forca - JavaFX");
        primaryStage.show();
    }

    private void escolherPalavraAleatoria() {
        Random r = new Random();
        palavra = PALAVRAS.get(r.nextInt(PALAVRAS.size()));
        progresso = new char[palavra.length()];
        Arrays.fill(progresso, '_');
        letrasErradas.clear();
        erros = 0;
    }

    private String getProgressoFormatado() {
        return Arrays.stream(new String(progresso).split(""))
                .collect(Collectors.joining(" "));
    }

    private void processarPalpite(char letra, Stage stage) {
        boolean acertou = false;

        for (int i = 0; i < palavra.length(); i++) {
            if (palavra.charAt(i) == letra) {
                progresso[i] = letra;
                acertou = true;
            }
        }

        if (!acertou) {
            if (!letrasErradas.contains(letra)) {
                letrasErradas.add(letra);
                erros++;
            }
        }

        atualizarInterface();
        desenharForca();
        checarFim(stage);
    }

    private void atualizarInterface() {
        lblProgresso.setText(getProgressoFormatado());
        lblErros.setText("Letras erradas: " +
                letrasErradas.stream().map(String::valueOf).collect(Collectors.joining(" ")) +
                " (" + erros + "/" + MAX_ERROS + ")");
    }

    private void checarFim(Stage stage) {
        boolean ganhou = true;
        for (char c : progresso)
            if (c == '_') ganhou = false;

        if (ganhou) {
            mostrarAlerta("Você ganhou!", "Parabéns! Palavra: " + palavra);
            voltarAoMenu(stage);
            return;
        }

        if (erros >= MAX_ERROS) {
            mostrarAlerta("Você perdeu!", "A palavra era: " + palavra);
            voltarAoMenu(stage);
        }
    }

    private void reiniciarJogo(BorderPane root) {
        escolherPalavraAleatoria();
        atualizarInterface();
        desenharForca();

        if (root != null) {
            FlowPane teclado = (FlowPane) ((ScrollPane) ((VBox) root.getBottom()).getChildren().get(0)).getContent();
            teclado.getChildren().forEach(n -> n.setDisable(false));
        }
    }

    private void voltarAoMenu(Stage stage) {
        AudioPlayer.stop(); // para música da Forca
        try {
            new Menu().start(new Stage()); // abre menu
            stage.close(); // fecha Forca
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void desenharForca() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);

        gc.strokeLine(30, 280, 270, 280);
        gc.strokeLine(80, 280, 80, 40);
        gc.strokeLine(80, 40, 200, 40);
        gc.strokeLine(200, 40, 200, 70);

        switch (erros) {
            case 6: gc.strokeLine(200, 170, 220, 210);
            case 5: gc.strokeLine(200, 170, 180, 210);
            case 4: gc.strokeLine(200, 120, 230, 150);
            case 3: gc.strokeLine(200, 120, 170, 150);
            case 2: gc.strokeLine(200, 110, 200, 170);
            case 1: gc.strokeOval(183, 73, 34, 34);
        }
    }
}
