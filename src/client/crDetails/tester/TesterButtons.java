package client.crDetails.tester;

import java.io.IOException;

import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
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

	public void initialize() {

		setDecisionInfo.setVisible(false);
		currPhase = CrDetails.getCurrRequest().getPhases().get(0);
		if (!(currPhase.getName().equals(Phase.PhaseName.VALIDATION))) {
			setDecisionButton.setDisable(true);
			setDecisionInfo.setVisible(true);
		}

	}

	@FXML
	void showSetDecisionDialog(ActionEvent event) {
		try {

			IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml", 400,
					300);
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void showEvaluationReport(ActionEvent event) throws IOException {

		IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/viewEvaluationReport.fxml", 600, 632);

	}

	@FXML
	void setDecisionInfoMsg(ActionEvent event) {
		IcmUtils.displayInformationMsg("Set Decision Help", "Decision Already Submitted",
				"The decision for this phase already submitted.");
	}

	public static void setPhase(Phase NewPhase) {
		currPhase = NewPhase;
	}

	public static Phase getPhase() {
		return currPhase;
	}

}
