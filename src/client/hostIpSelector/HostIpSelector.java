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
    private Button loginButton;

    @FXML
    private TextField hostTextField;

    @FXML
    void login(ActionEvent event) {
        ClientMain.setHost(ipTextField.getText());
        ClientMain.setPort(Integer.parseInt(hostTextField.getText()));

        // load the gui and starting it
        try {
            IcmUtils.loadScene(this, "ICM - Login", "/client/login/Login2.fxml", 800, 500);
//            IcmUtils.loadHomeScene(this);

        } catch (Exception e) {
            e.printStackTrace();
            IcmUtils.displayErrorMsg(e.getMessage());
        }
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}