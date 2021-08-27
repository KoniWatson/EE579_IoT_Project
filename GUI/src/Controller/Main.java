package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/OptionTwo.fxml"));
        Parent root = loader.load();
        Controller controller;
        controller = (Controller) loader.getController();
        controller.setOnClose(primaryStage);
        primaryStage.getIcons().add(new Image("View/Images/logo.png"));
        primaryStage.setTitle("AKIN");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

