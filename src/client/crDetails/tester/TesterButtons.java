package client.crDetails.tester;

import java.io.IOException;

import client.ClientUI;
import client.crDetails.CrDetails;
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
    private Button viewEvaluationReport;

    
    public void initialize() {
    	if(CrDetails.getCurrRequest().isSuspended() == true)
    	{//if the change request is suspended then disable the option to do something with the change request.
    		setDecisionButton1.setDisable(true);
    	}
    }
    
    @FXML
    void showSetDecisionDialog(ActionEvent event) {
    	try {
    		
    		IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml", 400, 300);
   		 } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    @FXML
    void viewAction(ActionEvent event) throws IOException {
		IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/viewEvaluationReport.fxml", 600, 632);

    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
