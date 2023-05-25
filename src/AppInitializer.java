import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("com/damian/chatapp/view/HomeScreen.fxml"))));
            primaryStage.getIcons().add(new Image("com/damian/chatapp/assets/logo.png"));
            primaryStage.setTitle("Home.");
            primaryStage.show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"Error while loading the Home UI : "+e.getLocalizedMessage()).show();
        }

    }
}
