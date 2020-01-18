package client;

import common.IcmUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import server.ServerService;

public class ClientMain extends Application implements ClientUI{

    final public static int DEFAULT_PORT = 5555;


    private static Stage primaryStage;


    private static String host;
    private static int port;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return ClientMain.primaryStage;
    }
    public static void setPrimaryStage(Stage primaryStage) {
        ClientMain.primaryStage = primaryStage;
    }

    public static int getPort() {
        return ClientMain.port;
    }
    public static void setPort(int port) {
        ClientMain.port = port;
    }

    public static String getHost() {
        return ClientMain.host;
    }
    public static void setHost(String host) {
        ClientMain.host = host;
    }

    @Override
    public void start(Stage primaryStage) {

        // store the primaryStage to static variables
        ClientMain.primaryStage = primaryStage;

        // load the gui and starting it
        try {
            IcmUtils.loadScene(this, "ICM - Client Setup", "/client/hostIpSelector/HostIpSelector.fxml", 480,460);
        } catch (Exception e) {
            e.printStackTrace();
            IcmUtils.displayErrorMsg(
                    "Load scene error",
                    "Load scene error",
                    e.getMessage());
        }
        primaryStage.setResizable(false);
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
