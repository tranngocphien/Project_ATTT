package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(login,800,600);
            stage.setScene(scene);
            stage.setTitle("LOGIN");
            stage.setResizable(false);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}
