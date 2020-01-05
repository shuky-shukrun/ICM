package client.crDetails.initiator;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;

public class InitiatorButtons implements ClientUI {

    @FXML
    private Button attachFilesButton;

    @FXML
    public void attachFiles(ActionEvent event) {
    	
    }
    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
