package client.crDetails.tester;

import java.io.IOException;

import client.ClientController;
import client.ClientMain;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import entities.Position;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;

public class TesterButtons implements ClientUI {

    
    

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
	@FXML
	private Button setDecisionButton;

	@FXML
	private Button viewEvaluationReportButton;
	@FXML
	private Button setDecisionInfo;

	private static Phase currPhase;

	/**
	 * Initialize the Tester buttons
	 */
	public void initialize() {

		setDecisionInfo.setVisible(false);
		currPhase = CrDetails.getCurrRequest().getPhases().get(0);
		if (!(currPhase.getName().equals(Phase.PhaseName.VALIDATION))) {
			setDecisionButton.setDisable(true);
			setDecisionInfo.setVisible(true);
		}

			setDecisionButton.setVisible(false);
			setDecisionInfo.setVisible(false);
	}

	@FXML
	/**
	 * Show the set decision dialog, if possible when setDecision button pressed
	 * 
	 * @param event-setDecision button pressed event
	 */
	void showSetDecisionDialog(ActionEvent event) {
		try {

			IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml", 588,
					688);
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	/**
	 * Show the evaluation report dialog, if possible when viewEvaluationReport button pressed
	 * 
	 * @param event-viewEvaluationReport button pressed event
	 */
	void showEvaluationReport(ActionEvent event) throws IOException {

		IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/viewEvaluationReport.fxml", 600, 632);

	}

	@FXML
	/**
	 * Show pop-up with the information why the set decision button is disabled.
	 * 
	 * @param event-setDecisionInfo button is pressed event
	 */
	void setDecisionInfoMsg(ActionEvent event) {
		IcmUtils.displayInformationMsg(
				"Set decision help",
				"Decision already submitted",
				"The decision for this phase already submitted.");
	}

	public static void setPhase(Phase NewPhase) {
		currPhase = NewPhase;
	}

	public static Phase getPhase() {
		return currPhase;
	}

}
