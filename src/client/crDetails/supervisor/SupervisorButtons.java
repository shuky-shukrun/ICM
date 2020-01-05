package client.crDetails.supervisor;

import java.io.IOException;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.Phase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;

public class SupervisorButtons implements ClientUI {


    @FXML
    private Button phaseTimeDecisionButton;

    @FXML
    private Button assignPhaseLeadersButton;

    @FXML
    private Button assignPhaseWorkersButton;

    @FXML
    private Button freezeRequestButton;

    @FXML
    private Button closeChangeRequestButton;

    
    
    @FXML
    void closeChangeRequest(ActionEvent event) {
        System.out.println("closed!");
    }

    @FXML
    void freezeRequest(ActionEvent event) {

    }

    @FXML
    void setTimeDecision(ActionEvent event) {

    }

    @FXML
    void showAssignPhaseLeadersDialog(ActionEvent event) {

		try {
			IcmUtils.popUpScene(this, "Assign Phase Leaders", "/client/crDetails/supervisor/AssignPhaseLeaders/AssignPhaseLeaders.fxml",600 ,655 );
		} catch (IOException e) {
			e.printStackTrace(); }
    	
    	
    	
    	
    }

    @FXML
    void showAssignPhaseWorkersDialog(ActionEvent event) {

    }











    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
