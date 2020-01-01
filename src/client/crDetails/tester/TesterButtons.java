package client.crDetails.tester;

import java.io.IOException;

import client.ClientUI;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.ServerService;

public class TesterButtons implements ClientUI {

    @FXML
    private Button setDecisionButton1;

    @FXML
    void showSetDecisionDialog(ActionEvent event) {
    	try {
    		
    		IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml", 400, 300);
   		 
   		 } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
