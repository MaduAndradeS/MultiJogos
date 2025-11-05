import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Jogo2 extends Application {

    @Override
    public void start(Stage stage) {

        Label label = new Label("Aqui ficaria o Jogo da Forca!");

        StackPane layout = new StackPane(label);

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Jogo da Forca");
        stage.show();
    }
}
