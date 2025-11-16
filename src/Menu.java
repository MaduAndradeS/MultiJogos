import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color; // se quiser mudar a cor do título
import javafx.scene.text.Font;   // se quiser mudar a fonte/tamanho


public class Menu extends Application {

    @Override
    public void start(Stage stage) {

        AudioPlayer.playLoop("/musica_menu.wav"); // música do menu

        stage.setTitle("Menu de Jogos");

        Label titulo = new Label("Menu de Jogos");
        titulo.setFont(Font.font("Arial", 48));  // tamanho grande
        titulo.setTextFill(Color.BLACK);         // cor branca
        titulo.setAlignment(Pos.CENTER);

        Image imgVelha = new Image(getClass().getResourceAsStream("/jogo_da_velha.png"), 180, 0, true, true);
        Image imgGenius = new Image(getClass().getResourceAsStream("/icone_genius.png"), 180, 0, true, true);
        Image imgForca = new Image(getClass().getResourceAsStream("/forca.png"), 180, 0, true, true);


        Button btnJogoVelha = new Button();
        btnJogoVelha.setGraphic(new ImageView(imgVelha));

        Button btnGenius = new Button();
        btnGenius.setGraphic(new ImageView(imgGenius));

        Button btnJogoForca = new Button();
        btnJogoForca.setGraphic(new ImageView(imgForca));

        Button btnSair = new Button("Sair");
        btnSair.setMinWidth(200);
        btnSair.setStyle("-fx-font-size: 18px; -fx-background-color:#8b0000; -fx-text-fill: white;");

        btnJogoVelha.setOnAction(e -> {
            try {
                AudioPlayer.stop(); // para música do menu
                new JogoDaVelha().start(new Stage());
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnGenius.setOnAction(e -> {
            try {
                AudioPlayer.stop(); // para música do menu
                new SimonGame().start(new Stage());
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnJogoForca.setOnAction(e -> {
            try {
                AudioPlayer.stop(); // para música do menu
                new ForcaJavaFX().start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        btnSair.setOnAction(e -> {
            AudioPlayer.stop(); // para música
            stage.close();
        });

        VBox layout = new VBox(20,titulo, btnJogoVelha, btnGenius, btnJogoForca, btnSair);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
