package Model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {

    private Scene scene0;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/View/ux.fxml"));
        primaryStage.setTitle("Sheep Counter");
        scene0 = new Scene(root);
        scene0.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene0);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
