package server;

import client.ClientUI;
import common.IcmUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application implements ClientUI {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // load the scene
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/server/serverUI/ServerUI.fxml").openStream());
            Scene scene = new Scene(root, 560, 550);
            primaryStage.setScene(scene);
            primaryStage.setTitle("ICM Server");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            IcmUtils.displayErrorMsg(e.getMessage());
        }
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
