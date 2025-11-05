import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Menu de Jogos");

        Button btnJogoVelha = new Button("Jogo da Velha");
        btnJogoVelha.setMinWidth(200);
        btnJogoVelha.setStyle("-fx-font-size: 20px; -fx-background-color:#483d8b; -fx-text-fill: white;");

        Button btnSair = new Button("Sair");
        btnSair.setMinWidth(200);
        btnSair.setStyle("-fx-font-size: 18px; -fx-background-color:#8b0000; -fx-text-fill: white;");

        // Ação dos botões
        btnJogoVelha.setOnAction(e -> {
            try {
                new JogoDaVelha().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnSair.setOnAction(e -> stage.close());

        VBox layout = new VBox(20, btnJogoVelha, btnSair);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
