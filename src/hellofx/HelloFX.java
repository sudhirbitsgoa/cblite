package hellofx;

import javafx.scene.control.Label;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import hellofx.Main;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Main.main(null);
//        Label l = new Label("Data Sync is running...");
//        Scene scene;
//        scene = new Scene(new StackPane(l), 300, 200);
//        stage.setScene(scene);
//        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}