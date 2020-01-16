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
import entities.IEPhasePosition;
import entities.Phase;
import entities.Position;
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
	private static Phase currPhase;
	private CrDetails crDetails;

	@FXML
	void showAssignTesterDialog(ActionEvent event) throws IOException {
		try {
			IcmUtils.popUpScene(this, "Assign Tester", "/client/crDetails/ccc/AssignTester.fxml", 400, 300);
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void showEvaluationReport(ActionEvent event) throws IOException {
		IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/ViewEvaluationReport.fxml", 588, 688);
	}

	@FXML
	void showSetDecisionDialog(ActionEvent event) {
		try {

			IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml",
					588,688);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void moreInformationEvent(ActionEvent event) {
		IcmUtils.displayInformationMsg("Information message",
				"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
						+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
				"Change request " + CrDetails.getCurrRequest().getId() + " -Tester was allready assignd");

	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {

	}

	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Phase.PhaseStatus phaseStatus = crDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
		Phase.PhaseName phase = crDetails.getCurrRequest().getCurrPhaseName();
		ChangeInitiator currUser = ClientController.getUser();
		currPhase=CrDetails.getCurrRequest().getPhases().get(0);
		switch (phase) {
		case EXAMINATION:
			if (currUser.getPosition() == Position.CCC) {
				setDecisionButton.setVisible(false);
				assignTesterButton.setVisible(false);
				moreInformationButton.setVisible(false);
			} else {
				setDecisionButton.setDisable(false);
				assignTesterButton.setVisible(false);
				moreInformationButton.setVisible(false);
			}
			break;
		case VALIDATION:
			switch (phaseStatus) {
			case PHASE_LEADER_ASSIGNED:
				setDecisionButton.setVisible(false);
				assignTesterButton.setVisible(true);
				assignTesterButton.setDisable(false);
				moreInformationButton.setVisible(false);
				break;
			case IN_PROCESS:
			case EXTENSION_TIME_REQUESTED:
			case EXTENSION_TIME_APPROVED:
				setDecisionButton.setVisible(false);
				assignTesterButton.setVisible(false);
				viewEvaluationReportButton.setVisible(true);
				viewEvaluationReportButton.setDisable(false);
				moreInformationButton.setVisible(false);
				break;
			}
			break;
		}
	}
	
    public static void setCurrPhase (Phase phase) {
    	CCCButtons.currPhase=phase;
    }
    
    public static Phase getPhase() {
    	return currPhase;
    	
    }
}
