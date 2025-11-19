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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class ForcaJavaFX extends Application {

    private static final List<String> PALAVRAS = Arrays.asList(
            "COMPUTADOR","PROGRAMACAO","JAVA","ALGORITMO","ESTRUTURA",
            "ENGENHARIA","PROFESSOR","FACULDADE","SISTEMA","REDE",
            "BIBLIOTECA","TECLADO","MOUSE","MONITOR","PROCESSADOR",
            "RESISTOR","VENTOINHA","CAPACITOR","LAPTOP","WINDOWS",
            "LINUX","MATRIZ","VETOR"
    );

    private String palavra;
    private char[] progresso;
    private List<Character> letrasErradas = new ArrayList<>();
    private int erros = 0;
    private static final int MAX_ERROS = 6;

    private Label lblProgresso;
    private Label lblErros;
    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) {

        AudioPlayer.stop();
        AudioPlayer.playLoop("/ffmusica.wav");

        escolherPalavraAleatoria();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-image: url('/fundoforca.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: no-repeat;");

        VBox topBox = new VBox(6);
        topBox.setAlignment(Pos.CENTER);

        Label titulo = new Label("FORCA NEON");
        titulo.setFont(Font.font("Arcade", 60));
        titulo.setTextFill(Color.web("#00ff7f"));

        DropShadow glow = new DropShadow();
        glow.setRadius(25);
        glow.setSpread(0.5);
        glow.setColor(Color.web("#00ff7f"));
        titulo.setEffect(glow);

        lblProgresso = new Label(getProgressoFormatado());
        lblProgresso.setFont(Font.font("Arcade", 40));
        lblProgresso.setTextFill(Color.web("#00ff7f"));

        lblErros = new Label("Letras erradas: ");
        lblErros.setFont(Font.font("Arcade", 22));
        lblErros.setTextFill(Color.web("#00ff7f"));

        topBox.getChildren().addAll(titulo, lblProgresso, lblErros);
        root.setTop(topBox);

        canvas = new Canvas(400, 400);

        canvas.setStyle("-fx-background-color: transparent;");

        desenharForca();

        HBox centerBox = new HBox(canvas);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(12));
        root.setCenter(centerBox);

        VBox bottom = new VBox(8);
        FlowPane teclado = new FlowPane();
        teclado.setHgap(6);
        teclado.setVgap(6);
        teclado.setPadding(new Insets(6));
        teclado.setPrefWrapLength(750);

        for (char c = 'A'; c <= 'Z'; c++) {
            Button btn = new Button(String.valueOf(c));
            btn.setPrefSize(45, 45); // 🔹 TAMANHO ANTIGO
            btn.setFont(Font.font("Arcade", 20));

            btn.setStyle(
                    "-fx-background-color: black;" +
                            "-fx-border-color: #00ff7f;" +
                            "-fx-border-width: 2;" +
                            "-fx-text-fill: #00ff7f;"
            );

            btn.setOnAction(e -> {
                btn.setDisable(true);
                processarPalpite(btn.getText().charAt(0), primaryStage);
            });

            teclado.getChildren().add(btn);
        }


        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button btnReiniciar = criarBotaoNeon("Reiniciar");
        btnReiniciar.setOnAction(e -> reiniciarJogo(root));

        Button btnDesistir = criarBotaoNeon("Revelar Palavra");
        btnDesistir.setTooltip(new Tooltip("Mostra a palavra"));
        btnDesistir.setOnAction(e -> {
            mostrarAlerta("Palavra", "A palavra era: " + palavra);
            reiniciarJogo(root);
        });

        Button btnMenu = criarBotaoNeon("Menu");
        btnMenu.setOnAction(e -> voltarAoMenu(primaryStage));

        controles.getChildren().addAll(btnReiniciar, btnDesistir, btnMenu);

        ScrollPane sp = new ScrollPane(teclado);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        bottom.getChildren().addAll(sp, controles);
        root.setBottom(bottom);

        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setTitle("Forca Neon");
        primaryStage.show();
    }

    private Button criarBotaoNeon(String texto) {
        Button b = new Button(texto);
        b.setFont(Font.font("Arcade", 22));
        b.setPrefSize(200, 45);
        b.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: #00ff7f;" +
                        "-fx-border-color: #00ff7f;" +
                        "-fx-border-width: 3;"
        );
        return b;
    }

    private void escolherPalavraAleatoria() {
        palavra = PALAVRAS.get(new Random().nextInt(PALAVRAS.size()));
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

        if (!acertou && !letrasErradas.contains(letra)) {
            letrasErradas.add(letra);
            erros++;
        }

        atualizarInterface();
        desenharForca();
        checarFim(stage);
    }

    private void atualizarInterface() {
        lblProgresso.setText(getProgressoFormatado());
        lblErros.setText("Erradas: " +
                letrasErradas.stream().map(String::valueOf).collect(Collectors.joining(" "))
                + " (" + erros + "/" + MAX_ERROS + ")");
    }

    private void checarFim(Stage stage) {
        if (new String(progresso).equals(palavra)) {
            mostrarAlerta("Você venceu!", "Palavra: " + palavra);
            voltarAoMenu(stage);
        } else if (erros >= MAX_ERROS) {
            mostrarAlerta("Você perdeu!", "Palavra: " + palavra);
            voltarAoMenu(stage);
        }
    }

    private void reiniciarJogo(BorderPane root) {
        escolherPalavraAleatoria();
        atualizarInterface();
        desenharForca();

        FlowPane teclado = (FlowPane) ((ScrollPane) ((VBox) root.getBottom()).getChildren().get(0)).getContent();
        teclado.getChildren().forEach(n -> n.setDisable(false));
    }

    private void voltarAoMenu(Stage stage) {
        AudioPlayer.stop();
        try {
            new Menu().start(new Stage());
            stage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void desenharForca() {
        GraphicsContext gc = canvas.getGraphicsContext2D();


        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.web("#00ff7f"));
        gc.setLineWidth(5);

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
            case 1: gc.strokeOval(180, 75, 40, 40);
        }
    }
}
