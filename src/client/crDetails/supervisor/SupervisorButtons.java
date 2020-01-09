package client.crDetails.supervisor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.Phase;
import entities.Phase.PhaseStatus;
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


    public void initialize() {
    	assignPhaseLeadersButton.setDisable(false);
    	
    		if(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus()!=PhaseStatus.SUBMITTED)
    			assignPhaseLeadersButton.setDisable(true);
	}
    
    
    
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
			IcmUtils.popUpScene(this, "Assign Phase Leaders", "/client/crDetails/supervisor/AssignPhaseLeaders/AssignPhaseLeaders.fxml",600 ,680 );
			initialize();
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
