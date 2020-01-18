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

    /**
	 * This function returns the stage
	 * @return ClientMain.primaryStage
	 */
    public static Stage getPrimaryStage() {
        return ClientMain.primaryStage;
    }
    
    /**
	 * This function set stage into ClientMain.primaryStage
	 * @param primaryStage
	 */
    public static void setPrimaryStage(Stage primaryStage) {
        ClientMain.primaryStage = primaryStage;
    }

    /**
	 * This function returns the port
	 * @return ClientMain.port 
	 */
    public static int getPort() {
        return ClientMain.port;
    }
    
    /**
	 * This function set port into ClientMain.port
	 * @param port
	 */
    public static void setPort(int port) {
        ClientMain.port = port;
    }

    /**
	 * This function returns the host
	 * @return ClientMain.host 
	 */
    public static String getHost() {
        return ClientMain.host;
    }
    
    /**
   	 * This function set host into ClientMain.host
   	 * @param host
   	 */
    public static void setHost(String host) {
        ClientMain.host = host;
    }

    
    /**
   	 *Loading the HostIpSelector Window 
   	 */
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

    /**
	 * handle the returned value from server.
	 * @param serverService-ServerService object that the client controller send
	 */
    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
