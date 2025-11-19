import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class Menu extends Application {

    @Override
    public void start(Stage stage) {

        AudioPlayer.playLoop("/musica_menu.wav");

        stage.setTitle("Menu de Jogos");

        Label titulo = new Label("Menu de Jogos");
        titulo.getStyleClass().add("titulo-menu");

        // fonte arcade
        Font arcade = Font.loadFont(
                Objects.requireNonNull(getClass().getResourceAsStream("/fonts/ARCADE.ttf")),
                24
        );

        // Imagens
        Image imgVelha  = new Image(getClass().getResourceAsStream("/jogo_da_velha.png"), 180, 0, true, true);
        Image imgGenius = new Image(getClass().getResourceAsStream("/icone_genius.png"), 180, 0, true, true);
        Image imgForca  = new Image(getClass().getResourceAsStream("/forca.png"), 180, 0, true, true);

        // Botões
        Button btnJogoVelha = new Button();
        btnJogoVelha.setGraphic(new ImageView(imgVelha));

        Button btnGenius = new Button();
        btnGenius.setGraphic(new ImageView(imgGenius));

        Button btnJogoForca = new Button();
        btnJogoForca.setGraphic(new ImageView(imgForca));

        Button btnSair = new Button("Sair");

        // Ações dos botões
        btnJogoVelha.setOnAction(e -> {
            try {
                AudioPlayer.stop();
                new JogoDaVelha().start(new Stage());
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnGenius.setOnAction(e -> {
            try {
                AudioPlayer.stop();
                new SimonGame().start(new Stage());
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnJogoForca.setOnAction(e -> {
            try {
                AudioPlayer.stop();
                new ForcaJavaFX().start(new Stage());
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnSair.setOnAction(e -> {
            AudioPlayer.stop();
            stage.close();
        });

        // Layout
        HBox linha2 = new HBox(30, btnGenius, btnJogoForca);
        linha2.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, titulo, linha2, btnJogoVelha, btnSair);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("menu-container");

        // Estilos
        btnJogoVelha.getStyleClass().add("botao-menu");
        btnGenius.getStyleClass().add("botao-menu");
        btnJogoForca.getStyleClass().add("botao-menu");

        // Botão sair especial (vermelho)
        btnSair.getStyleClass().add("botao-sair");
        btnSair.setPrefWidth(200);
        btnSair.setPrefHeight(60);
        btnSair.setFont(arcade);

        // Cena
        Scene scene = new Scene(layout, 800, 800);
        scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
