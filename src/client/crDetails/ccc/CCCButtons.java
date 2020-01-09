package client.crDetails.ccc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.EvaluationReport;
import entities.Phase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import server.ServerService;

public class CCCButtons implements ClientUI {


    // ccc buttons controller
    @FXML
    private VBox CCCButtonsVBox;

    @FXML
    private Button viewEvaluationReportButton;

    @FXML
    private Button setDecisionButton;

    @FXML
    private Button assignTesterButton;
    @FXML
    private Button moreInformationButton;

    private ClientController clientController;
    private CrDetails crDetails; 
    


    @FXML
    void showAssignTesterDialog(ActionEvent event) throws IOException {
    	
    	 IcmUtils.popUpScene(this, "Assign Tester","/client/crDetails/ccc/AssignTester.fxml", 400, 300);

    }

    @FXML
    void showEvaluationReport(ActionEvent event) throws IOException {

        IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/viewEvaluationReport.fxml", 600, 632);

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
    void moreInformationEvent(ActionEvent event) {
		IcmUtils.displayInformationMsg("Information message",
				"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
						+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
				"Change request " + CrDetails.getCurrRequest().getId()
						+ " -Tester was allready assignd");
		
	}
    

    @Override
    public void handleMessageFromClientController(ServerService serverService) {
  


    }

    public void enableChairmanButtons() {
        setDecisionButton.setDisable(false);
        //assignTesterButton.setDisable(false);
    }

    public void initialize() {
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        moreInformationButton.setVisible(false);
        Phase.PhaseStatus phaseStatus = crDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
        System.out.println(phaseStatus);
        System.out.println("2");
        Phase.PhaseName phase= crDetails.getCurrRequest().getCurrPhaseName();
        switch(phase) {
		case EXAMINATION:
			assignTesterButton.setDisable(false);
			break;
		case VALIDATION:
			setDecisionButton.setVisible(false);
			assignTesterButton.setDisable(false);
			switch (phaseStatus) {
			case PHASE_EXEC_LEADER_ASSIGNED:
				System.out.println("3");
				assignTesterButton.setDisable(true);
				moreInformationButton.setVisible(true);
				break;
			}
        		break;
        }
    }
}
