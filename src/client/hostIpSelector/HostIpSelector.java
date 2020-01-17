package client.hostIpSelector;

import client.ClientMain;
import client.ClientUI;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import server.ServerService;

public class HostIpSelector implements ClientUI {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField hostTextField;

    /**
     * gets the server ip and port from UI and connect to server
     */
    @FXML
    void login() {
        ClientMain.setHost(ipTextField.getText());
        ClientMain.setPort(Integer.parseInt(hostTextField.getText()));

        // load the gui and starting it
        try {
            IcmUtils.loadScene(this, "ICM - Login", "/client/login/Login.fxml", 800, 500);
        } catch (Exception e) {
            e.printStackTrace();
            IcmUtils.displayErrorMsg(e.getMessage());
        }
    }

    /**
     * handle the returned value from server. Does nothing in this case.
     */
    @Override
    public void handleMessageFromClientController(ServerService serverService) {
    }
}