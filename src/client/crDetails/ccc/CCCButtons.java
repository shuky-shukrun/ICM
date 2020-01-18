package client.crDetails.ccc;

import java.io.IOException;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeInitiator;
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
	private Button setDecisionInfoButton;

	private ClientController clientController;
	private static Phase currPhase;
	private CrDetails crDetails;
	private boolean flag = false;

	@FXML
	/**
	 * load the assign tester dialog when the appropriate button pressed
	 *
	 * @param event-"Assign Tester" button pressed event
	 */
	void showAssignTesterDialog(ActionEvent event) throws IOException {
		try {
			IcmUtils.popUpScene(this, "Assign Tester", "/client/crDetails/ccc/AssignTester.fxml", 588, 688);
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	/**
	 * load the view evaluation report dialog when the appropriate button pressed
	 *
	 * @param event-"View Evaluation Report" button pressed event
	 */
	void showEvaluationReport(ActionEvent event) throws IOException {
		IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/ViewEvaluationReport.fxml", 588, 688);
	}

	@FXML
	/**
	 * load the set decision dialog when the appropriate button pressed
	 *
	 * @param event-"Set Decision" button pressed event
	 */
	void showSetDecisionDialog(ActionEvent event) {
		try {
			IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml", 588,
					688);
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	/**
	 * load the set decision info dialog when the appropriate button pressed
	 *
	 * @param event-"Set Decision Info" button pressed event
	 */
	void setDecisionInfoMsg(ActionEvent event) {
		if(CrDetails.getCurrRequest().getCurrPhaseName() == Phase.PhaseName.VALIDATION &&
		CrDetails.getCurrRequest().getCurrPhaseStatus() == Phase.PhaseStatus.PHASE_LEADER_ASSIGNED) {
			IcmUtils.displayInformationMsg(
					"Set Decision Help",
					"Tester is not assigned",
					"Only the tester can use this button.\n" +
							"Please assign tester.");
		}
		IcmUtils.displayInformationMsg(
				"Set Decision Help",
				"Decision already submitted",
				"The decision for this request already submitted.");
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {

	}

	/**
	 * Initialize the CCC/ChairMan/tester change request summary dialog
	 */
	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Phase.PhaseStatus phaseStatus = crDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
		// Phase.PhaseName phase = crDetails.getCurrRequest().getCurrPhaseName();
		ChangeInitiator currUser = ClientController.getUser();
		if (flag == false) {
			currPhase = CrDetails.getCurrRequest().getPhases().get(0);
			flag = true;
		}
	//	Phase.PhaseName phase = currPhase.getName();
		IEPhasePosition tester = CrDetails.getCurrRequest().getPhases().get(0).getIePhasePosition()
				.get(IEPhasePosition.PhasePosition.TESTER);

		switch (currPhase.getName()) {
		case EXAMINATION:
			if (currUser.getPosition() == Position.CCC) {
				setDecisionButton.setVisible(false);
				assignTesterButton.setVisible(false);
				setDecisionInfoButton.setVisible(false);
			} else {
				setDecisionButton.setDisable(false);
				assignTesterButton.setVisible(false);
				setDecisionInfoButton.setVisible(false);
			}
			break;
		case VALIDATION:
			if (currUser.getPosition() == Position.CCC) {
				if (currUser.getId().equals(tester.getInformationEngineer().getId())) {
					viewEvaluationReportButton.setVisible(true);
					viewEvaluationReportButton.setDisable(false);
					setDecisionButton.setVisible(true);
					setDecisionButton.setDisable(false);
					setDecisionInfoButton.setVisible(true);
					setDecisionInfoButton.setDisable(true);
					assignTesterButton.setVisible(false);
				} else {
					viewEvaluationReportButton.setVisible(true);
					viewEvaluationReportButton.setDisable(false);
					setDecisionButton.setVisible(false);
					setDecisionInfoButton.setVisible(false);
					assignTesterButton.setVisible(false);
				}

			} else if (currUser.getPosition() == Position.CHAIRMAN) {
				switch (phaseStatus) {
				case PHASE_LEADER_ASSIGNED:
					viewEvaluationReportButton.setVisible(true);
					viewEvaluationReportButton.setDisable(false);
					setDecisionButton.setDisable(true);
					setDecisionInfoButton.setVisible(true);
					setDecisionInfoButton.setDisable(false);
					assignTesterButton.setVisible(true);
					assignTesterButton.setDisable(false);
					break;
				case IN_PROCESS:
				case EXTENSION_TIME_REQUESTED:
				case EXTENSION_TIME_APPROVED:
				case PHASE_EXEC_LEADER_ASSIGNED:
					if (currUser.getId().equals(tester.getInformationEngineer().getId())) {
						viewEvaluationReportButton.setVisible(true);
						viewEvaluationReportButton.setDisable(false);
						setDecisionButton.setVisible(true);
						setDecisionButton.setDisable(false);
						setDecisionInfoButton.setVisible(true);
						setDecisionInfoButton.setDisable(true);
						assignTesterButton.setVisible(false);
					} else {
						viewEvaluationReportButton.setVisible(true);
						viewEvaluationReportButton.setDisable(false);
						setDecisionButton.setVisible(false);
						setDecisionInfoButton.setVisible(false);
						assignTesterButton.setVisible(false);
					}

					break;
				}
			}

			break;
			case EVALUATION:
				viewEvaluationReportButton.setVisible(false);
				setDecisionButton.setVisible(true);
				setDecisionButton.setDisable(true);
				setDecisionInfoButton.setVisible(true);
				setDecisionInfoButton.setDisable(false);
				assignTesterButton.setVisible(false);
				break;
		default:
			viewEvaluationReportButton.setVisible(true);
			viewEvaluationReportButton.setDisable(false);
			setDecisionButton.setVisible(true);
			setDecisionButton.setDisable(true);
			setDecisionInfoButton.setVisible(true);
			setDecisionInfoButton.setDisable(false);
			assignTesterButton.setVisible(false);

			break;
		}
	}
	
	/**
	 * Sets the attribute of current phase with phase object
	 * @param phase-the current phase object
	 */
    public static void setCurrPhase (Phase phase) {
    	CCCButtons.currPhase=phase;
    }
    
	/**
	 * gets the attribute of current phase
	 * returns the current phase
	 */
    public static Phase getPhase() {
    	return currPhase;
    	
    }
}
